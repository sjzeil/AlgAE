/**
 *
 */
package edu.odu.cs.AlgAE.Server.Rendering;

import java.awt.Color;
import java.util.List;

import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;
import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;

/**
 * @author zeil
 *
 */
public class HorizontalRenderer<T> implements ObjectRenderer<T> {

    private T toRender;
    private double spacing;
    private boolean closed;
    
    /**
     *
     */
    public HorizontalRenderer(T objToHighlight) {
        toRender = objToHighlight;
        spacing = Renderer.DefaultSpacing;
        closed = false;
    }

    /**
     *
     */
    public HorizontalRenderer() {
        toRender = null;
        spacing = Renderer.DefaultSpacing;
        closed = false;
    }

    /* (non-Javadoc)
     * @see edu.odu.cs.AlgAE.Server.Rendering.ObjectRenderer#appliesTo()
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
    public String getValue(T obj) {
        return null;
    }

    @Override
    public Directions getDirection() {
        return Directions.Horizontal;
    }

    @Override
    public Double getSpacing() {
        return spacing;
    }

    @Override
    public Boolean getClosedOnConnections() {
        return closed;
    }

}
