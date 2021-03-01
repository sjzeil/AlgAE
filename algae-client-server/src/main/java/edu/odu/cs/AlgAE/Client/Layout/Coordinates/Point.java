/**
 *
 */
package edu.odu.cs.AlgAE.Client.Layout.Coordinates;

import java.awt.geom.Point2D;
import java.util.IdentityHashMap;

import edu.odu.cs.AlgAE.Client.Layout.Optimization.Variable;


/**
 * An X,Y coordinate pair
 *
 * @author zeil
 *
 */
public class Point implements Location {
    
    Variable x;
    Variable y;
    
    public Point (double x, double y)
    {
        this.x = new Variable(x, 0.0, 1000.0, 1.0);
        this.y = new Variable(y, 0.0, 1000.0, 1.0);
    }

    public Point(Point2D p) {
        this.x = new Variable(p.getX(), 0.0, 1000.0, 1.0);
        this.y = new Variable(p.getY(), 0.0, 1000.0, 1.0);        
    }

    public double getX() {
        return x.getValue();
    }

    public double getY() {
        return y.getValue();
    }
    
    public Point2D getCoordinates()
    {
        return new Point2D.Double(x.getValue(),y.getValue());
    }

    
    public boolean equals(Object obj)
    {
        if (obj instanceof Location) {
            Location p = (Location)obj;
            return getCoordinates().equals(p.getCoordinates());
        } else
            return false;
    }
    
    public String toString()
    {
        return "(" + x.getValue() + "," + y.getValue() + ")";
    }

    public Variable[] getVariables() {
        Variable[] results = new Variable[2];
        results[0] = x;
        results[1] = y;
        return results;
    }
    
    public Object clone()
    {
        return new Point (x.getValue(), y.getValue());
    }

    @Override
    public boolean isFixed(IdentityHashMap<FreeOrFixed, Boolean> alreadyChecked) {
        return true;
    }

    
    
}
