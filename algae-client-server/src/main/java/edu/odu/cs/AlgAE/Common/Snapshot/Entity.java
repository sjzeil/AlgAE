package edu.odu.cs.AlgAE.Common.Snapshot;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import edu.odu.cs.AlgAE.Server.MemoryModel.Identifier;

public class Entity implements Serializable {

    /**
     * Indicates the direction in which components are laid out.
     * 
     * "Square" describes a packing in which elements are packed into
     * rows and columns to fill a nearly square area.
     */
    public enum Directions {Vertical, Horizontal, Square, HorizontalTree, VerticalTree};

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
    private Directions direction;
    private double spacing;
    private boolean closedOnConnections;
    private EntityIdentifier container;


    
    /**
     * Create an entity representing a named "standalone" object
     * that is not a component of some larger entity
     * @param id    object to be represented by this new entity
     * @param label a descriptive name for this entity
     */
    public Entity (Identifier id, String label) {
        components = new LinkedList<EntityIdentifier>();
        connections = new LinkedList<Connector>();
        this.entityIdentifier = id.asEntityIdentifier();
        this.label = label;
        value = "";
        color = Color.lightGray;
        direction = Directions.Vertical;
        spacing = 1;
        closedOnConnections = false;
        container = null;
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
        this.entityIdentifier = id.asEntityIdentifier();
        label = componentLabel;
        value = "";
        color = Color.lightGray;
        direction = Directions.Vertical;
        spacing = 1;
        closedOnConnections = false;
        this.container = container;
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

    public EntityIdentifier getContainer() {
        return container;
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
                    && e.direction == direction
                    && e.spacing == spacing
                    && e.closedOnConnections == closedOnConnections
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
     * @param color the color to set
     */
    public void setColor(java.awt.Color color) {
        this.color = new Color(color);
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
    
    /**
     * Indicates how components will be packed into the box representing
     * this entity.
     * @return the direction
     */
    public Directions getDirection() {
        return direction;
    }

    /**
     * Indicates how components will be packed into the box representing
     * this entity.
     * @param direction the direction to set
     */
    public void setDirection(Directions direction) {
        this.direction = direction;
    }

    /**
     * The spacing between adjacent components.
     * 
     * @return the spacing
     */
    public double getSpacing() {
        return spacing;
    }

    /**
     * The spacing between adjacent components.
     * 
     * @param spacing the spacing to set
     */
    public void setSpacing(double spacing) {
        this.spacing = spacing;
    }

    /**
     * Will all entities accessible via connections from the
     * components of this entity be treated as components?
     * When true, 
     * @return the closedOnConnections
     */
    public boolean isClosedOnConnections() {
        return closedOnConnections;
    }

    /**
     * @param closedOnConnections the closedOnConnections to set
     */
    public void setClosedOnConnections(boolean closedOnConnections) {
        this.closedOnConnections = closedOnConnections;
    }
        

}
