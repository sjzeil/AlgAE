/**
 * activation.cpp
 *
 *
 *  Created on: July 25, 2012
 *      Author: zeil
 */


#include <string>
#include <typeinfo>

#include <algae/activation.h>
#include <algae/animation.h>

#include <algae/memoryModel/activationRecord.h>
#include <algae/memoryModel/activationStack.h>
#include <algae/memoryModel/memoryModel.h>


/**
 * An Activation records information about a function call being animated.
 *
 * This is the fundamental interface between algorithms being animated
 * and the AlgAE animation system.
 *
 * @author zeil
 *
 */

using namespace std;


namespace algae {

/*
private:
	ActivationRecord* arecord;
*/

/**
 * Create a new activation for a non-member (no "this") function
 */
Activation::Activation (std::string functionName)
{
	Animation* anim = Animation::algae();
	MemoryModel& mem = anim->getMemoryModel();
	ActivationStack& stack = mem.getActivationStack();
	stack.push(functionName);
	arecord = &(stack.top());
}




/**
 * Destruction of the Activation object signals to the animator that we have returned from
 * a function call being animated.
 */
Activation::~Activation()
{
	Animation* anim = Animation::algae();
	MemoryModel& mem = anim->getMemoryModel();
	ActivationStack& stack = mem.getActivationStack();
	stack.pop();
}




/**
 * Take a snapshot of the current program state and send it to the animator.
 *
 * This function is not normally called directly, but is invoked via the breakHere macro.
 *
 * @param description short message to be displayed by the animator explaining the current circumstances
 *                       the break
 * @param fileName  name of the file in which the breakpoint occurs.
 * @param lineNum   line number in that file where the breakpoint occurs
 */
Activation& Activation::breakPoint (std::string description, const char* fileName, int lineNumber)
{
	Animation* anim = Animation::algae();
	anim->breakPoint(description, fileName, lineNumber);
	return *this;
}


}

