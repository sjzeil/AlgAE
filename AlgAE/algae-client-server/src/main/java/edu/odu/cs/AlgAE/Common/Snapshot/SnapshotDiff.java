package edu.odu.cs.AlgAE.Common.Snapshot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Describes the differences between two snapshot and allows reconstruction
 * of a snapshot given a second snapshot and the difference between them.
 *
 * @author zeil
 *
 */
public class SnapshotDiff {

	/**
	 * All entities appearing in the base snapshot but not in the later derived one.
	 */
	private List<EntityIdentifier> removedEntities;
	
	/**
	 * All entities that are added to the base or changed from their base values in the
	 * derived snapshot.
	 */
	private List<Entity> changedEntities;

	/**
	 * All entities not global in the base snapshot but global in the new one.
	 */
	private List<EntityIdentifier> newGlobals;

	/**
	 * All entities global in the base snapshot but not global in the new one.
	 */
	private List<EntityIdentifier> newNonGlobals;

	
	/**
	 * An informational string to appear in a status line when the new snapshot is being displayed
	 */
	private String descriptor;
	
	/**
	 * The corresponding source code location to be shown while the new snapshot is being displayed.
	 */
	private SourceLocation breakpointLocation;
	
    /**
     * Entity representing the activation stack	in the new snapshot
     */
	private EntityIdentifier activationStack;

	
	/**
	 * The usual way to construct a diff - as the differences required to go from base to derived.
	 * If either parameter is null, it is treated as equivalent to Snapshot()
	 *
	 * @param base  the starting snapshot
	 * @param derived a snapshot derived from that base by one or more changes
	 */
	public SnapshotDiff (Snapshot base, Snapshot derived)
	{
		if (base == null)
			base = new Snapshot();
		if (derived == null)
			derived = new Snapshot();
		computeDifference (base, derived);
	}
	
	
	/**
	 * Equivalent to SnapshotDiff(null, null)
	 * - used mainly for XML conversion
	 *
	 */
	public SnapshotDiff ()
	{
		removedEntities = new LinkedList<EntityIdentifier>();
		changedEntities = new LinkedList<Entity>();
		newGlobals = new LinkedList<EntityIdentifier>();
		newNonGlobals = new LinkedList<EntityIdentifier>();
		descriptor = "";
		breakpointLocation = new SourceLocation();
		activationStack = null;
	}
	
	private void computeDifference(Snapshot base, Snapshot derived) {
		removedEntities = new LinkedList<EntityIdentifier>();
		changedEntities = new LinkedList<Entity>();
		newGlobals = new LinkedList<EntityIdentifier>();
		newNonGlobals = new LinkedList<EntityIdentifier>();
		HashMap<EntityIdentifier, Entity> baseEntities = new HashMap<EntityIdentifier, Entity>();
		for (Entity e: base) {
			EntityIdentifier eid = e.getEntityIdentifier();
			baseEntities.put (eid, e);
			if (base.isGlobal(eid) && !derived.isGlobal(eid)) {
				newNonGlobals.add(eid);
			}
		}

		for (Entity e: derived) {
			EntityIdentifier eid = e.getEntityIdentifier();
			Entity oldEntity = baseEntities.get(eid);
			if (oldEntity == null) {
				// This entity does not appear in the base
				changedEntities.add(e);
			} else {
				if (!e.equals(oldEntity)) {
					// This entity has been changed
					changedEntities.add(e);
				}
				baseEntities.remove(eid);
			}
			if (!base.isGlobal(eid) && derived.isGlobal(eid)) {
				newGlobals.add(eid);
			}	
		}
		
		for (EntityIdentifier eid: baseEntities.keySet()) {
			removedEntities.add (eid);
		}
		descriptor = derived.getDescriptor();
		breakpointLocation = derived.getBreakpointLocation();
		activationStack = derived.getActivationStack();
	}


