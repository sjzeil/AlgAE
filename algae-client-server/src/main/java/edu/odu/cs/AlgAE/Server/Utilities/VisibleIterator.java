package edu.odu.cs.AlgAE.Server.Utilities;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;
import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;

/**
 * A wrapper for iterators allowing them to be rendered.
 *
 * @author zeil
 *
 */
public class VisibleIterator<T> implements Iterator<T>, CanBeRendered<VisibleIterator<T>>, Renderer<VisibleIterator<T>> {

    private Color color;
    private double maxAngle;
    private double minAngle;
    
    private Iterable<T> indexesInto;
    private Iterator<T> index;
    private int count;
    

    public VisibleIterator(Iterator<T> value, Iterable<T> indexesInto) {
        this.index = value;
        count = 0;
        this.indexesInto = indexesInto;
        color = Color.blue.darker();
        minAngle = 0.0;
        maxAngle = 360.0;
    }



    @Override
    public boolean hasNext() {
        return index.hasNext();
    }

    @Override
    public T next() {
        ++count;
        return index.next();
    }

    @Override
    public void remove() {
        index.remove();
    }

    
    @Override
    public Renderer<VisibleIterator<T>> getRenderer() {
        return this;
    }

    /**
     * What string will be used as the value of this object?
     *     
     * @param obj: object to be drawn
     * @return a string or null to yield to other renderers
     */
    public String getValue(VisibleIterator<T> obj)
    {
        return "";
    }
    
    /**
     * What color will be used to draw this object?
     *     
     * @param obj: object to be drawn
     * @return a color or null to yield to other renderers
     */
    public Color getColor(VisibleIterator<T> obj)
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
    public List<Component> getComponents(VisibleIterator<T> obj)
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
    public List<Connection> getConnections(VisibleIterator<T> obj)
    {
        Connection conn1 = (indexesInto != null) ? connectTo(indexesInto, count) : null;
        
        LinkedList<Connection> conn = new LinkedList<Connection>();
        if (conn1 != null) {
            conn.add(conn1);
        }
        return conn;
    }
    

    private Connection connectTo(Iterable<T> indexesInto, int v) {
        Iterable<T> target = null;
        int index = v;
        target = indexesInto;

        if (target == null)
            return null;
        Connection conn = new Connection(target, minAngle, maxAngle);
        conn.setColor(color.brighter());
        conn.setComponentIndex(index);
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
