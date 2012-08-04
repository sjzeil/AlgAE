package edu.odu.cs.AlgAE.Server.Rendering;

import java.awt.Color;
import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;

import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;

/**
 * The default renderer uses toString() as a value, a randomly generated color 
 * (based upon a hash of the object's type) and no components or connections.
 * 
 * @author zeil
 *
 */
public class DefaultRenderer<T> implements Renderer<T> {

	public DefaultRenderer() {
		
	}
	
	
	@Override
	public Color getColor(T obj) {
		String className = obj.getClass().getName();
		int hc = Math.abs(className.hashCode());
		float red = ((float)(hc % 5)) * 0.25f;
		hc = hc / 5;
		float green = ((float)(hc % 5)) * 0.25f;
		hc = hc / 5;
		float blue = ((float)(hc % 5)) * 0.25f;
		return new Color (red, green, blue);
	}

	@Override
	public List<Component> getComponents(T obj) {
		String className = obj.getClass().getName();
		if (className.startsWith("[")) {
			// This is an array
			int len = Array.getLength(obj);
			LinkedList<Component> components = new LinkedList<Component>();
			for (int i = 0; i < len; ++i)
				components.add (new Component(Array.get(obj, i)));
			return components;
		} else if (obj instanceof Iterable<?>) {
			LinkedList<Component> componentsL = new LinkedList<Component>();
			Iterable<?> collection = (Iterable<?>)obj;
			for (Object component: collection) {
				componentsL.add(new Component(component));
			}
			return componentsL;
		} else {
			return new LinkedList<Component>();
		}
	}

	@Override
	public List<Connection> getConnections(T obj) {
		return new LinkedList<Connection>();
	}

	@Override
	public int getMaxComponentsPerRow(T obj) {
		String className = obj.getClass().getName();
		if (className.startsWith("[")) {
			// This is an array
			int cnt = 0;
			while (cnt < className.length() && className.charAt(cnt) == '[')
				++cnt;
			return (cnt % 2 == 0) ? 1 : 100;
		} else {
			return 1;
		}
	}

	@Override
	public String getValue(T obj) {
		String className = obj.getClass().getName();
		if (className.startsWith("[")) {
			return "";
		} else if (obj instanceof Iterable<?>) {
			return null;
		} else {
			return obj.toString();
		}
	}

}
