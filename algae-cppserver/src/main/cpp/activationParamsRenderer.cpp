/**
 * activationRecordRendering.cpp
 *
 *  Created on: Aug 23, 2012
 *      Author: zeil
 */


#include <algae/impl/activationRecordImpl.h>
#include <algae/memoryModel/activationRecordRenderer.h>
#include <algae/memoryModel/activationStackRenderer.h>
#include <algae/memoryModel/activationRecord.h>
#include <algae/memoryModel/activationStack.h>
#include <algae/memoryModel/simpleReference.h>
#include <algae/rendering/rendering.h>

namespace algae {



ActivationParamsRenderer::ActivationParamsRenderer (const ActivationParams& params)
 : TypeRendererOf(params)
{
}



/**
 * make a copy of this renderer
 *
 * @return a dynamically selected copy
 */
Renderer* ActivationParamsRenderer::clone() const
{
	return new ActivationParamsRenderer(*this);
}



/**
 * What string will be used as the value of this object?
 *
 * @return a string or null to yield to other renderers
 */
std::string ActivationParamsRenderer::getValue() const
{
	return "";
}

/**
 * What color will be used to draw this object?
 *
 * @return a color or null to yield to other renderers
 */
Color ActivationParamsRenderer::getColor() const
{
	return Color::MedGray;
}

/**
 * Collect a list of other objects to be drawn inside the
 * box portraying this one.
 *
 * @param components a collector to which components can be passed
 */
void ActivationParamsRenderer::getComponents(ComponentCollector& components) const
{
	int depth = instance->onStack->size() - instance->height;
	bool detailed = (depth <= ActivationStackRenderer::getMaxDetailed());

	if (instance->thisParam != 0)
	{
		components.add (*(instance->thisParam), "this");
		components.add ('.');
	}
	components.add(instance->name + "(");
	int i = 0;

	for (ActivationRecord::const_iterator it = instance->parameters.begin(); it != instance->parameters.end(); ++it)
	{
		if (i > 0)
			components.add(',');
		++i;
		const LabeledComponent& c = *it;
		if (detailed || typeid(c.oid.getType()) != typeid(SimpleReference))
		{
			components.addComponent(c.oid, c.label);
		} else {
			SimpleReference nullRef (Identifier::nullID());
			components.add (nullRef, c.label);
		}
	}
	components.add(')');

}

/**
 * Collect a list of other objects to which we will draw
 * pointers from this one.
 *
 * @param connections a collector to which connections can be passed
 */
void ActivationParamsRenderer::getConnections(ConnectionCollector& connections) const
{

}


/**
 * Indicates how components will be laid out within the box
 * representing this object.  A return value of 1 will force all
 * components to be laid out in a single vertical column. Larger
 * return values will permit a more horizontal layout.
 *
 * A zero value requests that components be laid out in a (more or less) minimal area.
 *
 * @param obj
 * @return max #components per row or a negative value to yield to other renderers
 */

int ActivationParamsRenderer::getMaxComponentsPerRow() const
{
	return 12;
}




}
