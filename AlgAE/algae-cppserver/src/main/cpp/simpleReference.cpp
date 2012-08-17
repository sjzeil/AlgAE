/**
 * simpleReference.cpp
 *
 *
 *  Created on: Aug 13, 2012
 *      Author: zeil
 */


#include <algae/memoryModel/simpleReference.h>
#include <algae/rendering/connection.h>

using namespace std;

namespace algae {


/**
 * A labeled pointer to an object.
 *
 * @author zeil
 *
 */

SimpleReference::SimpleReference(Identifier theRefersTo, double theMinAngle, double theMaxAngle)
: color(Color::LightGray), maxAngle(theMaxAngle), minAngle(theMinAngle), refersTo(theRefersTo)
{
}


/**
 * Get a list of other objects to which we will draw
 * pointers from this one.
 *
 * @param obj: object to be drawn
 *
 * @return an array of referenced objects or null to yield to other renderers
 */
void SimpleReference::getConnections(ConnectionCollector& collector) const
{
	Connection c ("1", refersTo, minAngle, maxAngle);
	c.setColor(Color::DarkGray);
	c.setElasticity(10.0);
	collector.addConnection(c);
}



std::ostream& operator<< (std::ostream& out, const SimpleReference& sref)
{
	out << "@" << sref.get();
	return out;
}


}
