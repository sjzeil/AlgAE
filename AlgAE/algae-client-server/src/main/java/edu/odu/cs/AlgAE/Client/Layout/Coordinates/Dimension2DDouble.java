/**
 *
 */
package edu.odu.cs.AlgAE.Client.Layout.Coordinates;

import java.awt.geom.Dimension2D;

/**
 * @author zeil
 *
 */
public class Dimension2DDouble extends Dimension2D {

	private double width;
	private double height;
	
	/**
	 *
	 */
	public Dimension2DDouble() {
		width = height = 0.0;
	}

	/**
	 *
	 */
	public Dimension2DDouble(double w, double h) {
		width = w;
		height = h;
	}

	
	/* (non-Javadoc)
	 * @see java.awt.geom.Dimension2D#getHeight()
	 */
	@Override
	public double getHeight() {
		return height;
	}

	/* (non-Javadoc)
	 * @see java.awt.geom.Dimension2D#getWidth()
	 */
	@Override
	public double getWidth() {
		return width;
	}

	/* (non-Javadoc)
	 * @see java.awt.geom.Dimension2D#setSize(double, double)
	 */
	@Override
	public void setSize(double w, double h) {
		width = w;
		height = h;
	}
	
	public String toString ()
	{
		return "[" + width + ":" + height + "]";
	}
	
	public boolean equals (Object obj) {
		if (obj == null)
			return false;
		else if (obj instanceof Dimension2DDouble) {
			Dimension2DDouble other = (Dimension2DDouble)obj;
			return (width == other.width) && (height == other.height);
		} else {
			return false;
		}
	}

}
