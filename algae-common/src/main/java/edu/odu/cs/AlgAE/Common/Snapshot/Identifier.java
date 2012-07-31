package edu.odu.cs.AlgAE.Common.Snapshot;

/**
 * Unique identifiers for objects in memory.
 * 
 * @author zeil
 *
 */
public abstract class Identifier {

	/**
	 * The implementation of this class is slightly complicated by the conflicting requirements that
	 * 1) Identifiers must be serializable when supplied from external servers such as the C++
	 *     animation server, which will supply them as long integer values.
	 * 2) The local Java client needs support for mapping Identifiers to the objects that they identify and
	 *     vice versa.
	 * Hence it is actually easiest to provide a pair of implementations via subclasses.
	 */

	
	public abstract String toString();
		
	public abstract int hashCode();
	
	public abstract boolean equals (Object obj);

}
