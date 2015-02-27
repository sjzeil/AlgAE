package edu.odu.cs.AlgAE.Server.Rendering;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import edu.odu.cs.AlgAE.Animations.AnimationContext;
import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;
import edu.odu.cs.AlgAE.Server.Utilities.SimpleReference;

/**
 * This renderer can be registered for java.util.LinkedList to
 * display the list as a chain of nodes with "next" links.
 * 
 * @author zeil
 *
 */
public class LinkedListRenderer<T> implements Renderer<List<T>> {

	private boolean doublyLinked;
	private boolean firstLast;
	private AnimationContext context;
	
	public LinkedListRenderer(boolean firstAndLast, boolean doubleLinkedNodes, AnimationContext context) {
		doublyLinked = doubleLinkedNodes;
		firstLast = firstAndLast;
		this.context = context;
	}
	
	
	@Override
	public Color getColor(List<T> obj) {
		return null;
	}

	@Override
	public List<Component> getComponents(List<T> llist) {
		LinkedList<Component> componentsL = new LinkedList<Component>();
		SimulatedNode firstNode = (llist.size() > 0) ? SimulatedNode.getNode(llist, 0, doublyLinked, context) : null;
		Component first = null;
		if (firstLast)
			first = new Component(new SimpleReference(firstNode, 190.0, 270.0), "head");
		else
			first = new Component(new SimpleReference(firstNode,80.0, 170.0), "head");
		componentsL.add(first);
		if (firstLast) {
			SimulatedNode lastNode = (llist.size() > 0) ? SimulatedNode.getNode(llist, llist.size()-1, doublyLinked, context) : null;
			Component last = new Component(new SimpleReference(lastNode, 90.0, 170.0), "tail");
			componentsL.add(last);	
		}
		return componentsL;
	}

	@Override
	public List<Connection> getConnections(List<T> obj) {
		return new LinkedList<Connection>();
	}

	@Override
	public int getMaxComponentsPerRow(List<T> obj) {
		return 2;
	}

	@Override
	public String getValue(List<T> obj) {
		return "";
	}

}
