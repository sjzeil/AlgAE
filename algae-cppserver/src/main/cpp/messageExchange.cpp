#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <iostream>
#include <string>

#include <algae/communications/messageExchange.h>
#include <algae/communications/capturedOutputMessage.h>
#include <algae/communications/forceShutDownMessage.h>
#include <algae/communications/promptForInputMessage.h>
#include <algae/communications/clientMessage.h>
#include <algae/communications/serverMessage.h>


using namespace std;

namespace algae {

MessageExchange* MessageExchange::singleton = 0;


//
//  The AlgAEStreamBuffer is used to divert C++ std I/O
//  from the algorithm code to/from the AlgAE client.
//
class AlgAEStreamBuffer: public std::streambuf
{
public:
  AlgAEStreamBuffer (int bufferSize, bool forInput);
  ~AlgAEStreamBuffer ();
  virtual int sync ();
  virtual int overflow (int ch);
  virtual int underflow();
private:
  char* buffer;
  unsigned bufSize;
};



AlgAEStreamBuffer::AlgAEStreamBuffer (int bufferSize, bool forInput)
: streambuf(), buffer(new char[bufferSize+1]), bufSize(bufferSize)
{
	if (forInput)
	{
		setg(buffer, buffer+bufferSize, buffer+bufferSize);
		setp(0, 0);
	}
	else
	{
		setp (buffer, buffer+bufferSize);
		setg (0,0,0);
	}
}

AlgAEStreamBuffer::~AlgAEStreamBuffer ()
{
  sync();
  delete [] buffer;
}



int AlgAEStreamBuffer::sync()
{
  overflow(EOF);
  return 0;
}

/**
 * If animated code writes to cout, the output characters are captured in this
 * buffer. On overflow (usually flush or endl), the accumulated output is packaged
 * into a CapturedOutput message and sent to the client for display.
 */
int AlgAEStreamBuffer::overflow (int ch)
{
  if (pbase() != pptr())
    {
      *(pptr()) = 0;
      setp (pbase(), epptr());
      MessageExchange::getMessageExchange().sendMessageToClient(CapturedOutputMessage(pbase()));
    }
  if (ch != EOF)
    sputc(ch);
 return 0;
}

/**
 * If animated code reads from cin, an underflow will be signaled.
 * A PromptForInput message is sent to the client. The function then
 * waits for an InputSupplied message to be returned. The input text
 * is loaded into the buffer to serve the cin input request.
 */
int AlgAEStreamBuffer::underflow ()
{
	string buf = MessageExchange::getMessageExchange().promptClientForInput("Enter input:", "..*");
	string::size_type len = buf.size();
	if (len > bufSize)
		len = bufSize;
	strncpy (buffer, buf.c_str(), len);
	setg (buffer, buffer, buffer+len);
	return *(buffer);
}


MessageExchange::MessageExchange(
		std::istream* actualInputStream,
		std::ostream* actualOutputStream)
{
	// msgsIn and msgsOut preserve access to the actual streams.
	msgsIn = new istream(actualInputStream->rdbuf());
	msgsOut = new ostream(actualOutputStream->rdbuf());
	// Redirect the actual streams
	actualInputStream->rdbuf (new AlgAEStreamBuffer(80, true));
	actualOutputStream->rdbuf (new AlgAEStreamBuffer(256, false));
}

/**
 * Provides access to the message exchange. As a side effect, the first call
 * to this function redirects the actualInputStream and actualOutputStream so that
 * subsequent I/O via those streams is packed into client/server messages.
 */
MessageExchange& MessageExchange::getMessageExchange(
		std::istream& actualInputStream,
		std::ostream& actualOutputStream)
{
	if (singleton == 0)
	{
		singleton = new MessageExchange(&actualInputStream, &actualOutputStream);
	}
	return *singleton;
}



/**
 * Intended for testing purposes: resets the exchange by destroying any existing
 * singleton. Next call to getMessageExchange() will redirect to new streams.
 */
void MessageExchange::reset()
{
	if (singleton != 0)
	{
		delete singleton;
		singleton = 0;
	}
}





/**
 * Writes a message to the client, encoded as XML
 */
void MessageExchange::writeMessage (const ClientMessage& cmsg)
{
	cmsg.printXML(*msgsOut);
	*msgsOut << "\n<<>>" << endl;
	msgsOut->flush();
}


/**
 * Sends a message to the client, waiting until the message
 * is acknowledged.
 */
void MessageExchange::sendMessageToClient (const ClientMessage& cmsg)
{
	writeMessage(cmsg);
	if (cmsg.getKind() == "ForceShutDownMessage")
		return;
	string line;
	while (line.size() == 0) {
		getline(*msgsIn, line);
		if (line == "ShutDown\t" || line == "ShutDown") {
			exit(0);
		}
		else if (line != "Ack" && line.substr(0,4) != "Ack\t")
		{
			string msg = "Protocol error: expected Ack, received: " + line;
			writeMessage(ForceShutDownMessage(msg));
			exit(-1);
		}
	}
}

/**
 * Obtains a message from the client, waiting until such a message is available.
 */
const ServerMessage MessageExchange::getMessageFromClient ()
{
	string line;
	while (line.size() == 0)
		getline(*msgsIn, line);
	string::size_type divide = line.find('\t');
	if (divide == string::npos)
	{
		return ServerMessage(line, "");
	}
	else
	{
		return ServerMessage(line.substr(0, divide), line.substr(divide+1));
	}
}

/**
 * Sends a prompt (and required pattern) to the client, then waits for a corresponding
 * InputSupplied message from the client. Any Pull messages received in the interim are
 * acknowledged. Any other message is considered a protocol violation.
 */
std::string MessageExchange::promptClientForInput (std::string prompt, std::string requiredPattern)
{
	writeMessage (PromptForInputMessage(prompt, requiredPattern));
	while (true)
	{
		ServerMessage smsg = getMessageFromClient();
		string msgKind = smsg.getKind();
		if (msgKind == "Ack")
			continue;
		else if (msgKind == "ShutDown")
			exit(0);
		else if (msgKind == "InputSupplied")
			return smsg.getDetail();
		else
		{
			string msg = "Protocol error: expected InputSupplied, received: " + smsg.getKind() + ":" + smsg.getDetail();
			writeMessage(ForceShutDownMessage(msg));
			exit(-1);
		}
	}
	return "";
}



}
