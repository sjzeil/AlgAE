/**
 * snapshot.h
 *
 *
 *  Created on: July 3, 2012
 *      Author: zeil
 */

#ifndef SNAPSHOT_H_
#define SNAPSHOT_H_

#include <cstddef>
#include <iostream>
#include <map>
#include <set>
#include <string>

#include <algae/snapshot/entity.h>
#include <algae/snapshot/identifier.h>
#include <algae/snapshot/sourceLocation.h>


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
class Snapshot
{
public:
	typedef std::multimap<Identifier, Entity> EntitiesTable;

	class iterator {
	public:
	  typedef std::forward_iterator_tag iterator_category;
	  typedef Entity                     value_type;
	  typedef std::ptrdiff_t             difference_type;
	  typedef const Entity*              pointer;
	  typedef const Entity&              reference;

	  iterator();

	  // Get the data element at this position
	  reference operator*() const;
	  pointer operator->() const;

	  // Move position forward 1 place
	  iterator& operator++();
	  iterator operator++(int);

	  // Comparison operators
	  bool operator== (const iterator&) const;
	  bool operator!= (const iterator&) const;
	private:
	  EntitiesTable::const_iterator pos;
	  friend class Snapshot;
	};
	typedef iterator const_iterator;
	


private:
	/**
	 *  Provides access to all objects in this snapshot, indexed by their identifiers.
	 *  
	 */
	EntitiesTable entities;

	
	/**
	 * An informational string to appear in a status line when this snapshot is being displayed 
	 */
	std::string descriptor;
	
	/**
	 * The corresponding source code location to be shown while this snapshot is being displayed.
	 */
	SourceLocation breakpointLocation;
	
    /**
     * Entity representing the activation stack	
     */
	EntityIdentifier activationStack;
	
	/**
	 * Entities denoting global variables
	 */
	std::set<EntityIdentifier> globals;

	
	
public:

	/**
	 * Create a new snapshot.
	 */
	Snapshot() {}
	
	
	/**
	 * Create a snapshot with a given description and breakpoint location
	 * 
	 * @param description
	 * @param breakpoint
	 */
	Snapshot(std::string description, const SourceLocation& breakpoint);

	/**
	 * Add an entity to the snapshot. Replaces any existing entry with the same EntityIdentifier.
	 * However, multiple entities with the same object Identifier can exist.
	 *
	 */
	void add (const Entity& entity);
	
	
	void setGlobal (const EntityIdentifier& eid, bool isGlobal);
	
	bool isGlobal (const EntityIdentifier& eid) const;
	

	void remove (const Entity& entity);


	// Iterators over the collection of entities in this snapshot
	const_iterator begin() const;
	const_iterator end() const;


	/**
	 * @return the entities
	 */
	EntitiesTable& getEntities() {
		return entities;
	}


	/**
	 * @return the descriptor
	 */
	std::string getDescriptor() const {
		return descriptor;
	}



	/**
	 * @param descriptor the descriptor to set
	 */
	void setDescriptor(std::string adescriptor) {
		descriptor = adescriptor;
	}



	/**
	 * @return the breakpointLocation
	 */
	const SourceLocation& getBreakpointLocation() const
	{
		return breakpointLocation;
	}



	/**
	 * @param breakpointLocation the breakpointLocation to set
	 */
	void setBreakpointLocation(const SourceLocation& aBreakpointLocation) {
		breakpointLocation = aBreakpointLocation;
	}



	/**
	 * @return the activationStack
	 */
	const EntityIdentifier& getActivationStack() const
	{
		return activationStack;
	}



	/**
	 * @param activationStack the activationStack to set
	 */
	void setActivationStack(const EntityIdentifier& theActivationStack)
	{
		activationStack = theActivationStack;
	}



	/**
	 * @return the globals
	 */
	std::set<EntityIdentifier>& getGlobals()
	{
		return globals;
	}
	const std::set<EntityIdentifier>& getGlobals() const
	{
		return globals;
	}


	void print(std::ostream& out) const;

	bool operator== (const Snapshot & s) const;
	bool operator!= (const Snapshot & s) const  {return !operator==(s);}
};

	
inline
std::ostream& operator<< (std::ostream& out, const Snapshot& s)
{
	s.print(out);
	return out;
}
	
}

#endif

