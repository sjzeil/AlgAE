/*
 * snapshotMessage.cpp
 *
 *  Created on: Jul 11, 2012
 *      Author: zeil
 */


#include <algae/communications/snapshotMessage.h>
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


void SnapshotMessage::printXML (std::ostream& out) const
{
	JavaTag jt (out);
	{
		ObjectTag ot (out, communicationsPackage + ".SnapshotMessage");
		{
			PropertyTag pt (out, "menuItemCompleted");
			{
				BoolValue (out, menuItemCompleted);
			}
		}
		{
			PropertyTag pt (out, "snapshot");
			{
				snapshot.printXML (out);
			}
		}
	}
	jt.close();
}

ClientMessage* SnapshotMessage::clone() const
{
	return new SnapshotMessage(*this);
}

bool SnapshotMessage::operator== (const ClientMessage& other) const
		{
	if (typeid(other) != typeid(SnapshotMessage))
		return false;
	else
	{
		const SnapshotMessage& msg =  (const SnapshotMessage&)other;
		return menuItemCompleted == msg.menuItemCompleted && snapshot == msg.snapshot;
	}
		}

}

