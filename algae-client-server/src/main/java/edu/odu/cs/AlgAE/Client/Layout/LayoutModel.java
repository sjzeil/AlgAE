package edu.odu.cs.AlgAE.Client.Layout;

import edu.odu.cs.AlgAE.Client.Layout.Coordinates.BoundedRegion;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.Dimension2DDouble;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity;

public interface LayoutModel {

    static final double VerticalMargin = 0.2;
    static final double HorizontalMargin = 0.25;
    static final double VerticalSpacing = 0.1;

    /**
     * Arranges a list of components, assigning each a position relative
     * to a given point. All component entities will have already 
     * had their sizes computed. This functions computes their positions.
     * 
     * @param container  entity whose components are to be inserted into the
     *                   snapshot. 
     * @param relativeTo upper left corner of the region where the components should
     *                   be placed
     * @param xOffset    distance from the left of relativeTo at which to start
     * @param yOffset    distance from the to of relativeTo at which to start
     */
Dimension2DDouble layoutComponents (
        Entity container, 
        BoundedRegion relativeTo,
        double xOffset, double yOffset);
    
}
