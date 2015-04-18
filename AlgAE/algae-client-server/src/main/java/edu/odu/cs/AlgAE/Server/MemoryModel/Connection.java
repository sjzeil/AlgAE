package edu.odu.cs.AlgAE.Server.MemoryModel;

import java.awt.Color;

public class Connection {

    private Object destination;
    private Color color;
    private String refID;
    private double elasticity;
    private String label;
    private String value;
    private double minAngle;
    private double maxAngle;
    private static final double DefaultLength = 3.0;
    private double preferredLength;
    private int componentIndex;
    
    
    public Connection (Object dest, Color c, double minA, double maxA) {
        destination = dest;
        color = c;
        refID = "";
        elasticity = 1.0;
        label = "";
        value = "";
        minAngle = minA;
        maxAngle = maxA;
        preferredLength = DefaultLength;
        componentIndex = -1;
    }
    
    public Connection (Object dest, double minA, double maxA) {
        destination = dest;
        color = Color.black;
        refID = "";
        elasticity = 1.0;
        label = "";
        value = "";
        minAngle = minA;
        maxAngle = maxA;
        preferredLength = DefaultLength;
        componentIndex = -1;
    }

    public Connection (Object dest) {
        destination = dest;
        color = Color.black;
        refID = "";
        elasticity = 1.0;
        label = "";
        value = "";
        minAngle = 0.0;
        maxAngle = 360.0;
        preferredLength = DefaultLength;
        componentIndex = -1;
    }

    public Connection (String id, Object dest, Color c, double minA, double maxA) {
        destination = dest;
        color = c;
        refID = id;
        elasticity = 1.0;
        label = "";
        value = "";
        minAngle = minA;
        maxAngle = maxA;
        preferredLength = DefaultLength;
        componentIndex = -1;
    }
    
    public Connection (String id, Object dest, double minA, double maxA) {
        destination = dest;
        color = Color.black;
        refID = id;
        elasticity = 1.0;
        label = "";
        value = "";
        minAngle = minA;
        maxAngle = maxA;
        preferredLength = DefaultLength;
        componentIndex = -1;
    }

    public Object getDestination() {
        return destination;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setID(String id) {
        refID = id;
    }

    public String getID() {
        return refID;
    }

    public void setElasticity(double elasticity) {
        this.elasticity = elasticity;
    }

    public double getElasticity() {
        return elasticity;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public String getLabel() {
        return label;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

    /**
     * @param minAngle the minAngle to set
     */
    public void setMinAngle(double minAngle) {
        this.minAngle = minAngle;
    }

    /**
     * @return the minAngle
     */
    public double getMinAngle() {
        return minAngle;
    }

    /**
     * @param maxAngle the maxAngle to set
     */
    public void setMaxAngle(double maxAngle) {
        this.maxAngle = maxAngle;
    }

    /**
     * @return the maxAngle
     */
    public double getMaxAngle() {
        return maxAngle;
    }

    /**
     * @param preferredLength the preferredLength to set
     */
    public void setPreferredLength(double preferredLength) {
        this.preferredLength = preferredLength;
    }

    /**
     * @return the preferredLength
     */
    public double getPreferredLength() {
        return preferredLength;
    }

    /**
     * Get an index for an internal component of the destination
     * that we wish to point to.
     */
    public int getComponentIndex() {
        return componentIndex;
    }

    /**
     * Set an index for an internal component of the destination
     * that we wish to point to.
     */
    public void setComponentIndex(int index) {
        componentIndex = index;;
    }

    
    
}
