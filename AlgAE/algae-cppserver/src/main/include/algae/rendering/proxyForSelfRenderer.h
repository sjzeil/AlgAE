/**
 * proxyForSelfRenderer.h
 *
 *
 *  Created on: Aug 22, 2012
 *      Author: zeil
 */

#ifndef PROXYFORSELFRENDERER_H_
#define PROXYFORSELFRENDERER_H_

#include <list>
#include <string>
#include <sstream>
#include <typeinfo>

#include <algae/snapshot/color.h>
#include <algae/rendering/connection.h>
#include <algae/rendering/typeRenderer.h>


namespace algae {

class SelfRenderer;


/**
 * The type renderer for objects that implement the SelfRendering interface.
 *
 */
class ProxyForSelfRenderer: public TypeRenderer
{
	const SelfRenderer* proxyFor;
public:

	ProxyForSelfRenderer (const SelfRenderer* instance);
	ProxyForSelfRenderer (const SelfRenderer& instance);

	/**
	 * make a copy of this renderer
	 *
	 * @return a dynamically selected copy
	 */
	virtual Renderer* clone() const;



	/**
	 * What string will be used as the value of this object?
	 *
	 * @return a string or null to yield to other renderers
	 */
	virtual std::string getValue() const;

	/**
	 * What color will be used to draw this object?
	 *
	 * @return a color or null to yield to other renderers
	 */
	virtual Color getColor() const;

	/**
	 * Collect a list of other objects to be drawn inside the
	 * box portraying this one.
	 *
	 * @param components a collector to which components can be passed
	 */
	virtual void getComponents(ComponentCollector& components) const;

	/**
	 * Collect a list of other objects to which we will draw
	 * pointers from this one.
	 *
	 * @param connections a collecotr to which connections can be passed
	 */
	virtual void getConnections(ConnectionCollector& connections) const;


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

	virtual int getMaxComponentsPerRow() const;


};

}

#endif
