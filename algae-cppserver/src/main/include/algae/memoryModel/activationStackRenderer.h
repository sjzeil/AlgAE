/**
 * activationStackRendering.h
 *
 *  Created on: Aug 22, 2012
 *      Author: zeil
 */

#ifndef ACTIVATIONSTACKRENDERER_H_
#define ACTIVATIONSTACKRENDERER_H_

#include <algae/rendering/typeRenderer.h>

namespace algae {

class ActivationStack;

/**
 *  Activation stacks are rendered as a box containing activation records
 *  (excepting the lowest record which is used for global/static storage).
 *
 */
class ActivationStackRenderer: public TypeRendererOf<ActivationStack>
{
	static int maxRecords;
	static int maxDetailedRecords;
public:

	ActivationStackRenderer (const ActivationStack& stack);

	/**
	 * Controls display of activation stack by setting a max to the number of
	 * activation records that will be shown. If not positive, suppresses display of the
	 * and only global variables will be renderered.
	 *
	 * Default is 16;
	 *
	 * @param maxToShow max # of activation records (counting from top of stack) to render
	 */
	static void setMaxRecords (int maxToShow)
	{
		maxRecords = maxToShow;
	}


	/**
	 * Controls display of activation stack by setting a max to the number of
	 * activation records that will be shown in full detail. Records not shown in full
	 * detail will still display the function names, names of parameters, and values of
	 * "inline" parameters, but reference parameters will not have connectors drawn.
	 *
	 * Default is 2.
	 *
	 * @param maxToShow max # of activation records (counting from top of stack) to render with ref params
	 */
	static void setMaxDetailed (int maxToShowDetailed)
	{
		maxDetailedRecords = maxToShowDetailed;
	}

	/**
	 * Returns the max number of activation records that will be shown in full detail. R
	 *
	 * @return max # of activation records (counting from top of stack) to render with ref params
	 */
	static int getMaxDetailed ()
	{
		return maxDetailedRecords;
	}


	/**
	 * make a copy of this renderer
	 *
	 * @return a dynamically selected copy
	 */
	virtual Renderer* clone() const;;



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
	 * @param connections a collector to which connections can be passed
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
