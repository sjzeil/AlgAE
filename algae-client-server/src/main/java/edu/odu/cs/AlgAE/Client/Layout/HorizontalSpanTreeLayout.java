package edu.odu.cs.AlgAE.Client.Layout;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.odu.cs.AlgAE.Client.Layout.Coordinates.BoundedRegion;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.Dimension2DDouble;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.RelativePoint;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.RelativePoint.Connections;
import edu.odu.cs.AlgAE.Common.Snapshot.Connector;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity;
import edu.odu.cs.AlgAE.Common.Snapshot.EntityIdentifier;

/**
 * Arranges components that form a tree with the first component as its root,
 * with the root on the left and the tree growing to the right.
 */
public class HorizontalSpanTreeLayout implements LayoutModel {

    private HashMap<EntityIdentifier, LocationInfo> locations;
    private HashMap<EntityIdentifier, Entity> entities;
    private HashSet<EntityIdentifier> positioned;
    private HashSet<EntityIdentifier> topLevel;
    private Entity theSpanTree;

    private class TreeNode {
        public Entity data;
        public java.util.ArrayList<TreeNode> children;
        public Rectangle2D.Double subtreeBB;

        public TreeNode(Entity e) {
            data = e;
            children = new ArrayList<>();
            subtreeBB = new Rectangle2D.Double();
        }
    }

    public HorizontalSpanTreeLayout(HashMap<EntityIdentifier, LocationInfo> locations,
            HashMap<EntityIdentifier, Entity> entities) {
        this.locations = locations;
        this.entities = entities;
        positioned = new HashSet<>();
        topLevel = new HashSet<>();
    }

    @Override
    public Dimension2DDouble layoutComponents(
            Entity spanTree,
            BoundedRegion relativeTo,
            double xOffset,
            double yOffset) {
        theSpanTree = spanTree;
        for (EntityIdentifier eid : spanTree.getComponents()) {
            topLevel.add(eid);
        }
        TreeNode root = new TreeNode(null);
        root.subtreeBB = findBBOfRoot(root);
        setPositions(root, relativeTo, xOffset, yOffset, spanTree);
        System.err.println("SpanTree width is " + root.subtreeBB.getWidth());
        return new Dimension2DDouble(root.subtreeBB.getWidth(), root.subtreeBB.getHeight());
    }

    private void setPositions(TreeNode root, BoundedRegion relativeTo, double xOffset, double yOffset,
            Entity spanTree) {
        System.err.print("(" + ((root.data == null) ? "0" : root.data.getEntityIdentifier().toString()) + " ");
        System.err.print("" + root.subtreeBB.getWidth()
                + "*" + root.subtreeBB.getHeight() + " ");

        double xOffsetChildren = xOffset;
        if (root.data != null) {
            LocationInfo loc = locations.get(root.data.getEntityIdentifier());
            double centeredY = root.subtreeBB.getHeight() / 2.0 - loc.getSize().getHeight() / 2.0;
            centeredY = Math.max(centeredY, 0.0);
            loc.setLoc(
                    new RelativePoint(xOffset, yOffset + centeredY, Connections.LU, relativeTo));
            xOffsetChildren += loc.getSize().getWidth() + spanTree.getSpacing();
        }
        double yOffsetChildren = yOffset;
        boolean firstTime = true;
        for (TreeNode child : root.children) {
            if (!firstTime) {
                yOffsetChildren += spanTree.getSpacing();
            }
            firstTime = false;
            setPositions(child, relativeTo, xOffsetChildren, yOffsetChildren, spanTree);
            LocationInfo childLoc = locations.get(child.data.getEntityIdentifier());
            yOffsetChildren += childLoc.getSize().getHeight();
        }
        System.err.println(")");
    }

    private Rectangle2D.Double findBBOfRoot(TreeNode root) {
        Rectangle2D.Double bb = new Rectangle2D.Double();
        EntityIdentifier newRoot = theSpanTree.getComponents().get(0);
        if (!positioned.contains(newRoot)) {
            positioned.add(newRoot);
            TreeNode child = new TreeNode(entities.get(newRoot));
            root.children.add(child);
            Rectangle2D.Double bb2 = findBBOfChildren(child);
            bb.height = bb2.height;
            bb.width = bb2.width;
        }

        root.subtreeBB = bb;
        return bb;
    }

    private Rectangle2D.Double findBBOfChildren(TreeNode root) {
        boolean atLeastOneChild = false;
        boolean firstTime = true;
        Rectangle2D.Double bb = new Rectangle2D.Double();

        java.util.LinkedList<EntityIdentifier> componentQueue = new java.util.LinkedList<>();
        componentQueue.add(root.data.getEntityIdentifier());

        while (!componentQueue.isEmpty()) {
            EntityIdentifier componentEID = componentQueue.remove();
            positioned.add(componentEID);
            Entity component = entities.get(componentEID);
            componentQueue.addAll(component.getComponents());
            for (Connector connector : component.getConnections()) {
                EntityIdentifier destEID = connector.getDestination();
                if (!destEID.isNull()) {
                    if (topLevel.contains(destEID) && !positioned.contains(destEID)) {
                        Entity destination = entities.get(destEID);
                        TreeNode child = new TreeNode(destination);
                        root.children.add(child);
                        positioned.add(destEID);
                        atLeastOneChild = true;

                        Rectangle2D.Double bb2 = findBBOfChildren(child);
                        if (firstTime) {
                            bb.height = bb2.height;
                            bb.width = bb2.width;
                            firstTime = false;
                        } else {
                            bb.height += theSpanTree.getSpacing();
                            bb.height += bb2.height;
                            bb.width = Math.max(bb.width, bb2.width);
                        }
                    }
                }
            }
            LocationInfo rootLoc = locations.get(root.data.getEntityIdentifier());
            if (atLeastOneChild) {
                bb.width = rootLoc.getSize().getWidth()
                        + theSpanTree.getSpacing()
                        + bb.width;
                bb.height = Math.max(bb.height, 
                    0.5 * rootLoc.getSize().getHeight() + 0.5 * bb.height );
                bb.height = Math.max(bb.height, rootLoc.getSize().getHeight());
            } else {
                bb.width = rootLoc.getSize().getWidth();
                bb.height = rootLoc.getSize().getHeight();
            }
        }
        root.subtreeBB = bb;
        return bb;
    }
}
