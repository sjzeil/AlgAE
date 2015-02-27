package edu.odu.cs.AlgAE.Server.Utilities;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import edu.odu.cs.AlgAE.Animations.AnimationContext;
import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;
import edu.odu.cs.AlgAE.Server.Rendering.SimulatedNode;

/**
 * A wrapper for ListIterators used in conjunction with LinkedListRenderer
 * 
 * @author zeil
 *
 */
public class LinkedIterator<T> implements ListIterator<T>, CanBeRendered<ListIterator<T>>, Renderer<ListIterator<T>> {

	private Color color;
	private double maxAngle;
	private double minAngle;
	
	private List<T> indexesInto;
	private ListIterator<T> index;
	private int count;
	
	private AnimationContext context;

	public LinkedIterator(ListIterator<T> value, List<T> indexesInto, AnimationContext context) {
		this.index = value;
		this.context = context;
		count = 0;
		this.indexesInto = indexesInto;
		color = Color.blue.darker();
		minAngle = 0.0;
		maxAngle = 360.0;
	}



	@Override
	public boolean hasNext() {
		return index.hasNext();
	}

	@Override
	public T next() {
		++count;
		return index.next();
	}

	@Override
	public void remove() {
		index.remove();
	}


	@Override
	public boolean hasPrevious() {
		return index.hasPrevious();
	}



	@Override
	public T previous() {
		--count;
		return index.previous();
	}



	@Override
	public int nextIndex() {
		return index.nextIndex();
	}



	@Override
	public int previousIndex() {
		return index.previousIndex();
	}



	@Override
	public void set(T e) {
		index.set(e);
	}



	@Override
	public void add(T e) {
		index.add(e);
	}

	
	
	@Override
	public Renderer<ListIterator<T>> getRenderer() {
		return this;
	}

	/**
	 * What string will be used as the value of this object?
	 * 	
	 * @param obj: object to be drawn
	 * @return a string or null to yield to other renderers
	 */
	public String getValue(ListIterator<T> obj)
	{
		return "";
	}
	
	/**
	 * What color will be used to draw this object?
	 * 	
	 * @param obj: object to be drawn
	 * @return a color or null to yield to other renderers
	 */
	public Color getColor(ListIterator<T> obj)
	{
		return color;
	}
	
	/**
	 * Get a list of other objects to be drawn inside the
	 * box portraying this one.
	 * 	
	 * @param obj: object to be drawn
	 * @return an array of contained objects or null to yield to other renderers
	 */
	public List<Component> getComponents(ListIterator<T> obj)
	{
		return new LinkedList<Component>();
	}
	
	/**
	 * Get a list of other objects to which we will draw
	 * pointers from this one.
	 * 	
	 * @param obj: object to be drawn
	 * @return an array of referenced objects or null to yield to other renderers
	 */
	public List<Connection> getConnections(ListIterator<T> obj)
	{
		Connection conn1 = (indexesInto != null) ? connectTo((List<T>)indexesInto, count) : null;
		
		LinkedList<Connection> conn = new LinkedList<Connection>();
		if (conn1 != null) {
			conn.add(conn1);
		}
		return conn;
	}
	

	private Connection connectTo(List<T> indexesInto, int v) {
		Object target = null;
		int index = v;
		target = SimulatedNode.getNode(indexesInto, v, false, context);

		if (target == null || v < 0 || v >= indexesInto.size())
			return null;
		Connection conn = new Connection(target, minAngle, maxAngle);
		conn.setColor(color.brighter());
		conn.setElasticity(10.0);
		conn.setComponentIndex(index);
		return conn;
	}

	/**
	 * Indicates how components will be laid out within the box
	 * representing this object.  A return value of 1 will force all
	 * components to be laid out in a single vertical column. Larger
	 * return values will permit a more horizontal layout.
	 * 
	 * @param obj
	 * @return max #components per row or a non-positive value to yield to other renderers 
	 */
			
	public int getMaxComponentsPerRow(ListIterator<T> obj)
	{
		return 1;
	}
	

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setMaxAngle(double maxAngle) {
		this.maxAngle = maxAngle;
	}

	public double getMaxAngle() {
		return maxAngle;
	}

	public void setMinAngle(double minAngle) {
		this.minAngle = minAngle;
	}

	public double getMinAngle() {
		return minAngle;
	}



}
