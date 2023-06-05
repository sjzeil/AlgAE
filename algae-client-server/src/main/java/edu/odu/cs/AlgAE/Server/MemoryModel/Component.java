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

    private Object component;
    private String label;
    private Component container;
        
    
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

    public Component (Object comp, Component container, String label) {
        component = comp;
        this.label = label;
        this.container = container;
    }
    
    public Component (Object comp, String label) {
        component = comp;
        this.label = label;
        this.container = null;
    }

    public Component (Object comp, Component container) {
        component = comp;
        this.label = null;
        this.container = container;
    }
    
    public Component (Object comp) {
        component = comp;
        this.label = null;
        this.container = null;
    }
    
    
    public Object getComponentObject() {
        return component;
    }
    
    public String getLabel() {
        return label;
    }
    
    public String toString() {
        if (label == null)
            return "<" + component.toString() + ">";
        else
            return "<" + label + ":" + component.toString() + ">";
    }
    
    
    
}
