package edu.odu.cs.AlgAE.Server.Utilities;

import java.awt.Color;
import java.util.List;

import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;
import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;

/**
 * An IndexedArray shadows a real array and can be rendered in place of
 * that array.  It offers the ability to decorate individual elements of the
 * array with the name of an integer index denoting that position in the array.
 * 
 * @author zeil
 *
 */
public class IndexedArray<T> 
    implements CanBeRendered<IndexedArray<T>>, Renderer<IndexedArray<T>> {
    
    
    private String labelPrefix = "[";
    private String labelSuffix = "]";

    
    private static class IndexEntry {
        public DiscreteInteger index;
        public String name;
        
        public IndexEntry (DiscreteInteger theIndex, String theName) {
            index = theIndex;
            name = theName;
        }

        public boolean equals(Object o) {
            if (o instanceof IndexEntry) {
                IndexEntry ie = (IndexEntry)o;
                return name.equals(ie.name);
            } else
                return false;
        }
    }
    
    private java.util.Stack<java.util.ArrayList<IndexEntry>> indexStack;
    
    private static class Label implements CanBeRendered<Label>, Renderer<Label> {
        
        private String label;
        
        public Label  (String theLabel)
        {
            label = theLabel;
        }

        @Override
        public String getValue(Label obj) {
            return label;
        }

        @Override
        public Color getColor(Label obj) {
            return new Color(1.0f, 1.0f, 1.0f, 0.0f);
       }

        @Override
        public List<Component> getComponents(Label obj) {
            return new java.util.LinkedList<Component>();
        }

        @Override
        public List<Connection> getConnections(Label obj) {
            return new java.util.LinkedList<Connection>();
        }

        @Override
        public Renderer<Label> getRenderer() {
            return this;
        }

        @Override
        public Directions getDirection() {
            return Directions.Vertical;
        }

        @Override
        public Double getSpacing() {
            return Renderer.DefaultSpacing;
        }

        @Override
        public Boolean getClosedOnConnections() {
            return false;
        }
        
    }
    
    private static class Cell<T> implements CanBeRendered<Cell<T>>, Renderer<Cell<T>> {
        
        private IndexedArray<T> inList;
        private int position;

        public Cell(IndexedArray<T> IndexedArray, int i) {
            inList = IndexedArray;
            position = i;
        }

        @Override
        public String getValue(Cell<T> obj) {
            return "";
        }

        @Override
        public Color getColor(Cell<T> obj) {
            return new Color(1.0f, 1.0f, 1.0f, 0.0f);
        }

        @Override
        public List<Component> getComponents(Cell<T> obj) {
            java.util.LinkedList<Component> components = new java.util.LinkedList<>(); 
            components.add (new Component(inList.data[position]));
            
            if (inList.indexStack.size() > 0) {
                java.util.ArrayList<IndexEntry> indices = inList.indexStack.peek();
                for (int i = 0; i < indices.size(); ++i) {
                    if (indices.get(i).index.get() == position) {
                        String label = inList.labelPrefix + indices.get(i).name + inList.labelSuffix;
                        components.add(new Component(new Label(label)));
                    }
                }
            }
            return components;
        }

        @Override
        public List<Connection> getConnections(Cell<T> obj) {
            return new java.util.LinkedList<Connection>();
        }

        @Override
        public Renderer<Cell<T>> getRenderer() {
            return this;
        }

        @Override
        public Directions getDirection() {
            if (inList.getDirection() == Directions.Vertical)
                return Directions.Horizontal;
            else
                return Directions.Vertical;
        }

        @Override
        public Double getSpacing() {
            return Renderer.DefaultSpacing;
        }

        @Override
        public Boolean getClosedOnConnections() {
            return false;
        }
        
    }
    
    private Object[] data;
    private Object[] cells;
    private Directions direction;
    private Color color;
    private boolean showingNumbers;

    /**
     * 
     */
    public IndexedArray(T[] theArray) {
        data = theArray;
        cells = new Object[data.length];
        for (int i = 0; i < data.length; ++i) {
            cells[i] = new Cell<T>(this, i);
        }
        indexStack = new java.util.Stack<>();
        direction = Directions.Horizontal;
        color = new Color(0, 0, 0, 0);
        showingNumbers = true;
    }


    

    //////// Indexing /////////////
    
    public void pushIndices() {
        indexStack.push(new java.util.ArrayList<>());
    }
    
    public void popIndices() {
        if (indexStack.size() > 0) {
            indexStack.pop();
        }
    }
    
    public void indexedBy(DiscreteInteger intVar, String name) {
        if (indexStack.size() == 0)
            pushIndices();
        java.util.ArrayList<IndexEntry> indices = indexStack.peek();
        IndexEntry newEntry = new IndexEntry(intVar, name);
        indices.remove(newEntry);
        indices.add(newEntry);
    }
    
    public void indexedBy(int intVar, String name) {
        if (indexStack.size() == 0)
            pushIndices();
        java.util.ArrayList<IndexEntry> indices = indexStack.peek();
        IndexEntry newEntry = new IndexEntry(new DiscreteInteger(intVar), name);
        indices.remove(newEntry);
        indices.add(newEntry);
    }

    public void removeIndex(String name) {
        if (indexStack.size() == 0)
            pushIndices();
        java.util.ArrayList<IndexEntry> indices = indexStack.peek();
        for (int i = 0; i < indices.size(); ++i)
        {
            if (indices.get(i).name.equals(name)) {
                indices.remove(i);
                return;
            }
        }
    }


    
    //////// Rendering ////////////

    public void showNumbers(boolean tf)
    {
        showingNumbers = tf;
    }

    @Override
    public String getValue(IndexedArray<T> obj) {
        return "";
    }

    @Override
    public Color getColor(IndexedArray<T> obj) {
        return color;
    }

    @Override
    public List<Component> getComponents(IndexedArray<T> obj) {
        java.util.LinkedList<Component> components = new java.util.LinkedList<>(); 
        int counter = 0;
        for (Object c: cells) {
            Component comp = (showingNumbers)? 
                new Component(c, "" + counter) : new Component(c);
            components.add (comp);
            ++counter;
        }
        return components;
    }

    @Override
    public List<Connection> getConnections(IndexedArray<T> obj) {
        return new java.util.LinkedList<Connection>();
    }

    @Override
    public Renderer<IndexedArray<T>> getRenderer() {
        return this;
    }

    public void renderHorizontally(boolean horizontal) {
        if (horizontal)
            direction = Directions.Horizontal;
        else
            direction = Directions.Vertical;
    }

    public void setDirection(Directions dir) {
        direction = dir;
    }

    public void setColor(Color c) {
        color = c;
    }
    public void setIndexLabels (String prefix, String suffix) {
        labelPrefix = prefix;
        labelSuffix = suffix;
    }


    @Override
    public Directions getDirection() {
        return direction;
    }

    @Override
    public Double getSpacing() {
        return 0.0;
    }

    @Override
    public Boolean getClosedOnConnections() {
        return false;
    }


}
