/**
 *
 */
package edu.odu.cs.AlgAE.Client.Layout.Coordinates;

import java.awt.geom.Point2D;

import edu.odu.cs.AlgAE.Client.Layout.Optimization.Variable;

/**
 * A location is a description of a point in the space used to
 * portray objects.  The most obvious manifestation of a Location
 * would be a simple (x,y) Cartesian coordinate. However it is often
 * more convenient to specify points relative to the locations of
 * other objects or, for points on the perimeter of an object, as
 * an angle.
 *
 * Locations also are compatible with the concept of Variables in the
 * AlgAE.Optimization package, allowing the layout engine to derive
 * locations by putting up an optimization problem.
 *
 * @author zeil
 *
 */
public interface Location extends Cloneable, FreeOrFixed {
    
    /**
     * Evaluate the location to get a cartesian coordinate.
     *
     */
    public Point2D getCoordinates();
    
    /**
     * Quantities that can be manipulated to change a location.
     * @return a list of 0-2 variables
     */
    public Variable[] getVariables();
    
    public Object clone();
    
    /**
     * Determine if this location has been fixed or is somehow relative
     * to some other location that has yet to be determined.
     */
    //public boolean isFixed();

}
