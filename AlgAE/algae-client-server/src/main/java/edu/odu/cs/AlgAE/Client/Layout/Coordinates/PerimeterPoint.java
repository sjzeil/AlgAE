/**
 *
 */
package edu.odu.cs.AlgAE.Client.Layout.Coordinates;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.IdentityHashMap;

import edu.odu.cs.AlgAE.Client.Layout.Optimization.Angle;
import edu.odu.cs.AlgAE.Client.Layout.Optimization.Variable;



/**
 * A point P on the perimeter of a bounded region
 * denoting the intersection of a ray from the center
 * of that region with its perimeter.
 * such that a line from the center of R1, P, and a
 * location L2 are co-linear, subject to the constraint that
 * P lies within a specified arc of the perimeter. If the
 * co-linear point P would lie outside that arc, then the closer
 * endpoint of that arc is selected.
 *
 * @author zeil
 *
 */
public class PerimeterPoint implements Location {
	
	protected BoundedRegion onPerimeterOf;
	protected Angle theta;
	
	/**
	 * Specifies a point on the edge of a rectangle where a ray
	 * from the center of the rectangle at a given angle would strike.
	 *
	 * The "angles" are scaled so that angle==0 is straight up on a
	 * drawing surface, the upper right corner is at 45.0, and each successive
	 * corner (moving clockwise) occurs at 90.0 after that.
	 *
	 * @param angle an angle in degrees
	 * @param onPerimeterOf a rectangular region
	 */
	public PerimeterPoint (double angle, BoundedRegion onPerimeterOf) {
		theta = new Angle (angle);
		this.onPerimeterOf = onPerimeterOf;
	}

	



	@Override
	public Point2D getCoordinates() {
		Rectangle2D bbox = onPerimeterOf.getBBox();
		Point2D result;
		double angle = theta.getValue();
		while (angle > 360.0)
			angle -= 360.0;
		while (angle < 0.0)
			angle += 360.0;
		if (angle < 45.0) {
			result = new Point2D.Double(
					bbox.getCenterX() + (angle/45.0) * bbox.getWidth()/2.0,
					bbox.getMinY());
		} else if (angle < 135.0) {
			result = new Point2D.Double(
					bbox.getMaxX(),
					bbox.getMinY() + (angle-45.0)/90.0 * bbox.getHeight());
		} else if (angle < 225.0) {
			result = new Point2D.Double(
					bbox.getMaxX() - (angle-135.0)/90.0 * bbox.getWidth(),
					bbox.getMaxY()
					);
		} else if (angle < 315.0) {
			result = new Point2D.Double(
					bbox.getMinX(),
					bbox.getMaxY() - (angle-225.0)/90.0 * bbox.getHeight());
		} else {
			result = new Point2D.Double(
					bbox.getMinX() + (angle - 315.0)/90.0 * bbox.getWidth(),
					bbox.getMinY());			
		}
		return result;
	}


	private boolean approxEqual (double w, double z)
	{
		return Math.abs(w - z) < 0.001;
	}
	
	protected double getAngleOf(Point2D perimeterPt) {
		Rectangle2D bbox = onPerimeterOf.getBBox();
		double result;
		if (approxEqual(perimeterPt.getX(), bbox.getMinX())) {
			// left side
			double s = (bbox.getHeight() > 0) ? (perimeterPt.getY() - bbox.getMinY()) / bbox.getHeight() : 1.0;
			result = 225.0 + (1-s)*90.0;
		} else if (approxEqual(perimeterPt.getX(), bbox.getMaxX())) {
			// right side
			double s = (bbox.getHeight() > 0) ? (perimeterPt.getY() - bbox.getMinY()) / bbox.getHeight() : 1.0;
			result = 45.0 + s*90.0;
		} else if (approxEqual(perimeterPt.getY(), bbox.getMaxY())) {
			// bottom side
			double s = (bbox.getWidth() > 0) ? (perimeterPt.getX() - bbox.getMinX()) / bbox.getWidth() : 1.0;
			result = 135.0 + (1.0-s)*90.0;
		} else if (approxEqual(perimeterPt.getY(), bbox.getMinY())) {
			// top side
			double s = (bbox.getWidth() > 0) ? (perimeterPt.getX() - bbox.getMinX()) / bbox.getWidth() : 1.0;
			result = 315.0 + s*90.0;
			if (result >= 360.0)
				result -= 360.0;
		} else {
			// Not on perimeter!
			result = -1.0;
		}
		return result;
	}

	@Override
	public Variable[] getVariables() {
		Variable[] result = new Variable[1];
		result[0] = theta;
		return result;
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
		return theta.getValue() + "@" + onPerimeterOf;
	}

	public Object clone()
	{
		return new PerimeterPoint (theta.getValue(), onPerimeterOf);
	}





	@Override
	public boolean isFixed(IdentityHashMap<FreeOrFixed, Boolean> alreadyChecked) {
		if (alreadyChecked == null) {
			return isFixed(new IdentityHashMap<FreeOrFixed, Boolean>());
		} else if (alreadyChecked.containsKey(this))
			return false;
		else {
			alreadyChecked.put(this, true);
			return onPerimeterOf.isFixed(alreadyChecked);
		}
	}

	

}
