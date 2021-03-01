/**
 * activationRecordRendering.cpp
 *
 *  Created on: Aug 23, 2012
 *      Author: zeil
 */


#include <algae/impl/activationRecordImpl.h>
#include <algae/memoryModel/activationRecordRenderer.h>
#include <algae/memoryModel/activationRecord.h>
#include <algae/memoryModel/activationStack.h>
#include <algae/memoryModel/simpleReference.h>
#include <algae/rendering/rendering.h>

namespace algae {



ActivationLocalsRenderer::ActivationLocalsRenderer (const ActivationLocals& loc)
: TypeRendererOf(loc)
{}



/**
 * make a copy of this renderer
 *
 * @return a dynamically selected copy
 */
Renderer* ActivationLocalsRenderer::clone() const
{
	return new ActivationLocalsRenderer(*this);
}



/**
 * What string will be used as the value of this object?
 *
 * @return a string or null to yield to other renderers
 */
std::string ActivationLocalsRenderer::getValue() const
{
	return "";
}

/**
 * What color will be used to draw this object?
 *
 * @return a color or null to yield to other renderers
 */
Color ActivationLocalsRenderer::getColor() const
{
	return Color::MedGray;
}

/**
 * Collect a list of other objects to be drawn inside the
 * box portraying this one.
 *
 * @param components a collector to which components can be passed
 */
void ActivationLocalsRenderer::getComponents(ComponentCollector& components) const
{
	for (ActivationRecord::const_iterator it = instance->locals.begin(); it != instance->locals.end(); ++it)
	{
		const LabeledComponent& c = *it;
		components.addComponent(c.oid, c.label);
	}
}

/**
 * Collect a list of other objects to which we will draw
 * pointers from this one.
 *
 * @param connections a collector to which connections can be passed
 */
void ActivationLocalsRenderer::getConnections(ConnectionCollector& connections) const
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

int ActivationLocalsRenderer::getMaxComponentsPerRow() const
{
	return 0;
}




}
