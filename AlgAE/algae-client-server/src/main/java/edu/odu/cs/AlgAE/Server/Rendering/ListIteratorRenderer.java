package edu.odu.cs.AlgAE.Server.Rendering;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

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
public class ListIteratorRenderer<T> implements ObjectRenderer<ListIterator<T>> {

    private boolean doublyLinked;
    private AnimationContext context;
    private ListIterator<T> it;
    private List<?> container;
    
    public ListIteratorRenderer(List<?> container, boolean firstAndLast, boolean doubleLinkedNodes, AnimationContext context) {
        doublyLinked = doubleLinkedNodes;
        this.context = context;
        this.container = container;
        it = null;
    }
    
    public ListIteratorRenderer(ListIterator<T> iter, List<T> container, boolean firstAndLast, boolean doubleLinkedNodes, AnimationContext context) {
        doublyLinked = doubleLinkedNodes;
        this.context = context;
        this.container = container;
        it = iter;
    }
    
    @Override
    public Color getColor(ListIterator<T> obj) {
        return null;
    }

    @Override
    public List<Component> getComponents(ListIterator<T> obj) {
        LinkedList<Component> componentsL = new LinkedList<Component>();
        ListIterator<T> iter = (it == null) ? obj : it;
        int position = iter.nextIndex() - 1;
        SimulatedNode simNode = null;
        if (position >= 0 && position < container.size()) {
            simNode = SimulatedNode.getNode(container, position, doublyLinked, context);
        }
        Component c = new Component(new SimpleReference(simNode,190.0, 270.0));
        componentsL.add(c);
        return componentsL;
    }

    @Override
    public List<Connection> getConnections(ListIterator<T> obj) {
        return new LinkedList<Connection>();
    }

    @Override
    public int getMaxComponentsPerRow(ListIterator<T> obj) {
        return 2;
    }

    @Override
    public String getValue(ListIterator<T> obj) {
        return "";
    }

    @Override
    public ListIterator<T> appliesTo() {
        return it;
    }

}
