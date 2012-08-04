/*
 * sourceCodeMessage.cpp
 *
 *  Created on: Jul 11, 2012
 *      Author: zeil
 */


#include <algae/communications/sourceCodeMessage.h>
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


void SourceCodeMessage::printXML (std::ostream& out) const
{
	JavaTag jt (out);
	{
		ObjectTag ot (out, communicationsPackage + ".SourceCodeMessage");
		{
			PropertyTag pt (out, "filePath");
			{
				StringValue (out, filePath);
			}
		}
		{
			PropertyTag pt (out, "sourceText");
			{
				StringValue (out, sourceText);
			}
		}
	}
	jt.close();
}

ClientMessage* SourceCodeMessage::clone() const
{
	return new SourceCodeMessage(*this);
}

bool SourceCodeMessage::operator== (const ClientMessage& other) const
		{
	if (typeid(other) != typeid(SourceCodeMessage))
		return false;
	else
	{
		const SourceCodeMessage& msg =  (const SourceCodeMessage&)other;
		return filePath == msg.filePath && sourceText == msg.sourceText;
	}
		}

}

