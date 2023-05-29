package edu.odu.cs.AlgAE.Demos;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.odu.cs.AlgAE.Animations.LocalJavaAnimation;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;
import edu.odu.cs.AlgAE.Server.LocalServer;
import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.MemoryModel.ActivationRecord;
import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;

public class HubDemo extends LocalJavaAnimation {

	public HubDemo() {
		super("Hub Demo");
	}

	@Override
	public String about() {
		return "This is a\ndemo of connections.";
	}

	
	
	public class Hub implements CanBeRendered<Hub>, Renderer<Hub> {

		private char c;
		private ArrayList<Hub> edges;
		private ArrayList<Integer> lengths;
		private ArrayList<Double> angles;
		
		public Hub (char c)
		{
			this.c = c;
			edges = new ArrayList<Hub>();
			lengths = new ArrayList<Integer>();
			angles = new ArrayList<Double>();
		}
		
		
		public void add (Hub h, int len, double angle)
		{
			edges.add (h);
			lengths.add (len);
			angles.add (angle);
			ActivationRecord aRec = LocalServer.activate(center);
			aRec.refParam("h", h).breakHere("added");
		}
		
		public void clear ()
		{
			edges.clear();
			lengths.clear();
			angles.clear();
			ActivationRecord aRec = LocalServer.activate(this);
			aRec.breakHere("cleared");
		}

		@Override
		public Color getColor(Hub obj) {
			return Color.green.brighter();
		}

		@Override
		public List<Component> getComponents(Hub obj) {
			return new LinkedList<Component>();
		}

		@Override
		public List<Connection> getConnections(Hub obj) {
			LinkedList<Connection> links = new LinkedList<Connection>();
			for (int i = 0; i < edges.size(); ++i) {
				Connection c =  new Connection(edges.get(i), angles.get(i), angles.get(i));
				c.setPreferredLength((double)lengths.get(i));
				links.add(c);
			}
			return links;
		}

		@Override
		public String getValue(Hub obj) {
			return "" + c;
		}

		
		public String toString() {
			return "" + c + " " + edges + " " + angles;
		}
		

		@Override
		public Renderer<Hub> getRenderer() {
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


	
	private Hub center; 
	
	@Override
	public void buildMenu() {
		
		
		
		registerStartingAction(new MenuFunction() {

			@Override
			public void selected() {
				center = new Hub('a');
				globalVar("center", center);
			}
			
		});
		
		
		register ("add1", new MenuFunction() {
			@Override
			public void selected() {
				ActivationRecord aRec = LocalServer.activate(center);
				aRec.breakHere("Add 1");
				center.add(new Hub('b'), 2, 5.0);
				aRec.breakHere("done");
			}
		});


		register ("add4", new MenuFunction() {
			@Override
			public void selected() {
				ActivationRecord aRec = LocalServer.activate(center);
				aRec.breakHere("Add 4");
				center.add(new Hub('b'), 2, 5.0);
				center.add(new Hub('c'), 4, 95.0);
				center.add(new Hub('d'), 8, 185.0);
				center.add(new Hub('e'), 12, 275.0);
				aRec.breakHere("done");
			}
		});

		register ("clear", new MenuFunction() {
			@Override
			public void selected() {
				LocalServer.activate(center).breakHere("clear1");
				center.clear();
				LocalServer.activate(center).breakHere("clear2");
			}
		});

		
	}

	
	
	public static void main (String[] args) {
		HubDemo demo = new HubDemo();
		demo.runAsMain();
	}

}
