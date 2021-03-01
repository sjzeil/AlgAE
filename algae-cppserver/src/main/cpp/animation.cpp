/**
 * animation.cpp
 *
 *
 *  Created on: Jul 14, 2012
 *      Author: zeil
 */



#include <fstream>
#include <iostream>
#include <set>
#include <string>
#include <sstream>

#include <algae/animation.h>
#include <algae/communications/messageExchange.h>
#include <algae/communications/serverMessage.h>
#include <algae/communications/forceShutDownMessage.h>
#include <algae/communications/menuMessage.h>
#include <algae/communications/snapshotMessage.h>
#include <algae/communications/sourceCodeMessage.h>

#include <algae/snapshot/snapshot.h>
#include <algae/snapshot/snapshotDiff.h>

using namespace std;

namespace algae {



/**
 * This is the base class from which all C++ animations are derived.
 *
 * @author zeil
 *
 */

Animation* Animation::theAnimation = 0; // singleton

Animation::Animation ()
: memoryModel(*this), breakpointsAreActive(true), startingAction(0)
{
	currentSnapshot = new Snapshot();
	theAnimation = this;
}




/**
 *  Called from buildMenu to register an initial action to be
 *  run at the start of the animation, before any selections
 *  from the menu.
 */
void Animation::registerStartingAction (MenuFunction action)
{
	startingAction = action;
}

/**
 *  Called from buildMenu to add an item to the Algorithm menu.
 */
void Animation::registerAction(std::string menuItem, MenuFunction action)
{
	menuItems[menuItem] = action;
	itemList.push_back(menuItem);
}






/**
 * Pops up a dialog box prompting for an input, pausing the
 * animation until a satisfactory input value is obtained from the
 * human operator.
 *
 * @param prompt  Text of the prompt message to be displayed
 * @param requiredPattern regular expression describing an acceptable input value
 * @return a human-entered string matching the requiredPattern
 */
std::string Animation::promptForInput(std::string prompt, std::string requiredPattern)
{
	return MessageExchange::getMessageExchange().promptClientForInput(prompt, requiredPattern);
}

void Animation::promptForInput(std::string prompt, int& response)
{
	string responseStr = MessageExchange::getMessageExchange().promptClientForInput(prompt, "[0-9][0-9]*");
	istringstream in (responseStr);
	in >> response;
}




/**
 * Begin running this animation
 */
void Animation::run()
{
	buildMenu();
	MessageExchange& msgs = MessageExchange::getMessageExchange();
	ServerMessage smsg = msgs.getMessageFromClient();
	if (smsg.getKind() != "Start")
	{
		msgs.sendMessageToClient (ForceShutDownMessage("Start", smsg));
		return;
	}
	buildMenu();
	composeMenu();
	if (startingAction != 0)
	{
		startingAction();
		readyForMenuItem();
	}
	while (true) {
		smsg = msgs.getMessageFromClient();
		if (smsg.getKind() == "MenuItemSelected")
		{
			string item = smsg.getDetail();
			if (menuItems.count(item) > 0)
			{
				MenuFunction action = menuItems[item];
				action();
				readyForMenuItem();
			}

		}
		else if (smsg.getKind() == "GetSourceCode")
		{
			sendSourceCode (smsg.getDetail());
		}
		else if (smsg.getKind() == "Ack")
		{
			// do nothing
		}
		else if (smsg.getKind() == "Pull")
		{
			// do nothing
		}
		else if (smsg.getKind() == "ShutDown")
		{
			return;
		}
		else
		{
			msgs.sendMessageToClient (ForceShutDownMessage("MenuItemSelected or GetSourceCode or ShoutDown", smsg));
			return;
		}
	}
}



void Animation::composeMenu()
{
	MenuMessage mmsg (about(), itemList.begin(), itemList.end());
	MessageExchange& msgs = MessageExchange::getMessageExchange();
	msgs.sendMessageToClient(mmsg);
}


void Animation::readyForMenuItem()
{
	Snapshot* previous = currentSnapshot;
	currentSnapshot = memoryModel.renderInto("Choose an algorithm", SourceLocation());
	SnapshotDiff diff (*previous, *currentSnapshot);
	SnapshotMessage smsg (diff, true);
	MessageExchange& msgs = MessageExchange::getMessageExchange();
	msgs.sendMessageToClient(smsg);
	delete previous;
}

void Animation::sendSourceCode (std::string filePath)
{
	ifstream codeIn (filePath.c_str());
	string text;
	string line;
	while (getline(codeIn, line))
	{
		text += line;
		text += "\n";
	}
	SourceCodeMessage scMsg (filePath, text);
	MessageExchange& msgs = MessageExchange::getMessageExchange();
	msgs.sendMessageToClient(scMsg);
}

/**
 * Take a snapshot of the current program state and send it to the animator.
 *
 * This function is not normally called directly, but is invoked via the Activation breakHere macro.
 *
 * @param description short message to be displayed by the animator explaining the current circumstances
 *                       the break
 * @param fileName  name of the file in which the breakpoint occurs.
 * @param lineNum   line number in that file where the breakpoint occurs
 */
void Animation::breakPoint (std::string description, const char* fileName, int lineNumber)
{
	if (breakpointsAreActive)
	{
		Snapshot* previous = currentSnapshot;
		SourceLocation loc (fileName, lineNumber);
		currentSnapshot = memoryModel.renderInto(description, loc);
		SnapshotDiff diff (*previous, *currentSnapshot);
		SnapshotMessage smsg (diff, false);
		MessageExchange& msgs = MessageExchange::getMessageExchange();
		msgs.sendMessageToClient(smsg);
		delete previous;
	}
}


}

