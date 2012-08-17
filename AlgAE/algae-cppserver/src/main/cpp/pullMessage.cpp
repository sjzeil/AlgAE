/*
 * pullMessage.cpp
 *
 *  Created on: Jun 10, 2012
 *      Author: zeil
 */


#include <algae/communications/pullMessage.h>
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



void PullMessage::printXML (std::ostream& out) const
{
	JavaTag jt (out);
	ObjectTag ot (out, communicationsPackage + ".PullMessage");
	ot.close();
	jt.close();
}

ClientMessage* PullMessage::clone() const
{
	return new PullMessage(*this);
}


bool PullMessage::operator== (const ClientMessage& other) const
{
	if (typeid(other) != typeid(PullMessage))
		return false;
	else
		return true;
}
