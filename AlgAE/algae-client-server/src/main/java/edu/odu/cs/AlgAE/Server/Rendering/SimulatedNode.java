/**
 * 
 */
package edu.odu.cs.AlgAE.Server.Rendering;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.odu.cs.AlgAE.Animations.AnimationContext;
import edu.odu.cs.AlgAE.Common.Snapshot.Identifier;
import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;

/**
 * Used in the default rendering of the LinkedList class
 * 
 * @author zeil
 *
 */
public class SimulatedNode implements CanBeRendered<SimulatedNode>, Renderer<SimulatedNode> {

	
	/**
	 * Cache of previously generated nodes.
	 */
	private static class NodeID {
		Identifier list;
		int componentNum;
		
		public NodeID (List<?> list, int component, AnimationContext context) {
			this.list = new Identifier(list);
			this.componentNum = component;
		}
		
		public boolean equals (Object obj) {
			if (obj instanceof NodeID) {
				NodeID other = (NodeID)obj;
				return (other.list.equals(list) && other.componentNum == componentNum);
			} else
				return false;
		}
		
		@Override
		public int hashCode() {
			return 13 * list.hashCode() + componentNum;
		}
	}
	private static HashMap<NodeID, SimulatedNode > cache
		= new HashMap<SimulatedNode.NodeID, SimulatedNode>();

	
	private List<?> list;
	private int componentNum;
	private boolean doublyLinked;
	private AnimationContext context;
	
	public static SimulatedNode getNode(List<?> llist, int componentNum, boolean doublyLinked, AnimationContext context) {
		NodeID id = new NodeID(llist, componentNum, context);
		SimulatedNode node = cache.get(id);
		if (node == null) {
			node = new SimulatedNode(llist, componentNum, doublyLinked, context);
			cache.put(id, node);
		}
		return node;
	}

	private SimulatedNode(List<?> llist, int componentNum, boolean doublyLinked, AnimationContext context) {
		this.context = context;
		list = llist;
		this.componentNum = componentNum;
		this.doublyLinked = doublyLinked;
	}

	@Override
	public String getValue(SimulatedNode obj) {
		return "";
	}

	/* (non-Javadoc)
	 * @see edu.odu.cs.AlgAE.Server.Rendering.Renderer#getColor(java.lang.Object)
	 */
	@Override
	public Color getColor(SimulatedNode obj) {
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.odu.cs.AlgAE.Server.Rendering.Renderer#getComponents(java.lang.Object)
	 */
	@Override
	public List<Component> getComponents(SimulatedNode obj) {
		LinkedList<Component> components = new LinkedList<Component>();
		if (componentNum >= 0 && componentNum < list.size()) {
			Object c = list.get(componentNum);
			components.add (new Component(c));
		}
		return components;
	}

	/* (non-Javadoc)
	 * @see edu.odu.cs.AlgAE.Server.Rendering.Renderer#getConnections(java.lang.Object)
	 */
	@Override
	public List<Connection> getConnections(SimulatedNode obj) {
		LinkedList<Connection> conn = new LinkedList<Connection>();
		if (doublyLinked) {
			SimulatedNode prev = (componentNum > 0) 
				? getNode(list, componentNum-1, doublyLinked, context)
				: null;
			conn.add (new Connection(prev, 280.0, 300.0));	
		}
		SimulatedNode next = (componentNum+1 < list.size()) 
		? getNode(list, componentNum+1, doublyLinked, context)
		: null;
		conn.add (new Connection(next, 75.0, 90.0));	
		return conn;
	}

	/* (non-Javadoc)
	 * @see edu.odu.cs.AlgAE.Server.Rendering.Renderer#getMaxComponentsPerRow(java.lang.Object)
	 */
	@Override
	public int getMaxComponentsPerRow(SimulatedNode obj) {
		return 1;
	}

	/* (non-Javadoc)
	 * @see eedu.odu.cs.AlgAE.Server.Rendering.CanBeRendered#getRenderer()
	 */
	@Override
	public Renderer<SimulatedNode> getRenderer() {
		return this;
	}

}
