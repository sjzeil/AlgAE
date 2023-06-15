package edu.odu.cs.AlgAE.Common.Snapshot;


public class Connector {
    private EntityIdentifier source;
    private EntityIdentifier destination;
    private double minAngle;
    private double maxAngle;
    private String value;
    private String label;
    private Color color;
    private String id;
    private int componentIndex;
    
    

    public Connector (String id, EntityIdentifier source, EntityIdentifier destination, double minAngle, double maxAngle)
    {
        this.source = source;
        this.destination = destination;
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
        this.id = id;
        color = new Color(java.awt.Color.black);
        value = "";
        label = "";
        componentIndex = -1;
    }

    public Connector (String id, EntityIdentifier source, EntityIdentifier destination, double minAngle, double maxAngle, int component)
    {
        this.source = source;
        this.destination = destination;
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
        this.id = id;
        color = new Color(java.awt.Color.black);
        value = "";
        label = "";
        componentIndex = component;
    }


    public Connector ()
    {
        this.source = null;
        this.destination = null;
        this.minAngle = 0;
        this.maxAngle = 360;
        this.id = null;
        color = null;
        value = "";
        label = "";
        componentIndex = -1;
    }

    
    
    
    public String toString()
    {
        return source + "=>" + ((destination != null) ? destination : "0");
    }
    
    public boolean equals (Object o) {
        if (o == null)
            return false;
        try {
            Connector c = (Connector)o;
            return c.source.equals(source) && c.destination.equals(destination)
                    && c.minAngle == minAngle && c.maxAngle == maxAngle
                    && c.id.equals(id) 
                    && c.color.equals(color)
                    && c.value.equals(value) && c.label.equals(label)
                    && c.componentIndex == componentIndex;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return the source
     */
    public EntityIdentifier getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(EntityIdentifier source) {
        this.source = source;
    }

    /**
     * @return the destination
     */
    public EntityIdentifier getDestination() {
        return destination;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(EntityIdentifier destination) {
        this.destination = destination;
    }

    /**
     * @return the minAngle
     */
    public double getMinAngle() {
        return minAngle;
    }

    /**
     * @param minAngle the minAngle to set
     */
    public void setMinAngle(double minAngle) {
        this.minAngle = minAngle;
    }

    /**
     * @return the maxAngle
     */
    public double getMaxAngle() {
        return maxAngle;
    }

    /**
     * @param maxAngle the maxAngle to set
     */
    public void setMaxAngle(double maxAngle) {
        this.maxAngle = maxAngle;
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
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the componentIndex
     */
    public int getComponentIndex() {
        return componentIndex;
    }

    /**
     * @param componentIndex the componentIndex to set
     */
    public void setComponentIndex(int componentIndex) {
        this.componentIndex = componentIndex;
    }


}