	/**
	 * Reconstruct a derived snapshot from a base value and this diff
	 *
	 * @param fromBase the starting value
	 * @return reconstructed derived snapshot
	 */
	public Snapshot reconstruct (Snapshot fromBase)
	{
		Snapshot derived = new Snapshot();
		HashSet<EntityIdentifier> removedEntitiesSet = new HashSet<EntityIdentifier>(removedEntities);
		Set<EntityIdentifier> dglobals = derived.getGlobals();
		dglobals.addAll(fromBase.getGlobals());
		dglobals.removeAll(newNonGlobals);
		dglobals.addAll(newGlobals);
		
		for (Entity e: fromBase) {
			EntityIdentifier eid = e.getEntityIdentifier();
			if (!removedEntitiesSet.contains(eid)) {
				derived.add (e);
			}
		}
		for (Entity e: changedEntities) {
			derived.add (e);
		}
		
		derived.setDescriptor(descriptor);
		derived.setBreakpointLocation(breakpointLocation);
		derived.setActivationStack(activationStack);
		
		return derived;
	}
	
	public boolean equals (Object o)
	{
		if (o == null)
			return false;
		try {
			SnapshotDiff d = (SnapshotDiff)o;
			return descriptor.equals(d.descriptor) && breakpointLocation.equals(d.breakpointLocation) &&
					(activationStack == d.activationStack || (activationStack != null && activationStack.equals(d.activationStack)))
					&& removedEntities.equals(d.removedEntities)
					&& changedEntities.equals(d.changedEntities)
					&& newGlobals.equals(d.newGlobals)
					&& newNonGlobals.equals(d.newNonGlobals);
		} catch (Exception e) {
			return false;
		}
	}
	
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append (descriptor);
		buf.append ("@");
		buf.append (breakpointLocation);
		buf.append (": ");
		buf.append (activationStack);
		buf.append ("\n");
		buf.append ("removed: ");
		buf.append (removedEntities.toString());
		buf.append ("\n");
		buf.append ("changed: ");
		buf.append (changedEntities.toString());
		buf.append ("\n");
		buf.append ("new globals: ");
		buf.append (newGlobals.toString());
		buf.append ("\n");
		buf.append ("old globals: ");
		buf.append (newNonGlobals.toString());
		buf.append ("\n");

		return buf.toString();
	}


	/**
	 * @return the removedEntities
	 */
	public List<EntityIdentifier> getRemovedEntities() {
		return removedEntities;
	}


	/**
	 * @param removedEntities the removedEntities to set
	 */
	public void setRemovedEntities(List<EntityIdentifier> removedEntities) {
		this.removedEntities = removedEntities;
	}


	/**
	 * @return the changedEntities
	 */
	public List<Entity> getChangedEntities() {
		return changedEntities;
	}


	/**
	 * @param changedEntities the changedEntities to set
	 */
	public void setChangedEntities(List<Entity> changedEntities) {
		this.changedEntities = changedEntities;
	}


	/**
	 * @return the newGlobals
	 */
	public List<EntityIdentifier> getNewGlobals() {
		return newGlobals;
	}


	/**
	 * @param newGlobals the newGlobals to set
	 */
	public void setNewGlobals(List<EntityIdentifier> newGlobals) {
		this.newGlobals = newGlobals;
	}


	/**
	 * @return the newNonGlobals
	 */
	public List<EntityIdentifier> getNewNonGlobals() {
		return newNonGlobals;
	}


	/**
	 * @param newNonGlobals the newNonGlobals to set
	 */
	public void setNewNonGlobals(List<EntityIdentifier> newNonGlobals) {
		this.newNonGlobals = newNonGlobals;
	}


	/**
	 * @return the descriptor
	 */
	public String getDescriptor() {
		return descriptor;
	}


	/**
	 * @param descriptor the descriptor to set
	 */
	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}


	/**
	 * @return the breakpointLocation
	 */
	public SourceLocation getBreakpointLocation() {
		return breakpointLocation;
	}


	/**
	 * @param breakpointLocation the breakpointLocation to set
	 */
	public void setBreakpointLocation(SourceLocation breakpointLocation) {
		this.breakpointLocation = breakpointLocation;
	}


	/**
	 * @return the activationStack
	 */
	public EntityIdentifier getActivationStack() {
		return activationStack;
	}


	/**
	 * @param activationStack the activationStack to set
	 */
	public void setActivationStack(EntityIdentifier activationStack) {
		this.activationStack = activationStack;
	}
	
	
}
