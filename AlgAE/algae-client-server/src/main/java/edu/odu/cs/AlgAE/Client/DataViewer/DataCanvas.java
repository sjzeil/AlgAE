package edu.odu.cs.AlgAE.Client.DataViewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JPanel;

import edu.odu.cs.AlgAE.Client.DataViewer.Frames.Box;
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


  // Data members for portrayal of dragged boxes
  private Box movingBox;
  private Rectangle2D lastBoxMove;
  private Point2D movingBoxOffset;

  private ShapeMover shapeMover;
  private boolean movingEnabled;


  public DataCanvas(SourceViewer source, ShapeMover mover)
  {
      sourceCode = source;
      zoom = 100.0f;
      xyScalingfactor = 1.0f;
       currentPicture = null;
       size = new Dimension(50,50);
      setBackground (Color.white);
      setPainted(false);

      movingBox = null;
      lastBoxMove = null;
      movingBoxOffset = null;
      shapeMover = mover;

      addMouseListener(new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
              if (movingEnabled)
                  selectBox (e);
          }

        @Override
        public void mouseReleased(MouseEvent e) {
              if (movingEnabled)
                  releaseBox(e);    
        }
          
          
      });
      addMouseMotionListener(new MouseMotionAdapter() {
        
        @Override
        public void mouseDragged(MouseEvent e) {
              if (movingEnabled)
                  dragBox(e);
        }
    });
  }


  /**
 * @param movingEnabled the movingEnabled to set
 */
public void setMovingEnabled(boolean movingEnabled) {
    this.movingEnabled = movingEnabled;
}


/**
 * @return the movingEnabled
 */
public boolean isMovingEnabled() {
    return movingEnabled;
}


public synchronized void setPicture (Frame newPicture)
  {
      currentPicture = newPicture;
      if (newPicture.getLocation() != null)
          sourceCode.display(newPicture.getLocation());
      setPainted(false);
      sizeCheck();
  }




  public void paint (Graphics g)
  {
    super.paint(g);
    paintCurrent (g);
  }

  private synchronized void paintCurrent (Graphics g)
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


      if (currentPicture != null) {
          float scale = fontScale * zoom / 100.0f;
          g2d.scale (scale*xyScalingfactor, scale);
          ArrayList<LinkedList<DataShape> > byDepth = new ArrayList<LinkedList<DataShape>>();
          for (DataShape shape: currentPicture) {
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
    
      // Draw box being dragged
      if (lastBoxMove != null) {
          g2d.setColor (new Color(0.5f, 0.5f, 0.5f, 0.5f));
          g2d.fill(lastBoxMove);
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


private void selectBox(MouseEvent e) {
    //System.out.println (e.getX() + " " + e.getY());
    Box selected = null;
    double x = e.getX() / getXFontScale() / (zoom/100.0);
    double y = e.getY() / getYFontScale() / (zoom/100.0);
    for (DataShape s: currentPicture) {
        if (s instanceof Box){
            Box b = (Box)s;
            Point2D p = new Point2D.Double(x,y);    
            if (b.getBounds().contains(p)) {
                //System.out.println (b.getID() + " at " + b.getBounds());
                if (selected == null ||
                        b.getBounds().getWidth() * b.getBounds().getHeight()
                        > selected.getBounds().getWidth() * selected.getBounds().getHeight()) {
                    selected = b;
                }
            }
        }
    }
    if (selected != null) {
        movingBox = selected;
        lastBoxMove = selected.getBounds();
        movingBoxOffset = new Point2D.Double(selected.getBounds().getX()-x, selected.getBounds().getY()-y);
    }
    repaint();
}

public void dragBox(MouseEvent e) {
    if (movingBox != null) {
        double x = e.getX() / getXFontScale() / (zoom/100.0);
        double y = e.getY() / getYFontScale() / (zoom/100.0);
        x += movingBoxOffset.getX();
        y += movingBoxOffset.getY();
        lastBoxMove = new Rectangle2D.Double (x, y, lastBoxMove.getWidth(), lastBoxMove.getHeight());
        repaint();
    }
}

public void releaseBox(MouseEvent e) {
    if (movingBox != null) {
        double x = e.getX() / getXFontScale() / (zoom/100.0);
        double y = e.getY() / getYFontScale() / (zoom/100.0);
        x += movingBoxOffset.getX();
        y += movingBoxOffset.getY();
        shapeMover.moved (movingBox.getID(), x, y);
        currentPicture.add (new Box(movingBox.getID(), (float)x, (float)y,
                (float)movingBox.getBounds().getWidth(), (float)movingBox.getBounds().getHeight(),
                movingBox.getColor(), movingBox.getDepth()));
        movingBox = null;
        lastBoxMove = null;
        repaint();
    }
}


}
