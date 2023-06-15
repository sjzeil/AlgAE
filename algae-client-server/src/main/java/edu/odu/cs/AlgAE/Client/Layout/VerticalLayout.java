package edu.odu.cs.AlgAE.Client.Layout;

import java.util.HashMap;

import edu.odu.cs.AlgAE.Client.Layout.Coordinates.BoundedRegion;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.Dimension2DDouble;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.RelativePoint;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.RelativePoint.Connections;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity;
import edu.odu.cs.AlgAE.Common.Snapshot.EntityIdentifier;

/**
 * Arranges components in a vertical column.
 */
public class VerticalLayout implements LayoutModel {

    private HashMap<EntityIdentifier, LocationInfo> locations;
    
    public VerticalLayout (HashMap<EntityIdentifier, LocationInfo> locations)
    {
        this.locations = locations;
    }

    @Override
    public Dimension2DDouble layoutComponents(
            Entity container, 
            BoundedRegion relativeTo, 
            double xOffset,
            double yOffset) {
        // Arrange components into multiple rows.
        double y = VerticalMargin + yOffset;
        double width = 0;
        boolean first = true;
        for (EntityIdentifier eid : container.getComponents()) {
            if (!first) {
                y += container.getSpacing();
            }
            first = false;
            double x = HorizontalMargin;
            double height = 0.0;
            LocationInfo loc = locations.get(eid);
            Dimension2DDouble sz = loc.getSize();
            loc.setLoc(new RelativePoint(x + xOffset, y + yOffset, Connections.LU, relativeTo));
            x += sz.getWidth();
            height = Math.max(height, sz.getHeight());
            width = Math.max(width, x + HorizontalMargin);
            y += height;
        }
        width += HorizontalMargin;
        y += VerticalMargin;
        return new Dimension2DDouble(width, y);
    }
    
}
