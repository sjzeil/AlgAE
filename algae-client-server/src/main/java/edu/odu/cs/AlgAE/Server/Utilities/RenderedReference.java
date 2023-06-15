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
 * A labeled pointer to an object.
 *
 * @author zeil
 *
 */
public class RenderedReference<T> implements CanBeRendered<RenderedReference<T>>, Renderer<RenderedReference<T>> {

    private Color color;
    private double maxAngle;
    private double minAngle;
    private T refersTo;
    
    public RenderedReference(T refersTo) {
        this.refersTo = refersTo;
        color = Color.gray.brighter();
    }

    public RenderedReference(T refersTo, double minAngle, double maxAngle) {
        this.refersTo = refersTo;
        color = Color.gray.brighter();
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
    }
    
    public void set (T newRefersTo)
    {
        refersTo = newRefersTo;
    }
    
    public T get()
    {
        return refersTo;
    }
    

    @Override
    public Renderer<RenderedReference<T>> getRenderer() {
        return this;
    }

    /**
     * What string will be used as the value of this object?
     *     
     * @param obj: object to be drawn
     * @return a string or null to yield to other renderers
     */
    public String getValue(RenderedReference<T> obj)
    {
        return "";
    }
    
    /**
     * What color will be used to draw this object?
     *     
     * @param obj: object to be drawn
     * @return a color or null to yield to other renderers
     */
    public Color getColor(RenderedReference<T> obj)
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
    public List<Component> getComponents(RenderedReference<T> obj)
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
    public List<Connection> getConnections(RenderedReference<T> obj)
    {
        LinkedList<Connection> conn = new LinkedList<Connection>();
        Connection c = new Connection(refersTo, minAngle, maxAngle);
        c.setColor(Color.darkGray);
        conn.add(c);
        return conn;
    }
    

    

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setMaxAngle(double maxAngle) {
        this.maxAngle = maxAngle;
    }

    public double getMaxAngle() {
        return maxAngle;
    }

    public void setMinAngle(double minAngle) {
        this.minAngle = minAngle;
    }

    public double getMinAngle() {
        return minAngle;
    }

    @Override
    public Directions getDirection() {
        return Directions.Vertical;
    }

    @Override
    public Double getSpacing() {
        return Renderer.DefaultSpacing;
    }

    @Override
    public Boolean getClosedOnConnections() {
        return false;
    }

}
