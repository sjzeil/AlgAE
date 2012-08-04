/*
 * ackMessage.h
 *
 *  Created on: Jun 10, 2012
 *      Author: zeil
 */

#ifndef ACKMESSAGE_H_
#define ACKMESSAGE_H_

#include <algae/communications/clientMessage.h>
#include <string>
#include <iostream>

namespace algae {

/**
 * Generic acknowledgment message from server to client.
 *
 *
 * @author zeil
 *
 */
class AckMessage: public ClientMessage
{

public:

	/**
	 * Construct a new client message
	 *
	 * @param messageKind type of message
	 */
	AckMessage();


	virtual void printXML (std::ostream& out) const;

	virtual ClientMessage* clone() const;


	virtual bool operator== (const ClientMessage& other) const;


};

}
#endif /* CLIENTMESSAGE_H_ */
