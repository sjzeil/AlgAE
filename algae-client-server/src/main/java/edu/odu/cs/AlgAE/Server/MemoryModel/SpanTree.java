package edu.odu.cs.AlgAE.Server.MemoryModel;

import edu.odu.cs.AlgAE.Common.Snapshot.EntityIdentifier;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;

import java.awt.Color;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;

public class SpanTree implements CanBeRendered<SpanTree>, Renderer<SpanTree> {

    Object theRoot;
    Directions direction;
    Map<Identifier, Component> componentsMap;
    private ActivationStack activationStack;

    public SpanTree(
      Object root, 
      Directions containerDirection, 
      Map<Identifier, Component> components,
      ActivationStack theActivationStack) 
    {
        theRoot = root;
        this.componentsMap = components;
        activationStack = theActivationStack;
        if (containerDirection.equals(Directions.Horizontal)) {
            direction = Directions.VerticalTree;
        } else {
            direction = Directions.HorizontalTree;
        }
    }

    @Override
    public String getValue(SpanTree obj) {
        return "";
    }

    @Override
    public Color getColor(SpanTree obj) {
        return Color.white;
    }

    @Override
    public List<Component> getComponents(SpanTree spanTree) {
        Component spanTreeComponent = componentsMap.get(new Identifier(this));
        LinkedList<Component> components = new LinkedList<>();
        components.add(new Component(theRoot));
        Queue<Object> queue = new java.util.LinkedList<Object>();
        queue.add(theRoot);
        while (!queue.isEmpty()) {
            Object obj = queue.remove();
            Identifier oid = new Identifier(obj);
            if (!componentsMap.containsKey(oid)) {
                Component newComponent = new Component(obj, spanTreeComponent);
                components.add(newComponent);
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
            }
        }
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
        return 2.0;
    }

    @Override
    public Boolean getClosedOnConnections() {
        return false;
    }

    @Override
    public Renderer<SpanTree> getRenderer() {
        return this;
    }


}
