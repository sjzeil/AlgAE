/**
 *
 */
package edu.odu.cs.AlgAE.Server.Rendering;

import java.awt.Color;
import java.util.List;

import edu.odu.cs.AlgAE.Animations.AnimationContext;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;
import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;

/**
 * @author zeil
 *
 */
public class HighlightRenderer<T> implements ObjectRenderer<T> {

    private T toRender;
    private boolean passThru;
    private Color color;
    private AnimationContext context;
    
    /**
     *
     */
    public HighlightRenderer(T objToHighlight, AnimationContext theContext) {
        toRender = objToHighlight;
        context = theContext;
        passThru = false;
        color = null;
    }

    /**
     *
     */
    public HighlightRenderer(T objToHighlight, Color newColor, AnimationContext theContext) {
        toRender = objToHighlight;
        context = theContext;
        passThru = false;
        color = newColor;
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
        if (color == null) {
            if (passThru) {
                return null;
            } else {
                passThru = true;
                Renderer<T> renderings = context.getMemoryModel().getRenderer (obj);
                Color c = renderings.getColor(obj);
                passThru = false;
                float[] colorComp = c.getComponents(null);
                for (int i = 0; i < 3; ++i)
                    colorComp[i] = 1.0f - colorComp[i];
                c = new Color(colorComp[0], colorComp[1], colorComp[2], colorComp[3]);
                return c;
            }
        } else {
            return color;
        }
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
