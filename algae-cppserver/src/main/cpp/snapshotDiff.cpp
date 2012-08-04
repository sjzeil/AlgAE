/**
 * snapshotDiff.cpp
 *
 *
 *  Created on: July 4, 2012
 *      Author: zeil
 */


#include <algorithm>
#include <iostream>
#include <iterator>
#include <list>
#include <map>
#include <set>
#include <string>

#include <algae/snapshot/snapshotDiff.h>
#include <algae/communications/xmlOutput.h>

using namespace std;

namespace algae {

/**
 * Describes the differences between two snapshot and allows reconstruction
 * of a snapshot given a second snapshot and the difference between them.
 * 
 * @author zeil
 *
 */

void SnapshotDiff::computeDifference(const Snapshot& base, const Snapshot& derived)
{
	//cerr << "derived: " << derived << endl;
	removedEntities.clear();
	changedEntities.clear();
	newGlobals.clear();
	newNonGlobals.clear();
	map<EntityIdentifier, Entity> baseEntities;
	for (Snapshot::const_iterator bi = base.begin(); bi != base.end(); ++bi)
	{
		const Entity& e = *bi;
		const EntityIdentifier& eid = e.getEntityIdentifier();
		baseEntities[eid] = e;
		//cerr << "base[" << eid << "]=" << e << endl;
		if (base.isGlobal(eid) && !derived.isGlobal(eid)) {
			newNonGlobals.push_back(eid);
		}
	}

	for (Snapshot::const_iterator di = derived.begin(); di != derived.end(); ++di)
	{
		const Entity& e = *di;
		const EntityIdentifier& eid = e.getEntityIdentifier();
		map<EntityIdentifier, Entity>::iterator oldEntity = baseEntities.find(eid);
		if (oldEntity == baseEntities.end())
		{
			// This entity does not appear in the base
			changedEntities.push_back(e);
			//cerr << eid << " was not in the base." << endl;
		}
		else
		{
			//cerr << eid << " was in the base." << endl;
			if (!(e == oldEntity->second))
			{
				// This entity has been changed
				changedEntities.push_back(e);
				//cerr << "  " << eid << " has changed." << endl;
			}
			baseEntities.erase(oldEntity);
		}
		if (!base.isGlobal(eid) && derived.isGlobal(eid)) {
			newGlobals.push_back(eid);
		}
	}

	for (map<EntityIdentifier, Entity>::const_iterator bi = baseEntities.begin(); bi != baseEntities.end(); ++bi)
		{
			const Entity& e = bi->second;
			const EntityIdentifier& eid = e.getEntityIdentifier();
			//cerr << eid << " is not in the derived snapshot" << endl;
			removedEntities.push_back (eid);
		}
	descriptor = derived.getDescriptor();
	breakpointLocation = derived.getBreakpointLocation();
	activationStack = derived.getActivationStack();
}


struct addEIDsTo {
	set<EntityIdentifier>& aSet;

	addEIDsTo (set<EntityIdentifier>& theSet): aSet(theSet) {}

	void operator() (const EntityIdentifier& eid) {aSet.insert(eid);}
};

struct removeEIDsFrom {
	set<EntityIdentifier>& aSet;

	removeEIDsFrom (set<EntityIdentifier>& theSet): aSet(theSet) {}

