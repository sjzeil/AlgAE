/*
 * ackMessage.cpp
 *
 *  Created on: Jun 10, 2012
 *      Author: zeil
 */


#include <algae/communications/ackMessage.h>
#include <algae/communications/xmlOutput.h>
#include <string>
#include <iostream>
#include <typeinfo>

using namespace std;
using namespace algae;

/**
 * Generic acknowledgment message from server to client.
 *
 *
 * @author zeil
 *
 */

AckMessage::AckMessage()
 : ClientMessage("Ack")
{

}

void AckMessage::printXML (std::ostream& out) const
{
	JavaTag jt (out);
	ObjectTag ot (out, communicationsPackage + ".AckMessage");
	ot.close();
	jt.close();
}

ClientMessage* AckMessage::clone() const
{
	return new AckMessage(*this);
}


bool AckMessage::operator== (const ClientMessage& other) const
{
	if (typeid(other) != typeid(AckMessage))
		return false;
	else
		return true;
}
