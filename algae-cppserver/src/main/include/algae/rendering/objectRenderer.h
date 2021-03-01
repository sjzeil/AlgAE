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
#include <algae/rendering/connection.h>
#include <algae/rendering/renderer.h>


namespace algae {

/**
 * One of the two major divisions of renderers, Object Renderers are charged with the details
 * of a specific object. They are commonly used to implement "exceptions" to the normal rendering
 * rules for a data type, such as highlighting a selected object by changing its color for a limited
 * period of time.
 *
 * Because of this specialized use, many object renderers do not implement the entire functionality for
 * a type. Instead, they will implement a few functions directly, and defer to other renderers for the
 * remainder. For example, a highlighting renderer would implement getColor(), but defer on all other
 * rendering functions.
 */
class ObjectRenderer: public Renderer
{
	Identifier renders;
	const Renderer* prior;

public:
	ObjectRenderer (const Identifier& renderingOf, const Renderer* deferringTo = 0)
	: renders(renderingOf), prior(deferringTo) {}


	/**
	 * What object is this a rendering of?
	 *
	 */
	const Identifier& getRenders() const { return renders; }



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
	virtual void getComponents(ComponentCollector& components) const {deferTo()->getComponents(components);}

	/**
	 * Get a list of other objects to which we will draw
	 * pointers from this one.
	 *
	 * @param obj: object to be drawn
	 *
	 * @return an array of referenced objects or null to yield to other renderers
	 */
	virtual void getConnections(ConnectionCollector& connections) const {deferTo()->getConnections(connections);}



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


	/**
	 * To what other renderer will this one defer?
	 *
	 * @return a renderer used as a fall-back for rules not implemented in this one
	 */
	const Renderer* deferTo() const {return prior;}


	/**
	 * Sets another renderer to which this one may defer
	 *
	 * @param deferTo a renderer used as a fall-back for rules not implemented in this one
	 */
	void setDeferTo (const Renderer* deferTo)  {prior = deferTo;}


	/**
	 * Convenience method for cloning object renderers
	 */
	ObjectRenderer* cloneOR() const {return (ObjectRenderer*)clone();}


};



}

#endif
