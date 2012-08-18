/**
 * entity.cpp
 *
 *  An entity represents an object in memory as a labeled box that can contain and connect 
 *  to other entities
 *  
 *  Created on: July 1, 2012
 *      Author: zeil
 */


#include <algorithm>
#include <iostream>
#include <iterator>
#include <list>
#include <string>


#include <algae/snapshot/entity.h>
#include <algae/communications/xmlOutput.h>

using namespace std;

namespace algae {




/**
 * Create an entity representing a named "standalone" object
 * that is not a component of some larger entity
 * @param id    object to be represented by this new entity
 * @param label a descriptive name for this entity
 */
Entity::Entity (const Identifier& id, std::string theLabel)
: entityIdentifier(id,theLabel), label(theLabel), color(Color::LightGray), maxComponentsPerRow(1)
{
}


/**
 * Create an entity representing an object that is a component
 * of a larger entity.
 *
 * @param id   object to be represented by this new entity
 * @param container  entity that is considered to contain this one as a component
 * @param componentLabel  a string differentiating this component from all others of the same container
 */
Entity::Entity (const Identifier& id, const EntityIdentifier& container, std::string componentLabel)
: entityIdentifier(id, container, componentLabel),
  label(componentLabel), color(Color::LightGray), maxComponentsPerRow(1)
{
}




/**
 * Create an entity that can be rebuilt via XML decoding
 * @param entityIdentifier    object to be represented by this new entity
 */
Entity::Entity ()
: color(Color::LightGray), maxComponentsPerRow(1)
{
}


std::string Entity::getDescription() const
{
	std::string result;
	if (label.length() > 0 && label[0] >= ' ') {
		result.append (label);
		result.append (": ");
	}
	result.append(value);
	return result;
}



void Entity::printXML (std::ostream& out) const
{
	ObjectTag e (out, XMLTag::snapshotPackage + ".Entity");
	{
		PropertyTag pt (out, "entityIdentifier");
		entityIdentifier.printXML(out);
		pt.close();
	}
	{
		PropertyTag pt (out, "label");
		StringValue (out, label);
		pt.close();
	}
	{
		PropertyTag pt (out, "value");
		StringValue (out, value);
		pt.close();
	}
	{
		PropertyTag pt (out, "color");
		color.printXML(out);
		pt.close();
	}
	{
		PropertyTag pt (out, "maxComponentsPerRow");
		IntValue (out, maxComponentsPerRow);
		pt.close();
	}
	{
		PropertyTag pt (out, "components");
		for (list<EntityIdentifier>::const_iterator i = components.begin(); i != components.end(); ++i)
		{
			MethodTag mt (out, "add");
			i->printXML(out);
			mt.close();
		}
		pt.close();
	}
	{
		PropertyTag pt (out, "connections");
		for (list<Connector>::const_iterator i = connections.begin(); i != connections.end(); ++i)
		{
			MethodTag mt (out, "add");
			i->printXML(out);
			mt.close();
		}
		pt.close();
	}


	e.close();
}


void Entity::print (std::ostream& out) const
{
	out << entityIdentifier << ":" << label << ":" << value << ":" << color << "\n";
	out << "  components[" << components.size() << "]=";
	copy (components.begin(), components.end(), ostream_iterator<EntityIdentifier>(out, ", "));
	out << "\n";
	out << "  connections[" << connections.size() << "]=";
	copy (connections.begin(), connections.end(), ostream_iterator<Connector>(out, ", "));
	out << "\n";
}


bool Entity::operator== (const Entity& e) const
{
	if (!(entityIdentifier == e.entityIdentifier))
		return false;
	if (label != e.label || value != e.value)
		return false;
	if (maxComponentsPerRow != e.maxComponentsPerRow)
		return false;
	if (!(color == e.color))
		return false;
	if (components.size() != e.components.size())
		return false;
	if (connections.size() != e.connections.size())
		return false;
	return equal(components.begin(), components.end(), e.components.begin())
			&& equal(connections.begin(), connections.end(), e.connections.begin());
}


ostream& operator<< (ostream& out, const Entity& e)
{
	e.print(out);
	return out;
}


}

