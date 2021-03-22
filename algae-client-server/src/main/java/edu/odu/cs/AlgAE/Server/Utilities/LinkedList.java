/**
 * 
 */
package edu.odu.cs.AlgAE.Server.Utilities;

import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

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
    
    private RenderedReference<LLNode> firstref; 
    private RenderedReference<LLNode> lastref; 
    private boolean showingBackLinks;
    
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
        public int getMaxComponentsPerRow(LLNode obj) {
            return 1;
        }

        @Override
        public Renderer<LLNode> getRenderer() {
            return this;
        }
        
    }
   
    private  class LLIterator implements java.util.ListIterator<T>,
            CanBeRendered<LLIterator>, Renderer<LLIterator> {
        
        LinkedList<T> theList;
        LLNode current;
        
        RenderedReference<LinkedList<T>> theListref;
        RenderedReference<LLNode> currentref;

        public LLIterator(LinkedList<T> inList) {
            theList = inList;
            current = null;
            theListref = new RenderedReference<LinkedList<T>>(theList);
            currentref = new RenderedReference<LinkedList<T>.LLNode>(current);
        }

        public LLIterator(LinkedList<T> inList, int index) {
            theList = inList;
            if (index >= inList.theSize-1) {
                if (index == inList.theSize) {
                    current = null;
                } else if (index == inList.theSize-1){
                    current = inList.last;
                } else {
                    throw new IndexOutOfBoundsException(index);
                }
            } else {
                current = theList.first;
                try {
                    for (int i = 1; i < index; ++i)
                        current = current.next;
                } catch (NullPointerException ex) {
                    throw new IndexOutOfBoundsException(index);
                }
            }
            theListref = new RenderedReference<LinkedList<T>>(theList);
            currentref = new RenderedReference<LinkedList<T>.LLNode>(current);
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
            if (current != null)
                current = current.next;
            return saved;
        }

        @Override
        public boolean hasPrevious() {
            return (current == null && theList.last != null) 
                    || (current != null && current.prev != null);  
        }

        @Override
        public T previous() {
            if (current != null)
                current = current.prev;
            else
                current = theList.last;
            if (current == null)
                throw new NoSuchElementException();
            return current.data;
        }

        @Override
        public int nextIndex() {
            if (current == null)
                return theList.theSize;
            else {
                int i = 0;
                LLNode p = theList.first;
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
                return theList.theSize-1;
            else {
                int i = 0;
                LLNode p = theList.first;
                while (p != current) {
                    p = p.next;
                    ++i;
                }
                return i-1;
            }
        }

        @Override
        public void remove() {
            if (current == null)
                return;
            if (current.prev == null && current.next == null)
                theList.first = theList.last = null;
            else if (current.prev == null) {
                theList.first = current.next;
                current.next.prev = null;
                current.dead = true;
            } else if (current.next == null) {
                theList.last = current.prev;
                current.prev.next = null;
                current.dead = true;
            } else {
                current.next.prev = current.prev;
                current.prev.next = current.next;
                current.dead = true;
            }
            --theList.theSize;
        }

        @Override
        public void set(T e) {
           if (current != null)
               current.data = e;
        }

        @Override
        public void add(T e) {
            if (current == null) {
                if (theList.first == null) {
                    theList.first = theList.last = new LLNode(e, null, null);
                } else {
                    theList.last.next = new LLNode(e, theList.last, null);
                    theList.last = theList.last.next; 
                }
            } else if (current == theList.first) {
                theList.first = new LLNode(e, null, current);
                current.prev = theList.first;
            } else {
                LLNode newNode = new LLNode(e, current.prev, current);
                current.prev.next = newNode;
                current.prev = newNode;
            }
            ++theList.theSize;
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
            components.add(new Component(theListref, "list"));
            currentref.set(current);
            components.add(new Component(currentref, "current"));
            return components;
        }

        @Override
        public List<Connection> getConnections(LLIterator obj) {
            ArrayList<Connection> results = new ArrayList<>();
            return results;
        }

        @Override
        public int getMaxComponentsPerRow(LLIterator obj) {
            return 1;
        }

        @Override
        public Renderer<LLIterator> getRenderer() {
            return this;
        }

    }
    
    
    

    /**
     * 
     */
    public LinkedList() {
        super();
        first = last = null; 
        theSize = 0;
        firstref = new RenderedReference<LLNode>(first);
        lastref = new RenderedReference<LLNode>(last);
        showingBackLinks = true;
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
        firstref = new RenderedReference<>(first);
        lastref = new RenderedReference<>(last);
        showingBackLinks = true;
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
        T removed = it.current.data;
        it.remove();
        return removed;
    }

    @Override
    public int size() {
        return theSize;
    }

    public void trimToSize() {
    }
    
    @Override
    public ListIterator<T> listIterator(int index) {
        return new LLIterator(this, index);
    }


    @Override
    public String toString() {
    	return "List of size " + theSize;
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
        firstref.set(first);
        lastref.set(last);
        components.add(new Component(firstref, "first"));
        components.add(new Component(lastref, "last"));
        return components;
    }

    @Override
    public List<Connection> getConnections(LinkedList<T> obj) {
        return new java.util.LinkedList<Connection>();
    }

    @Override
    public int getMaxComponentsPerRow(LinkedList<T> obj) {
        return 1;
    }

    @Override
    public Renderer<LinkedList<T>> getRenderer() {
        return this;
    }


    public void showBackLinks(boolean b) {
        showingBackLinks = b;
    }



}
