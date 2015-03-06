package edu.odu.cs.AlgAE.Server.Utilities;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;

/**
 * A labeled pointer to an object.
 *
 * @author zeil
 *
 */
public class SimpleReference implements CanBeRendered<SimpleReference>, Renderer<SimpleReference> {

	private Color color;
	private double maxAngle;
	private double minAngle;
	private Object refersTo;
	
	public SimpleReference(Object refersTo) {
		this.refersTo = refersTo;
		color = Color.gray.brighter();
	}

	public SimpleReference(Object refersTo, double minAngle, double maxAngle) {
		this.refersTo = refersTo;
		color = Color.gray.brighter();
		this.minAngle = minAngle;
		this.maxAngle = maxAngle;
	}
	
	public void set (Object newRefersTo)
	{
		refersTo = newRefersTo;
	}
	
	public Object get()
	{
		return refersTo;
	}
	

	@Override
	public Renderer<SimpleReference> getRenderer() {
		return this;
	}

	/**
	 * What string will be used as the value of this object?
	 * 	
	 * @param obj: object to be drawn
	 * @return a string or null to yield to other renderers
	 */
	public String getValue(SimpleReference obj)
	{
		return "";
	}
	
	/**
	 * What color will be used to draw this object?
	 * 	
	 * @param obj: object to be drawn
	 * @return a color or null to yield to other renderers
	 */
	public Color getColor(SimpleReference obj)
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
	public List<Component> getComponents(SimpleReference obj)
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
	public List<Connection> getConnections(SimpleReference obj)
	{
		LinkedList<Connection> conn = new LinkedList<Connection>();
		Connection c = new Connection(refersTo, minAngle, maxAngle);
		c.setColor(Color.darkGray);
		c.setElasticity(10.0);
		conn.add(c);
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
			
	public int getMaxComponentsPerRow(SimpleReference obj)
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
