/**
 * simpleReference.h
 *
 *
 *  Created on: Aug 11, 2012
 *      Author: zeil
 */

#ifndef SIMPLEREFERENCE_H_
#define SIMPLEREFERENCE_H_

#include <ostream>
#include <algae/rendering/typeRendering.h>
#include <algae/snapshot/color.h>
#include <algae/snapshot/identifier.h>

namespace algae {


/**
 * A labeled pointer to an object.
 *
 * @author zeil
 *
 */
class SimpleReference: public SelfRenderer
{
private:
	Color color;
	double maxAngle;
	double minAngle;
	Identifier refersTo;

public:

	SimpleReference(Identifier refersTo, double minAngle = 0.0, double maxAngle = 360.0);

	void set (Identifier newRefersTo)
	{
		refersTo = newRefersTo;
	}

	Identifier get() const
	{
		return refersTo;
	}


	/**
	 * What string will be used as the value of this object?
	 *
	 * @param obj: object to be drawn
	 * @return a string or null to yield to other renderers
	 */
	std::string getValue() const
	{
		return "";
	}

	/**
	 * What color will be used to draw this object?
	 *
	 * @param obj: object to be drawn
	 * @return a color or null to yield to other renderers
	 */
	Color getColor() const
	{
		return color;
	}


	/**
	 * Indicates how components will be laid out within the box
	 * representing this object.  A return value of 1 will force all
	 * components to be laid out in a single vertical column. Larger
	 * return values will permit a more horizontal layout.
	 *
	 * @param obj
	 * @return max #components per row or a non-positive value to yield to other renderers
	 */

	int getMaxComponentsPerRow() const
	{
		return 1;
	}


	void setColor(Color acolor) {
		color = acolor;
	}

	void setMaxAngle(double mxAngle) {
		maxAngle = mxAngle;
	}

	double getMaxAngle() const {
		return maxAngle;
	}

	void setMinAngle(double mnAngle) {
		minAngle = mnAngle;
	}

	double getMinAngle() const {
		return minAngle;
	}

	/**
	 * Get a list of other objects to be drawn inside the
	 * box portraying this one.
	 *
	 * @param obj: object to be drawn
	 *
	 * @return an array of contained objects or null to yield to other renderers
	 */
	virtual void getComponents(ComponentCollector& components) const
	{}

	/**
	 * Get a list of other objects to which we will draw
	 * pointers from this one.
	 *
	 * @param obj: object to be drawn
	 *
	 * @return an array of referenced objects or null to yield to other renderers
	 */
	virtual void getConnections(ConnectionCollector& connections) const;



};

std::ostream& operator<< (std::ostream& out, const SimpleReference& sref);


}

#endif
