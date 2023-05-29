package edu.odu.cs.AlgAE.Server.Rendering;

import java.awt.Color;
import java.util.List;

import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;
import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;

/**
 * Determines how a given object or class of objects should be drawn.
 *
 * In determining the rendering of any object, a number of different renderers
 * may be consulted. These renderers are arranged by precedence, and higher-precedence
 * are consulted first.
 *
 * Each of the functions below may return a value (usually a null pointer) indicating that
 * it wishes to "yield" to lower-precedence renderers.  For example, one could change the
 * color of a drawn object by overriding the getColor function only, and allowing each of
 * the other functions to yield.
 *
 * @author zeil
 *
 */
public interface Renderer<T> {

    public final double DefaultSpacing = 0.25;
    
    /**
     * What string will be used as the value of this object?
     *     
     * @param obj: object to be drawn
     * @return a string or null to yield to other renderers
     */
    public String getValue(T obj);
    
    /**
     * What color will be used to draw this object?
     *     
     * @param obj: object to be drawn
     * @return a color or null to yield to other renderers
     */
    public Color getColor(T obj);
    
    /**
     * Get a list of other objects to be drawn inside the
     * box portraying this one.
     *     
     * @param obj: object to be drawn
     * @return an array of contained objects or null to yield to other renderers
     */
    public List<Component> getComponents(T obj);
    
    /**
     * Get a list of other objects to which we will draw
     * pointers from this one.
     *     
     * @param obj: object to be drawn
     *
     * @return an array of referenced objects or null to yield to other renderers
     */
    public List<Connection> getConnections(T obj);
    

    /**
     * Indicate how components will be layed out.
     * @return direction in which components wil be placed
     */
    public Directions getDirection();

    /**
     * How much space should be placed between components?
     * @return amount of space
     */
    public Double getSpacing();

    /**
     * Should connected objects be layed out next to this one?
     * @return true to include connected objects in the layout of this one.
     */
    public Boolean getClosedOnConnections();
    
}
