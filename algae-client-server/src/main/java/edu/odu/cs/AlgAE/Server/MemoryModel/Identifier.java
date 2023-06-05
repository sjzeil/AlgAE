package edu.odu.cs.AlgAE.Server.MemoryModel;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import edu.odu.cs.AlgAE.Common.Snapshot.Entity;
import edu.odu.cs.AlgAE.Common.Snapshot.EntityIdentifier;

/**
 * Unique identifiers for objects in memory, with support for the local Java server
 * which needs to map objects to identifiers and vice versa.
 *
 * @author zeil
 *
 */
public class Identifier {

    private static final boolean keepClassNamesForDebugging = true;

    private static final int hashTableSize = 7919; // prime

    private static class HashTableEntry {
        WeakReference<Object> key;
        int id;

        public HashTableEntry(Object obj, int identifier) {
            key = new WeakReference<Object>(obj);
            id = identifier;
        }
    }

    private static class Bucket extends LinkedList<HashTableEntry> {}

    private static Bucket[] identifiers = new Bucket[hashTableSize];

    private static int nextIdentifier = 1;

    private String className;
    protected int id;

    static HashMap<Identifier, WeakReference<Object>> objectsFor
        = new HashMap<>();

    private int getPossibleID(Object obj) {
        if (obj == null) {
            return 0;
        }
        int hash = (obj.hashCode() & 0x7fffffff) % hashTableSize;
        Bucket bucket = identifiers[hash];
        if (bucket == null) {
            return 0;
        }
        for (ListIterator<HashTableEntry> iter = bucket.listIterator(); iter.hasNext();) {
            HashTableEntry entry = iter.next();
            Object keyObj = entry.key.get();
            if (keyObj == null) {
                // This object is no longer in memory. Remove from the identifier table.
                iter.remove();
            } else if (keyObj == obj) {
                // Yes, that's ==, not equals. We are deliberately testing for
                // object identity.
                return entry.id;
            }
        }
        return 0;
    }


    private int addID(Object obj) {
        if (obj != null) {
            int hash = (obj.hashCode() & 0x7fffffff)  % hashTableSize;
            Bucket bucket = identifiers[hash];
            if (bucket == null) {
                bucket = new Bucket();
                identifiers[hash] = bucket;
            }
            bucket.add (new HashTableEntry(obj, nextIdentifier));
            ++nextIdentifier;
            return nextIdentifier-1;
        } else {
            return 0;
        }
    }

    public Identifier()
    {
        className = "";
        id = 0;
    }

    public Identifier(Object instance) {
        if (instance != null) {
            if (keepClassNamesForDebugging) {
                className = instance.getClass().getName();
            }
            id = getPossibleID(instance);
            if (id == 0) {
                id = addID(instance);
                objectsFor.put(this, new WeakReference<Object>(instance));
            }
        } else {
            if (keepClassNamesForDebugging) {
                className = "null";
            }
            id = 0;
        }
    }

    public Identifier (EntityIdentifier eid) {
        className = eid.getLabel();
        id = eid.getID();
        if (!objectsFor.containsKey(this)) {
            throw new IdentifierError("Constructed invalid Identifier from entity ID " + eid);
        }
    }

    public String toString()
    {
        return className + "@" + id;
    }

    public int hashCode() {
        return id;
    }

    public boolean equals (Object obj) {
        if (obj == null || ! (obj instanceof Identifier)) {
            return false;
        }
        return id == ((Identifier)obj).id;
    }

    public boolean isNull() {
        return id == 0;
    }

    public EntityIdentifier asEntityIdentifier() {
        return new EntityIdentifier(className, id);
    }

    /**
     * Return the object to which this identifier refers.
     * 
     * @return an object or null if the identifier is null or if
     *    it refers to an object that has been garbage collected.
     */
    public Object getObject() {
        if (isNull()) {
            return null;
        } else {
            WeakReference<Object> ref = objectsFor.get(this);
            return ref.get();
        }
    }

}
