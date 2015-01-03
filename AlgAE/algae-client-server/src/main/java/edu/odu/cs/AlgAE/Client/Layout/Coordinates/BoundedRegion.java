package edu.odu.cs.AlgAE.Client.Layout.Coordinates;

import java.awt.geom.Rectangle2D;

/**
 * Regions with rectangular bounding boxes.
 * 
 * @author zeil
 *
 */
public interface BoundedRegion extends FreeOrFixed {
	
	/**
	 * Get the bounding box for this region.
	 * 
	 * @return Smallest rectangular area containing the region of interest.
	 */
	public Rectangle2D getBBox();
	
	
}
