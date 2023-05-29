/**
 * 
 */
package edu.odu.cs.AlgAE.Server.Utilities;

import java.awt.Color;
import java.util.Collection;
import java.util.List;

import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;
import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;

/**
 * @author zeil
 *
 */
public class ArrayList<T> 
    extends java.util.AbstractList<T> 
    implements CanBeRendered<ArrayList<T>>, Renderer<ArrayList<T>> {
    
    
    private String labelPrefix = "[";
    private String labelSuffix = "]";

    
    private static class IndexEntry {
        public DiscreteInteger index;
        public String name;
        
        public IndexEntry (DiscreteInteger theIndex, String theName) {
            index = theIndex;
            name = theName;
        }
    }
    
    private java.util.ArrayList<java.util.ArrayList<IndexEntry>> indexStack;
    
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
        
        private ArrayList<T> inList;
        private int position;

        public Cell(ArrayList<T> arrayList, int i) {
            inList = arrayList;
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
            components.add (new Component(inList.data.get(position)));
            
            if (inList.indexStack.size() > 0) {
                java.util.ArrayList<IndexEntry> indices = inList.indexStack.get(inList.indexStack.size()-1);
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
    
    private java.util.ArrayList<T> data;
    private java.util.ArrayList<Cell<T>> cells;
    private Directions direction;
    private Color color;

    /**
     * 
     */
    public ArrayList() {
        super();
        data = new java.util.ArrayList<T>();
        cells = new java.util.ArrayList<Cell<T>>();
        indexStack = new java.util.ArrayList<>();
        direction = Directions.Horizontal;
        color = null;
    }

    /**
     * @param initialCapacity
     */
    public ArrayList(int initialCapacity) {
        super();
        data = new java.util.ArrayList<T>(initialCapacity);
        cells = new java.util.ArrayList<Cell<T>>(initialCapacity);
        indexStack = new java.util.ArrayList<>();
        direction = Directions.Horizontal;
    }

    /**
     * @param c
     */
    public ArrayList(Collection<? extends T> c) {
        super();
        data = new java.util.ArrayList<T>(c); 
        cells = new java.util.ArrayList<Cell<T>>();
        for (int i = 0; i < data.size(); ++i) {
            cells.add(new Cell<T>(this, i));
        }
        indexStack = new java.util.ArrayList<>();
        direction = Directions.Horizontal;
    }

    @Override
    public void add(int index, T element) {
        data.add(index, element);
        cells.add(new Cell<T>(this, data.size()-1));
    }
    
    @Override
    public T set (int index, T element) {
        data.set(index, element);
        return element;
    }
    public T set (DiscreteInteger index, T element) {
        data.set(index.get(), element);
        return element;
    }
    
    @Override
    public T get(int index) {
        return data.get(index);
    }
    public T get(DiscreteInteger index) {
        return data.get(index.get());
    }
    
    @Override
    public T remove (int index) {
        T removed = data.remove(index);
        cells.remove(cells.size()-1);
        return removed;
    }

    @Override
    public int size() {
        return data.size();
    }

    public void trimToSize() {
    }
    

    //////// Indexing /////////////
    
    public void pushIndices() {
        indexStack.add(new java.util.ArrayList<>());
    }
    
    public void popIndices() {
        if (indexStack.size() > 0) {
            indexStack.remove(indexStack.size()-1);
        }
    }
    
    public void indexedBy(DiscreteInteger intVar, String name) {
        if (indexStack.size() == 0)
            pushIndices();
        java.util.ArrayList<IndexEntry> indices = indexStack.get(indexStack.size()-1);
        indices.add(new IndexEntry(intVar, name));
    }
    
    public void removeIndex(String name) {
        if (indexStack.size() == 0)
            pushIndices();
        java.util.ArrayList<IndexEntry> indices = indexStack.get(indexStack.size()-1);
        for (int i = 0; i < indices.size(); ++i)
        {
            if (indices.get(i).name.equals(name)) {
                indices.remove(i);
                return;
            }
        }
    }


    
    //////// Rendering ////////////
    @Override
    public String getValue(ArrayList<T> obj) {
        return "";
    }

    @Override
    public Color getColor(ArrayList<T> obj) {
        return color;
    }

    @Override
    public List<Component> getComponents(ArrayList<T> obj) {
        java.util.LinkedList<Component> components = new java.util.LinkedList<>(); 
        for (Cell<T> c: cells) {
            components.add (new Component(c));
        }
        return components;
    }

    @Override
    public List<Connection> getConnections(ArrayList<T> obj) {
        return new java.util.LinkedList<Connection>();
    }

    @Override
    public Renderer<ArrayList<T>> getRenderer() {
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
    public boolean addAll(Collection<? extends T> arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addAll'");
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
