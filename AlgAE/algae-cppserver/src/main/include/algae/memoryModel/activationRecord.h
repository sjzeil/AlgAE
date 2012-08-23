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

namespace algae {

struct LabeledComponent {
	Identifier oid;
	std::string label;

	LabeledComponent (Identifier obj, std::string aLabel=std::string())
	: oid(obj), label(aLabel)
	{}
};

class ActivationRecord;

struct ActivationParams {
		std::string name;
		ActivationStack* onStack;
		int depth;
		std::list<LabeledComponent> parameters;
		EntityIdentifier* thisParam;
};

struct ActivationLocals {
	std::list<LabeledComponent> locals;
};

class ActivationStack;
class ObjectRenderer;
class SimpleReference;

class ActivationRecord
{

	ActivationParams params;

	ActivationLocals locals;

	std::list<ObjectRenderer*> localRenderings;
	std::list<SimpleReference*> artificialReferences;

	friend class Scope;
	friend class ActivationRecordRenderer;

public:

	ActivationRecord (std::string functionName, ActivationStack* stack)
	 : name(functionName), onStack(stack), thisParam(0)
	{}

	~ActivationRecord();

	std::string getName() const {return name;}

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



	const_iterator beginParams() const {return parameters.begin();}
	iterator beginParams() {return parameters.begin();}

	const_iterator endParams() const {return parameters.end();}
	iterator endParams() {return parameters.end();}

	const_iterator beginLocals() const {return locals.begin();}
	iterator beginLocals() {return locals.begin();}

	const_iterator endLocals() const {return locals.end();}
	iterator endLocals() {return locals.end();}

	const_render_iterator beginRenderings() const {return localRenderings.begin();}
	render_iterator beginRenderings() {return localRenderings.begin();}

	const_render_iterator endRenderings() const {return localRenderings.end();}
	render_iterator endRenderings() {return localRenderings.end();}


};


/**
 * Specialization for rendering of types that specialize CanBeRendered
 */
template <typename T>
class TypeRendering<sameClassAs(T, ActivationRecord)> {
public:
	const TypeRenderer* getRenderer (const ActivationRecord& anObject) const
	{
		return new ActivationRecordRenderer(anObject);
	}
};

/**
 * Specialization for rendering of types that specialize CanBeRendered
 */
template <typename T>
class TypeRendering<sameClassAs(T, ActivationParams)> {
public:
	const TypeRenderer* getRenderer (const ActivationParams& anObject) const
	{
		return new ActivationParamsRenderer(anObject);
	}
};

/**
 * Specialization for rendering of types that specialize CanBeRendered
 */
template <typename T>
class TypeRendering<sameClassAs(T, ActivationLocals)> {
public:
	const TypeRenderer* getRenderer (const ActivationLocals& anObject) const
	{
		return new ActivationLocalsRenderer(anObject);
	}
};

}


#endif
