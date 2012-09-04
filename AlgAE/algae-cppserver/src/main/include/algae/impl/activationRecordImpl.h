/**
 * activationRecordImpl.h
 *
 *
 *  Created on: Aug 25, 2012
 *      Author: zeil
 */

#ifndef ACTIVATIONRECORDIMPL_H_
#define ACTIVATIONRECORDIMPL_H_

#include <string>
#include <iostream>
#include <list>

#include <algae/snapshot/entityIdentifier.h>
#include <algae/snapshot/color.h>
#include <algae/memoryModel/activationRecord.h>
#include <algae/memoryModel/activationRecordRenderer.h>
#include <algae/rendering/typeRendering.h>

namespace algae {


class ActivationStack;
class ObjectRenderer;
class SimpleReference;


struct ActivationParams {
		std::string name;
		ActivationStack* onStack;
		int height;
		std::list<LabeledComponent> parameters;
		Identifier thisParam;

		ActivationParams (std::string nm, ActivationStack* stck)
		 : name(nm), onStack(stck), height(-1), thisParam(Identifier::nullID()) {}
};

struct ActivationLocals {
	std::list<LabeledComponent> locals;
};

class ActivationRecordImpl
{
public:
	ActivationParams params;
	ActivationLocals locals;

	std::list<ObjectRenderer*> localRenderings;
	std::list<SimpleReference*> artificialReferences;

public:

	ActivationRecordImpl (std::string functionName, ActivationStack* stack)
	 : params(functionName, stack)
	{}

	~ActivationRecordImpl();

	std::string getName() const {return params.name;}

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
	void thisParam (const Identifier& oid) {params.thisParam = oid;}

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
	int getHeight() const {return params.height;}
	void setHeight (int h) {params.height = h;}



	const_iterator beginParams() const {return params.parameters.begin();}
	iterator beginParams() {return params.parameters.begin();}

	const_iterator endParams() const {return params.parameters.end();}
	iterator endParams() {return params.parameters.end();}

	const_iterator beginLocals() const {return locals.locals.begin();}
	iterator beginLocals() {return locals.locals.begin();}

	const_iterator endLocals() const {return locals.locals.end();}
	iterator endLocals() {return locals.locals.end();}

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
