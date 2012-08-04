#include <algae/communications/messageExchange.h>
#include <algae/communications/sourceCodeMessage.h>

#include <iostream>
#include <string>
#include <sstream>

#include "gtest/gtest.h"

#include "xmlTester.h"


namespace {

using namespace std;
using namespace algae;

// The fixture for testing class Foo.
class MessageExchangeTests : public ::testing::Test {
protected:
	  ostringstream out;
	  stringbuf& output;


	MessageExchangeTests()
	  : out(), output(*(out.rdbuf()))
	{
	}

  virtual ~MessageExchangeTests()
  {
  }

  virtual void SetUp() {
	  MessageExchange::reset();
  }

  virtual void TearDown() {
	  MessageExchange::reset();
  }



};


TEST_F (MessageExchangeTests, receiveMessage) {
	istringstream in ("ShutDown\tignored\n");
	MessageExchange& msgs = MessageExchange::getMessageExchange(in, out);
	ServerMessage smsg = msgs.getMessageFromClient();
	EXPECT_EQ ("ShutDown", smsg.getKind());
}


TEST_F (MessageExchangeTests, sendMessage) {
	istringstream in ("Ack\n");
	MessageExchange& msgs = MessageExchange::getMessageExchange(in, out);
	msgs.sendMessageToClient(SourceCodeMessage("foo.cpp", "// foobar\nfoobaz\n"));
	EXPECT_TRUE (output.str().find("SourceCode") != string::npos);
	EXPECT_TRUE (output.str().find("foobar") != string::npos);
	EXPECT_TRUE (output.str().find("foobaz") != string::npos);
}

TEST_F (MessageExchangeTests, capturedOutput) {
	istringstream in ("Ack\n");
	MessageExchange& msgs = MessageExchange::getMessageExchange(in, out);
	out << "Hello World" << endl;
	EXPECT_TRUE (output.str().find("CapturedOutput") != string::npos);
	EXPECT_TRUE (output.str().find("Hello World") != string::npos);
}


TEST_F (MessageExchangeTests, promptForOutput) {
	istringstream in ("Ack\nInputSupplied\t42\nAck\n");
	MessageExchange& msgs = MessageExchange::getMessageExchange(in, out);
	string response = msgs.promptClientForInput("Tell Me", ".*");
	EXPECT_EQ ("42", response);
	EXPECT_TRUE (output.str().find("PromptForInput") != string::npos);
	EXPECT_TRUE (output.str().find("Tell Me") != string::npos);
}



}  // namespace



