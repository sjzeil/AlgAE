/**
 * 
 */
package edu.odu.cs.AlgAE.Server.Rendering;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;

/**
 * @author zeil
 *
 */
public class CompoundRenderer<T> implements Renderer<T> {
	
	/**
	 * List of renderers, from highest priority to lowest
	 */
	private LinkedList<Renderer<T>> renderers;
	
	
	public CompoundRenderer() {
		renderers = new LinkedList<Renderer<T>>();
	}
	
	
	
	/**
	 * Add a renderer of high priority than all previous
	 */
	public void add (Renderer<T> r) {
		renderers.addFirst(r);
	}

	/* (non-Javadoc)
	 * @see edu.odu.cs.zeil.AlgAE.DataModel.Renderer#getColor(java.lang.Object)
	 */
	@Override
	public Color getColor(T obj) {
		for (Renderer<T> r: renderers) {
			Color result = r.getColor(obj);
			if (result != null)
				return result;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.odu.cs.zeil.AlgAE.DataModel.Renderer#getComponents()
	 */
	@Override
	public List<Component> getComponents(T obj) {
		for (Renderer<T> r: renderers) {
			List<Component> result = r.getComponents(obj);
			if (result != null)
				return result;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.odu.cs.zeil.AlgAE.DataModel.Renderer#getConnections()
	 */
	@Override
	public List<Connection> getConnections(T obj) {
		for (Renderer<T> r: renderers) {
			List<Connection> result = r.getConnections(obj);
			if (result != null)
				return result;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.odu.cs.zeil.AlgAE.DataModel.Renderer#getMaxComponentsPerRow(java.lang.Object)
	 */
	@Override
	public int getMaxComponentsPerRow(T obj) {
		for (Renderer<T> r: renderers) {
			int result = r.getMaxComponentsPerRow(obj);
			if (result >= 0)
				return result;
		}
		return 1;
	}

	/* (non-Javadoc)
	 * @see edu.odu.cs.zeil.AlgAE.DataModel.Renderer#getValue(java.lang.Object)
	 */
	@Override
	public String getValue(T obj) {
		for (Renderer<T> r: renderers) {
			String result = r.getValue(obj);
			if (result != null)
				return result;
		}
		return "";
	}

}
