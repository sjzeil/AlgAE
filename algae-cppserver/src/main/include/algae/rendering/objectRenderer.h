/**
 * objectRenderer.h
 *
 *
 *  Created on: June 26, 2012
 *      Author: zeil
 */

#ifndef OBJECTRENDERER_H_
#define OBJECTRENDERER_H_

#include <list>
#include <string>

#include <algae/snapshot/color.h>
#include <algae/memoryModel/connection.h>
#include <algae/rendering/renderer.h>


namespace algae {


class ObjectRenderer: public Renderer
{
public:
	ObjectRenderer (const Renderer* deferringTo = 0)
	: Renderer(deferringTo) {}


	/**
	 * What string will be used as the value of this object?
	 *
	 * @param obj: object to be drawn
	 * @return a string or null to yield to other renderers
	 */
	virtual std::string getValue() const {return deferTo()->getValue();}

	/**
	 * What color will be used to draw this object?
	 *
	 * @param obj: object to be drawn
	 * @return a color or null to yield to other renderers
	 */
	virtual Color getColor() const {return deferTo()->getColor();}

	/**
	 * Get a list of other objects to be drawn inside the
	 * box portraying this one.
	 *
	 * @param obj: object to be drawn
	 *
	 * @return an array of contained objects or null to yield to other renderers
	 */
	virtual void getComponents(ComponentCollector& components) const {deferTo()-->getComponents(components);}

	/**
	 * Get a list of other objects to which we will draw
	 * pointers from this one.
	 *
	 * @param obj: object to be drawn
	 *
	 * @return an array of referenced objects or null to yield to other renderers
	 */
	virtual void getConnections(ConnectionCollector& connections) const {deferTo()-->getConnections(connections);}



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

	virtual int getMaxComponentsPerRow() const {return deferTo()->getMaxComponentsPerRow();}

};



}

#endif
