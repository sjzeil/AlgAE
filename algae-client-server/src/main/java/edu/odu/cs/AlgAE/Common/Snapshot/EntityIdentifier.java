package edu.odu.cs.AlgAE.Common.Snapshot;

import java.io.Serializable;

/**
 * Unique identifiers for each entity in the model.
 *
 * One object may map onto several distinct entities to support the
 * illusion that a given object is simultaneously a component of
 * multiple compound objects (or comprises multiple distinct components
 * of a single parent: e.g., arrays of Strings or Integers may contain
 * many duplicate references to one object, but we still prefer to render
 * these as if they were distinct.)
 *
 *  Consequently, the Entity identifier contains the object identifier
 *  but also encodes the parent container and a label or other component
 *  indicator.
 *
 * @author zeil
 *
 */
        
public class EntityIdentifier implements Serializable {
    
    
    private String label;
    private int id;
    


    
    /**
     * Used for XML decoding only
     *
     */
    public EntityIdentifier () {
        this.label = "null";
        id = 0;
    }

    


    public EntityIdentifier(String label, int id) {
        this.label = label;
        this.id = id;
    }




    /**
     * The special ID value used for null pointers
     *
     */
    public static EntityIdentifier nullID() {
        return new EntityIdentifier();
    }
        
    
          
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(label);
        result.append("#");
        result.append(id);
        return result.toString();
    }
    

    public boolean equals (Object obj)
    {
        if (obj instanceof EntityIdentifier) {
            EntityIdentifier eid = (EntityIdentifier)obj;
            return (eid.id == id) && (id == 0 || label.equals(eid.label));
        } else
            return false;
    }
    

    public boolean isNull() {
        return id == 0;
    }
    
    public int hashCode ()
    {
        return label.hashCode() + 7549 * id;
    }




    public String getLabel() {
        return label;
    }




    public int getID() {
        return id;
    }
    

}
