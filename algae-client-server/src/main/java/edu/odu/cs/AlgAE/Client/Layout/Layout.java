package edu.odu.cs.AlgAE.Client.Layout;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.odu.cs.AlgAE.Client.DataViewer.Frames.Arrow;
import edu.odu.cs.AlgAE.Client.DataViewer.Frames.ArrowToSelf;
import edu.odu.cs.AlgAE.Client.DataViewer.Frames.Box;
import edu.odu.cs.AlgAE.Client.DataViewer.Frames.Frame;
import edu.odu.cs.AlgAE.Client.DataViewer.Frames.Text;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.BoundedRegion;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.ClosestPointOnPerimeter;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.Dimension2DDouble;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.FreeOrFixed;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.Location;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.PerimeterPoint;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.Point;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.RelativePoint;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.RelativePoint.Connections;
import edu.odu.cs.AlgAE.Client.Layout.Optimization.OptimizationProblem;
import edu.odu.cs.AlgAE.Client.Layout.Optimization.Optimizer;
import edu.odu.cs.AlgAE.Client.Layout.Optimization.Variable;
import edu.odu.cs.AlgAE.Common.Snapshot.Connector;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity;
import edu.odu.cs.AlgAE.Common.Snapshot.EntityIdentifier;
import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;
import edu.odu.cs.AlgAE.Common.Snapshot.SourceLocation;

/**
 * A layout is a snapshot in which all entities and connections have been
 * assigned a location.
 *
 * A layout is created from a snapshot and a (possibly null) prior layout, so
 * that entities that
 * have already been assigned a location in the prior layout can retain those
 * positions in the
 * subsequent layout.
 *
 *
 * @author zeil
 *
 */
public class Layout {

    private HashMap<EntityIdentifier, Entity> entities;
    private HashMap<EntityIdentifier, LocationInfo> locations;
    private LinkedList<LocationInfo> movable;
    private String descriptor;
    private SourceLocation sourceLoc;
    private Anchors anchors;

    /**
     * A subset of the total objects of the snapshot, consisting of those objects
     * that
     * are not components of other objects (i.e., container == null)
     *
     */
    private HashSet<EntityIdentifier> baseObjectIDs;

    private class LocationInfo implements BoundedRegion {
        private Dimension2DDouble size;
        private Location loc;
        private int height;
        private Entity describes;

        public LocationInfo(Entity e) {
            size = null;
            loc = null;
            describes = e;
            height = 0;
        }

        /**
         * @param size the size to set
         */
        public void setSize(Dimension2DDouble size) {
            this.size = size;
        }

        /**
         * @return the size
         */
        public Dimension2DDouble getSize() {
            return size;
        }

        /**
         * @param loc the location to set
         */
        public void setLoc(Location loc) {
            this.loc = loc;
        }

        /**
         * @return the location
         */
        public Location getLoc() {
            if (loc == null)
                return new Point(0.0, 0.0);
            else
                return loc;
        }

        /**
         * True if this entity has been assigned a position
         *
         */
        public boolean hasBeenPositioned() {
            return loc != null;
        }

        /**
         * @return the entity described by this location
         */
        // public Entity getEntity() {
        // return describes;
        // }

        @Override
        public Rectangle2D getBBox() {
            Point2D location = getLoc().getCoordinates();
            Dimension2DDouble sz = getSize();
            return new Rectangle2D.Double(location.getX(), location.getY(), sz.getWidth(), sz.getHeight());
        }

        @Override
        public boolean isFixed(IdentityHashMap<FreeOrFixed, Boolean> alreadyChecked) {
            if (loc == null || size == null)
                return false;
            else if (alreadyChecked == null) {
                return isFixed(new IdentityHashMap<FreeOrFixed, Boolean>());
            } else if (alreadyChecked.containsKey(this))
                return false;
            else {
                alreadyChecked.put(this, true);
                return loc.isFixed(alreadyChecked);
            }
        }

        /**
         * The height of an entity is the number of containers
         * it lies inside of.
         *
         * @param h the height to set
         */
        public void setHeight(int h) {
            this.height = h;
        }

        /**
         * @return the height
         */
        public int getHeight() {
            return height;
        }

