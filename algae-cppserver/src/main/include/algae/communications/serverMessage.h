/*
 * serverMessage.h
 *
 *  Created on: Jul 14, 2012
 *      Author: zeil
 */

#ifndef SERVERMESSAGE_H_
#define SERVERMESSAGE_H_

#include <algae/communications/messageBase.h>
#include <string>
#include <iostream>


namespace algae {

/**
 * Messages sent to the server.
 *
 * Server messages have a fairly simple structure: a message kind and a details string.
 *
 * @author zeil
 *
 */
class ServerMessage: public MessageBase {
	std::string detail;
public:

	/**
	 * Construct a new client message
	 *
	 * @param messageKind type of message
	 */
	ServerMessage(std::string messageKind, std::string details = std::string()) :
		MessageBase (messageKind), detail(details)
	{ }

	const std::string& getKind() const {return kind;}

	const std::string& getDetail() const {return detail;}

	virtual void print (std::ostream& out) const
	{
		out << kind << ":\t" << detail; 
	}



	bool operator== (const ServerMessage& other) const
	{
		return kind == other.kind && detail == other.detail;
	}

};

inline
bool operator!= (const ServerMessage& left, const ServerMessage& right)
{
	return !(left == right);
}


inline
std::ostream& operator<< (std::ostream& out, const ServerMessage& msg)
{
	msg.print(out);
	return out;
}


}
#endif /* SERVERMESSAGE_H_ */
