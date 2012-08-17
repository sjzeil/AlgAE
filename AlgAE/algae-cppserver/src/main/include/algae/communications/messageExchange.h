#ifndef MESSAGEEXCHANGE_H
#define MESSAGEEXCHANGE_H

#include <iostream>
#include <algae/communications/serverMessage.h>

namespace algae {


class ClientMessage;


/**
 * Base class for operations involved in exchanging messages between the client and server
 */
class MessageExchange {
	MessageExchange(
			std::istream* actualInputStream,
			std::ostream* actualOutputStream);

	std::istream* msgsIn;
	std::ostream* msgsOut;

	std::istream* actualIn;
	std::ostream* actualOut;

	static MessageExchange* singleton;


public:
	/**
	 * Provides access to the message exchange. As a side effect, the first call
	 * to this function redirects the actualInputStream and actualOutputStream so that
	 * subsequent I/O via those streams is packed into client/server messages.
	 */
	static MessageExchange& getMessageExchange(
			std::istream& actualInputStream = std::cin,
			std::ostream& actualOutputStream = std::cout);

	/**
	 * Sends a message to the client, waiting until the message
	 * is acknowledged.
	 */
	void sendMessageToClient (const ClientMessage&);

	/**
	 * Obtains a message from the client, waiting until such a message is available.
	 */
	const ServerMessage getMessageFromClient ();

	/**
	 * Sends a prompt (and required pattern) to the client, then waits for a corresponding
	 * InputSupplied message from the client. Any Pull messages received in the interim are
	 * acknowledged. Any other message is considered a protocol violation.
	 */
	std::string promptClientForInput (std::string prompt, std::string requiredPattern);


	/**
	 * Intended for testing purposes: resets the exchange by destroying any existing
	 * singleton. Next call to getMessageExchange() will redirect to new streams.
	 */
	static void reset();

private:
	/**
	 * Writes a message to the client, encoded as XML
	 */
	void writeMessage (const ClientMessage& cmsg);
};



}

#endif
