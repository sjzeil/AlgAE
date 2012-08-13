/**
 * activationRecord.h
 *
 *
 *  Created on: Jul 14, 2012
 *      Author: zeil
 */

#ifndef ACTIVATIONRECORD_H_
#define ACTIVATIONRECORD_H_

#include <string>
#include <iostream>
#include <list>

#include <algae/snapshot/entityIdentifier.h>

namespace algae {

struct LabeledComponent {
	Identifier oid;
	std::string label;

	LabeledComponent (Identifier obj, std::string aLabel=std::string())
	: oid(obj), label(aLabel)
	{}
};

class ActivationRecord
{
	std::string name;
	bool isOnTop;



	std::list<LabeledComponent> parameters;
	std::list<LabeledComponent> locals;
	EntityIdentifier* thisParam;


	friend class ActivationRecordRendering;

public:

	typedef std::list<LabeledComponent>::const_iterator const_iterator;
	typedef std::list<LabeledComponent>::iterator iterator;


	/**
	 * Show a variable as a parameter of the current activation
	 * Variables portrayed by this call are shown "in-line".
	 *
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 */
	void param(std::string label, const Identifier& value);


	/**
	 * Show a variable as a parameter of the current activation.
	 * Variables portrayed by this call are shown as labeled
	 * pointers to the actual value.
	 *
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 * @return a reference to this breakpoint
	 */
	void refParam (std::string  label, const Identifier& value);


	/**
	 * Show a variable as a local variable of the current activation
	 * Variables portrayed by this call are shown "in-line".
	 *
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 */
	void var(std::string label, const Identifier& value);


	/**
	 * Show a variable as a local variable of the current activation.
	 * Variables portrayed by this call are shown as labeled
	 * pointers to the actual value.
	 *
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 * @return a reference to this breakpoint
	 */
	void refVar (std::string  label, const Identifier& value);


	/**
	 * Change the rendering of the indicated object to highlight it.
	 * This stays in effect until the end of the current scope/activation.
	 *
	 */
	void highlight (const Identifier& value);

	void highlight (const Identifier& value, Color c);


	/**
	 * Reverse the effect of a prior highlight call
	 *
	 */
	void unhighlight (const Identifier& value);


	/**
	 * Establish a rendering for a specific object. This rendering will only
	 * remain in effect until the current activation/scope is exited.
	 */
	void render(const Identifier& object, const Renderer& newRendering);

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
	void breakPoint (std::string description, const char* fileName, int lineNumber);






	const_iterator beginParams() const;
	iterator beginParams();

	const_iterator endParams() const;
	iterator endParams();

	const_iterator beginLocals() const;
	iterator beginLocals();

	const_iterator endLocals() const;
	iterator endLocals();



};

}


#endif
