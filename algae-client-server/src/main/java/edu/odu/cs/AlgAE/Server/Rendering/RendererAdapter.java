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
 * A Renderer class that provides a "no decision" default
 * for every Renderer function.
 *
 * @author zeil
 *
 */
public abstract class RendererAdapter<T> implements Renderer<T> {

    /* (non-Javadoc)
     * @see edu.odu.cs.AlgAE.Server.Rendering.Renderer#getColor(java.lang.Object)
     */
    @Override
    public Color getColor(T obj) {
        return null;
    }

    /* (non-Javadoc)
     * @see edu.odu.cs.AlgAE.Server.Rendering.Renderer#getComponents(java.lang.Object)
     */
    @Override
    public List<Component> getComponents(T obj) {
        return null;
    }

    /* (non-Javadoc)
     * @see edu.odu.cs.AlgAE.Server.Rendering.Renderer#getConnections(java.lang.Object)
     */
    @Override
    public List<Connection> getConnections(T obj) {
        return null;
    }


    /* (non-Javadoc)
     * @see edu.odu.cs.AlgAE.Server.Rendering.Renderer#getValue(java.lang.Object)
     */
    @Override
    public String getValue(T obj) {
        return null;
    }

    @Override
    public Directions getDirection() {
        return null;
    }

    @Override
    public Double getSpacing() {
        return null;
    }

    @Override
    public Boolean getClosedOnConnections() {
        return null;
    }

}
