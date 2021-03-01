package edu.odu.cs.AlgAE.Client.DataViewer.Frames;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import edu.odu.cs.AlgAE.Client.DataViewer.DataCanvas;


/**
 * An arrow
 *
 * @author zeil
 *
 */
public class ArrowToSelf extends Path2D.Float implements DataShape {

    /**
     * Possible senses (what part of the box does this connect to?)
     */
    public final int TOP = 0;
    public final int RIGHT = 1;
    public final int BOTTOM = 2;
    public final int LEFT = 3;
    
    private Color color;
    private int depth;
    private String id;
    private boolean dotted;
    private Point2D p1;
    private int sense;
    
    private final float DecorationSize = 0.33f;
    private final float ArcSize = 1.5f;
    
    private Ellipse2D bentShaft;
    
    public ArrowToSelf(String id, boolean dotted, Point2D p1, int sense, Color c, int depth) {
        if (dotted) {
            append (new Ellipse2D.Double(
                    p1.getX() - DecorationSize/2.0,
                    p1.getY() - DecorationSize/2.0,
                    DecorationSize, DecorationSize), false);
        }
        Point2D p0;
        if (sense == 0) {
            bentShaft = new Ellipse2D.Float((float)p1.getX() - ArcSize/2.0f, (float)p1.getY() - ArcSize, ArcSize, ArcSize);
            p0 = new Point2D.Double (p1.getX()+0.1, p1.getY() - 0.05);
        } else if (sense == 1) {
            bentShaft = new Ellipse2D.Float((float)p1.getX(), (float)p1.getY() - ArcSize/2.0f, ArcSize, ArcSize);
            p0 = new Point2D.Double (p1.getX()+0.05, p1.getY()+0.1);
        } else if (sense == 2) {
            bentShaft = new Ellipse2D.Float((float)p1.getX() - ArcSize/2.0f, (float)p1.getY(), ArcSize, ArcSize);
            p0 = new Point2D.Double (p1.getX() - 0.1, p1.getY()+0.05);             
        } else {
            bentShaft = new Ellipse2D.Float((float)p1.getX() - ArcSize, (float)p1.getY() - ArcSize/2.0f, ArcSize, ArcSize);
            p0 = new Point2D.Double (p1.getX() - 0.05, p1.getY() - 0.1);             
        }
        float dist = (float)p1.distance(p0);
        append (new Line2D.Float(p0, p1), false);
        Point2D p3 = new Point2D.Double (
                p1.getX() - (p1.getX() - p0.getX())*DecorationSize/dist,
                p1.getY() - (p1.getY() - p0.getY())*DecorationSize/dist);
        double dx = (p3.getY() - p1.getY())/2.0;
        double dy = (p3.getX() - p1.getX())/2.0;
        Path2D arrowHead = new Path2D.Float();
        arrowHead.moveTo(p1.getX(), p1.getY());
        arrowHead.lineTo(p3.getX() + dx, p3.getY() - dy);
        arrowHead.lineTo(p3.getX() - dx, p3.getY() + dy);
        arrowHead.closePath();
        append (arrowHead, false);
            
        this.id = id;
        this.color = c;
        this.depth = depth;
        this.dotted = dotted;
        this.p1 = p1;
        this.sense = sense;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fill(this);
        g.setStroke(new BasicStroke(1.0f / DataCanvas.getYFontScale()));
        g.draw(this);
        g.draw(bentShaft);
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
            return new ArrowToSelf (id, dotted, p1, sense, c, depth);
        } else if (other instanceof ArrowToSelf) {
            ArrowToSelf obox = (ArrowToSelf)other;
            float[] rgb1 = color.getRGBColorComponents(null);
            float[] rgb2 = obox.color.getRGBColorComponents(null);
            Color c = new Color (
                    interpolate(rgb1[0], rgb2[0], blend),
                    interpolate(rgb1[1], rgb2[1], blend),
                    interpolate(rgb1[2], rgb2[2], blend));
            
            return new ArrowToSelf (id, obox.dotted, interpolate(p1, obox.p1, blend), sense, c, depth);
        } else {
            return (blend < 0.5) ? tween(null, blend) : other.tween(null, 1.0f - blend);
        }
    }
    
    private double interpolate (double c1, double c2, float blend)
    {
        return blend * c2 + (1.0 - blend) * c1;
    }

    private float interpolate (float c1, float c2, float blend)
    {
        return blend * c2 + (1.0f - blend) * c1;
    }
    
    private Point2D interpolate (Point2D p1, Point2D p2, float blend)
    {
        return new Point2D.Double (interpolate(p1.getX(), p2.getX(), blend),
                    interpolate(p1.getY(), p2.getY(), blend));
    }
}
