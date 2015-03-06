package edu.odu.cs.AlgAE.Common.Snapshot;

import java.awt.Color;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Entity implements Serializable {
	/**
	 * The objects immediately contained within this one
	 */
	private List<EntityIdentifier> components;
	
	/**
	 * The objects that this one points to
	 */
	private List<Connector> connections;

	

	private EntityIdentifier entityIdentifier;
	private String label;
	private String value;
	private Color color;
	private int maxComponentsPerRow;
	

	/**
	 * Create an entity representing a "standalone" object
	 * that is not a component of some larger entity
	 * @param id    object to be represented by this new entity
	 */
	public Entity (Identifier id) {
		components = new LinkedList<EntityIdentifier>();
		connections = new LinkedList<Connector>();
		this.entityIdentifier = new EntityIdentifier(id);
		label = "";
		value = "";
		color = Color.lightGray;
		maxComponentsPerRow = 1;
	}
	
	/**
	 * Create an entity representing a named "standalone" object
	 * that is not a component of some larger entity
	 * @param id    object to be represented by this new entity
	 * @param label a descriptive name for this entity
	 */
	public Entity (Identifier id, String label) {
		components = new LinkedList<EntityIdentifier>();
		connections = new LinkedList<Connector>();
		this.entityIdentifier = new EntityIdentifier(id,label);
		this.label = label;
		value = "";
		color = Color.lightGray;
		maxComponentsPerRow = 1;
	}

	
	/**
	 * Create an entity representing an object that is a component
	 * of a larger entity.
	 *
	 * @param id   object to be represented by this new entity
	 * @param container  entity that is considered to contain this one as a component
	 * @param componentLabel  a string differentiating this component from all others of the same container
	 */
	public Entity (Identifier id, Entity container, String componentLabel) {
		components = new LinkedList<EntityIdentifier>();
		connections = new LinkedList<Connector>();
		this.entityIdentifier = new EntityIdentifier(id, container.getEntityIdentifier(), componentLabel);
		label = componentLabel;
		value = "";
		color = Color.lightGray;
		maxComponentsPerRow = 1;
	}
	
	
	/**
	 * Create an entity representing an object that is a component
	 * of a larger entity.
	 *
	 * @param id   object to be represented by this new entity
	 * @param container  entity that is considered to contain this one as a component
	 * @param componentLabel  a string differentiating this component from all others of the same container
	 */
	public Entity (Identifier id, EntityIdentifier container, String componentLabel) {
		components = new LinkedList<EntityIdentifier>();
		connections = new LinkedList<Connector>();
		this.entityIdentifier = new EntityIdentifier(id, container, componentLabel);
		label = componentLabel;
		value = "";
		color = Color.lightGray;
		maxComponentsPerRow = 1;
	}
	

	/**
	 * Create an entity that can be rebuilt via XML decoding
	 * @param entityIdentifier    object to be represented by this new entity
	 */
	public Entity () {
		components = new LinkedList<EntityIdentifier>();
		connections = new LinkedList<Connector>();
		this.entityIdentifier = new EntityIdentifier();
		label = "";
		value = "";
		color = null;
		maxComponentsPerRow = 1;
	}

	
	public String getDescription()
	{
		StringBuffer result = new StringBuffer();
		if (label != null && label.length() > 0 && label.charAt(0) >= ' ') {
			result.append (label);
			result.append (": ");
		}
		if (value != null && value.length() > 0) {
			result.append(value);
		}
		return result.toString();
	}

	public Identifier getObjectIdentifier() {
		return entityIdentifier.getObjectIdentifier();
	}

	public EntityIdentifier getContainer() {
		return entityIdentifier.getContainer();
	}

		
	public String toString() {
		StringBuffer result = new StringBuffer();
		
		result.append(entityIdentifier);
		result.append(": ");
		
		String description = value;
		if (label != null && label.length() > 0) {
			description = label + ": " + value;
		}
		result.append (description);
		
		result.append ("{eid:" + entityIdentifier + ", label:" + label + ", value:" + value + ", #comp:" + components.size() + ", #conn:" + connections.size() + "}");
		return result.toString();
	}
	
	
	public boolean equals (Object o)
	{
		if (o == null)
			return false;
		try {
			Entity e = (Entity)o;
			return e.entityIdentifier.equals(entityIdentifier)
					&& e.label.equals(label)
					&& e.value.equals(value)
					&& e.color.equals(color)
					&& e.maxComponentsPerRow == maxComponentsPerRow
					&& e.components.equals(components)
					&& e.connections.equals(connections);
			
		} catch (Exception e) {
			return false;
		}
	}
	

	/**
	 * @return the entityIdentifier
	 */
	public EntityIdentifier getEntityIdentifier() {
		return entityIdentifier;
	}

	/**
	 * @param entityIdentifier the entityIdentifier to set
	 */
	public void setEntityIdentifier(EntityIdentifier entityIdentifier) {
		this.entityIdentifier = entityIdentifier;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the maxComponentsPerRow
	 */
	public int getMaxComponentsPerRow() {
		return maxComponentsPerRow;
	}

	/**
	 * @param maxComponentsPerRow the maxComponentsPerRow to set
	 */
	public void setMaxComponentsPerRow(int maxComponentsPerRow) {
		this.maxComponentsPerRow = maxComponentsPerRow;
	}

	/**
	 * @return the components
	 */
	public List<EntityIdentifier> getComponents() {
		return components;
	}

	/**
	 * @param components the components to set
	 */
	public void setComponents(List<EntityIdentifier> components) {
		this.components = components;
	}

	/**
	 * @return the connections
	 */
	public List<Connector> getConnections() {
		return connections;
	}

	/**
	 * @param connections the connections to set
	 */
	public void setConnections(List<Connector> connections) {
		this.connections = connections;
	}
	
	
	

}
