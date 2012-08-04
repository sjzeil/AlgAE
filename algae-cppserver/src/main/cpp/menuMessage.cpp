/*
 * menuMessage.cpp
 *
 *  Created on: Jul 11, 2012
 *      Author: zeil
 */


#include <algae/communications/menuMessage.h>
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


void MenuMessage::printXML (std::ostream& out) const
{
	JavaTag jt (out);
	{
		ObjectTag ot (out, communicationsPackage + ".MenuMessage");
		{
			PropertyTag pt (out, "about");
			{
				StringValue (out, about);
			}
		}
		{
			PropertyTag pt (out, "menuItems");
			{
				ArrayTag at (out, "java.lang.String", menuItems.size());
				for (vector<string>::size_type i = 0; i < menuItems.size(); ++i)
				{
					IndexTag vt(out, i);
					StringValue sv (out, menuItems[i]);
				}
				at.close();
			}
		}
	}
	jt.close();
}

ClientMessage* MenuMessage::clone() const
{
	return new MenuMessage(*this);
}

bool MenuMessage::operator== (const ClientMessage& other) const
{
	if (typeid(other) != typeid(MenuMessage))
		return false;
	else
	{
		const MenuMessage& msg =  (const MenuMessage&)other;
		return about == msg.about && menuItems == msg.menuItems;
	}
}

}

