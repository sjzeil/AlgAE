/**
 * 
 */
package edu.odu.cs.AlgAE.Server.Utilities;

import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;
import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;

/**
 * @author zeil
 *
 */
public class LinkedList<T> 
    extends java.util.AbstractSequentialList<T> 
    implements CanBeRendered<LinkedList<T>>, Renderer<LinkedList<T>> {
    
    
    private LLNode first;
    private LLNode last;
    private int theSize;
    
    private RenderedReference<LLNode> firstRef; 
    private RenderedReference<LLNode> lastRef; 
    private boolean showingBackLinks;
    private boolean showingFirstLast;
    
    private class LLNode implements CanBeRendered<LLNode>, Renderer<LLNode> {

        T data;
        
        LLNode prev;
        LLNode next;
        
        boolean dead;
        
        public LLNode (T value, LLNode prv, LLNode nxt)
        {
            data = value;
            prev = prv;
            next = nxt;
            dead = false;
        }
        
        @Override
        public String getValue(LLNode obj) {
            return "";
        }

        @Override
        public Color getColor(LLNode obj) {
            if (dead)
                return Color.black;
            else
                return Color.green.darker();
        }

        @Override
        public List<Component> getComponents(LLNode obj) {
            java.util.ArrayList<Component> components = new java.util.ArrayList<>();
            components.add(new Component(data));
            return components;
        }

        @Override
        public List<Connection> getConnections(LLNode obj) {
            java.util.ArrayList<Connection> links = new java.util.ArrayList<Connection>();//!
            Connection c =  new Connection(next, 80.0, 80.0);//!
            links.add(c);//!
            if (showingBackLinks) {
                Connection c1 =  new Connection(prev, 260.0, 260.0);//!
                links.add(c1);//!
            }
            return links;//!
        }

        @Override
        public Renderer<LLNode> getRenderer() {
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
   
    private  class LLIterator implements java.util.ListIterator<T>,
            CanBeRendered<LLIterator>, Renderer<LLIterator> {
        
        LLNode current;
        
        RenderedReference<LinkedList<T>> theListRef;
        RenderedReference<LLNode> currentRef;

        public LLIterator(LinkedList<T> inList, int index) {
            if (index >= inList.theSize-1) {
                if (index == inList.theSize) {
                    current = null;
                } else if (index == inList.theSize-1){
                    current = inList.last;
                } else {
                    throw new IndexOutOfBoundsException(index);
                }
            } else {
                current = first;
                try {
                    for (int i = 0; i < index; ++i)
                    {
                        if (current != last)
                            current = current.next;
                        else
                            current = null;
                    }
                } catch (NullPointerException ex) {
                    throw new IndexOutOfBoundsException(index);
                }
            }
            theListRef = new RenderedReference<LinkedList<T>>(inList);
            currentRef = new RenderedReference<LinkedList<T>.LLNode>(current);
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            if (current == null)
                throw new NoSuchElementException();
            T saved = current.data;
            if (current != last)
                current = current.next;
            else
                current = null;
            return saved;
        }

        @Override
        public boolean hasPrevious() {
            return (current == null && last != null) 
                    || (current != null && current != first);  
        }

        @Override
        public T previous() {
            if (current != null) {
                if (current != first) 
                    current = current.prev;
                else
                    current = null;
            }
            else
                current = last;
            if (current == null)
                throw new NoSuchElementException();
            return current.data;
        }

        @Override
        public int nextIndex() {
            if (current == null)
                return theSize;
            else {
                int i = 0;
                LLNode p = first;
                while (p != current) {
                    p = p.next;
                    ++i;
                }
                return i;
            }
        }

        @Override
        public int previousIndex() {
            if (current == null)
                return theSize-1;
            else {
                int i = 0;
                LLNode p = first;
                while (p != current) {
                    p = p.next;
                    ++i;
                }
                return i-1;
            }
        }

        @Override
        public void remove() {
            if (current == null) {
              last = last.prev;
              if (last == null)
                  first = null;
              else
                  last.next = null;
            } else if (current.prev == null && current.next == null) {
                first = last = null;
                current = null;
            }
            else {
                current = current.prev;
                if (current.prev == null) {
                    first = current.next;
                    current.next.prev = null;
                    current.dead = true;
                } else if (current.next == null) {
                    last = current.prev;
                    current.prev.next = null;
                    current.dead = true;
                } else {
                    current.next.prev = current.prev;
                    current.prev.next = current.next;
                    current.dead = true;
                }
                current = current.next;
            }
            --theSize;
        }

        @Override
        public void set(T e) {
           LLNode prev = null;
           if (current != null) {
        	   prev = current.prev;
           } else {
        	   prev = last;
           }
           if (prev == null)
        	   throw new IllegalStateException();
           prev.data = e;
        }

        @Override
        public void add(T e) {
            if (current == null) {
                if (first == null) {
                    first = last = new LLNode(e, null, null);
                } else {
                    last.next = new LLNode(e, last, null);
                    last = last.next; 
                }
            } else if (current == first) {
                first = new LLNode(e, null, current);
                current.prev = first;
            } else {
                LLNode newNode = new LLNode(e, current.prev, current);
                current.prev.next = newNode;
                current.prev = newNode;
            }
            ++theSize;
        }

        
        
        // Rendering
        @Override
        public String getValue(LLIterator obj) {
            return "";
        }

        @Override
        public Color getColor(LLIterator obj) {
            return Color.green;
        }

        @Override
        public List<Component> getComponents(LLIterator obj) {
            ArrayList<Component> components = new ArrayList<>();
            components.add(new Component(theListRef, "list"));
            currentRef.set(current);
            components.add(new Component(currentRef, "current"));
            return components;
        }

        @Override
        public List<Connection> getConnections(LLIterator obj) {
            ArrayList<Connection> results = new ArrayList<>();
            return results;
        }

        @Override
        public Renderer<LLIterator> getRenderer() {
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
    
    
    

    /**
     * 
     */
    public LinkedList() {
        super();
        first = last = null; 
        theSize = 0;
        firstRef = new RenderedReference<LLNode>(first, 80, 100);
        lastRef = new RenderedReference<LLNode>(last, 80, 100);
        showingBackLinks = true;
        showingFirstLast = true;
    }


    /**
     * @param c
     */
    public LinkedList(Collection<? extends T> c) {
        super();
        first = last = null; 
        theSize = 0;
        for (T data: c) {
            add(data);
        }
        firstRef = new RenderedReference<>(first);
        lastRef = new RenderedReference<>(last);
        showingBackLinks = true;
        showingFirstLast = true;
    }

    @Override
    public void add(int index, T element) {
        ListIterator<T> it = listIterator(index);
        it.add(element);
    }
    
    @Override
    public T set (int index, T element) {
        ListIterator<T> it = listIterator(index);
        it.next();
        it.set(element);
        return element;
    }

    @Override
    public T get(int index) {
        LLIterator it = new LLIterator(this, index);
        if (it.current != null)
            return it.current.data;
        else
            throw new IndexOutOfBoundsException(index);
    }
    
    @Override
    public T remove (int index) {
        LLIterator it = new LLIterator(this, index);
        it.next();
        T removed = (it.current != null) ? it.current.data : last.data;
        it.remove();
        return removed;
    }

    @Override
    public int size() {
        return theSize;
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new LLIterator(this, index);
    }


    @Override
    public List<T> subList(int fromIndex, int toIndex)
    {
        LinkedList<T> result = new LinkedList<T>();
        result.first = new LLIterator(this, fromIndex).current;
        result.last = new LLIterator(this, toIndex).current;
        result.theSize = toIndex - fromIndex;
        return result;
    }
    
    //////// Rendering ////////////
    @Override
    public String getValue(LinkedList<T> obj) {
        return "";
    }

    @Override
    public Color getColor(LinkedList<T> obj) {
        return Color.green;
    }

    @Override
    public List<Component> getComponents(LinkedList<T> obj) {
        java.util.LinkedList<Component> components = new java.util.LinkedList<>();
        if (showingFirstLast) {
            firstRef.set(first);
            lastRef.set(last);
            components.add(new Component(firstRef, "first"));
            components.add(new Component(lastRef, "last"));
        }
        return components;
    }

    @Override
    public List<Connection> getConnections(LinkedList<T> obj) {
        java.util.LinkedList<Connection> connections = new java.util.LinkedList<Connection>();
        if (!showingFirstLast) {
            connections.add(new Connection(first, 80, 100));
        }
        return connections;
    }

    
    @Override
    public Renderer<LinkedList<T>> getRenderer() {
        return this;
    }


    public void showBackLinks(boolean b) {
        showingBackLinks = b;
    }

    public void showFirstLast(boolean b) {
        showingFirstLast = b;
    }


    @Override
    public boolean addAll(Collection<? extends T> collection) {
        boolean added = false;
        for (T t: collection) {
            add(t);
            added = true;
        }
        return added;
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
