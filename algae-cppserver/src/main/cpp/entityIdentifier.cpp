/**
 * entityIdentifier.cpp
 *
 *
 *  Created on: June 26, 2012
 *      Author: zeil
 */


#include <iostream>
#include <string>

#include <algae/snapshot/entityIdentifier.h>
#include <algae/communications/xmlOutput.h>

using namespace std;

namespace algae {


/**
 * For objects that are not components of larger objects
 *
 * @param id  object identifier\
 * @param label decorative label/name used for display purposes but is ignored
 * when doing comparisons
 */
EntityIdentifier::EntityIdentifier (const Identifier& oid, std::string label)
: id(oid), container(0), componentLabel(label)
{}



/**
 * Used for XML decoding only
 *
 */
EntityIdentifier::EntityIdentifier ()
: id(Identifier::NullID), container(0), componentLabel("")
{
}

EntityIdentifier::EntityIdentifier (const EntityIdentifier& eid)
 : id(eid.id), container(0), componentLabel(eid.componentLabel)
{
	if (eid.container != 0)
		container = new EntityIdentifier(*eid.container);
}

EntityIdentifier::~EntityIdentifier()
{
	if (container != 0)
		delete container;
}

EntityIdentifier& EntityIdentifier::operator= (const EntityIdentifier& eid)
{
	if (this != &eid)
	{
		if (container != 0)
			delete container;
		id = eid.id;
		container = 0;
		componentLabel = eid.componentLabel;
		if (eid.container != 0)
			container = new EntityIdentifier(*eid.container);
	}
	return *this;
}



/**
 * For objects that are components of larger objects
 *
 * @param id  object identifier
 * @param container object that contains this one as a component
 * @param label name that distinguishes this component from others of the same parent
 *
 */
EntityIdentifier::EntityIdentifier (const Identifier& oid, const EntityIdentifier& theContainer, std::string label)
: id(oid), container(new EntityIdentifier(theContainer)), componentLabel(label)
{
}









/**
 * How many nested containers is this considered to be a component of?
 * @return
 */
int EntityIdentifier::depth() const
{
	if (container == 0)
		return 0;
	else
		return 1 + container->depth();
}


void EntityIdentifier::printXML (std::ostream& out) const {
	ObjectTag eid (out, XMLTag::snapshotPackage + ".EntityIdentifier");
	{
		PropertyTag idTag (out, "objectIdentifier");
		id.printXML(out);
		idTag.close();
	}
	if (container != 0)
	{
		PropertyTag containerTag (out, "container");
		container->printXML(out);
		containerTag.close();
	}
	if (componentLabel != "")
	{
		PropertyTag componentLabelTag (out, "componentLabel");
		{
			StringValue clv (out, componentLabel);
			clv.close();
		}
		componentLabelTag.close();
	}
	eid.close();
}


void EntityIdentifier::print (std::ostream& out) const {
	out << id << "@" << componentLabel;
	if (container != 0)
	{
		out << " in " << *container;
	}
}



bool EntityIdentifier::operator== (const EntityIdentifier& eid) const
{
	if (eid.id != id)
		return false;
	if (eid.componentLabel != componentLabel)
		return false;
	if (container == 0 && eid.container == 0)
		return true;
	if (container == 0 || eid.container == 0)
			return false;
	return *container == *(eid.container);
}


bool EntityIdentifier::operator< (const EntityIdentifier& eid) const
{
	if (eid.id < id)
		return false;
	if (id < eid.id)
		return true;
	if (componentLabel < eid.componentLabel)
		return true;
	if (componentLabel > eid.componentLabel)
		return false;

	if (container == 0 && eid.container != 0)
		return true;
	if (eid.container == 0)
		return false;

	return *container < *(eid.container);
}





std::ostream& operator<< (std::ostream& out, const EntityIdentifier& eid)
{
	eid.print(out);
	return out;
}


}
