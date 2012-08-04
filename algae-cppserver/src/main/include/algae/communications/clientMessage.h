/*
 * clientMessage.h
 *
 *  Created on: Jun 10, 2012
 *      Author: zeil
 */

#ifndef CLIENTMESSAGE_H_
#define CLIENTMESSAGE_H_

#include <algae/communications/messageBase.h>
#include <string>
#include <iostream>


namespace algae {

/**
 * Messages sent to the client.
 *
 * Client messages have a highly variable and sometimes complex structure.
 *
 * @author zeil
 *
 */
class ClientMessage: public MessageBase {

public:

	/**
	 * Construct a new client message
	 *
	 * @param messageKind type of message
	 */
	ClientMessage(std::string messageKind) :
		MessageBase (messageKind)
	{ }


	virtual void printXML (std::ostream& out) const = 0;

	virtual void print (std::ostream& out) const {printXML(out);}


	virtual ClientMessage* clone() const = 0;


	virtual bool operator== (const ClientMessage& other) const = 0;

	static const std::string communicationsPackage;
	static const std::string snapshotPackage;

protected:
	static const std::string javaXMLTag;

};

inline
bool operator!= (const ClientMessage& left, const ClientMessage& right)
{
	return !(left == right);
}

}
#endif /* CLIENTMESSAGE_H_ */
