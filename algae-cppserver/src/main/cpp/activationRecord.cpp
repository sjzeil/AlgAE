/**
 * activationRecord.cpp
 *
 *
 *  Created on: Aug 11, 2012
 *      Author: zeil
 */


#include <algae/memoryModel/activationRecord.h>
#include <algae/impl/activationRecordImpl.h>

using namespace std;

namespace algae {

ActivationRecord::ActivationRecord (std::string functionName, ActivationStack* stack)
 : impl(new ActivationRecordImpl(functionName, stack))
{}


ActivationRecord::~ActivationRecord()
{
	delete impl;
}

/**
 * Returns the name of the function that is active
 */
std::string ActivationRecord::getName() const {return impl->getName();}


/**
 * Show a variable as the "this" parameter of the current activation.
 * Variables portrayed by this call are shown as labeled ("this")
 * pointers to the actual value.
 *
 * @param label  the variable name (optional, can be "" or null)
 * @param param  the variable/value
 * @return a reference to this breakpoint
 */
void ActivationRecord::thisParam (const Identifier& oid)
{
	impl->thisParam(oid);
}


/**
 * Show a variable as a parameter of the current activation
 * Variables portrayed by this call are shown "in-line".
 *
 * @param label  the variable name (optional, can be "" or null)
 * @param param  the variable/value
 */
void ActivationRecord::param(std::string label, const Identifier& value)
{
	impl->param(label, value);
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
	impl->refParam(label, value);
}


/**
 * Show a variable as a local variable of the current activation
 * Variables portrayed by this call are shown "in-line".
 *
 * @param label  the variable name (optional, can be "" or null)
 * @param param  the variable/value
 */
void ActivationRecord::var(std::string label, const Identifier& value)
{
	impl->var(label, value);
}


/**
 * Show a variable as a local variable of the current activation.
 * Variables portrayed by this call are shown as labeled
 * pointers to the actual value.
 *
 * @param label  the variable name (optional, can be "" or null)
 * @param param  the variable/value
 * @return a reference to this breakpoint
 */
void ActivationRecord::refVar (std::string  label, const Identifier& value)
{
	impl->refVar(label, value);
}


/**
 * Change the rendering of the indicated object to highlight it.
 * This stays in effect until the end of the current scope/activation.
 *
 */
void ActivationRecord::highlight (const Identifier& value)
{
	impl->highlight(value);
}

void ActivationRecord::highlight (const Identifier& value, Color c)
{
	impl->highlight(value, c);
}


/**
 * Reverse the effect of a prior highlight call
 *
 */
void ActivationRecord::unhighlight (const Identifier& value)
{
	impl->unhighlight(value);
}


/**
 * Establish a rendering for a specific object. This rendering will only
 * remain in effect until the current activation/scope is exited.
 */
void ActivationRecord::render(const ObjectRenderer& newRendering)
{
	impl->render(newRendering);
}


/**
	 * The height of an AR is its position on the activation stack.
	 * The first record pushed has height 1.
	 */
int ActivationRecord::getHeight() const
{
	return impl->getHeight();
}

void ActivationRecord::setHeight (int h)
{
	impl->setHeight(h);
}


ActivationRecord::const_iterator ActivationRecord::beginParams() const {return impl->beginParams();}
ActivationRecord::iterator ActivationRecord::beginParams() {return impl->beginParams();}

ActivationRecord::const_iterator ActivationRecord::endParams() const {return impl->endParams();}
ActivationRecord::iterator ActivationRecord::endParams() {return impl->endParams();}

ActivationRecord::const_iterator ActivationRecord::beginLocals() const {return impl->beginLocals();}
ActivationRecord::iterator ActivationRecord::beginLocals() {return impl->beginLocals();}

ActivationRecord::const_iterator ActivationRecord::endLocals() const {return impl->endLocals();}
ActivationRecord::iterator ActivationRecord::endLocals() {return impl->endLocals();}

ActivationRecord::const_render_iterator ActivationRecord::beginRenderings() const {return impl->beginRenderings();}
ActivationRecord::render_iterator ActivationRecord::beginRenderings() {return impl->beginRenderings();}

ActivationRecord::const_render_iterator ActivationRecord::endRenderings() const {return impl->endRenderings();}
ActivationRecord::render_iterator ActivationRecord::endRenderings() {return impl->endRenderings();}




}


