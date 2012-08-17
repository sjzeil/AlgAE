/**
 * animation.h
 *
 *
 *  Created on: Jun 14, 2012
 *      Author: zeil
 */

#ifndef ANIMATION_H_
#define ANIMATION_H_

#include <iostream>
#include <map>
#include <set>
#include <string>

#include <algae/communications/clientMessage.h>
#include <algae/communications/messageExchange.h>
#include <algae/communications/serverMessage.h>
#include <algae/memoryModel/memoryModel.h>



namespace algae {


class ActivationRecord;


/**
 * This is the base class from which all C++ animations are derived.
 *
 * @author zeil
 *
 */

class Animation
{
private:
	static Animation* theAnimation; // singleton
	MemoryModel memoryModel;
	std::set<std::string> sourceCodeAlreadySent;
	bool breakpointsAreActive;
	Snapshot* currentSnapshot;


public:
	Animation ();

	virtual ~Animation() {}

	/**
	 *  Supply a message to appear in the Help..About dialog.
	 *  Typically, this indicates the origin of the source code
	 *  being animated and the name of the person who prepared the
	 *  animation.
	 **/
	virtual std::string about() = 0;


	/**
	 * Override this to call register (below) to set up the menu items that will
	 * be displayed in the Algorithms menu and optionally to call registerStartingAction
	 * to set up code to be animated immediately upon launch.
	 */
	virtual void buildMenu() = 0;



	typedef void (*MenuFunction)();

	/**
	 *  Called from buildMenu to register an initial action to be
	 *  run at the start of the animation, before any selections
	 *  from the menu.
	 */
	void registerStartingAction (MenuFunction action);

	/**
	 *  Called from buildMenu to add an item to the Algorithm menu.
	 */
	void registerAction(std::string menuItem, MenuFunction action);




	/**
	 * Each animation has a unique activation stack.
	 * @return the activation stack for this animation
	 */
	static MemoryModel& getMemoryModel() {
		return algae()->memoryModel;
	}


	/**
	 * Each animation has a unique server.
	 * @return the server for this animation
	 */
	//	public Server getServer ()
	//	{
	//		return server;
	//	}


	/**
	 * Animated code must be able to access the relevant Animation instance
	 * even though such code was written independently of the animation system.
	 * This function returns that animation instance, under the assumption that the
	 * server (which launches the animated code) will have registered its thread
	 * for that purpose.
	 *
	 * @return the animation associated with a thread
	 */
	static Animation* algae()
	{
		return theAnimation;
	}







	/**
	 * Show a variable as a global value in all displays.
	 * Variables portrayed by this call are shown "in-line".
	 *
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 */
	template <typename Object>
	static void globalVar (std::string label, Object param)
	{
		theAnimation->memoryModel.globalVar(label, param);
	}



	/**
	 * Show a variable as a global value in all displays.
	 * Variables portrayed by this call are shown as labeled
	 * pointers to the actual value.
	 *
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 * @return a reference to this breakpoint
	 */
	template <typename Object>
	static void globalRefVar (std::string label, Object param)
	{
		theAnimation->memoryModel.globalRefVar(label, param);
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
	static std::string promptForInput(std::string prompt, std::string requiredPattern = ".*");
	static void promptForInput(std::string prompt, int& response);


	void setBreakpointsEnabled (bool enabled)
	{
		theAnimation->breakpointsAreActive = enabled;
	}


	/**
	 * Begin running this animation
	 */
	void run();


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
	void breakPoint (std::string description, const char* fileName, int lineNumber);


	/**
	 * For testing purposes only!!
	 */
	static void reset() {	delete theAnimation; theAnimation = 0;}



private:
	MenuFunction startingAction;
	std::map<std::string, MenuFunction> menuItems;
	std::vector<std::string> itemList;

	void composeMenu();
	void readyForMenuItem();
	void sendSourceCode (std::string filePath);


};


}

#endif
