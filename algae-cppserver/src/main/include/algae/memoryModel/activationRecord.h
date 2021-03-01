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
#include <algae/snapshot/color.h>
#include <algae/memoryModel/activationRecordRenderer.h>
#include <algae/rendering/typeRendering.h>
#include <algae/rendering/objectRenderer.h>

namespace algae {

struct LabeledComponent {
	Identifier oid;
	std::string label;

	LabeledComponent (Identifier obj, std::string aLabel=std::string())
	: oid(obj), label(aLabel)
	{}
};

class ActivationRecordImpl;
class ActivationStack;


class ActivationRecord
{
protected:
	ActivationRecordImpl* impl;

	friend class Scope;
	friend class ActivationRecordRenderer;

public:

	ActivationRecord (std::string functionName, ActivationStack* stack);

	~ActivationRecord();

	/**
	 * Returns the name of the function that is active
	 */
	std::string getName() const;

	typedef std::list<LabeledComponent>::const_iterator const_iterator;
	typedef std::list<LabeledComponent>::iterator iterator;

	typedef std::list<ObjectRenderer*>::const_iterator const_render_iterator;
	typedef std::list<ObjectRenderer*>::iterator render_iterator;

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
	 * Show a variable as the "this" parameter of the current activation.
	 * Variables portrayed by this call are shown as labeled ("this")
	 * pointers to the actual value.
	 *
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 * @return a reference to this breakpoint
	 */
	void thisParam (const Identifier& oid);

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
	void render(const ObjectRenderer& newRendering);


	/**
	 * The height of an AR is its position on the activation stack.
	 * The first record pushed has height 1.
	 */
	int getHeight() const;
	void setHeight (int h);


	const_iterator beginParams() const;
	iterator beginParams();

	const_iterator endParams() const;
	iterator endParams();

	const_iterator beginLocals() const;
	iterator beginLocals();

	const_iterator endLocals() const;
	iterator endLocals();

	const_render_iterator beginRenderings() const;
	render_iterator beginRenderings();

	const_render_iterator endRenderings() const;
	render_iterator endRenderings();

};


}


#endif
