package edu.odu.cs.AlgAE.Common.Snapshot;

import java.io.Serializable;

/**
 * Unique identifiers for each entity in the model.
 * 
 * One object may map onto several distinct entities to support the
 * illusion that a given object is simultaneously a component of
 * multiple compound objects (or comprises multiple distinct components
 * of a single parent: e.g., arrays of Strings or Integers may contain
 * many duplicate references to one object, but we still prefer to render
 * these as if they were distinc.)
 * 
 *  Consequently, the Entity identifier contains the object identifier
 *  but also encodes the parent container and a label or other component
 *  indicator.
 *  
 * @author zeil
 *
 */
		
public class EntityIdentifier implements Serializable {
	
	
	/**
	 * The objects immediately contained within this one
	 */
	
	private Identifier id;
	private EntityIdentifier container;
	private String componentLabel;
	

	/**
	 * For objects that are not components of larger objects
	 * 
	 * @param id  object identifier
	 */
	public EntityIdentifier (Identifier id) {
		this.id = id;
		container = null;
		componentLabel = "";
	}
	
	/**
	 * For objects that are not components of larger objects.
	 * 
	 * The label will be used for display purposes but is ignored
	 * when doing comparisons and hashing.
	 * 
	 * @param id object identifier
	 * @param label decorative label/name
	 */
	public EntityIdentifier (Identifier id, String label) {
		this.id = id;
		container = null;
		componentLabel = label;
		if (label == null)
			componentLabel = "";
	}

	
	/**
	 * Used for XML decoding only
	 * 
	 */
	public EntityIdentifier () {
		this.id = null;
		container = null;
		componentLabel = "";
	}

	
	/**
	 * For objects that are components of larger objects
	 * 
	 * @param id  object identifier
	 * @param container object that contains this one as a component
	 * @param label name that distinguishes this component from others of the same parent
	 *                      
	 */
	public EntityIdentifier (Identifier id, EntityIdentifier container, String label) {
		this.id = id;
		componentLabel = label;
		if (label == null)
			componentLabel = "";
		this.container = container;
	}
	
	/**
	 * The special ID value used for null pointers
	 * 
	 */
	public static EntityIdentifier nullID() {
		return new EntityIdentifier();
	}
	
	
	/**
	 * 	Return a string that differentiates this component from other components of 
	 *    the same container. May be null for objects that are not components of others.
	 *
	 */
	public String getComponentLabel() {
		return componentLabel;
	}
	
	public void setComponentLabel(String label) {
		componentLabel = label;
	}
	
	

	/**
	 * The identifier of the object denoted by this entity
	 * @return
	 */
	public Identifier getObjectIdentifier() {
		return id;
	}
	
	public void setObjectIdentifier(Identifier ident) {
		id = ident;
	}

	/**
	 * The identifier of the entity of which this one is a component
	 * 
	 * @return
	 */
	public EntityIdentifier getContainer() {
		return container;
	}

	public void setContainer(EntityIdentifier c) {
		container = c;
	}

	
	
	/**
	 * How many nested containers is this considered to be a component of?
	 * @return
	 */
	public int depth()
	{
		if (container == null)
			return 0;
		else
			return 1 + container.depth();
	}

		
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append(id.toString());
		if (componentLabel != null && componentLabel.length() > 0) {
			result.append("@");
			result.append (componentLabel);
		}
		if (container != null) {
			result.append(".in.");
			result.append (container.getObjectIdentifier().toString());
		}
		return result.toString();
	}
	

	public boolean equals (Object obj)
	{
		if (obj instanceof EntityIdentifier) {
			EntityIdentifier eid = (EntityIdentifier)obj;
			if (eid.id == null && id == null)
			    return true;
			else if (eid.id == null)
			    return id.isNull();
			else if (id == null)
			    return eid.id.isNull();
			else if (!eid.id.equals(id))
				return false;
			if (container == null) {
				if (eid.container != null)
					return false;
				if (componentLabel == null)
					return eid.componentLabel == null;
				return componentLabel.equals(eid.componentLabel);
			} else {
				if (eid.container == null)
					return false;
				return componentLabel.equals(eid.componentLabel)
				&& container.equals(eid.container);
			}
		} else
			return false;
	}
	
	
	public int hashCode ()
	{
		int result = 0;
		if (container == null) {
			result += id.hashCode();
			if (componentLabel != null)
				result += 3 * componentLabel.hashCode();
		} else {
			if (componentLabel != null)
				result += 3 * componentLabel.hashCode();
			result += 7 * container.hashCode();
		}
		return result;
	}
	

}
