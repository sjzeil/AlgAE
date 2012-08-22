/**
 * connector.h
 *
 *  Connectors represent pointers or references from one entity to another.
 *  They differ from Connections in two ways:
 *  1) They record the source of the reference as well as the destination
 *  2) They can be rendered as XML
 *  
 *  Created on: June 23, 2012
 *      Author: zeil
 */

#ifndef CONNECTOR_H_
#define CONNECTOR_H_

#include <iostream>
#include <string>


#include <algae/rendering/connection.h>
#include <algae/snapshot/entityIdentifier.h>
#include <algae/snapshot/color.h>

namespace algae {

class Connector: public Connection
{
private:
	EntityIdentifier source;
	
	
public:

	Connector (std::string ident, const EntityIdentifier& theSource,
			const EntityIdentifier& theDestination,
			double theMinAngle, double theMaxAngle, int component = -1)
	: Connection(ident, theDestination, theMinAngle, theMaxAngle, component),
	  source(theSource)
	{
	}

	
	Connector ()
	: source(EntityIdentifier::nullEID())
	{
	}


	
	void printXML (std::ostream& out) const;
	
	bool operator== (const Connector& c) const;

	/**
	 * @return the source
	 */
	const EntityIdentifier& getSource() const {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	void setSource(const EntityIdentifier& theSource) {
		source = theSource;
	}



};

inline
std::ostream& operator<< (std::ostream& out, const Connector& c)
{
	out << c.getSource() << "=>" << c.getDestination();
	return out;
}

}

#endif


