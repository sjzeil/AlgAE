package edu.odu.cs.AlgAE.Client.DataViewer.Frames;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

/**
 * A DataShape is a primitive shape handled by the lowest-level
 * animation code.
 *
 * DataShapes are generally colored boxes, arrows, and text.
 *
 *
 * @author zeil
 *
 */
public interface DataShape extends Shape {
    
    /**
     * Depth controls how overlapping objects are drawn. Lesser depth objects
     * can entirely or partially obscure higher depth objects.
     *
     * @return
     */
    public int getDepth();
    
    /**
     * Color used to render this object.
     *
     * @return
     */
    public Color getColor();
    
    /**
     * Draw this object on indicated graphics context.
     *
     * @param g
     */
    public void draw (Graphics2D g);
    
    /**
     * Each DataShape within a given picture should have a unique ID.
     *
     * Datashapes with matching IDs in different pictures are assumed to be
     * different renderings of the same conceptual object. During animation,
     * such shapes can be tweened to provide intermediate animation frames.
     * @return
     */
    public String getID();
    
    /**
     * Create a new DataShape whose rendering falls between this one and an other.
     * If blend == 0, the new DataShape should be identical to this one. If blend==1.0,
     * the new shape should be idnetical to the other. For intermediate values of blend, the position,
     * size, color, and other visual properties should be interpolated between those two extremes.
     *
     * If other==null, we are looking at an object that is either just being created or is being destroyed.
     * An appropriate rendering of positon/color/etc. should be chosen to illustrate that fact.
     *
     * @param other another datashape representing an alternate view of the same object.
     * @param blend : blending control factor, 0.0 <= blend <= 1.0
     * @return
     */
    public DataShape tween (DataShape other, float blend);

}
