package edu.odu.cs.AlgAE.Client.Layout;

import edu.odu.cs.AlgAE.Client.Layout.Coordinates.BoundedRegion;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.Dimension2DDouble;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.FreeOrFixed;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.Location;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.Point;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.IdentityHashMap;

public class LocationInfo implements BoundedRegion {
        private Dimension2DDouble size;
        private Location loc;
        private int depth;
        private Entity describes;

        public LocationInfo(Entity e) {
            size = null;
            loc = null;
            describes = e;
            depth = 0;
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
        public void setDepth(int h) {
            this.depth = h;
        }

        /**
         * @return the height
         */
        public int getDepth() {
            return depth;
        }

        public String toString() {
            StringBuffer result = new StringBuffer();
            result.append("describes:");
            result.append(describes.getEntityIdentifier());
            result.append(", height:");
            result.append(depth);
            result.append(", loc:");
            result.append(loc);
            result.append(", size:");
            result.append(size);
            return result.toString();
        }
    }

