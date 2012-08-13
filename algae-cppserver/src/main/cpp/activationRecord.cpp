/**
 * activationRecord.cpp
 *
 *
 *  Created on: Aug 11, 2012
 *      Author: zeil
 */


#include <algae/memoryModel/activationRecord.h>
#include <algae/memoryModel/simpleReference.h>

using namespace std;

namespace algae {

/*class ActivationRecord
{
	std::string name;
	bool isOnTop;


	std::list<EntityIdentifier> parameters;
	std::list<EntityIdentifier> locals;
	EntityIdentifier* thisParam;


	friend class ActivationRecordRendering;

public:

	typedef std::list<EntityIdentifier>::const_iterator const_iterator;
	typedef std::list<EntityIdentifier>::iterator iterator;
*/

/**
 * Show a variable as a parameter of the current activation
 * Variables portrayed by this call are shown "in-line".
 *
 * @param label  the variable name (optional, can be "" or null)
 * @param param  the variable/value
 */
void ActivationRecord::param(std::string label, const Identifier& value)
{
	parameters.push_back(LabeledComponent(value, label));
}


/**
 * Show a variable as a parameter of the current activation.
 * Variables portrayed by this call are shown as labeled
 * pointers to the actual value.
 *
 * @param label  the variable name (optional, can be "" or null)
 * @param param  the variable/value
 * @return a reference to this breakpoint
 */
void ActivationRecord::refParam (std::string  label, const Identifier& value)
{
	param (label, Identifier(SimpleReference(Identifier)));
}


/**
 * Show a variable as a local variable of the current activation
 * Variables portrayed by this call are shown "in-line".
 *
 * @param label  the variable name (optional, can be "" or null)
 * @param param  the variable/value
 */
void ActivationRecord::var(std::string label, const Identifier& value);


/**
 * Show a variable as a local variable of the current activation.
 * Variables portrayed by this call are shown as labeled
 * pointers to the actual value.
 *
 * @param label  the variable name (optional, can be "" or null)
 * @param param  the variable/value
 * @return a reference to this breakpoint
 */
void ActivationRecord::refVar (std::string  label, const Identifier& value);


/**
 * Change the rendering of the indicated object to highlight it.
 * This stays in effect until the end of the current scope/activation.
 *
 */
void ActivationRecord::highlight (const Identifier& value);

void ActivationRecord::highlight (const Identifier& value, Color c);


/**
 * Reverse the effect of a prior highlight call
 *
 */
void ActivationRecord::unhighlight (const Identifier& value);


/**
 * Establish a rendering for a specific object. This rendering will only
 * remain in effect until the current activation/scope is exited.
 */
void ActivationRecord::render(const Identifier& object, const Renderer& newRendering);

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
void ActivationRecord::breakPoint (std::string description, const char* fileName, int lineNumber);







}


