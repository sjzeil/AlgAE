/*
 * pullMessage.h
 *
 *  Created on: Jun 10, 2012
 *      Author: zeil
 */

#ifndef PULLMESSAGE_H_
#define PULLMESSAGE_H_

#include <algae/communications/clientMessage.h>
#include <string>
#include <iostream>

namespace algae {

/**
 * A generic message, used by a remote/process server to indicate
 * that it is prepared to receive a server message
 *
 *
 * @author zeil
 *
 */
class PullMessage: public ClientMessage
{

public:

	/**
	 * Construct a new client message
	 *
	 * @param messageKind type of message
	 */
	PullMessage() :
		ClientMessage ("Pull")
	{ }


	virtual void printXML (std::ostream& out) const;

	virtual ClientMessage* clone() const;


	virtual bool operator== (const ClientMessage& other) const;


};

}
#endif /* CLIENTMESSAGE_H_ */
