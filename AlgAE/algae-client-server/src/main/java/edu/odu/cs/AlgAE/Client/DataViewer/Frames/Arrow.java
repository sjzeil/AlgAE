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
public class Arrow extends Path2D.Float implements DataShape {

	private Color color;
	private int depth;
	private String id;
	private boolean dotted;
	private Point2D p1;
	private Point2D p2;
	private Ellipse2D sourceMarker;
	private Path2D arrowHead;
	
	private final double DecorationSize = 0.5;
	
	public Arrow(String id, boolean dotted, Point2D p1, Point2D p2, Color c, int depth) {
		double xyRatio = DataCanvas.getXFontScale() / DataCanvas.getYFontScale();
		if (dotted) {
			sourceMarker = new Ellipse2D.Double(
					p1.getX() - DecorationSize/2.0,
					p1.getY() - xyRatio*DecorationSize/2.0,
					DecorationSize, DecorationSize);
		} else {
			sourceMarker = null;
		}
		arrowHead = null;
		if (p2 != null) {
			float dist = (float)p1.distance(p2);
			if (dist > 1.0f) {
				Point2D p3 = new Point2D.Double (
						p2.getX() - (p2.getX() - p1.getX())*DecorationSize/dist,
						p2.getY() - (p2.getY() - p1.getY())*DecorationSize/dist);
				double dx = (p3.getY() - p2.getY())/2.0;
				double dy = (p3.getX() - p2.getX())/2.0;
				arrowHead = new Path2D.Float();
				arrowHead.moveTo(p2.getX(), p2.getY());
				arrowHead.lineTo(p3.getX() + dx, p3.getY() - dy);
				arrowHead.lineTo(p3.getX() - dx, p3.getY() + dy);
				arrowHead.closePath();
			}
		}
		this.id = id;
		this.color = c;
		this.depth = depth;
		this.dotted = dotted;
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(color);
		g.setStroke(new BasicStroke(0.0f));
		if (p2 != null) {
			if (!color.equals(Color.black))
				g.setXORMode(Color.white);
			g.draw(new Line2D.Double(p1, p2));
			g.setPaintMode();
		}
		if (arrowHead != null)
			g.fill(arrowHead);
		g.setColor(Color.black);
		if (sourceMarker != null)
			g.fill(sourceMarker);
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
			float[] rgb = color.getRGBColorComponents(null);
			Color c = new Color (rgb[0], rgb[1], rgb[2]); //, (float)blend);
			if (p2 != null)
				return new Arrow (id, dotted, p1, interpolate(p1, p2, 1.0f - (float)blend), c, depth);
			else
				return new Arrow (id, dotted, p1, null, c, depth);
		} else if (other instanceof Arrow) {
			Arrow obox = (Arrow)other;
			float[] rgb1 = color.getRGBColorComponents(null);
			float[] rgb2 = obox.color.getRGBColorComponents(null);
			Color c = new Color (
					interpolate(rgb1[0], rgb2[0], blend),
					interpolate(rgb1[1], rgb2[1], blend),
					interpolate(rgb1[2], rgb2[2], blend));
			Point2D p2a = (p2 != null) ? p2 : p1;
			Point2D op2a = (obox.p2 != null) ? obox.p2 : obox.p1;
			return new Arrow (id, obox.dotted, interpolate(p1, obox.p1, blend), interpolate(p2a, op2a, blend), c, depth);
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
