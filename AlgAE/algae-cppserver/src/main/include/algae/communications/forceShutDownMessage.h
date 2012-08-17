/*
 * ackMessage.h
 *
 *  Created on: Jun 10, 2012
 *      Author: zeil
 */

#ifndef FORCESHUTDOWNMESSAGE_H_
#define FORCESHUTDOWNMESSAGE_H_

#include <algae/communications/clientMessage.h>
#include <algae/communications/serverMessage.h>
#include <string>
#include <iostream>

namespace algae {


/**
 * Sent from the server to indicate that something has gone wrong and that the
 * animation is unable to continue.
 *
 *
 * @author zeil
 *
 */
class ForceShutDownMessage: public ClientMessage
{
	std::string explanation;

public:

	/**
	 * Construct a new client message
	 *
	 * @param messageKind type of message
	 */
	ForceShutDownMessage(std::string explain = std::string()) :
		ClientMessage ("ForceShutDown"), explanation(explain)
	{ }

	ForceShutDownMessage(const ServerMessage& protocolViolation);
	ForceShutDownMessage(const std::string& expectedKind, const ServerMessage& protocolViolation);

	const std::string& getExplanation() const {return explanation;}

	virtual void printXML (std::ostream& out) const;

	virtual ClientMessage* clone() const;


	virtual bool operator== (const ClientMessage& other) const;


};

}
#endif
