package edu.odu.cs.AlgAE.Client.Layout.Coordinates;

import java.util.IdentityHashMap;


/**
 * A geometric quantity is fixed if its numeric value can be determined, free if
 * its value depends on one or more other quantities that have yet to be determined.
 *
 * @author zeil
 *
 */
public interface FreeOrFixed {
	
	public boolean isFixed(IdentityHashMap<FreeOrFixed, Boolean> alreadyChecked);


}
