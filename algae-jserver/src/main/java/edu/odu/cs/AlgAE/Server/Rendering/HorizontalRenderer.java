/**
 * 
 */
package edu.odu.cs.AlgAE.Server.Rendering;

import java.awt.Color;
import java.util.List;

import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;

/**
 * @author zeil
 *
 */
public class HorizontalRenderer<T> implements ObjectRenderer<T> {

	private T toRender;
	
	/**
	 * 
	 */
	public HorizontalRenderer(T objToHighlight) {
		toRender = objToHighlight;
	}

	/**
	 * 
	 */
	public HorizontalRenderer() {
		toRender = null;
	}

	/* (non-Javadoc)
	 * @see edu.odu.cs.zeil.AlgAE.DataModel.ObjectRenderer#appliesTo()
	 */
	@Override
	public T appliesTo() {
		return toRender;
	}
	
	@Override
	public Color getColor (T obj)
	{
		return null;
	}

	@Override
	public List<Component> getComponents(T obj) {
		return null;
	}

	@Override
	public List<Connection> getConnections(T obj) {
		return null;
	}

	@Override
	public int getMaxComponentsPerRow(T obj) {
		return 100;
	}

	@Override
	public String getValue(T obj) {
		return null;
	}

}
