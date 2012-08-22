/**
 * connection.cpp
 *
 *  Connections represent pointers or references to some entity and/or object in memory.
 *  
 *  Created on: June 30, 2012
 *      Author: zeil
 */


#include <algae/rendering/connection.h>


namespace algae {

using namespace std;

const double Connection::DefaultMinAngle = 0.0;
const double Connection::DefaultMaxAngle = 360.0;
const double Connection::DefaultPreferredLength = 3.0;
const double Connection::DefaultElasticity = 1.0;
const int Connection::NotAnInternalReference = -1;


Connection::Connection (std::string ident, const EntityIdentifier& theDestination,
		double theMinAngle, double theMaxAngle,
		int component)
: destination(theDestination), minAngle(theMinAngle),
	maxAngle(theMaxAngle), preferredLength (DefaultPreferredLength),
	elasticity(DefaultElasticity), value(""), label(""),
	color(Color::Black), id(ident), componentIndex(component)
{
}


Connection::Connection ()
: destination(EntityIdentifier::nullEID()), minAngle(DefaultMinAngle),
	maxAngle(DefaultMaxAngle), preferredLength (DefaultPreferredLength),
	elasticity(DefaultElasticity), value(""), label(""),
	color(Color::MedGray), id(""), componentIndex(NotAnInternalReference)
{
}



void Connection::print (std::ostream& out) const
{
	out << "=>" << destination;
}

bool Connection::operator== (const Connection& c) const
{
	if (!(destination == c.destination))
		return false;
	if (id != c.id)
		return false;
	if (componentIndex != c.componentIndex)
		return false;
	if (label != c.label)
		return false;
	if (value != c.value)
		return false;
	if (minAngle != c.minAngle || maxAngle != c.maxAngle)
		return false;
	if (!(color == c.color))
		return false;
	return (preferredLength == c.preferredLength) && (elasticity == c.elasticity);
}




}
