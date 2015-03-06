/**
 *
 */
package edu.odu.cs.AlgAE.Client.Layout.Coordinates;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.IdentityHashMap;

import edu.odu.cs.AlgAE.Client.Layout.Optimization.Variable;


/**
 * A relative quantity, specified as a (dx, dy) offset from the a
 *  "connection point" on the periphery of a rectangular region.
 *
 *  The connection points are specified as a combination of
 *  Left/Center/Right and Lower/Center/Upper:
 *
 *         LU-----CU-----RU
 *         |              |
 *         LC     CC     RC
 *         |              |
 *         LL-----CL-----RL
 *
 * @author zeil
 *
 */
public class RelativePoint implements Location {
	
	private BoundedRegion relativeTo;
	private Variable xOffset;
	private Variable yOffset;
	private Connections connection;
	
	
	public enum Connections {LU, CU, RU, LC, CC, RC, LL, CL, RL};
	
	public RelativePoint (double dx, double dy, Connections conn, BoundedRegion relativeTo)
	{
		xOffset = new Variable(dx, -500.0, 500.0, 1.0);
		yOffset = new Variable(dy, -500.0, 500.0, 1.0);
		connection = conn;
		this.relativeTo = relativeTo;
		if (relativeTo == null)
			throw new NullPointerException();
	}


	@Override
	public Point2D getCoordinates() {
		double xb = 0.0;
		double yb = 0.0;
		Rectangle2D relBounds = relativeTo.getBBox();
		switch (connection) {
		case LU:
		case LC:
		case LL:
			xb = relBounds.getMinX();
			break;
		case CU:
		case CC:
		case CL:
			xb = relBounds.getCenterX();
			break;
		case RU:
		case RC:
		case RL:
			xb = relBounds.getMaxX();
			break;
		}
		switch (connection) {
		case LU:
		case CU:
		case RU:
			yb = relBounds.getMinY();
			break;
		case LC:
		case CC:
		case RC:
			yb = relBounds.getCenterY();
			break;
		case LL:
		case CL:
		case RL:
			yb = relBounds.getMaxY();
			break;			
		}
		return new Point2D.Double(xb+xOffset.getValue(), yb+yOffset.getValue());
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
		return "(" + xOffset.getValue() + "," + yOffset.getValue() + ")+" + relativeTo + "." + connection;
	}


	public Object clone()
	{
		return new RelativePoint (xOffset.getValue(), yOffset.getValue(), connection, relativeTo);
	}


	@Override
	public Variable[] getVariables() {
		Variable[] results = new Variable[2];
		results[0] = xOffset;
		results[1] = yOffset;
		return results;
	}


	@Override
	public boolean isFixed(IdentityHashMap<FreeOrFixed, Boolean> alreadyChecked) {
		if (alreadyChecked == null) {
			return isFixed(new IdentityHashMap<FreeOrFixed, Boolean>());
		} else if (alreadyChecked.containsKey(this))
			return false;
		else {
			alreadyChecked.put(this, true);
			return relativeTo.isFixed(alreadyChecked);
		}
	}



	

}
