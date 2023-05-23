package edu.odu.cs.AlgAE.Client.DataViewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JPanel;

import edu.odu.cs.AlgAE.Client.DataViewer.Frames.DataShape;
import edu.odu.cs.AlgAE.Client.DataViewer.Frames.Frame;
import edu.odu.cs.AlgAE.Client.SourceViewer.SourceViewer;



public class DataCanvas extends JPanel
{



    //private static Logger logger = Logger.getLogger(DataCanvas.class.getName());

    private static Font BasicFont = null;
    private static float fontScale = 12.0f;


    private float zoom;
    private static float xyScalingfactor;
    private Frame currentPicture;
    private Dimension size;
    private boolean painted;

    private SourceViewer sourceCode;


    public DataCanvas(SourceViewer source)
    {
        sourceCode = source;
        zoom = 100.0f;
        xyScalingfactor = 1.0f;
        currentPicture = null;
        size = new Dimension(50,50);
        setBackground (Color.white);
        setPainted(false);
    }




    public synchronized void setPicture (Frame newPicture)
    {
        currentPicture = newPicture;
        if (newPicture.getLocation() != null)
            sourceCode.display(newPicture.getLocation());
        setPainted(false);
        sizeCheck();
    }



    @Override
    public void paintComponent (Graphics g)
    {
        super.paintComponent(g);
        paintCurrent (g);
    }

    private void paintCurrent (Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;

        if (BasicFont == null) {
            BasicFont = new Font(Font.MONOSPACED, Font.BOLD, (int)fontScale);
        }
        g2d.setFont(BasicFont);
        g2d.setPaintMode();

        FontMetrics metrics = g2d.getFontMetrics(BasicFont);
        int hgt = metrics.getHeight();
        fontScale = (float)hgt;
        int wd = metrics.stringWidth("MWX");
        xyScalingfactor = (((float)wd) / 3.0f) /hgt;
        g2d.setStroke(new BasicStroke(0.0f));

        Frame toBeDrawn = null;
        synchronized (this) {
            toBeDrawn = currentPicture;
        }
        
        if (toBeDrawn != null) {
            float scale = fontScale * zoom / 100.0f;
            g2d.scale (scale*xyScalingfactor, scale);
            ArrayList<LinkedList<DataShape> > byDepth = new ArrayList<LinkedList<DataShape>>();
            for (DataShape shape: toBeDrawn) {
                int d = shape.getDepth();
                while (byDepth.size() <= d) {
                    byDepth.add(new LinkedList<DataShape>());
                }
                byDepth.get(d).addLast(shape);
            }
            for (int d = byDepth.size()-1; d >= 0; --d) {
                for (DataShape shape: byDepth.get(d)) {
                    shape.draw (g2d);
                }
            }
        }
        

        setPainted(true);
    }


    private synchronized void sizeCheck()
    {
        Rectangle2D bbox = null;
        if (currentPicture != null) {
            for (DataShape shape: currentPicture) {
                if (bbox == null) {
                    bbox = shape.getBounds2D();
                } else {
                    Rectangle2D.union(bbox, shape.getBounds2D(), bbox);
                }
            }
        }
        if (bbox == null) {
            bbox = new Rectangle2D.Double(0.0, 0.0, 50.0, 50.0);
        }
        int w = (int)(0.99 + fontScale*(bbox.getX() + bbox.getWidth())* zoom/100.0f);
        int h = (int)(0.99 + fontScale*(bbox.getY() + bbox.getHeight()) * zoom/100.0f);
        if (w != size.width || h != size.height) {
            setPreferredSize (new Dimension(w, h));
            revalidate();
        }
    }






    private void setPainted(boolean painted) {
        this.painted = painted;
    }


    public boolean isPainted() {
        return painted;
    }



    public static float getYFontScale() {
        return fontScale;
    }

    public static float getXFontScale() {
        return fontScale * xyScalingfactor;
    }

    public void setZoom(float zoomValue) {
        zoom = zoomValue;
        sizeCheck();
    }


}
