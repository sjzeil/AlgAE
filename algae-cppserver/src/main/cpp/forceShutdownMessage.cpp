/*
 * forceShutdownMessage.cpp
 *
 *  Created on: July 11, 2012
 *      Author: zeil
 */


#include <algae/communications/forceShutDownMessage.h>
#include <algae/communications/xmlOutput.h>
#include <string>
#include <iostream>
#include <typeinfo>

using namespace std;

namespace algae {

/**
 * Sent from the server to indicate that something has gone wrong and that the
 * animation is unable to continue.
 *
 * @author zeil
 *
 */


ForceShutDownMessage::ForceShutDownMessage(const ServerMessage& protocolViolation)
 : ClientMessage ("ForceShutDown"), explanation("Protocol violation: unexpected "
		 	+ protocolViolation.getKind() + ": " + protocolViolation.getDetail())
{
}


ForceShutDownMessage::ForceShutDownMessage(const string& expectedKind, const ServerMessage& protocolViolation)
 : ClientMessage ("ForceShutDown"), explanation("Protocol violation: expected " + expectedKind
		 + ", received "
		 + protocolViolation.getKind() + ": " + protocolViolation.getDetail())
{
}



void ForceShutDownMessage::printXML (std::ostream& out) const
{
	JavaTag jt (out);
	{
		ObjectTag ot (out, communicationsPackage + ".ForceShutDownMessage");
		{
			PropertyTag pt (out, "explanation");
			{
				StringValue (out, explanation);
			}
		}
	}
	jt.close();
}

ClientMessage* ForceShutDownMessage::clone() const
{
	return new ForceShutDownMessage(*this);
}


bool ForceShutDownMessage::operator== (const ClientMessage& other) const
{
	if (typeid(other) != typeid(ForceShutDownMessage))
		return false;
	else
	{
		const ForceShutDownMessage& msg =  (const ForceShutDownMessage&)other;
		return explanation == msg.explanation;
	}
}

}
