/**
 * Anchors.java
 *
 * A collection of information about objects that have been "anchored"
 * at a particular screen location for all layouts (usually because a
 * user has dragged an object to a specific location).
 *
 * Created: 6/7/2011
 *
 * @author Steven J. Zeil
 * @version
 */

package edu.odu.cs.AlgAE.Client.Layout;

import java.awt.geom.Point2D;
import java.util.HashMap;

import edu.odu.cs.AlgAE.Common.Snapshot.EntityIdentifier;



public class Anchors {

    
    private HashMap <EntityIdentifier, Point2D> anchors;


    
    public Anchors()
    {
        anchors    = new HashMap<EntityIdentifier, Point2D>();
    }


    /**
     * Force an entity to always be drawn at a fixed position
     * in any future layouts where it appears.
     *
     * @param eid
     * @param position
     */
    public void anchorAt (EntityIdentifier eid, Point2D position)
    {
        anchors.put(eid, position);
    }
    
    /**
     * Find the fixed position, if any, at which
     * an entity should be positioned in all layouts.
     *
     * @param eid
     * @return position or null
     */
    public Point2D getAnchor (EntityIdentifier eid)
    {
        return anchors.get(eid);
    }


}
