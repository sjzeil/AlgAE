/**
 * activationRecord.cpp
 *
 *
 *  Created on: Aug 11, 2012
 *      Author: zeil
 */


#include <algae/memoryModel/activationRecord.h>
#include <algae/memoryModel/activationStack.h>
#include <algae/memoryModel/simpleReference.h>
#include <algae/rendering/colorChanger.h>
#include <algae/rendering/colorInverter.h>
#include <algae/rendering/objectRenderer.h>
#include <algae/animation.h>

using namespace std;

namespace algae {


ActivationRecord::~ActivationRecord()
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
void ActivationRecord::var(std::string label, const Identifier& value)
{
	locals.push_back(LabeledComponent(value, label));
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
	SimpleReference* sref = new SimpleReference(value);
	artificialReferences.push_back (sref);
	var(label, *sref);
}


/**
 * Change the rendering of the indicated object to highlight it.
 * This stays in effect until the end of the current scope/activation.
 *
 */
void ActivationRecord::highlight (const Identifier& value)
{
	ColorInverter ci (value);
	render (ci);
}

void ActivationRecord::highlight (const Identifier& value, Color c)
{
	ColorChanger cc(value, c);
	render (cc);
}


/**
 * Reverse the effect of a prior highlight call
 *
 */
void ActivationRecord::unhighlight (const Identifier& value)
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
void ActivationRecord::render(const ObjectRenderer& newRendering)
{
	ObjectRenderer* newRendering2 = newRendering.cloneOR();
	Renderer* oldRenderer = Animation::algae()->getMemoryModel().getActivationStack().getRenderingOf(newRendering.getRenders());
	newRendering2->setDeferTo(oldRenderer);
	localRenderings.push_back (newRendering2);
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
void ActivationRecord::breakPoint (std::string description, const char* fileName, int lineNumber)
{
	// Todo
}







}


