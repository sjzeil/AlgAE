/**
 * proxyForSelfRenderer.cpp
 *
 *
 *  Created on: Aug 22, 2012
 *      Author: zeil
 */


#include <algae/rendering/proxyForSelfRenderer.h>
#include <algae/rendering/selfRenderer.h>

using namespace std;

namespace algae {



/**
 * The type renderer for objects that implement the SelfRendering interface.
 *
class ProxyForSelfRenderer: public TypeRenderer
{
	const SelfRenderer* proxyFor;
public:
 */

ProxyForSelfRenderer::ProxyForSelfRenderer (const SelfRenderer* instance)
 : proxyFor(instance)
{}


ProxyForSelfRenderer::ProxyForSelfRenderer (const SelfRenderer& instance)
: proxyFor(&instance)
{}

/**
 * make a copy of this renderer
 *
 * @return a dynamically selected copy
 */
Renderer* ProxyForSelfRenderer::clone() const
{
	return new ProxyForSelfRenderer(*this);
}



/**
 * What string will be used as the value of this object?
 *
 * @return a string or null to yield to other renderers
 */
std::string ProxyForSelfRenderer::getValue() const
{
	return proxyFor->getValue();
}

/**
 * What color will be used to draw this object?
 *
 * @return a color or null to yield to other renderers
 */
Color ProxyForSelfRenderer::getColor() const
{
	return proxyFor->getColor();
}


/**
 * Collect a list of other objects to be drawn inside the
 * box portraying this one.
 *
 * @param components a collector to which components can be passed
 */
void ProxyForSelfRenderer::getComponents(ComponentCollector& components) const
{
	proxyFor->getComponents(components);
}


/**
 * Collect a list of other objects to which we will draw
 * pointers from this one.
 *
 * @param connections a collecotr to which connections can be passed
 */
void ProxyForSelfRenderer::getConnections(ConnectionCollector& connections) const
{
	proxyFor->getConnections(connections);
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

int ProxyForSelfRenderer::getMaxComponentsPerRow() const
{
	return proxyFor->getMaxComponentsPerRow();
}



}
