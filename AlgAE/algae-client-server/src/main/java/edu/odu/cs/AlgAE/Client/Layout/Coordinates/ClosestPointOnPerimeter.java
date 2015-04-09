/**
 *
 */
package edu.odu.cs.AlgAE.Client.Layout.Coordinates;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.odu.cs.AlgAE.Client.Layout.Optimization.Variable;



/**
 * A point P on the perimeter of a bounded region R1
 * such that a line from the center of R1, P, and a
 * location L2 are co-linear, subject to the constraint that
 * P lies within a specified arc of the perimeter. If the
 * co-linear point P would lie outside that arc, then the closer
 * endpoint of that arc is selected.
 *
 * @author zeil
 *
 */
public class ClosestPointOnPerimeter extends PerimeterPoint {
	
	private Location L;
	
	/**
	 * A point P on the perimeter of a bounded region R1
	 * such that a line from the center of R1, P, and a
	 * location L2 are co-linear, subject to the constraint that
	 * P lies within a specified arc of the perimeter. If the
	 * co-linear point P would lie outside that arc, then the closer
	 * endpoint of that arc is selected.
	 *
	 * The "angles" are scaled so that angle==0 is straight up on a
	 * drawing surface, the upper right corner is at 45.0, and each successive
	 * corner (moving clockwise) occurs at 90.0 after that.
	 *
	 */
	public ClosestPointOnPerimeter (BoundedRegion onPerimeterOf, Location referencePoint, double angleMin, double angleMax)
	{
		super(0.0,onPerimeterOf);
		L = referencePoint;
		theta.setMin(angleMin);
		theta.setMax(angleMax);
	}

	
	
	@Override
	public Point2D getCoordinates() {
		Rectangle2D perimeter = onPerimeterOf.getBBox();
		Point2D.Double center = new Point2D.Double(perimeter.getCenterX(), perimeter.getCenterY());
		Point2D reference = L.getCoordinates();
		if (center.getX() == reference.getX()) {
			if (center.getY() == reference.getY()) {
				// reference point lies on center of region - punt!
				theta.setValue ((theta.getMin() + theta.getMax())/2.0);
			} else {
				// [reference,center] is a vertical line
				theta.setValue((center.getY() < reference.getY()) ? 180.0 : 0.0);
			}
		} else if (center.getY() == reference.getY()) {
			// [reference,center] is a horizontal line
			theta.setValue((center.getX() < reference.getX()) ? 90.0 : 270.0);
		} else {
			Point2D p = null;
			double x0 = center.getX();
			double y0 = center.getY();
			double x1 = reference.getX();
			double y1 = reference.getY();
			
			double x = perimeter.getMinX();
			double slope = (y1 - y0) / (x1 - x0);
			double y = y0 + slope * (x - x0);
			if (y >= perimeter.getMinY() && y <= perimeter.getMaxY()) {
				p = new Point2D.Double(x,y);
			}
			x = perimeter.getMaxX();
			y = y0 + slope * (x - x0);
			Point2D.Double p1 = new Point2D.Double (x, y);
			if (y >= perimeter.getMinY() && y <= perimeter.getMaxY()) {
				if (p == null || p1.distance(reference) < p.distance(reference))
					p = p1;
			}
			y = perimeter.getMinY();
			x = x0 + (y - y0) / slope;
			p1 = new Point2D.Double (x, y);
			if (x >= perimeter.getMinX() && x <= perimeter.getMaxX()) {
				if (p == null || p1.distance(reference) < p.distance(reference))
					p = p1;
			}
			y = perimeter.getMaxY();
			x = x0 + (y - y0) / slope;
			p1 = new Point2D.Double (x, y);
			if (x >= perimeter.getMinX() && x <= perimeter.getMaxX()) {
				if (p == null || p1.distance(reference) < p.distance(reference))
					p = p1;
			}
			double a = getAngleOf(p);
			theta.setValue(a);
		}
		return super.getCoordinates();
	}



	
	
	public String toString()
	{
		return super.toString() + ":" + L;
	}


	public boolean equals(Object obj)
	{
		if (obj instanceof Location) {
			Location p = (Location)obj;
			return getCoordinates().equals(p.getCoordinates());
		} else
			return false;
	}
	
	
	public Object clone()
	{
		return new ClosestPointOnPerimeter(onPerimeterOf, L, theta.getMin(), theta.getMax());
	}


	@Override
	public Variable[] getVariables() {
		return new Variable[0];
	}


	

}
