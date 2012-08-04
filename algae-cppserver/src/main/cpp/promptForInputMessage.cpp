/*
 * menuMessage.cpp
 *
 *  Created on: Jul 11, 2012
 *      Author: zeil
 */


#include <algae/communications/promptForInputMessage.h>
#include <algae/communications/xmlOutput.h>
#include <string>
#include <string>
#include <iostream>
#include <typeinfo>
#include <vector>

using namespace std;

namespace algae {

/**
 * Sent as an animation is starting, this message tells the client
 * what text should appear in the Help->About menu and what menu items
 * should be placed in the Algorithm menu listing functions that users
 * can select to see animations of.
 *
 * @author zeil
 *
 */


void PromptForInputMessage::printXML (std::ostream& out) const
{
	JavaTag jt (out);
	{
		ObjectTag ot (out, communicationsPackage + ".PromptForInputMessage");
		{
			PropertyTag pt (out, "prompt");
			{
				StringValue (out, prompt);
			}
		}
		{
			PropertyTag pt (out, "requiredPattern");
			{
				StringValue (out, requiredPattern);
			}
		}
	}
	jt.close();
}

ClientMessage* PromptForInputMessage::clone() const
{
	return new PromptForInputMessage(*this);
}

bool PromptForInputMessage::operator== (const ClientMessage& other) const
		{
	if (typeid(other) != typeid(PromptForInputMessage))
		return false;
	else
	{
		const PromptForInputMessage& msg =  (const PromptForInputMessage&)other;
		return prompt == msg.prompt && requiredPattern == msg.requiredPattern;
	}
		}

}

