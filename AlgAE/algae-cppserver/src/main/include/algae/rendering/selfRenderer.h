/**
 * selfRenderer.h
 *
 *
 *  Created on: Aug 22, 2012
 *      Author: zeil
 */

#ifndef SELFRENDERER_H_
#define SELFRENDERER_H_

#include <list>
#include <string>

#include <algae/snapshot/color.h>
#include <algae/snapshot/identifier.h>
#include <algae/rendering/componentCollector.h>
#include <algae/rendering/connectionCollector.h>


namespace algae {

class Entity;

/**
 * A SelfRenderer is a class that knows how to render itself.
 * i.e., in addition to its "real" operations, it supports the
 * operations expected of a renderer.
 *
 * It is not, however, a subclass of Renderer and therefore need not
 * support cloning (which could otherwise interfere with the copy
 * policy of the class).
 *
 */
class SelfRenderer
{

public:

	virtual ~SelfRenderer() {}


	/**
	 * What string will be used as the value of this object?
	 *
	 * @return a string or null to yield to other renderers
	 */
	virtual std::string getValue() const = 0;

	/**
	 * What color will be used to draw this object?
	 *
	 * @return a color or null to yield to other renderers
	 */
	virtual Color getColor() const = 0;

	/**
	 * Collect a list of other objects to be drawn inside the
	 * box portraying this one.
	 *
	 * @param components a collector to which components can be passed
	 */
	virtual void getComponents(ComponentCollector& components) const = 0;

	/**
	 * Collect a list of other objects to which we will draw
	 * pointers from this one.
	 *
	 * @param connections a collecotr to which connections can be passed
	 */
	virtual void getConnections(ConnectionCollector& connections) const = 0;


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

	virtual int getMaxComponentsPerRow() const = 0;


};




}

#endif
