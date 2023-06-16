package edu.odu.cs.AlgAE.Common.Snapshot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * A snapshot is a model of the current data state in terms of a graph of connected
 * Entities.  The graph is "rooted" at an activation stack and at a set of global entities.
 *
 * The complete graph is formed from the transitive closure over the component and connection
 * relations starting from those roots.
 *
 * @author zeil
 *
 */
public class Snapshot  implements Iterable<Entity> {
    
    /**
     *  Provides access to all objects in this snapshot, indexed by their identifiers.
     *
     */
    private HashMap<EntityIdentifier, Entity> entities;

    
    /**
     * An informational string to appear in a status line when this snapshot is being displayed
     */
    private String descriptor;
    
    /**
     * The corresponding source code location to be shown while this snapshot is being displayed.
     */
    private SourceLocation breakpointLocation;
    
    /**
     * The root of the entity containment tree
     */
    private EntityIdentifier rootEntity;
    
    
    
    /**
     * Create a new snapshot.
     */
    public Snapshot() {
        entities = new HashMap<>();
        descriptor = "";
        breakpointLocation = new SourceLocation();
        rootEntity = null;
    }
    
    
    /**
     * Create a snapshot with a given description and breakpoint location
     *
     * @param description
     * @param breakpoint
     */
    public Snapshot(String description, SourceLocation breakpoint) {
        entities = new HashMap<>();
        descriptor = description;
        breakpointLocation = breakpoint;
        rootEntity = null;
    }

    
    public void add (Entity entity)
    {
        EntityIdentifier eid = entity.getEntityIdentifier();
        entities.put(eid,  entity);
    }
    
    

    public void remove (Entity entity)
    {
        EntityIdentifier eid = entity.getEntityIdentifier();
        entities.remove(eid);
    }


    private class EntityIterator implements Iterator<Entity> {
        
        private Iterator<Entity> theIterator;
        
        EntityIterator()
        {
            theIterator = entities.values().iterator();
        }

        @Override
        public boolean hasNext() {
            return theIterator.hasNext();
        }

        @Override
        public Entity next() {
            return theIterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        
    }

    @Override
    public Iterator<Entity> iterator() {
        return new EntityIterator();
    }




    /**
     * @return the entities
     */
    public Map<EntityIdentifier, Entity> getEntities() {
        return entities;
    }


    /**
     * @return the descriptor
     */
    public String getDescriptor() {
        return descriptor;
    }



    /**
     * @param descriptor the descriptor to set
     */
    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }



    /**
     * @return the breakpointLocation
     */
    public SourceLocation getBreakpointLocation() {
        return breakpointLocation;
    }



    /**
     * @param breakpointLocation the breakpointLocation to set
     */
    public void setBreakpointLocation(SourceLocation breakpointLocation) {
        this.breakpointLocation = breakpointLocation;
    }



    /**
     * @return the activationStack
     */
    public EntityIdentifier getRootEntity() {
        return rootEntity;
    }



    /**
     * @param activationStack the activationStack to set
     */
    public void setRootEntity(EntityIdentifier activationStack) {
        this.rootEntity = activationStack;
    }





    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append (descriptor);
        buf.append ("@");
        buf.append (breakpointLocation);
        buf.append (": ");
        buf.append (rootEntity);
        buf.append ("\n");
        buf.append ("entities: ");
        buf.append (printEntityTree(rootEntity, ""));
        buf.append ("\n");
    
        return buf.toString();
    }

    private String printEntityTree(EntityIdentifier eid, String indent) {
        if (eid == null || eid.isNull()) {
            return indent + "null\n";
        } else {
           StringBuffer buf = new StringBuffer();
           buf.append(indent + "(" + eid.toString() + "\n");
           Entity entity = entities.get(eid);
           for (EntityIdentifier child: entity.getComponents()) {
              buf.append(printEntityTree(child, indent + "   "));
           }
           buf.append(indent + ")\n");
           return buf.toString();
        }
    }


    public boolean equals (Object o) {
        if (o == null)
            return false;
        try {
            Snapshot s = (Snapshot)o;
            return s.descriptor.equals(descriptor) && s.breakpointLocation.equals(breakpointLocation)
                    && (s.rootEntity == rootEntity || s.rootEntity.equals(rootEntity))
                    && s.entities.equals(entities);
                    
        } catch (Exception e) {
            return false;
        }
    }
    
    
    
}
