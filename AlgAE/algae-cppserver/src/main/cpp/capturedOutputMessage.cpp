/*
 * capturedOutputMessage.cpp
 *
 *  Created on: July 8, 2012
 *      Author: zeil
 */


#include <algae/communications/capturedOutputMessage.h>
#include <algae/communications/xmlOutput.h>
#include <string>
#include <iostream>
#include <typeinfo>

using namespace std;

namespace algae {

/**
 * Generic acknowledgment message from server to client.
 *
 *
 * @author zeil
 *
 */


void CapturedOutputMessage::printXML (std::ostream& out) const
{
	JavaTag jt (out);
	{
		ObjectTag ot (out, communicationsPackage + ".CapturedOutputMessage");
		{
			PropertyTag pt (out, "output");
			{
				StringValue (out, output);
			}
		}
	}
	jt.close();
}

ClientMessage* CapturedOutputMessage::clone() const
{
	return new CapturedOutputMessage(*this);
}


bool CapturedOutputMessage::operator== (const ClientMessage& other) const
{
	if (typeid(other) != typeid(CapturedOutputMessage))
		return false;
	else
	{
		const CapturedOutputMessage& msg =  (const CapturedOutputMessage&)other;
		return output == msg.output;
	}
}

}