        public String toString() {
            StringBuffer result = new StringBuffer();
            result.append("describes:");
            result.append(describes.getEntityIdentifier());
            result.append(", height:");
            result.append(height);
            result.append(", loc:");
            result.append(loc);
            result.append(", size:");
            result.append(size);
            return result.toString();
        }
    }

    /**
     * Create a new layout, similar to basedOn but reflecting any
     * anchorAt calls since the former layout was created.
     *
     * @param Snapshot the memory snapshot for which a layout is being created
     * @param previous an existing layout
     * @param anchors  entities that have been assigned a fixed position on
     *                 the screen.
     */
    public Layout(Snapshot current, Layout previous, Anchors anchors) {
        this.anchors = anchors;
        entities = new HashMap<EntityIdentifier, Entity>();
        locations = new HashMap<EntityIdentifier, LocationInfo>();
        baseObjectIDs = new HashSet<EntityIdentifier>();
        movable = new LinkedList<LocationInfo>();
        descriptor = current.getDescriptor();
        sourceLoc = current.getBreakpointLocation();

        loadEntities(current);
        positionComponents();
        positionFixedEntities(current);
        /*
        boolean anyNew = (previous == null);
        if (previous != null) {
            anyNew = positionOldEntities(previous);
        }
        anyNew = true;
        if (anyNew) {
            positionGlobals(current.getGlobals(), ReservedHorizontalMargin);
            positionNewEntities();
            repositionAllEntities();
        }
        */
    }

    /**
     * Load entities from the snapshot into this scene, with
     * placeholders for the location info.
     *
     * @param current
     */
    private void loadEntities(Snapshot snapshot) {
        for (Entity e : snapshot) {
            EntityIdentifier eid = e.getEntityIdentifier();
            entities.put(eid, e);
            locations.put(eid, new LocationInfo(e));
            if (e.getContainer() == null)
                baseObjectIDs.add(eid);
        }
    }

    /**
     * Set the locations of all entities that are components of larger
     * entities.
     */
    private void positionComponents() {
        for (EntityIdentifier eid : baseObjectIDs) {
            positionComponentsOf(eid);
        }
    }

    private static final double VerticalMargin = 0.2;
    private static final double VerticalSpacing = 0.1;
    private static final double HorizontalMargin = 0.25;

    /**
     * Position all internal components of entity eid
     *
     * @param eid identifier of a container of zero or more components
     *
     */
    private void positionComponentsOf(EntityIdentifier eid) {
        Entity e = entities.get(eid);
        System.err.println ("Layout: positioning components of " + eid);
        int maxHeight = -1;
        for (EntityIdentifier cEID : e.getComponents()) {
            positionComponentsOf(cEID);
        }
        double yOffset = VerticalMargin;
        double xOffset = HorizontalMargin;
        double minWidth = 0;
        double yAddition = 0.0;
        String description = e.getDescription();
        if (description.length() > 0) {
            // Leave room at the top for the label and value
            yOffset = 1.0 + VerticalMargin + VerticalSpacing;
            yAddition = 1.0 + VerticalSpacing;
            minWidth = 2 * HorizontalMargin + description.length();
        }
        LocationInfo loc = locations.get(eid);
        loc.setHeight(1 + maxHeight);
        Dimension2DDouble sz = layoutComponents(e, loc, xOffset, yOffset - VerticalMargin);
        sz.setSize(Math.max(minWidth, sz.getWidth()) + HorizontalMargin, sz.getHeight() + yAddition);
        loc.setSize(sz);
    }

    private void positionFixedEntities(Snapshot snapshot) {
        EntityIdentifier stack = snapshot.getRootEntity();
        LocationInfo sLoc = locations.get(stack);
        if (sLoc != null) {
            sLoc.setLoc(new Point(0.25, 0.0));
            anchors.anchorAt(stack, new Point2D.Double(0.0, 0.0));
        }
    }

