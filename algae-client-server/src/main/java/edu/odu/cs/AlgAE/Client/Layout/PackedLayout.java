package edu.odu.cs.AlgAE.Client.Layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

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

    @Override
    public Dimension2DDouble layoutComponents(
            Entity container, 
            BoundedRegion relativeTo, 
            double xOffset,
            double yOffset) {
        ArrayList<LinkedList<EntityIdentifier>> rows 
           = new ArrayList<LinkedList<EntityIdentifier>>();

        // Arranges a list of components to pack a rectangular space whose upper left corner is
            //   specified.
            int numRows = 0;
            int r = 0;
            rows.add(new LinkedList<EntityIdentifier>());
            for (EntityIdentifier eid: container.getComponents()) {
                int nextr = r+1;
                rows.get(r).add (eid);
                if (r >= numRows) {
                    ++numRows;
                    rows.add(new LinkedList<EntityIdentifier>());
                    nextr = 0;
                }
                r = nextr;
            }
        
        
        // Compute the actual position within each row
            double y = VerticalMargin;
            double width = 0;
            for (int i = 0; i < rows.size(); ++i) {
                LinkedList<EntityIdentifier> row = rows.get(i);
                if (i > 0 && row.size() > 0) {
                    y += VerticalSpacing;
                }
                double x = HorizontalMargin - container.getSpacing();
                double height = 0.0;
                if (row.size() > 0) {
                    for (EntityIdentifier eid: row) {
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
    
}
