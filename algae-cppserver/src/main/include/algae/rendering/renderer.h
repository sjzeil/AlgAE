/**
 * renderer.h
 *
 *
 *  Created on: June 17, 2012
 *      Author: zeil
 */

#ifndef RENDERER_H_
#define RENDERER_H_

#include <list>
#include <string>

#include <algae/snapshot/color.h>
#include <algae/snapshot/identifier.h>
#include <algae/rendering/componentCollector.h>
#include <algae/rendering/connectionCollector.h>


namespace algae {

class Entity;

/**
 * A Renderer is a class that has the task of representing objects as
 * Entities - colored boxes with text strings (labels and values), possible
 * nested entity components, and possible connections to other entities.
 *
 *
 */
class Renderer
{

public:

	virtual ~Renderer() {}

	/**
	 * make a copy of this renderer
	 *
	 * @return a dynamically selected copy
	 */
	virtual Renderer* clone() const = 0;



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



	/**
	 * Apply this renderer to fill in the details of an Entity
	 * that already contains contains the appropriate object identifier and label.
	 *
	 * @param e An entity already carrying the appropriate object identifier
	 *          and label. This function adds the color, value, components, and
	 *          connections to the entiry.
	 */
	void renderInto (Entity& e) const;


};




}

#endif
