/**
 * snapshot.cpp
 *
 *
 *  Created on: July 3, 2012
 *      Author: zeil
 */


#include <iostream>
#include <map>
#include <set>
#include <string>

#include <algae/snapshot/snapshot.h>


namespace algae {



/**
 * A snapshot is a model of the current data state in terms of a graph of connected
 * Entities.  The graph is "rooted" at an activation stack and at a set of global entities.
 * 
 * The complete graph is formed from the transitive closure over the component and connection
 * relations starting from those roots.
 * 
 * @author zeil
 *
 */

Snapshot::iterator::iterator()
{}


// Get the data element at this position
Snapshot::iterator::reference Snapshot::iterator::operator*() const
{
	return pos->second;
}

Snapshot::iterator::pointer Snapshot::iterator::operator->() const
{
	return &(pos->second);
}


// Move position forward 1 place
Snapshot::iterator& Snapshot::iterator::operator++()
{
  ++pos;
  return *this;
}

Snapshot::iterator Snapshot::iterator::operator++(int)
{
	Snapshot::iterator saved = *this;
	++pos;
	return saved;
}

// Comparison operators
bool Snapshot::iterator::operator== (const Snapshot::iterator& i) const
{
	return pos == i.pos;
}

bool Snapshot::iterator::operator!= (const Snapshot::iterator& i) const
{
	return pos != i.pos;
}



/**
 * Create a snapshot with a given description and breakpoint location
 *
 * @param description
 * @param breakpoint
 */
Snapshot::Snapshot(std::string description, const SourceLocation& breakpoint)
  : descriptor(description), breakpointLocation(breakpoint)
{
}

/**
 * Add an entity to the snapshot. Replaces any existing entry with the same EntityIdentifier.
 * However, multiple entities with the same object Identifier can exist.
 *
 */
void Snapshot::add (const Entity& entity)
{
	EntityIdentifier eid = entity.getEntityIdentifier();
	Identifier id = eid.getObjectIdentifier();
	bool found = false;
	std::pair<EntitiesTable::iterator, EntitiesTable::iterator> range = entities.equal_range(id);
	for (EntitiesTable::iterator it = range.first; it != range.second; ++it)
	{
		Entity& e = it->second;
		if (e.getEntityIdentifier() == eid)
		{
			found = true;
			e = entity;
			break;
		}
	}
	if (!found) {
		entities.insert (EntitiesTable::value_type(id, entity));
	}
}


void Snapshot::setGlobal (const EntityIdentifier& eid, bool isGlobal)
{
	if (isGlobal)
		globals.insert(eid);
	else
		globals.erase(eid);
}

bool Snapshot::isGlobal (const EntityIdentifier& eid) const
{
	return globals.count(eid) > 0;
}


void Snapshot::remove (const Entity& entity)
{
	EntityIdentifier eid = entity.getEntityIdentifier();
	globals.erase(eid);
	Identifier id = eid.getObjectIdentifier();
	std::pair<EntitiesTable::iterator, EntitiesTable::iterator> range = entities.equal_range(id);
	for (EntitiesTable::iterator it = range.first; it != range.second; ++it)
	{
		Entity& e = it->second;
		if (e.getEntityIdentifier() == eid)
		{
			entities.erase(it);
			break;
		}
	}
}



Snapshot::const_iterator Snapshot::begin() const
{
	const_iterator it;
	it.pos = entities.begin();
	return it;
}

Snapshot::const_iterator Snapshot::end() const
{
	const_iterator it;
	it.pos = entities.end();
	return it;
}




void Snapshot::print(std::ostream& out) const
{
	out << descriptor;
	out << "@" << breakpointLocation;
	out << ": " << activationStack;
	out << "\n";
	out << "entities: ";
	for (const_iterator it = begin(); it != end(); ++it)
	{
		out << *it << " ";
	}
	out << "\n";
	out << "globals: ";
	for (std::set<EntityIdentifier>::const_iterator it = globals.begin(); it != globals.end(); ++it)
	{
		out << *it << " ";
	}
	out << "\n";
}

bool Snapshot::operator== (const Snapshot& s) const
{
	return s.descriptor == descriptor
			&& s.breakpointLocation == breakpointLocation
			&& s.activationStack == activationStack
			&& s.globals == globals
			&& s.entities == entities;
}




}
