package edu.odu.cs.AlgAE.Common.Snapshot;

/**
 * Unique identifiers for objects in memory (of a remote server).
 * 
 * These are typically received in an XML-encoded snapshot as unique integer identifiers
 * 
 * @author zeil
 *
 */
public class RemoteIdentifier extends Identifier {

	private long id;
	
	/**
	 * Create a null identifier
	 */
	public RemoteIdentifier() {
		id = 0;
	}	
	
	public RemoteIdentifier(long forcedID) {
		id = forcedID;
	}
	
	public String toString()
	{
		return "" + id;
	}
		
	public int hashCode() {
		long modulo = (long)Integer.MAX_VALUE;
		return (int)(id % modulo);
	}
	
	public boolean equals (Object obj) {
		if (obj == null || ! (obj instanceof RemoteIdentifier))
			return false;
		RemoteIdentifier other = (RemoteIdentifier)obj;
			return id == other.id;
	}

	/**
	 * Used only for XML encoding!
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Used only for XML encoding!
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

    @Override
    public boolean isNull() {
        return id == 0L;
    }
}