	void operator() (const EntityIdentifier& eid) {aSet.erase(eid);}
};



/**
 * Reconstruct a derived snapshot from a base value and this diff
 *
 * @param fromBase the starting value
 * @return reconstructed derived snapshot
 */
Snapshot SnapshotDiff::reconstruct (const Snapshot& fromBase) const
{
	Snapshot derived;
	set<EntityIdentifier> removedEntitiesSet (removedEntities.begin(), removedEntities.end());
    //copy (removedEntities.begin(), removedEntities.end(), ostream_iterator<EntityIdentifier>(cerr, " || ")); cerr << endl;
    //copy (removedEntitiesSet.begin(), removedEntitiesSet.end(), ostream_iterator<EntityIdentifier>(cerr, " || ")); cerr << endl;
    set<EntityIdentifier>& dglobals = derived.getGlobals();
	for_each(fromBase.getGlobals().begin(), fromBase.getGlobals().end(), addEIDsTo(dglobals));
	for_each(newNonGlobals.begin(), newNonGlobals.end(), removeEIDsFrom(dglobals));
	for_each(newGlobals.begin(), newGlobals.end(), addEIDsTo(dglobals));

	for (Snapshot::const_iterator fi = fromBase.begin(); fi != fromBase.end(); ++fi)
	{
		const Entity& e = *fi;
		const EntityIdentifier& eid = e.getEntityIdentifier();
		if (removedEntitiesSet.count(eid) == 0) {
			//cerr << "Keeping " << eid << endl;
			derived.add (e);
		}
	}
	for (list<Entity>::const_iterator it = changedEntities.begin(); it != changedEntities.end(); ++it)
	{
		const Entity& e = *it;
		derived.add(e);
	}

	derived.setDescriptor(descriptor);
	derived.setBreakpointLocation(breakpointLocation);
	derived.setActivationStack(activationStack);

	return derived;
}

bool SnapshotDiff::operator== (const SnapshotDiff& d) const
{
	return descriptor == d.descriptor
			&& breakpointLocation == d.breakpointLocation
			&& activationStack == d.activationStack
			&& removedEntities ==d.removedEntities
			&& changedEntities == d.changedEntities
			&& newGlobals == d.newGlobals
			&& newNonGlobals == d.newNonGlobals;
}


void SnapshotDiff::printXML(std::ostream& out) const
{
	ObjectTag diff (out, XMLTag::snapshotPackage + ".SnapshotDiff");
	{
		PropertyTag pt (out, "activationStack");
		activationStack.printXML(out);
	}
	{
		PropertyTag pt (out, "breakpointLocation");
		{
			PropertyTag pt2 (out, "fileName");
			StringValue sv (out, breakpointLocation.getFileName());
		}
		{
			PropertyTag pt2 (out, "lineNumber");
			IntValue iv (out, breakpointLocation.getLineNumber());
		}
	}
	{
		PropertyTag pt (out, "descriptor");
		StringValue sv (out, descriptor);
	}
	if  (changedEntities.size() > 0)
	{
		PropertyTag pt (out, "changedEntities");
		for (list<Entity>::const_iterator it = changedEntities.begin(); it != changedEntities.end(); ++it)
		{
			MethodTag mt (out, "add");
			it->printXML(out);
		}
	}
	if  (removedEntities.size() > 0)
	{
		PropertyTag pt (out, "removedEntities");
		for (list<EntityIdentifier>::const_iterator it = removedEntities.begin(); it != removedEntities.end(); ++it)
		{
			MethodTag mt (out, "add");
			it->printXML(out);
		}
	}
	if  (newGlobals.size() > 0)
	{
		PropertyTag pt (out, "newGlobals");
		for (list<EntityIdentifier>::const_iterator it = newGlobals.begin(); it != newGlobals.end(); ++it)
		{
			MethodTag mt (out, "add");
			it->printXML(out);
		}
	}
	if  (newNonGlobals.size() > 0)
	{
		PropertyTag pt (out, "newNonGlobals");
		for (list<EntityIdentifier>::const_iterator it = newNonGlobals.begin(); it != newNonGlobals.end(); ++it)
		{
			MethodTag mt (out, "add");
			it->printXML(out);
		}
	}
}

void SnapshotDiff::print(std::ostream& out) const
{
	out << descriptor;
	out << "@" << breakpointLocation;
	out << ": " << activationStack << "\n";
	out << "removed: ";
	copy (removedEntities.begin(), removedEntities.end(), ostream_iterator<EntityIdentifier>(out, " "));
	out << "\n";
	out << "changed: ";
	copy (changedEntities.begin(), changedEntities.end(), ostream_iterator<Entity>(out, " "));
	out << "\n";
	out << "new globals: ";
	copy (newGlobals.begin(), newGlobals.end(), ostream_iterator<EntityIdentifier>(out, " "));
	out << "\n";
	out << "old globals: ";
	copy (newNonGlobals.begin(), newNonGlobals.end(), ostream_iterator<EntityIdentifier>(out, " "));
	out << "\n";
}


}

