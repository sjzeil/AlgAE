package edu.odu.cs.AlgAE.Server.MemoryModel;

import java.awt.Color;
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

    public Alias(Renderer<Object> renderer) {
    }

    @Override
    public String getValue(Alias obj) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getValue'");
    }

    @Override
    public Color getColor(Alias obj) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getColor'");
    }

    @Override
    public List<Component> getComponents(Alias obj) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getComponents'");
    }

    @Override
    public List<Connection> getConnections(Alias obj) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getConnections'");
    }

    @Override
    public Directions getDirection() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDirection'");
    }

    @Override
    public Double getSpacing() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSpacing'");
    }

    @Override
    public Boolean getClosedOnConnections() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getClosedOnConnections'");
    }

    @Override
    public Renderer<Alias> getRenderer() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRenderer'");
    }

}
