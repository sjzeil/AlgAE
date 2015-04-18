/**
 *
 */
package edu.odu.cs.AlgAE.Server.Utilities;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;

/**
 * A transparent box with an optional string "label" and a list of components.
 *
 * @author zeil
 *
 */
public class GlassBox implements CanBeRendered<GlassBox>, Renderer<GlassBox> {
    
    
    private String label;
    private LinkedList<Component> components;
    
    
    public GlassBox()
    {
        label = "";
        components = new LinkedList<Component>();
    }
    
    /* (non-Javadoc)
     * @see edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered#getRenderer()
     */
    @Override
    public Renderer<GlassBox> getRenderer() {
        return this;
    }

    /* (non-Javadoc)
     * @see edu.odu.cs.AlgAE.Server.Rendering.Renderer#getColor(java.lang.Object)
     */
    @Override
    public Color getColor(GlassBox obj) {
        return new Color(1.0f, 1.0f, 1.0f, 0.0f);
    }

    /* (non-Javadoc)
     * @see edu.odu.cs.AlgAE.Server.Rendering.Renderer#getComponents(java.lang.Object)
     */
    @Override
    public List<Component> getComponents(GlassBox obj) {
        return components;
    }

    /* (non-Javadoc)
     * @see edu.odu.cs.AlgAE.Server.Rendering.Renderer#getConnections(java.lang.Object)
     */
    @Override
    public List<Connection> getConnections(GlassBox obj) {
        return new LinkedList<Connection>();
    }

    /* Use space-packing layout
     * @see edu.odu.cs.AlgAE.Server.Rendering.Renderer#getMaxComponentsPerRow(java.lang.Object)
     */
    @Override
    public int getMaxComponentsPerRow(GlassBox obj) {
        return 0;
    }

    /* (non-Javadoc)
     * @see edu.odu.cs.AlgAE.Server.Rendering.Renderer#getValue(java.lang.Object)
     */
    @Override
    public String getValue(GlassBox obj) {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }


    /**
     * @param components the components to set
     */
    public void setComponents(List<Component> components) {
        this.components.clear();
        this.components.addAll(components);
    }

    

}
