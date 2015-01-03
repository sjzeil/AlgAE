/**
 * 
 */
package edu.odu.cs.AlgAE.Client.Layout.Coordinates;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.IdentityHashMap;

/**
 * A zero-area region used mainly as an intermediate connection point 
 * between objects.
 * 
 * @author zeil
 *
 */
public class ConnectionPoint implements BoundedRegion {

	private Location location;
	
	
	public ConnectionPoint (Location loc) {
		location = loc;
	}
	
	/* (non-Javadoc)
	 * @see edu.odu.cs.AlgAE.Client.Layout.Coordinates.BoundedRegion#getBBox()
	 */
	@Override
	public Rectangle2D getBBox() {
		Point2D p = location.getCoordinates();
		return new Rectangle2D.Double(p.getX(), p.getY(), 0.0, 0.0);
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	@Override
	public boolean isFixed(IdentityHashMap<FreeOrFixed, Boolean> alreadyChecked) {
		if (location == null) {
			return false;
		} else if (alreadyChecked == null) {
			return isFixed(new IdentityHashMap<FreeOrFixed, Boolean>());
		} else if (alreadyChecked.containsKey(this))
			return false;
		else {
			alreadyChecked.put(this, true);
			return location.isFixed(alreadyChecked);
		}
	}

}


