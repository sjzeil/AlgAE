package edu.odu.cs.AlgAE.Client.DataViewer.Frames;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import edu.odu.cs.AlgAE.Client.DataViewer.DataCanvas;


/**
 * Colored text
 *
 * @author zeil
 *
 */
public class Text extends Rectangle2D.Float implements DataShape {

	private Color color;
	private int depth;
	private String id;
	private String text;
	private String tweenText;
	private Color tweenColor;
	
	public Text(String id, String text, float x, float y, Color c, int depth) {
		super(x, y, (float)text.length(), 1.0f);
		this.id = id;
		this.text = text;
		this.color = c;
		this.depth = depth;
		tweenText = null;
		tweenColor = null;
	}

	public Text(String id, String text, String tweenText, float x, float y, Color c, Color tweenC, int depth) {
		super(x, y, (float)text.length(), 1.0f);
		this.id = id;
		this.text = text;
		this.color = c;
		this.depth = depth;
		this.tweenText = tweenText;;
		tweenColor = tweenC;
	}

	@Override
	public void draw(Graphics2D g) {
		AffineTransform old = g.getTransform();
		float xscale = 1.0f / DataCanvas.getXFontScale();
		float yscale = 1.0f / DataCanvas.getYFontScale();
		g.scale(xscale, yscale);
		if (tweenText != null) {
			g.setColor(tweenColor);
			g.drawString(tweenText, (float)getX()/xscale, ((float)getY()+0.75f)/yscale);			
		}
		g.setColor(color);
		g.drawString(text, (float)getX()/xscale, ((float)getY()+0.75f)/yscale);
		g.setTransform(old);
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
			// Use blend as the alpha value so that the text becomes increasingly transparent.
			float[] rgb = color.getRGBColorComponents(null);
			Color c = new Color (rgb[0], rgb[1], rgb[2], 1.0f - (float)blend);
			return new Text (id, text, (float)getX(), (float)getY(),  c, depth);
		} else if (other instanceof Text) {
			Text otext = (Text)other;
			if (text.equals(otext.text)) {
				float[] rgba1 = color.getComponents(null);
				float[] rgba2 = otext.color.getComponents(null);
				Color c = new Color (
						interpolate(rgba1[0], rgba2[0], blend),
						interpolate(rgba1[1], rgba2[1], blend),
						interpolate(rgba1[2], rgba2[2], blend),
						interpolate(rgba1[3], rgba2[3], blend)
						);
				return new Text(id, text, interpolate((float)getX(), (float)otext.getX(), blend),
						interpolate((float)getY(), (float)otext.getY(), blend), c, depth);
			} else {
				float[] rgb1 = color.getRGBColorComponents(null);
				float[] rgb2 = otext.color.getRGBColorComponents(null);
				Color c1 = new Color (rgb1[0], rgb1[1], rgb1[2], 1.0f - blend);
				Color c2 = new Color (rgb2[0], rgb2[1], rgb2[2], blend);
				float x = interpolate((float)getX(), (float)otext.getX(), blend);
				float y = interpolate((float)getY(), (float)otext.getY(), blend);
				return new Text (id, text, otext.text, x, y, c1, c2, depth);
			}
		} else {
			return (blend < 0.5) ? tween(null, blend) : other.tween(null, 1.0f-blend);
		}
	}
	

	private float interpolate (float c1, float c2, float blend)
	{
		return blend * c2 + (1.0f - blend) * c1;
	}
}
