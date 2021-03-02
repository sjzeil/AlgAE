package edu.odu.cs.AlgAE.Demos;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.odu.cs.AlgAE.Animations.LocalJavaAnimation;
import edu.odu.cs.AlgAE.Server.LocalServer;
import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.MemoryModel.ActivationRecord;
import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;

public class TransitionDemo extends LocalJavaAnimation {

	public TransitionDemo() {
		super("Transition Demo");
	}

	@Override
	public String about() {
		return "Demo/test of tweening/transitions.";
	}

	
	
	public class Node implements CanBeRendered<Node>, Renderer<Node> {

		public String data;
		public Node left;
		public Node right;
		public ArrayList<String> values;
		public Color color = Color.magenta;

		@Override
		public Color getColor(Node obj) {
			return color;
		}

		@Override
		public List<Component> getComponents(Node obj) {
			LinkedList<Component> components = new LinkedList<Component>();
			for (int i = 0; i < values.size(); ++i) {
				String v = values.get(i);
				components.add(new Component(v));
			}
			return components;
		}

		@Override
		public List<Connection> getConnections(Node obj) {
			LinkedList<Connection> links = new LinkedList<Connection>();
			Connection c = new Connection(right, 135, 135);
			links.add (c);
			c = new Connection(left, 225, 225);
			links.add (c);
			return links;
		}

		@Override
		public int getMaxComponentsPerRow(Node obj) {
			return 3;
		}

		
		public Node (String data, Node lft, Node rt)
		{
			this.data = data;
			left = lft;
			right = rt;
			values = new ArrayList<String>();
		}
		
		public String toString() {
			return data;
		}
		
		public String getData()
		{
			return data;
		}
		

		@Override
		public Renderer<Node> getRenderer() {
			return this;
		}

		@Override
		public String getValue(Node obj) {
			return data;
		}
		
	}


	
	private Node root = new Node("abcdef", null, null);
	
	@Override
	public void buildMenu() {
		
		
		registerStartingAction(new MenuFunction() {

			@Override
			public void selected() {
				globalVar("root", root);
			}
			
		});
		
		
		register ("changeColor", new MenuFunction() {
			@Override
			public void selected() {
				root.color = Color.blue;
				LocalServer.activate(root).breakHere("change");
				root.color = Color.yellow;
				LocalServer.activate(root).breakHere("changed");
			}
		});

		register ("changeText", new MenuFunction() {
			@Override
			public void selected() {
				root.data = "X";
				LocalServer.activate(root).breakHere("change");
				root.data = "abcdef";
				LocalServer.activate(root).breakHere("changed");
				
			}
		});

		register ("createObject", new MenuFunction() {
			@Override
			public void selected() {
				ActivationRecord ar = LocalServer.activate(root);
				root.left = root.right = null;;
				ar.breakHere("change");
				root.left = new Node("A", null, null);
				ar.breakHere("changed");
				root.right = new Node("B", root.left, null);
				ar.breakHere("changed again");
			}
		});
		
		register ("shiftPointers", new MenuFunction() {
			@Override
			public void selected() {
				ActivationRecord ar = LocalServer.activate(root);
				root.left = root.right = null;
				ar.breakHere("start");
				Node A = new Node("A", null, null);
				root.left = A;
				Node B = new Node("B", root.left, null);
				root.right = B;
				ar.breakHere("change");
				root.left = B;
				ar.breakHere("changed");
				root.right = A;
				ar.breakHere("changed again");
			}
		});

		register ("dropPointers", new MenuFunction() {
			@Override
			public void selected() {
				root.left = root.right = null;
				Node A = new Node("A", null, null);
				root.left = A;
				Node B = new Node("B", root.left, null);
				root.right = B;
				LocalServer.activate(root).breakHere("change");
				root.left = null;
				LocalServer.activate(root).breakHere("changed");
				root.right = null;
				LocalServer.activate(root).breakHere("changed again");
			}
		});
		
		
	}

	
	
	public static void main (String[] args) {
		TransitionDemo demo = new TransitionDemo();
		demo.runAsMain();
	}

}
