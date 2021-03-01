/**
 * activationStackRendering.cpp
 *
 *  Created on: Aug 22, 2012
 *      Author: zeil
 */

#include <algae/memoryModel/activationStack.h>
#include <algae/memoryModel/activationStackRenderer.h>
#include <algae/rendering/rendering.h>

namespace algae {

using namespace std;

/**
 *  Activation stacks are rendered as a box containing activation records
 *  (excepting the lowest record which is used for global/static storage).
 *
class ActivationStackRenderer: public TypeRendererOf<ActivationStack>
{
public:
 */

int ActivationStackRenderer::maxRecords = 16;
int ActivationStackRenderer::maxDetailedRecords = 2;


ActivationStackRenderer::ActivationStackRenderer (const ActivationStack& stack)
 : TypeRendererOf(stack)
{
}


/**
 * make a copy of this renderer
 *
 * @return a dynamically selected copy
 */
Renderer* ActivationStackRenderer::clone() const
{
	return new ActivationStackRenderer(*this);
}



/**
 * What string will be used as the value of this object?
 *
 * @return a string or null to yield to other renderers
 */
std::string ActivationStackRenderer::getValue() const
{
	return "";
}

/**
 * What color will be used to draw this object?
 *
 * @return a color or null to yield to other renderers
 */
Color ActivationStackRenderer::getColor() const
{
	if (maxRecords > 0)
		return Color::DarkGray;
	else
		return Color::White;
}

/**
 * Collect a list of other objects to be drawn inside the
 * box portraying this one.
 *
 * @param components a collector to which components can be passed
 */
void ActivationStackRenderer::getComponents(ComponentCollector& components) const
{
	cerr << "Rendering activation stack" << endl;
	for (unsigned i = 1; i < instance->size(); ++i)
	{
		cerr << "component for AR " << i << ": " << instance->activations[i]->getName() << endl;
		components.add(*(instance->activations[i]));
	}
}

/**
 * Collect a list of other objects to which we will draw
 * pointers from this one.
 *
 * @param connections a collector to which connections can be passed
 */
void ActivationStackRenderer::getConnections(ConnectionCollector& connections) const
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

int ActivationStackRenderer::getMaxComponentsPerRow() const
{
	return 1;
}





}
