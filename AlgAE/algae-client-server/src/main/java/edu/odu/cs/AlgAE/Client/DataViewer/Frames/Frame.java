package edu.odu.cs.AlgAE.Client.DataViewer.Frames;

import java.util.HashMap;
import java.util.Iterator;

import edu.odu.cs.AlgAE.Client.Layout.Layout;
import edu.odu.cs.AlgAE.Common.Snapshot.SourceLocation;


public class Frame implements Iterable<DataShape> {
    
    private HashMap<String, DataShape> shapes;
    private String message;
    private SourceLocation sourceLoc;
    private Layout keyFor;
    
    public Frame(String message, SourceLocation sloc) {
        this.message = message;
        shapes = new HashMap<String, DataShape>();
        sourceLoc = sloc;
        keyFor = null;
    }
        
    public Frame(String message, SourceLocation sloc, Layout derivedFrom) {
        this.message = message;
        shapes = new HashMap<String, DataShape>();
        sourceLoc = sloc;
        keyFor = derivedFrom;
    }

    public Iterator<DataShape> iterator() {
        return shapes.values().iterator();
    }
    
    
    public void add (DataShape ds) {
        shapes.put (ds.getID(), ds);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public SourceLocation getLocation()
    {
        return sourceLoc;
    }
    
    public String toString() {
        return shapes.keySet().toString();
    }


    /**
     * Returns the Layout (if any) from which this Frame was generated
     * @return original layout or null if this Frame was generated in some other fashion (e.g., by tweening)
     */
    public Layout getKeyFor() {
        return keyFor;
    }

    /**
     * Indicates whether this is a key frame.
     * @return true if this is a key frame
     */
    public boolean isKey() {
        return keyFor != null;
    }
    
    /**
     * Provided for testing purposes only
     *
     * @return
     */
    public Iterator<String> shapeKeys() {
        return shapes.keySet().iterator();
    }
}
