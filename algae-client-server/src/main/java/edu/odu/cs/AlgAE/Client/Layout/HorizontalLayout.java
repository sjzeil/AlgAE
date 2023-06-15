package edu.odu.cs.AlgAE.Client.Layout;

import java.util.HashMap;

import edu.odu.cs.AlgAE.Client.Layout.Coordinates.BoundedRegion;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.Dimension2DDouble;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.RelativePoint;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.RelativePoint.Connections;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity;
import edu.odu.cs.AlgAE.Common.Snapshot.EntityIdentifier;

/**
 * Arranges components in a horizontal row.
 */
public class HorizontalLayout implements LayoutModel {

    private HashMap<EntityIdentifier, LocationInfo> locations;
    
    public HorizontalLayout (HashMap<EntityIdentifier, LocationInfo> locations)
    {
        this.locations = locations;
    }

    @Override
    public Dimension2DDouble layoutComponents(
            Entity container, 
            BoundedRegion relativeTo, 
            double xOffset,
            double yOffset) {
        double x = HorizontalMargin;
        double height = 0;
        double width = 0;
        boolean first = true;
        for (EntityIdentifier eid : container.getComponents()) {
            if (!first) {
                x += container.getSpacing();
            }
            first = false;
            double y = VerticalMargin;
            LocationInfo loc = locations.get(eid);
            Dimension2DDouble sz = loc.getSize();
            loc.setLoc(new RelativePoint(x + xOffset, y + yOffset, Connections.LU, relativeTo));
            y += sz.getHeight();
            width = sz.getWidth();
            height = Math.max(height, y + VerticalMargin);
            x += width;
        }
        x += HorizontalMargin;
        height += VerticalMargin;
        return new Dimension2DDouble(x, height);

    }
    
}