    /**
     * Assign initial positions to any unfixed globals
     * at top of screen alongside the activation stack
     * 
     * @param set
     * @param xOffset
     */
    private void positionGlobals(Set<EntityIdentifier> set, double xOffset) {
        int count = 0;
        double y = 0.0;
        double height = 0.0;
        double x = 0.0;
        double maxWidth = 0.0;
        for (EntityIdentifier eid : set) {
            LocationInfo loc = locations.get(eid);
            if (loc != null && !loc.hasBeenPositioned()) {
                ++count;
                Dimension2DDouble sz = loc.getSize();
                if (count > 2 && x + sz.getWidth() > maxWidth) {
                    y += VerticalMargin + height;
                    height = 0.0;
                    x = HorizontalMargin;
                } else {
                    x += HorizontalMargin;
                }
                loc.setLoc(new Point(x + ReservedHorizontalMargin, y));
                movable.add(loc);
                x += sz.getWidth();
                maxWidth = Math.max(maxWidth, x);
                height = Math.max(height, sz.getHeight());
            }
        }
    }

    /**
     * Check to see if any of the base entities in this scene were
     * present in a prior layout. If so, copy their former locations into
     * this layout.
     *
     * As a side effect, builds the list movable of all base objects that
     * have not already been assigned a location.
     *
     * @param previous a prior scene
     * @return true if any base entities in this scene were not in the prior scene
     *         and therefore still have no location
     */
    private boolean positionOldEntities(Layout previous) {
        if (previous != null) {
            boolean anyNew = false;
            for (EntityIdentifier eid : baseObjectIDs) {
                LocationInfo newLocationInfo = locations.get(eid);
                boolean hasOldPosition = previous.baseObjectIDs.contains(eid);
                anyNew = anyNew || !hasOldPosition;
                if (!newLocationInfo.hasBeenPositioned()) {
                    movable.add(newLocationInfo);
                    if (hasOldPosition) {
                        Location formerLocation = previous.locations.get(eid).getLoc();
                        Location newLocation = new Point(formerLocation.getCoordinates());
                        newLocationInfo.setLoc(newLocation);
                    }
                }
            }
            return anyNew;
        } else {
            return true;
        }
    }

    static private Random random = new Random();
    static private final double InitialXRange = 100.0;
    static private final double InitialYRange = 10.0;

    private class MinimizePositionScore implements OptimizationProblem {

        private ArrayList<Variable> variables;

        public MinimizePositionScore(ArrayList<Variable> vars) {
            variables = vars;
        }

        @Override
        public ArrayList<Variable> getVariables() {
            return variables;
        }

        @Override
        public double objectiveFunction() {
            return positionScore();
        }

    }

    private void positionNewEntities() {
        ArrayList<Variable> newEntityVariables = new ArrayList<Variable>();
        HashSet<EntityIdentifier> newEntities = new HashSet<EntityIdentifier>();
        // Collect all the new entities
        for (EntityIdentifier eid : baseObjectIDs) {
            LocationInfo locInfo = locations.get(eid);
            if (!locInfo.hasBeenPositioned()) {
                newEntities.add(eid);
            }
        }

        boolean done = false;
        while (!done) {
            // Try to find a good position for at least one new entity
            done = true;
            for (EntityIdentifier eid : entities.keySet()) {
                LocationInfo locInfo = locations.get(eid);
                if (locInfo.isFixed(null)) {
                    // See if this already positioned entity has a connector to
                    // any of the new entities
                    Entity oldEntity = entities.get(eid);
                    for (Connector c : oldEntity.getConnections()) {
                        if (newEntities.contains(c.getDestination())) {
                            double angle = c.getMinAngle() + random.nextDouble() * (c.getMaxAngle() - c.getMinAngle());
                            PerimeterPoint pp = new PerimeterPoint(angle, locInfo);
                            Point2D p = pp.getCoordinates();
                            double dx = p.getX() - locInfo.getBBox().getCenterX();
                            double dy = p.getY() - locInfo.getBBox().getCenterY();
                            double d = Math.sqrt(dx * dx + dy * dy);
                            if (d == 0.0)
                                d = 1.0;
                            dx = dx / d;
                            dy = dy / d;
                            double x = p.getX() + c.getPreferredLength() * dx;
                            double y = p.getY() + c.getPreferredLength() * dy;
                            LocationInfo newInfo = locations.get(c.getDestination());
                            newInfo.setLoc(new Point(x, y));
                            done = false;
                            newEntities.remove(c.getDestination());
                            break;
                        }
                    }
                }

            }

            if (done) {
                // Fallback: random initial position
                for (EntityIdentifier eid : newEntities) {
                    LocationInfo locInfo = locations.get(eid);
                    double x = random.nextDouble() * (InitialXRange - ReservedHorizontalMargin)
                            + ReservedHorizontalMargin;
                    double y = random.nextDouble() * InitialYRange + InitialYRange / 2.0;
                    Point p = new Point(x, y);
                    locInfo.setLoc(p);
                    for (Variable v : p.getVariables()) {
                        newEntityVariables.add(v);
                    }
                    done = false;
                    newEntities.remove(eid);
                    break;
                }
            }
        }
        // Now solve the optimization problems to get a better position
        // for all new entities.
        MinimizePositionScore optProblem = new MinimizePositionScore(newEntityVariables);
        Optimizer opt = new Optimizer(optProblem);
        opt.solve(20.0, 0.25, 10000);
    }

