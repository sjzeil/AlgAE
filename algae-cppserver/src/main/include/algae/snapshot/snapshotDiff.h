/**
 * snapshotDiff.h
 *
 *
 *  Created on: July 4, 2012
 *      Author: zeil
 */

#ifndef SNAPSHOTDIFF_H_
#define SNAPSHOTDIFF_H_

#include <iostream>
#include <list>
#include <string>

#include <algae/snapshot/snapshot.h>


namespace algae {
	
/**
 * Describes the differences between two snapshot and allows reconstruction
 * of a snapshot given a second snapshot and the difference between them.
 * 
 * @author zeil
 *
 */
class SnapshotDiff {
private:
	/**
	 * All entities appearing in the base snapshot but not in the later derived one.
	 */
	std::list<EntityIdentifier> removedEntities;
	
	/**
	 * All entities that are added to the base or changed from their base values in the
	 * derived snapshot. 
	 */
	std::list<Entity> changedEntities;

	/**
	 * All entities not global in the base snapshot but global in the new one.
	 */
	std::list<EntityIdentifier> newGlobals;

	/**
	 * All entities global in the base snapshot but not global in the new one.
	 */
	std::list<EntityIdentifier> newNonGlobals;

	
	/**
	 * An informational string to appear in a status line when the new snapshot is being displayed 
	 */
	std::string descriptor;
	
	/**
	 * The corresponding source code location to be shown while the new snapshot is being displayed.
	 */
	SourceLocation breakpointLocation;
	
    /**
     * Entity representing the activation stack	in the new snapshot
     */
	EntityIdentifier activationStack;

	void computeDifference(const Snapshot& base, const Snapshot& derived);

public:
	
	/**
	 * The usual way to construct a diff - as the differences required to go from base to derived.
	 * 
	 * @param base  the starting snapshot
	 * @param derived a snapshot derived from that base by one or more changes
	 */
	SnapshotDiff (const Snapshot& base, const Snapshot& derived)
	{
		computeDifference (base, derived);
	}
		


	/**
	 * Reconstruct a derived snapshot from a base value and this diff
	 * 
	 * @param fromBase the starting value
	 * @return reconstructed derived snapshot
	 */
	Snapshot reconstruct (const Snapshot& fromBase) const;
	
	bool operator== (const SnapshotDiff& d) const;
	
	void printXML(std::ostream& out) const;
	
	void print(std::ostream& out) const;

	/**
	 * @return the removedEntities
	 */
	std::list<EntityIdentifier>& getRemovedEntities() {
		return removedEntities;
	}


	/**
	 * @return the changedEntities
	 */
	std::list<Entity>& getChangedEntities() {
		return changedEntities;
	}



	/**
	 * @return the newGlobals
	 */
	std::list<EntityIdentifier>& getNewGlobals() {
		return newGlobals;
	}




	/**
	 * @return the newNonGlobals
	 */
	std::list<EntityIdentifier>& getNewNonGlobals() {
		return newNonGlobals;
	}


	/**
	 * @return the descriptor
	 */
	std::string getDescriptor() {
		return descriptor;
	}


	/**
	 * @return the breakpointLocation
	 */
	SourceLocation getBreakpointLocation() {
		return breakpointLocation;
	}


	/**
	 * @return the activationStack
	 */
	EntityIdentifier getActivationStack() {
		return activationStack;
	}


	
};

inline
std::ostream& operator<< (std::ostream& out, const SnapshotDiff& sd)
{
	sd.print(out);
	return out;
}


}

#endif

