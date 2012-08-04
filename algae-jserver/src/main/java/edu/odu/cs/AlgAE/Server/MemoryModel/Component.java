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
		
	
	public Component (Object comp, String label) {
		component = comp;
		this.label = label;
	}
	
	public Component (Object comp) {
		component = comp;
		this.label = null;
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
