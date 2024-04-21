package edu.odu.cs.AlgAE.Client.Layout;

import java.util.ArrayList;
import java.util.HashMap;

import edu.odu.cs.AlgAE.Client.Layout.Coordinates.BoundedRegion;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.Dimension2DDouble;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.RelativePoint;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.RelativePoint.Connections;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity;
import edu.odu.cs.AlgAE.Common.Snapshot.EntityIdentifier;

/**
 * Arranges components in the upper left- corner of a rectangle.
 */
public class PackedLayout implements LayoutModel {

    private HashMap<EntityIdentifier, LocationInfo> locations;
    
    public PackedLayout (HashMap<EntityIdentifier, LocationInfo> locations)
    {
        this.locations = locations;
    }

    private class Row {
        public Dimension2DDouble boundingBox;
        public ArrayList<EntityIdentifier> contents;
        public ArrayList<Double> heights;
        public double spacing;


        public Row(double horizontalSpacing) {
            boundingBox = new Dimension2DDouble(0.0, 0.0);
            contents = new ArrayList<>();
            heights = new ArrayList<>();
            spacing = horizontalSpacing;
        }

        public void add(EntityIdentifier eid) {
            LocationInfo loc = locations.get(eid);
            Dimension2DDouble sz = loc.getSize();

            double w = boundingBox.getWidth();
            double h = Math.max(boundingBox.getHeight(), sz.getHeight());
            if (contents.size() > 0) {
                w += spacing;
            }
            w += sz.getWidth();
            contents.add(eid);
            heights.add(boundingBox.getHeight());
            boundingBox.setSize(w, h);
        }

        public void removeLast() {
            EntityIdentifier eid = contents.remove(contents.size()-1);
            LocationInfo loc = locations.get(eid);
            Dimension2DDouble sz = loc.getSize();

            double w = boundingBox.getWidth();
            double h = heights.remove(heights.size()-1);
            if (contents.size() > 0) {
                w -= spacing;
            }
            w -= sz.getWidth();
            boundingBox.setSize(w, h);
        }

    }

    @Override
    public Dimension2DDouble layoutComponents(
            Entity container, 
            BoundedRegion relativeTo, 
            double xOffset,
            double yOffset) {
        //Dimension2DDouble bounds = new Dimension2DDouble(0.0, 0.0);
        ArrayList<Row> rows = new ArrayList<>();

        for (EntityIdentifier eid: container.getComponents()) {
            //LocationInfo loc = locations.get(eid);
            //Dimension2DDouble sz = loc.getSize();

            Row appended = new Row(container.getSpacing());
            appended.add(eid);
            rows.add(appended);

            Dimension2DDouble bestBound = boundsOf(rows, container.getSpacing());
            int bestRow = rows.size();

            rows.remove(rows.size()-1);

            for (int i = 0; i < rows.size(); ++i) {
                Row row = rows.get(i);
                row.add(eid);
                Dimension2DDouble newBound = boundsOf(rows, container.getSpacing());
                if (Math.abs(newBound.getHeight() - newBound.getWidth()) 
                    < Math.abs(bestBound.getHeight() - bestBound.getWidth())) {
                    bestBound = (Dimension2DDouble)newBound.clone();
                    bestRow = i;
                }
                row.removeLast();
            }
            //bounds = bestBound;
            if (bestRow < rows.size()) {
                rows.get(bestRow).add(eid);
            } else {
                rows.add(appended);
            }
        }
        
      
        // Compute the actual position within each row
            double y = VerticalMargin;
            double width = 0;
            for (int i = 0; i < rows.size(); ++i) {
                Row row = rows.get(i);
                if (i > 0 && row.contents.size() > 0) {
                    y += VerticalSpacing;
                }
                double x = HorizontalMargin - container.getSpacing();
                double height = 0.0;
                if (row.contents.size() > 0) {
                    for (EntityIdentifier eid: row.contents) {
                        x += container.getSpacing();
                        LocationInfo loc = locations.get(eid);
                        Dimension2DDouble sz = loc.getSize();
                        loc.setLoc(new RelativePoint(x+xOffset, y+yOffset, Connections.LU, relativeTo));
                        x += sz.getWidth();
                        height = Math.max(height, sz.getHeight());
                    }
                    width = Math.max(width, x + HorizontalMargin);
                    y += height;
                }
            }
            width += HorizontalMargin;
            y += VerticalMargin;
            return new Dimension2DDouble(width, y);
    }
 

    private Dimension2DDouble boundsOf(ArrayList<Row> rows, double spacing) {
        double w = 0.0;
        double h = spacing * (rows.size() - 1);
        for (Row row: rows) {
            w = Math.max(w, row.boundingBox.getWidth());
            h += row.boundingBox.getHeight();
        }
        return new Dimension2DDouble(w, h);
    }

    /*
    private double delta(Dimension2DDouble b1, Dimension2DDouble b2) {
        return Math.abs(b1.getHeight() - b2.getHeight())
          + Math.abs(b1.getWidth() - b2.getWidth());
    }
    */
}