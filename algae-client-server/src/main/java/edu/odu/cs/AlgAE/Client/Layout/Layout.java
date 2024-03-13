package edu.odu.cs.AlgAE.Client.Layout;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import edu.odu.cs.AlgAE.Client.DataViewer.Frames.Arrow;
import edu.odu.cs.AlgAE.Client.DataViewer.Frames.ArrowToSelf;
import edu.odu.cs.AlgAE.Client.DataViewer.Frames.Box;
import edu.odu.cs.AlgAE.Client.DataViewer.Frames.Frame;
import edu.odu.cs.AlgAE.Client.DataViewer.Frames.Text;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.BoundedRegion;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.ClosestPointOnPerimeter;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.Dimension2DDouble;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.Location;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.PerimeterPoint;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.Point;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.RelativePoint;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.RelativePoint.Connections;
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
    private String descriptor;
    private SourceLocation sourceLoc;

    /**
     * A subset of the total objects of the snapshot, consisting of those objects
     * that
     * are not components of other objects (i.e., container == null)
     *
     */
    private HashSet<EntityIdentifier> baseObjectIDs;


    /**
     * Create a new layout, similar to basedOn but reflecting any
     * anchorAt calls since the former layout was created.
     *
     * @param Snapshot the memory snapshot for which a layout is being created
     */
    public Layout(Snapshot current) {
        entities = new HashMap<EntityIdentifier, Entity>();
        locations = new HashMap<EntityIdentifier, LocationInfo>();
        baseObjectIDs = new HashSet<EntityIdentifier>();
        descriptor = current.getDescriptor();
        sourceLoc = current.getBreakpointLocation();

        loadEntities(current);
        positionComponents();
        positionFixedEntities(current);
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
     * @return max depth of recursion of this function
     *
     */
    int positionComponentsOf(EntityIdentifier eid) {
        Entity e = entities.get(eid);
        //System.err.println ("Layout: positioning components of " + eid);
        int maxDepth = -1;
        for (EntityIdentifier cEID : e.getComponents()) {
            int depth = positionComponentsOf(cEID);
            maxDepth = Math.max(maxDepth, depth);
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
        loc.setDepth(1 + maxDepth);
        Dimension2DDouble sz = layoutComponents(e, loc, xOffset, yOffset - VerticalMargin);
        sz.setSize(Math.max(minWidth, sz.getWidth()) + HorizontalMargin, sz.getHeight() + yAddition);
        loc.setSize(sz);
        return 1 + maxDepth;
    }

    private void positionFixedEntities(Snapshot snapshot) {
        EntityIdentifier memory = snapshot.getRootEntity();
        LocationInfo sLoc = locations.get(memory);
        if (sLoc != null) {
            sLoc.setLoc(new Point(0.25, 0.0));
        }
    }


    static private Random random = new Random();



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
                return new VerticalLayout(locations).layoutComponents(container, relativeTo, xOffset, yOffset);
            case Horizontal:
                return new HorizontalLayout(locations).layoutComponents(container, relativeTo, xOffset, yOffset);
            case Square:
                return new PackedLayout(locations).layoutComponents(container, relativeTo, xOffset, yOffset);
            case HorizontalTree:
                return new HorizontalSpanTreeLayout(locations, entities).layoutComponents(container, relativeTo, xOffset, yOffset);
            case VerticalTree:
                return new VerticalSpanTreeLayout(locations, entities).layoutComponents(container, relativeTo, xOffset, yOffset);
            default:
                return new Dimension2DDouble();
        }

    }



    private static final float TextColorThreshold = 1.5f;




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
        //System.err.println("Layout: Rendering " + eid + " into a frame.");
        Entity e = entities.get(eid);
        LocationInfo loc = locations.get(eid);

        Point2D p = loc.getLoc().getCoordinates();
        float xx = (float) p.getX();
        float yy = (float) p.getY();
        Dimension2DDouble size = loc.getSize();
        int depth = loc.getDepth();
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

    /**
     * Provided for testing purposes (and therefore restricted to package
     * visibility)
     * 
     * @return the entities
     */
    Location getLocationOf(EntityIdentifier eid) {
        LocationInfo loc = locations.get(eid);
        return loc.getLoc();
    }
}
