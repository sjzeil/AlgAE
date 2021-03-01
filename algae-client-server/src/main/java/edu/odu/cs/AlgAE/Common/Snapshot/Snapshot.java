package edu.odu.cs.AlgAE.Common.Snapshot;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

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
    private HashMap<Identifier, LinkedList<Entity> > entities;

    
    /**
     * An informational string to appear in a status line when this snapshot is being displayed
     */
    private String descriptor;
    
    /**
     * The corresponding source code location to be shown while this snapshot is being displayed.
     */
    private SourceLocation breakpointLocation;
    
    /**
     * Entity representing the activation stack    
     */
    private EntityIdentifier activationStack;
    
    /**
     * Entities denoting global variables
     */
    private Set<EntityIdentifier> globals;

    
    
    /**
     * Create a new snapshot.
     */
    public Snapshot() {
        entities = new HashMap<Identifier, LinkedList<Entity>>();
        descriptor = "";
        breakpointLocation = new SourceLocation();
        activationStack = null;
        globals = new HashSet<EntityIdentifier>();
    }
    
    
    /**
     * Create a snapshot with a given description and breakpoint location
     *
     * @param description
     * @param breakpoint
     */
    public Snapshot(String description, SourceLocation breakpoint) {
        entities = new HashMap<Identifier, LinkedList<Entity>>();
        descriptor = description;
        breakpointLocation = breakpoint;
        activationStack = null;
        globals = new HashSet<EntityIdentifier>();
    }

    
    public void add (Entity entity)
    {
        EntityIdentifier eid = entity.getEntityIdentifier();
        Identifier id = eid.getObjectIdentifier();
        LinkedList<Entity> aliases = entities.get(id);
        if (aliases == null) {
            aliases = new LinkedList<Entity>();
            entities.put(id,  aliases);
        }
        boolean found = false;
        for (ListIterator<Entity> it = aliases.listIterator(); it.hasNext();) {
            Entity e = it.next();
            if (e.getEntityIdentifier().equals(eid)) {
                found = true;
                it.set(entity);
                break;
            }
        }
        if (!found) {
            aliases.add (entity);
        }
    }
    
    
    public void setGlobal (EntityIdentifier eid, boolean isGlobal)
    {
        if (isGlobal)
            globals.add(eid);
        else
            globals.remove(eid);
    }
    
    public boolean isGlobal (EntityIdentifier eid)
    {
        return globals.contains(eid);
    }
    

    public void remove (Entity entity)
    {
        EntityIdentifier eid = entity.getEntityIdentifier();
        globals.remove(eid);
        Identifier id = eid.getObjectIdentifier();
        LinkedList<Entity> aliases = entities.get(id);
        if (aliases != null) {
            for (ListIterator<Entity> it = aliases.listIterator(); it.hasNext();) {
                Entity e = it.next();
                if (e.getEntityIdentifier().equals(eid)) {
                    it.remove();
                    if (aliases.size() == 0) {
                        entities.remove(id);
                    }
                    return;
                }
            }
        }
    }


    private class EntityIterator implements Iterator<Entity> {
        private Iterator<Identifier> mapIterator;
        private Iterator<Entity> listIterator;
        
        EntityIterator()
        {
            mapIterator = getEntities().keySet().iterator();
            listIterator = null;
        }

        @Override
        public boolean hasNext() {
            return mapIterator.hasNext()
              || (listIterator != null && listIterator.hasNext());
        }

        @Override
        public Entity next() {
            while (listIterator == null || !listIterator.hasNext()) {
                if (mapIterator.hasNext()) {
                    Identifier id = mapIterator.next();
                    listIterator = getEntities().get(id).iterator();
                } else {
                    throw new NoSuchElementException();
                }
            }
            return listIterator.next();
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
    public Map<Identifier, LinkedList<Entity> > getEntities() {
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
    public EntityIdentifier getActivationStack() {
        return activationStack;
    }



    /**
     * @param activationStack the activationStack to set
     */
    public void setActivationStack(EntityIdentifier activationStack) {
        this.activationStack = activationStack;
    }



    /**
     * @return the globals
     */
    public Set<EntityIdentifier> getGlobals() {
        return globals;
    }


    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append (descriptor);
        buf.append ("@");
        buf.append (breakpointLocation);
        buf.append (": ");
        buf.append (activationStack);
        buf.append ("\n");
        buf.append ("entities: ");
        buf.append (entities.toString());
        buf.append ("\n");
        buf.append ("globals: ");
        buf.append (globals.toString());
    
        return buf.toString();
    }

    public boolean equals (Object o) {
        if (o == null)
            return false;
        try {
            Snapshot s = (Snapshot)o;
            return s.descriptor.equals(descriptor) && s.breakpointLocation.equals(breakpointLocation)
                    && (s.activationStack == activationStack || s.activationStack.equals(activationStack))
                    && s.globals.equals(globals) && s.entities.equals(entities);
                    
        } catch (Exception e) {
            return false;
        }
    }
    
    static public class SnapshotPersistenceDelegate extends DefaultPersistenceDelegate {
        protected void initialize(@SuppressWarnings("rawtypes") Class type, Object oldInstance,
                                  Object newInstance, Encoder out) {
            super.initialize(type, oldInstance,  newInstance, out);

            Snapshot oldSnap = (Snapshot)oldInstance;

            for (Entity e: oldSnap) {
                boolean isGlobal = oldSnap.globals.contains(e.getEntityIdentifier());
                out.writeStatement (new Statement(oldInstance, "add", new Object[]{ e } ));
                if (isGlobal) {
                    out.writeStatement (new Statement(oldInstance, "setGlobal",
                            new Object[]{ e.getEntityIdentifier(), new Boolean(isGlobal) } ));
                }
            }
        }
    }
    
    
}
