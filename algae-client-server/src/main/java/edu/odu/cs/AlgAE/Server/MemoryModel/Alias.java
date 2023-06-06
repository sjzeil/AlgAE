package edu.odu.cs.AlgAE.Server.MemoryModel;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;

/**
 * An Alias is a placeholder for a rendered component that is already
 * being shown elsewhere but needs to be placed as a component of
 * some container.  Aliases look just like their originals except that
 * they do not have outgoing connections nor can they receive incoming
 * connections.  (These connections will appear on the original object.)
 * 
 * This is an imperfect compromise in a situation that should be avoided
 * when possible.
 */
public class Alias implements CanBeRendered<Alias>, Renderer<Alias>{

    Object original;
    Renderer<Object> originalRenderer;

    public Alias(Object original, ActivationStack aStack) {
        this.original = original;
        originalRenderer = aStack.getRenderer(original);
    }

    @Override
    public String getValue(Alias obj) {
        return originalRenderer.getValue(original);
    }

    @Override
    public Color getColor(Alias obj) {
        return originalRenderer.getColor(original);
    }

    @Override
    public List<Component> getComponents(Alias obj) {
        return originalRenderer.getComponents(original);
    }

    @Override
    public List<Connection> getConnections(Alias obj) {
        return new LinkedList<>();
    }

    @Override
    public Directions getDirection() {
        return originalRenderer.getDirection();
    }

    @Override
    public Double getSpacing() {
        return originalRenderer.getSpacing();
    }

    @Override
    public Boolean getClosedOnConnections() {
        return originalRenderer.getClosedOnConnections();
    }

    @Override
    public Renderer<Alias> getRenderer() {
        return this;
    }

}
