package edu.odu.cs.AlgAE.Common.Snapshot;

/**
 * Unique identifiers for objects in memory, with support for the local Java server
 * which needs to map objects to identifiers and vice versa.
 * 
 * @author zeil
 *
 */
public class LocalIdentifier extends Identifier {

	private Object id;
	
	public LocalIdentifier(Object instance) {
		id = instance;
	}
	
	public String toString()
	{
		if (id != null)
			return id.getClass().getSimpleName() + "@" + System.identityHashCode(id);
		else
			return "nullID";
	}
		
	public int hashCode() {
		if (id != null)
			return System.identityHashCode(id);
		else
			return 0;
	}
	
	public boolean equals (Object obj) {
		if (obj == null || ! (obj instanceof LocalIdentifier))
			return false;
		LocalIdentifier other = (LocalIdentifier)obj;
			return id == other.id;
	}

    @Override
    public boolean isNull() {
        return id == null;
    }

}
