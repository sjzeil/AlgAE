package edu.odu.cs.AlgAE.Client.DataViewer.Frames;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;


/**
 * A colored box
 *
 * @author zeil
 *
 */
public class Box extends Rectangle2D.Float implements DataShape {

    private Color color;
    private int depth;
    private String id;
    
    public Box(String id, float x, float y, float w, float h, Color c, int depth) {
        super(x, y, w, h);
        this.id = id;
        this.color = c;
        this.depth = depth;
    }

    @Override
    public void draw(Graphics2D g) {
        if (color.getAlpha() == 0)
            return;
        g.setColor(color);
        g.fill(this);
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public DataShape tween(DataShape other, float blend) {
        if (blend <= 0.0)
            return this;
        else if (blend >= 1.0)
            return other;
        
        if (other == null) {
            // Use blend as the alpha value so that the box becomes increasingly transparent.
            float[] rgb = color.getRGBColorComponents(null);
            Color c = new Color (rgb[0], rgb[1], rgb[2], 1.0f - (float)blend);
            return new Box (id, (float)getX(), (float)getY(), (float)getWidth(), (float)getHeight(), c, depth);
        } else if (other instanceof Box) {
            Box obox = (Box)other;
            float[] rgba1 = color.getComponents(null);
            float[] rgba2 = obox.color.getComponents(null);
            Color c = new Color (
                    interpolate(rgba1[0], rgba2[0], blend),
                    interpolate(rgba1[1], rgba2[1], blend),
                    interpolate(rgba1[2], rgba2[2], blend),
                    interpolate(rgba1[3], rgba2[3], blend)
                    );
            float x = interpolate((float)getX(), (float)obox.getX(), blend);
            float y = interpolate((float)getY(), (float)obox.getY(), blend);
            float w = interpolate((float)getWidth(), (float)obox.getWidth(), blend);
            float h = interpolate((float)getHeight(), (float)obox.getHeight(), blend);
            return new Box (id, x, y, w, h, c, depth);
        } else {
            return (blend < 0.5) ? tween(null, blend) : other.tween(null, 1.0f-blend);
        }
    }
    

    private float interpolate (float c1, float c2, float blend)
    {
        return (1.0f - blend) * c1 + blend * c2;
    }
    
    public String toString()
    {
        return id + ":" + super.toString();
    }
}
