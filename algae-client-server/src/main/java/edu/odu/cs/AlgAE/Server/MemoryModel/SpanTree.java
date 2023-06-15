package edu.odu.cs.AlgAE.Server.MemoryModel;

import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;

public class SpanTree implements CanBeRendered<SpanTree>, Renderer<SpanTree> {

    private Object theRoot;
    private Directions direction;
    private Map<Identifier, Component> componentsMap;
    private ActivationStack activationStack;
    private ArrayList<Component> components;
    private Component container;

    public SpanTree(
      Object root, 
      Component container,
      Directions containerDirection, 
      Map<Identifier, Component> components,
      ActivationStack theActivationStack) 
    {
        theRoot = root;
        this.container = container;
        this.componentsMap = components;
        activationStack = theActivationStack;
        if (containerDirection.equals(Directions.Horizontal)) {
            direction = Directions.VerticalTree;
        } else {
            direction = Directions.HorizontalTree;
        }
        this.components = collectRenderedComponents();
    }

    private ArrayList<Component> collectRenderedComponents() {
        Component spanTreeComponent = componentsMap.get(new Identifier(this));
        if (spanTreeComponent == null) {
            spanTreeComponent = new Component(this, container);
        }
        /*
        componentsMap.put(new Identifier(this), spanTreeComponent);
        */
        ArrayList<Component> components = new ArrayList<>();
        Set<Identifier> seen = new HashSet<>();


        Queue<Object> queue = new java.util.LinkedList<Object>();
        queue.add(theRoot);
        while (!queue.isEmpty()) {
            Object obj = queue.remove();
            Identifier oid = new Identifier(obj);
            if (seen.contains(oid)) {
                continue;
            }
            seen.add(oid);
            if (obj == theRoot || !componentsMap.containsKey(oid)) {
                
                Component newComponent = new Component(obj, spanTreeComponent);
                components.add(newComponent);
                /* componentsMap.put(new Identifier(newComponent.getActualObject()),
                    newComponent);
                    */
                Renderer<Object> render = activationStack.getRenderer(obj);
                List<Connection> connections = render.getConnections(obj);
                if (connections != null) {
                    for (Connection conn: connections) {
                        Object destObject = conn.getDestination();
                        if (destObject != null) {
                            queue.add(destObject);
                        }
                    }
                }
                // Also look through the components: add their connections
                // (but not the components) themselves
                LinkedList<Component> componentsQueue = new LinkedList<>();
                List<Component> innerComponents = render.getComponents(obj);
                if (innerComponents != null)
                    componentsQueue.addAll(innerComponents);
                while (!componentsQueue.isEmpty()) {
                    Component innerComponent = componentsQueue.remove();
                    Object innerObj = innerComponent.getActualObject();
                    Renderer<Object> innerRender = activationStack.getRenderer(innerObj);
                    List<Connection> innerConnections = innerRender.getConnections(innerObj);
                    if (innerConnections != null) {
                        for (Connection conn: innerConnections) {
                            Object destObject = conn.getDestination();
                            if (destObject != null) {
                                queue.add(destObject);
                            }
                        }
                    }
                    innerComponents = innerRender.getComponents(innerObj);
                    if (innerComponents != null)
                        componentsQueue.addAll(innerComponents);
                }
            }
        }
        return components;
    }

    @Override
    public String getValue(SpanTree obj) {
        return "";
    }

    @Override
    public Color getColor(SpanTree obj) {
        return edu.odu.cs.AlgAE.Common.Snapshot.Color.transparent;
    }

    @Override
    public List<Component> getComponents(SpanTree spanTree) {
        return components;
    }

    @Override
    public List<Connection> getConnections(SpanTree obj) {
        return new java.util.LinkedList<>();
    }

    @Override
    public Directions getDirection() {
        return direction;
    }

    @Override
    public Double getSpacing() {
        return 3.0;
    }

    @Override
    public Boolean getClosedOnConnections() {
        return false;
    }

    @Override
    public Renderer<SpanTree> getRenderer() {
        return this;
    }

    /**
     * Get the set of all objects that will be rendered as nodes in
     * this spanning tree. This will include the root and all objects
     * reachable by connections from the root that are not already known to
     * be components of some other container.
     * 
     * @return a set of object identifiers
     */
    public Collection<Identifier> getClosure() {
        ArrayList<Identifier> ids = new ArrayList<>();
        for (Component c: components) {
            ids.add(new Identifier(c.getActualObject()));
        }
        return ids;
    }


}
