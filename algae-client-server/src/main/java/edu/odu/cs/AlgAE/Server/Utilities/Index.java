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
 * An integer value that may index into an array or
 * array-like structure.
 *
 * @author zeil
 *
 */
public class Index implements CanBeRendered<Index>, Renderer<Index> {

    private Color color;
    private double maxAngle;
    private double minAngle;
    private Object indexesInto1;
    private Object indexesInto2;
    private int value;
    
    public Index(int value) {
        this.value = value;
        this.indexesInto1 = null;
        this.indexesInto2 = null;
        color = Color.green.darker();
        minAngle = 0.0;
        maxAngle = 360.0;
    }

    public Index(int value, Object indexesInto) {
        this.value = value;
        this.indexesInto1 = indexesInto;
        this.indexesInto2 = null;
        color = Color.green.darker();
        minAngle = 0.0;
        maxAngle = 360.0;
    }

    public Index(int value, Object indexesInto1, Object indexesInto2) {
        this.value = value;
        this.indexesInto1 = indexesInto1;
        this.indexesInto2 = indexesInto2;
        color = Color.green.darker();
        minAngle = 0.0;
        maxAngle = 360.0;
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

    
    @Override
    public Renderer<Index> getRenderer() {
        return this;
    }

    /**
     * What string will be used as the value of this object?
     *     
     * @param obj: object to be drawn
     * @return a string or null to yield to other renderers
     */
    public String getValue(Index obj)
    {
        return "" + value;
    }
    
    /**
     * What color will be used to draw this object?
     *     
     * @param obj: object to be drawn
     * @return a color or null to yield to other renderers
     */
    public Color getColor(Index obj)
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
    public List<Component> getComponents(Index obj)
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
    public List<Connection> getConnections(Index obj)
    {
        Connection conn1 = (indexesInto1 != null) ? connectTo(indexesInto1, value) : null;
        Connection conn2 = (indexesInto2 != null) ? connectTo(indexesInto2, value) : null;
        
        LinkedList<Connection> conn = new LinkedList<Connection>();
        if (conn1 != null) {
            conn.add(conn1);
        }
        if (conn2 != null) {
            conn.add(conn2);
        }
        return conn;
    }
    

    private Connection connectTo(Object indexesInto, int v) {
        Object target = null;
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
        return false;  // Should this inherit from container?
    }

}
