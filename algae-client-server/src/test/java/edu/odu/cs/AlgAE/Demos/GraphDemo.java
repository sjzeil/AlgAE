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

public class GraphDemo extends LocalJavaAnimation {

	public GraphDemo() {
		super("Graph Demo");
	}

	@Override
	public String about() {
		return "This is a\ndemo of connections within a container.";
	}

	
	
	public class Graph implements CanBeRendered<Graph>, Renderer<Graph> {

		public class Vertex implements CanBeRendered<Vertex>, Renderer<Vertex> {
			String name;

			Vertex(String nm) {
				name = nm;
				vertices.add(this);
			}

			@Override
			public String getValue(Vertex obj) {
				return name;
			}

			@Override
			public Color getColor(Vertex obj) {
				return null;
			}

			@Override
			public List<Component> getComponents(Vertex obj) {
				return null;
			}

			@Override
			public List<Connection> getConnections(Vertex obj) {
				List<Connection> connections = new LinkedList<>();
				for (Edge e: edges) {
					if (e.source == this) {
						connections.add(new Connection(e.dest));
					}
				}
				return connections;
			}

			@Override
			public Directions getDirection() {
				return Directions.Horizontal;
			}

			@Override
			public Double getSpacing() {
				return Renderer.DefaultSpacing;
			}

			@Override
			public Boolean getClosedOnConnections() {
				return false;
			}

			@Override
			public Renderer<Vertex> getRenderer() {
				return this;
			}
		}

		public class Edge {
			Vertex source;
			Vertex dest;

			Edge (Vertex s, Vertex d) {
				source = s;
				dest = d;
				edges.add(this);
			}
		}

		private String label;
		private ArrayList<Vertex> vertices;
		private ArrayList<Edge> edges;
		
		public Graph (String nm)
		{
			label = nm;
			edges = new ArrayList<>();
			vertices = new ArrayList<>();
		}
		
		public Vertex addVertex(String name) {
			return new Vertex(name);
		}

		public Edge addEdge(Vertex from, Vertex to) {
			return new Edge(from, to);
		}
		
		public void clear ()
		{
			edges.clear();
			vertices.clear();
		}

		@Override
		public Color getColor(Graph obj) {
			return Color.green.brighter();
		}

		@Override
		public List<Component> getComponents(Graph obj) {
			List<Component> components = new ArrayList<Component>();
			for (Vertex v: vertices) {
				components.add(new Component(v, v.name));
			}
			return components;
		}

		@Override
		public List<Connection> getConnections(Graph obj) {
			return new LinkedList<Connection>();
		}

		@Override
		public String getValue(Graph obj) {
			return label;
		}

		
		public String toString() {
			return "" + label + " " + vertices + " " + edges;
		}
		

		@Override
		public Renderer<Graph> getRenderer() {
			return this;
		}


		@Override
		public Directions getDirection() {
			return Directions.Horizontal;
		}


		@Override
		public Double getSpacing() {
			return 4.0 * Renderer.DefaultSpacing;
		}


		@Override
		public Boolean getClosedOnConnections() {
			return true;
		}
		
	}


	
	private Graph g; 
	
	@Override
	public void buildMenu() {
		
		
		
		registerStartingAction(new MenuFunction() {

			@Override
			public void selected() {
				g = new Graph("graph");
				globalVar("g", g);
			}
			
		});
		
		
		register ("disconnected", new MenuFunction() {
			@Override
			public void selected() {
				ActivationRecord aRec = LocalServer.activate(g);
				aRec.breakHere("disconnected");
				g.clear();
				Graph.Vertex a = g.addVertex("A");
				Graph.Vertex b = g.addVertex("B");
				g.addVertex("C");
				g.addVertex("D");
				g.addEdge(a, b);
				aRec.breakHere("done");
			}
		});


		register ("tree", new MenuFunction() {
			@Override
			public void selected() {
				ActivationRecord aRec = LocalServer.activate(g);
				aRec.breakHere("tree");
				g.clear();
				Graph.Vertex a = g.addVertex("A");
				Graph.Vertex b = g.addVertex("B");
				Graph.Vertex c = g.addVertex("C");
				Graph.Vertex d = g.addVertex("D");
				aRec.breakHere("vertices only");
				g.addEdge(a, b);
				aRec.breakHere("edge from A to B");
				g.addEdge(a, c);
				aRec.breakHere("edge from A to C");
				g.addEdge(b, d);
				aRec.breakHere("edge from B to D");
			}
		});


		register ("graph", new MenuFunction() {
			@Override
			public void selected() {
				ActivationRecord aRec = LocalServer.activate(g);
				aRec.breakHere("tree");
				g.clear();
				Graph.Vertex a = g.addVertex("A");
				Graph.Vertex b = g.addVertex("B");
				Graph.Vertex c = g.addVertex("C");
				Graph.Vertex d = g.addVertex("D");
				aRec.breakHere("vertices only");
				g.addEdge(a, b);
				g.addEdge(a, c);
				g.addEdge(b, d);
				g.addEdge(d, a);
				g.addEdge(d, b);
				aRec.breakHere("done");
			}
		});



		
	}

	
	
	public static void main (String[] args) {
		GraphDemo demo = new GraphDemo();
		demo.runAsMain();
	}

}
