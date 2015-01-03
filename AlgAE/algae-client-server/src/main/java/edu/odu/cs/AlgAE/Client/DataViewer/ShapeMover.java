package edu.odu.cs.AlgAE.Client.DataViewer;

public interface ShapeMover {

	/**
	 * Receive notification that a box with the indicated id
	 * has been dragged to location (x,y)
	 */
	void moved(String id, double x, double y);

}
