package edu.odu.cs.AlgAE.Server.Utilities;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;
import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;

/**
 * A wrapper for ints.  Unlike Integer, this is mutable. More important,
 * there is no hidden sharing.
 *
 * @author zeil
 *
 */
public class DiscreteInteger implements CanBeRendered<DiscreteInteger>, Renderer<DiscreteInteger>, Comparable<DiscreteInteger> {

    private static Color color;

    private int value;

    
    
    public DiscreteInteger() {
        this.value = 0;
    }

    public DiscreteInteger(int value) {
        this.value = value;
    }

    public static void setRenderingColor (Color c)
    {
        color = c;
    }
    

    /**
     * Get the integer value stored in this index
     */
    public int get()
    {
        return value;
    }

    /**
     * Set the integer value stored in this index
     */
    public void set(int v)
    {
        value = v;
    }

    /**
     * Set the integer value stored in this index
     */
    public void set(DiscreteInteger v)
    {
        value = v.value;
    }
    
    public void incr() {
        ++value;
    }

    public void decr() {
        --value;
    }
    
    public void swap (DiscreteInteger other) {
        int temp = value;
        value = other.value;
        other.value = temp;
    }

    @Override
    public Renderer<DiscreteInteger> getRenderer() {
        return this;
    }

    /**
     * What string will be used as the value of this object?
     *     
     * @param obj: object to be drawn
     * @return a string or null to yield to other renderers
     */
    public String getValue(DiscreteInteger obj)
    {
        return "" + value;
    }
    
    /**
     * What color will be used to draw this object?
     *     
     * @param obj: object to be drawn
     * @return a color or null to yield to other renderers
     */
    public Color getColor(DiscreteInteger obj)
    {
        return color;
    }
    
    /**
     * Get a list of other objects to be drawn inside the
     * box portraying this one.
     *     
     * @param obj: object to be drawn
     * @return an array of contained objects or null to yield to other renderers
     */
    public List<Component> getComponents(DiscreteInteger obj)
    {
        return new LinkedList<Component>();
    }
    
    /**
     * Get a list of other objects to which we will draw
     * pointers from this one.
     *     
     * @param obj: object to be drawn
     * @return an array of referenced objects or null to yield to other renderers
     */
    public List<Connection> getConnections(DiscreteInteger obj)
    {
        return new LinkedList<Connection>();
    }
    


    


    public String toString()
    {
        return "" + value;
    }
    
    public int hashCode()
    {
        return value;
    }

    @Override
    public Directions getDirection() {
        return Directions.Horizontal;
    }

    @Override
    public Double getSpacing() {
        return Renderer.DefaultSpacing;
    }

    @Override
    public Boolean getClosedOnConnections() {
        return false;
    }

    @Override
    public int compareTo(DiscreteInteger i) {
        return value - i.value;
    }

}