    private void repositionAllEntities() {
        ArrayList<Variable> entityVariables = new ArrayList<Variable>();
        // Copy the locations of all non-anchored base objects into
        for (LocationInfo locInfo : movable) {
            Location loc = locInfo.getLoc();
            for (Variable v : loc.getVariables()) {
                entityVariables.add(v);
            }
        }
        // Now solve the optimization problems to get a better position
        // for all entities.
        MinimizePositionScore optProblem = new MinimizePositionScore(entityVariables);
        // System.err.println ("repositionAllEntities: " + entityVariables.size() + "
        // variables");
        Optimizer opt = new Optimizer(optProblem);
        opt.solve(20.0, 0.25, 10000);
    }

    /**
     * Arranges a list of components to pack a rectangular space whose upper left
     * corner is
     * specified.
     * 
     * @param container  entity whose components are to be inserted into the
     *                   snapshot
     * @param relativeTo upper left corner of the region where the components should
     *                   be placed
     * @param xOffset    distance from the left of relativeTo at which to start
     * @param yOffset    distance from the to of relativeTo at which to start
     */
    private Dimension2DDouble layoutComponents(Entity container, BoundedRegion relativeTo,
            double xOffset, double yOffset) {

        switch (container.getDirection()) {
            case Vertical:
                return layoutComponentsVertically(container, relativeTo, xOffset, yOffset);
            case Horizontal:
                return layoutComponentsHorizontally(container, relativeTo, xOffset, yOffset);
            case Square:
                return layoutComponentsInSquare(container, relativeTo, xOffset, yOffset);
            case HorizontalTree:
                return layoutComponentsInSquare(container, relativeTo, xOffset, yOffset);
            case VerticalTree:
                return layoutComponentsInSquare(container, relativeTo, xOffset, yOffset);
            default:
                return new Dimension2DDouble();
        }

    }

