package edu.odu.cs.AlgAE.Server.MemoryModel;


/**
 * A component of a compound object, such as a data field or and array element.
 *
 * Components may optionally be labeled.
 *
 * @author zeil
 *
 */
public class Component {

    private Object actual;
    private String label;
    private Component container;
        
    public Component (Object comp, Component container, String label) {
        actual = comp;
        this.label = label;
        this.container = container;
        nullCheck();
    }
    
    public Component (Object comp, String label) {
        actual = comp;
        this.label = label;
        this.container = null;
        nullCheck();
    }

    public Component (Object comp, Component container) {
        actual = comp;
        this.label = null;
        this.container = container;
        nullCheck();
    }
    
    public Component (Object comp) {
        actual = comp;
        this.label = null;
        this.container = null;
        nullCheck();
    }
    
    private void nullCheck() {
    	if (actual == null)
    		actual = "\u2400";   // null components rendered as the Unicode NUL character
    }
    
    public Object getActualObject() {
        return actual;
    }
    
    public String getLabel() {
        return label;
    }
    
    public String toString() {
        if (label == null)
            return "<" + actual.toString() + ">";
        else
            return "<" + label + ":" + actual.toString() + ">";
    }
    
    
    /**
     * @return the container
     */
    public Component getContainer() {
        return container;
    }

    /**
     * @param container the container to set
     */
    public void setContainer(Component container) {
        this.container = container;
    }

    
    
}
