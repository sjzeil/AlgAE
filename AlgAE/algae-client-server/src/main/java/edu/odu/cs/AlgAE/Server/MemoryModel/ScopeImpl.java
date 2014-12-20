package edu.odu.cs.AlgAE.Server.MemoryModel;

import java.util.LinkedList;
import java.util.List;

import edu.odu.cs.AlgAE.Common.Snapshot.SourceLocation;

public class ScopeImpl  {

	
	private List<Component> params;
	private List<Component> locals;
	private SourceLocation location;

	
	public ScopeImpl () {
		params = new LinkedList<Component>();
		locals = new LinkedList<Component>();
		location = null;
	}
	

	
	public String toString()
	{
		return params + ":" + locals + "@" + location;
	}


	/**
	 * @return the locals
	 */
	public List<Component> getLocals() {
		return locals;
	}

	/**
	 * @return the parameters
	 */
	public List<Component> getParams() {
		return params;
	}



	/**
	 * @return the location
	 */
	public SourceLocation getLocation() {
		return location;
	}



	/**
	 * @param location the location to set
	 */
	public void setLocation(SourceLocation location) {
		this.location = location;
	}


	
	
}
