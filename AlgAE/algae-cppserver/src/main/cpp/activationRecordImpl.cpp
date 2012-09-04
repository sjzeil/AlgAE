/**
 * activationRecord.cpp
 *
 *
 *  Created on: Aug 11, 2012
 *      Author: zeil
 */


#include <algae/impl/activationRecordImpl.h>
#include <algae/memoryModel/activationStack.h>
#include <algae/memoryModel/simpleReference.h>
#include <algae/rendering/colorChanger.h>
#include <algae/rendering/colorInverter.h>
#include <algae/rendering/objectRenderer.h>
#include <algae/animation.h>

using namespace std;

namespace algae {


ActivationRecordImpl::~ActivationRecordImpl()
{
	for (render_iterator it = beginRenderings(); it != endRenderings(); ++it)
	{
		ObjectRenderer* orend = *it;
		delete orend;
	}
}


/**
 * Show a variable as a parameter of the current activation
 * Variables portrayed by this call are shown "in-line".
 *
 * @param label  the variable name (optional, can be "" or null)
 * @param param  the variable/value
 */
void ActivationRecordImpl::param(std::string label, const Identifier& value)
{
	params.parameters.push_back(LabeledComponent(value, label));
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
void ActivationRecordImpl::refParam (std::string  label, const Identifier& value)
{
	SimpleReference* sref = new SimpleReference(value);
	artificialReferences.push_back (sref);
	param (label, *sref);
}


/**
 * Show a variable as a local variable of the current activation
 * Variables portrayed by this call are shown "in-line".
 *
 * @param label  the variable name (optional, can be "" or null)
 * @param param  the variable/value
 */
void ActivationRecordImpl::var(std::string label, const Identifier& value)
{
	locals.locals.push_back(LabeledComponent(value, label));
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
void ActivationRecordImpl::refVar (std::string  label, const Identifier& value)
{
	SimpleReference* sref = new SimpleReference(value);
	artificialReferences.push_back (sref);
	var(label, *sref);
}


/**
 * Change the rendering of the indicated object to highlight it.
 * This stays in effect until the end of the current scope/activation.
 *
 */
void ActivationRecordImpl::highlight (const Identifier& value)
{
	ColorInverter ci (value);
	render (ci);
}

void ActivationRecordImpl::highlight (const Identifier& value, Color c)
{
	ColorChanger cc(value, c);
	render (cc);
}


/**
 * Reverse the effect of a prior highlight call
 *
 */
void ActivationRecordImpl::unhighlight (const Identifier& value)
{
	for (list<ObjectRenderer*>::iterator i = localRenderings.begin(); i != localRenderings.end(); ++i)
	{
		ObjectRenderer* orend = *i;
		if (value == orend->getRenders())
		{
			ObjectRenderer& renderer = *orend;
			if (typeid(renderer) == typeid(ColorChanger) || typeid(renderer) == typeid(ColorInverter))
			{
				localRenderings.erase(i);
				break;
			}
		}
	}
}


/**
 * Establish a rendering for a specific object. This rendering will only
 * remain in effect until the current activation/scope is exited.
 */
void ActivationRecordImpl::render(const ObjectRenderer& newRendering)
{
	ObjectRenderer* newRendering2 = newRendering.cloneOR();
	Renderer* oldRenderer = Animation::algae()->getMemoryModel().getActivationStack().getRenderingOf(newRendering.getRenders());
	newRendering2->setDeferTo(oldRenderer);
	localRenderings.push_back (newRendering2);
}






}