    /**
     * Arranges a list of components to pack a rectangular space whose upper left
     * corner is
     * specified.
     * 
     * @param container  entity whose components are to be inserted into the
     *                   snapshot
     * @param relativeTo upper left corner of the region where the components should
     *                   be placed
     * @param xOffset    distance from the left of relativeTo at which to start
     * @param yOffset    distance from the to of relativeTo at which to start
     */
    private Dimension2DDouble layoutComponentsHorizontally(Entity container, BoundedRegion relativeTo,
            double xOffset, double yOffset) {

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

    /**
     * Arranges a list of components to pack a rectangular space whose upper left
     * corner is
     * specified.
     * 
     * @param container  entity whose components are to be inserted into the
     *                   snapshot
     * @param relativeTo upper left corner of the region where the components should
     *                   be placed
     * @param xOffset    distance from the left of relativeTo at which to start
     * @param yOffset    distance from the to of relativeTo at which to start
     */
    private Dimension2DDouble layoutComponentsVertically(Entity container, BoundedRegion relativeTo,
            double xOffset, double yOffset) {

        // Arrange components into several rows, each with
        // maxComponentsPerRow columns.
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

    /**
     * Arranges a list of components to pack a rectangular space.
     * 
     * @param container  entity whose components are to be inserted into the
     *                   snapshot
     * @param relativeTo upper left corner of the region where the components
     *                   should be placed
     * @param xOffset    distance from the left of relativeTo at which to start
     * @param yOffset    distance from the to of relativeTo at which to start
     */
    private Dimension2DDouble layoutComponentsInSquare(Entity container, BoundedRegion relativeTo,
            double xOffset, double yOffset) {

        ArrayList<LinkedList<EntityIdentifier>> rows = new ArrayList<LinkedList<EntityIdentifier>>();
        ArrayList<Double> rowHeights = new ArrayList<>();
        ArrayList<Double> rowWidths = new ArrayList<>();

        // Apportion the components to different rows.
        // Arranges a list of components to pack a rectangular space.
        int numRows = 0;
        for (EntityIdentifier eid : container.getComponents()) {
            double totalRowHeight = sum(rowHeights);
            double maxRowWidth = max(rowWidths);

            double baseArea = totalRowHeight * maxRowWidth;

            // Suppose that we add a new row...
            LocationInfo loc = locations.get(eid);
            Dimension2DDouble sz = loc.getSize();
            double h = totalRowHeight + sz.getHeight();
            double w = Math.max(maxRowWidth, sz.getWidth());

            double bestDiffSoFar = Math.abs(h - w);
            int bestRowSoFar = numRows;

            for (int r = 0; r < numRows; ++r) {
                // Suppose that we add this to row r
                double h0 = Math.max(rowHeights.get(r), sz.getHeight());
                double w0 = Math.max(maxRowWidth,
                        rowWidths.get(r) + sz.getWidth());
                double newDiff = Math.abs(h0 - w0);
                double deltaHeight = Math.max(h0, rowHeights.get(r))
                        - rowHeights.get(r);
                double newArea = (totalRowHeight + deltaHeight) * w0;
                if ((newArea <= baseArea) ||
                        (newDiff < bestDiffSoFar)) {
                    bestDiffSoFar = newDiff;
                    bestRowSoFar = r;
                }
            }

            if (bestRowSoFar < numRows) {
                // Add this to an existing row.
                double y = sum(rowHeights, bestRowSoFar)
                        + bestRowSoFar * container.getSpacing();
                double h1 = sz.getHeight();
                rowHeights.set(bestRowSoFar,
                        Math.max(h1, rowHeights.get(bestRowSoFar)));
                double w1 = (rowWidths.get(bestRowSoFar) > 0.0) ? container.getSpacing() : HorizontalMargin;
                w1 += sz.getWidth() + rowWidths.get(bestRowSoFar);
                rowWidths.set(bestRowSoFar, w1);
                double x = rowWidths.get(bestRowSoFar) + (rowHeights.size() - 1) * container.getSpacing();
                loc.setLoc(new RelativePoint(x + xOffset, y + yOffset, Connections.LU, relativeTo));
                rows.get(bestRowSoFar).add(eid);
            } else {
                // Add this to a new row
                double x = 0.0;
                double y = sum(rowHeights)
                        + rows.size() * container.getSpacing();
                rows.add(new LinkedList<EntityIdentifier>());
                rowHeights.add(h + container.getSpacing());
                rowWidths.add(w);
                rows.get(numRows).add(eid);
                loc.setLoc(new RelativePoint(x + xOffset,
                        y + yOffset, Connections.LU, relativeTo));
                ++numRows;
            }
        }
        double totalRowHeight = sum(rowHeights) + VerticalMargin;
        double maxRowWidth = max(rowWidths) + HorizontalMargin;
        return new Dimension2DDouble(maxRowWidth, totalRowHeight);
    }

    private double max(ArrayList<Double> values) {
        if (values.size() == 0) {
            return 0.0;
        }
        double theMax = values.get(0);
        for (double d : values) {
            theMax = Math.max(theMax, d);
        }
        return theMax;
    }

    private double sum(ArrayList<Double> values) {
        double sum = 0.0;
        for (double d : values) {
            sum += d;
        }
        return sum;
    }

    private double sum(ArrayList<Double> values, int n) {
        double sum = 0.0;
        for (int i = 0; i < n; ++i) {
            sum += values.get(i);
        }
        return sum;
    }

    private double positionScore() {
        double obScore = 0.0;
        double connScore = 0.0;
        for (EntityIdentifier eid : baseObjectIDs) {
            obScore += baseObjectScore(eid);
        }
        for (EntityIdentifier eid : entities.keySet()) {
            Entity e = entities.get(eid);
            for (Connector conn : e.getConnections()) {
                connScore += connectorScore(conn);
            }
        }
        double score = OverlapForceMultiplier * obScore + connScore;
        return score;
    }

    private static double OverlapForceMultiplier = 10.0;
    private static double OverlapGutter = 1.0;
    private static double Gravity = 0.01;
    private static final float TextColorThreshold = 1.5f;

    private final static double ReservedHorizontalMargin = 40;

    private double baseObjectScore(EntityIdentifier eid) {
        /*
         * This is a piecewise linear function that peaks at M1*M2
         * when the center of two objects overlap and that falls to 0
         * when their centers are far enough apart that the objects are
         * separated by OverlapGutter.
         */
        LocationInfo loc = locations.get(eid);
        Rectangle2D bBox = loc.getBBox();
        double score = 0.0;
        for (EntityIdentifier other : baseObjectIDs) {
            if (!other.equals(eid)) {
                LocationInfo otherLoc = locations.get(other);
                Rectangle2D oBox = otherLoc.getBBox();

                double dx0 = bBox.getWidth() / 2.0 + oBox.getWidth() / 2.0 + OverlapGutter;
                double dy0 = bBox.getHeight() / 2.0 + oBox.getHeight() / 2.0 + OverlapGutter;

                double dx = Math.abs(bBox.getCenterX() - oBox.getCenterX());
                double dy = Math.abs(bBox.getCenterY() - oBox.getCenterY());

                if (dx < dx0 && dy < dy0) {
                    // Boxes overlap or nearly overlap
                    double fx = 1.0 - dx / dx0;
                    double fy = 1.0 - dy / dy0;

                    score += fx + fy;
                }
            }
        }

        // Artificial collision calculation to keep coordinates positive
        double dx = 0.0;
        double dy = 0.0;
        if (bBox.getX() < ReservedHorizontalMargin)
            dx = ReservedHorizontalMargin - bBox.getX();
        else
            dx = 0.0;
        if (bBox.getY() < 0)
            dy = -bBox.getY();
        else
            dy = 0.0;
        score += dx + dy;

        // Gravity pulls all objects gently towards 0,0
        double gravity = Math.abs(bBox.getX() - HorizontalMargin) + Math.abs(bBox.getY() - VerticalMargin);
        score += Gravity * gravity;

        return score;
    }

    private static final double TorsionTensionRatio = 5.0;

    private double connectorScore(Connector conn) {
        EntityIdentifier destination = conn.getDestination();
        EntityIdentifier source = conn.getSource();

        if (destination.equals(EntityIdentifier.nullID()) || destination.equals(source))
            return 0.0;

        LocationInfo destInfo = locations.get(destination);
        LocationInfo sourceInfo = locations.get(source);

        Location destCenter = new RelativePoint(0.0, 0.0, Connections.CC, destInfo);
        Location sourceCenter = new RelativePoint(0.0, 0.0, Connections.CC, sourceInfo);

        Location sourceExitLoc = new ClosestPointOnPerimeter(sourceInfo, destCenter, conn.getMinAngle(),
                conn.getMaxAngle());
        Location destEntryLoc = new ClosestPointOnPerimeter(destInfo, sourceCenter, 0.0, 360.0);

        Point2D sourceCenterPt = new Point2D.Double(sourceInfo.getBBox().getCenterX(),
                sourceInfo.getBBox().getCenterY());
        Point2D sourceExit = sourceExitLoc.getCoordinates();
        Point2D destEntry = destEntryLoc.getCoordinates();

        double len = sourceExit.distance(destEntry);
        double tension = Math.abs(len - conn.getPreferredLength());

        double dx1 = sourceExit.getX() - sourceCenterPt.getX();
        double dy1 = sourceExit.getY() - sourceCenterPt.getY();

        double d1 = Math.sqrt(dx1 * dx1 + dy1 * dy1);
        if (d1 == 0.0)
            d1 = 1.0;

        double dx2 = destEntry.getX() - sourceExit.getX();
        double dy2 = destEntry.getY() - sourceExit.getY();

        double d2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);
        if (d2 == 0.0)
            d2 = 1.0;

        double torsion = 1.0 - (dx1 * dx2 + dy1 * dy2) / (d1 * d2);
        return TorsionTensionRatio * torsion + tension / conn.getElasticity();
    }

    /**
     * Convert this state model to a DataPicture suitable for tweening and drawing.
     *
     * @return a DataPicture of this snapshot
     */
    public Frame toPicture() {
        Frame result = new Frame(descriptor, sourceLoc, this);
        for (EntityIdentifier eid : entities.keySet()) {
            renderInto(eid, result);
        }
        return result;
    }

    /**
     * Render this entity, and its outgoing connections in a frame
     * 
     * @param eid   ID of the entity to be rendered
     * @param frame the frame into which the entity should be rendered
     */
    private void renderInto(EntityIdentifier eid, Frame frame) {
        System.out.println("Rendering " + eid + " into a frame.");
        Entity e = entities.get(eid);
        LocationInfo loc = locations.get(eid);

        Point2D p = loc.getLoc().getCoordinates();
        float xx = (float) p.getX();
        float yy = (float) p.getY();
        Dimension2DDouble size = loc.getSize();
        int depth = loc.getHeight();
        Color color = e.getColor().toAWTColor();
        String description = e.getDescription();

        // background box
        frame.add(
                new Box(eid + "__box",
                        xx, yy, (float) size.getWidth(), (float) size.getHeight(),
                        color, 2 * depth + 2));

        float[] colorComponents = color.getRGBColorComponents(null);
        Color textColor = (colorComponents[0] + colorComponents[1] + colorComponents[2] >= TextColorThreshold)
                ? Color.black
                : Color.white;

        // Text value
        if (description.length() > 0) {
            frame.add(
                    new Text(eid + "__value", description,
                            xx + (float) size.getWidth() / 2.0f - ((float) description.length()) / 2.0f,
                            yy + (float) VerticalMargin,
                            textColor, 2 * depth + 1));
        }
        int connectorNum = 0;
        for (Connector connector : e.getConnections()) {
            renderInto(connector, connectorNum, frame);
            ++connectorNum;
        }
    }

    /**
     * Render this connector in a frame
     * 
     * @param eid   ID of the entity to be rendered
     * @param frame the frame into which the entity should be rendered
     */
    private void renderInto(Connector connector, int connectorNum, Frame frame) {
        EntityIdentifier destination = connector.getDestination();
        EntityIdentifier source = connector.getSource();

        int componentNum = connector.getComponentIndex();
        if (componentNum >= 0 && !destination.equals(EntityIdentifier.nullID())) {
            // This pointer is actually for an internal component of the destination object.
            Entity destEntity = entities.get(destination);
            List<EntityIdentifier> destComponents = destEntity.getComponents();
            if (componentNum < destComponents.size()) {
                destination = destComponents.get(componentNum);
            }
        }

        LocationInfo sourceInfo = locations.get(source);
        Location sourceCenterLoc = new RelativePoint(0.0, 0.0, Connections.CC, sourceInfo);
        Point2D sourceCenter = sourceCenterLoc.getCoordinates();

        String value = connector.getValue();
        if (value == null)
            value = "";
        String label = connector.getLabel();
        if (label == null)
            label = "";
        Color color = connector.getColor().toAWTColor();
        String id = source + "[" + connectorNum + "]=>";

        double minA = connector.getMinAngle();
        double maxA = connector.getMaxAngle();
        if (minA > maxA)
            maxA += 360.0;

        if (destination.equals(EntityIdentifier.nullID())) {
            double angle = random.nextDouble() * (maxA - minA) + minA;
            Location sourceExitLoc = new PerimeterPoint(angle, sourceInfo);
            Point2D exitPoint = sourceExitLoc.getCoordinates();
            if ((maxA - minA <= 45.0))
                frame.add(new Arrow(id, true,
                        exitPoint, null, color, 0));
            if (label.length() > 0 || value.length() > 0) {
                String description = label + ": " + value;
                Point2D pLabel = getLabelLoc(exitPoint, null, sourceCenter);
                frame.add(new Text(id + "*label", description,
                        (float) pLabel.getX(), (float) pLabel.getY(),
                        Color.black, 0));
            }
        } else if (destination != source) {
            LocationInfo destInfo = locations.get(destination);

            Location destCenterLoc = new RelativePoint(0.0, 0.0, Connections.CC, destInfo);

            Location sourceExitLoc = new ClosestPointOnPerimeter(sourceInfo, destCenterLoc,
                    connector.getMinAngle(), connector.getMaxAngle());
            Location destEntryLoc = new ClosestPointOnPerimeter(destInfo, sourceCenterLoc,
                    0.0, 360.0);

            Point2D exitPoint = sourceExitLoc.getCoordinates();
            Point2D entryPoint = destEntryLoc.getCoordinates();

            frame.add(new Arrow(id, (maxA - minA <= 45.0),
                    exitPoint, entryPoint, color, 0));
            if (label.length() > 0) {
                Point2D pLabel = getLabelLoc(exitPoint, entryPoint, sourceCenter);
                frame.add(new Text(id + "*label", label + ":",
                        (float) pLabel.getX(), (float) pLabel.getY(),
                        Color.black, 0));
            }
            if (value.length() > 0) {
                Point2D pValue = getValueLoc(exitPoint, entryPoint, sourceCenter);
                frame.add(new Text(id + "*value", value,
                        (float) pValue.getX(), (float) pValue.getY(),
                        Color.black, 0));
            }
        } else {
            double angle = random.nextDouble() * (maxA - minA) + minA;
            Location sourceExitLoc = new PerimeterPoint(angle, sourceInfo);
            Point2D exitPoint = sourceExitLoc.getCoordinates();
            int sense = 0;
            Rectangle2D sourceBox = sourceInfo.getBBox();
            if (exitPoint.getX() >= sourceBox.getMaxX())
                sense = 1;
            else if (exitPoint.getX() <= sourceBox.getMinX())
                sense = 3;
            else if (exitPoint.getY() >= sourceBox.getMaxY())
                sense = 2;
            frame.add(new ArrowToSelf(id, (maxA - minA <= 45.0),
                    exitPoint, sense, color, 0));
            if (label.length() > 0 || value.length() > 0) {
                String description = label + ": " + value;
                Point2D pLabel = getLabelLoc(exitPoint, exitPoint, sourceCenter);
                frame.add(new Text(id + "*label", description,
                        (float) pLabel.getX(), (float) pLabel.getY(),
                        Color.black, 0));
            }
        }
    }

    private Point2D getLabelLoc(Point2D exitPoint, Point2D entryPoint, Point2D sourceCenter) {
        double dx = 0.5;
        double dy = 0.5;
        if (exitPoint.getX() < sourceCenter.getX() + 0.1)
            dx = -dx;
        if (exitPoint.getY() < sourceCenter.getY() + 0.1)
            dy = -dy;
        return new Point2D.Double(exitPoint.getX() + dx, exitPoint.getY() + dy);
    }

    private Point2D getValueLoc(Point2D exitPoint, Point2D entryPoint, Point2D sourceCenter) {
        double frac = 0.67;
        if (entryPoint == null || entryPoint.equals(exitPoint))
            return getLabelLoc(exitPoint, entryPoint, sourceCenter);
        else {
            double x = exitPoint.getX() + frac * (entryPoint.getX() - exitPoint.getX());
            double y = exitPoint.getY() + frac * (entryPoint.getY() - exitPoint.getY());
            return new Point2D.Double(x, y);
        }
    }

    /**
     * Provided for testing purposes (and therefore restricted to package
     * visibility)
     * 
     * @return the entities
     */
    HashMap<EntityIdentifier, Entity> getEntities() {
        return entities;
    }

    /**
     * Provided for testing purposes (and therefore restricted to package
     * visibility)
     * 
     * @return the entities
     */
    Dimension2DDouble getSizeOf(EntityIdentifier eid) {
        LocationInfo loc = locations.get(eid);
        return loc.getSize();
    }

}
