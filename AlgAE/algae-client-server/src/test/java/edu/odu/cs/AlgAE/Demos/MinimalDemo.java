package edu.odu.cs.AlgAE.Demos;

import static edu.odu.cs.AlgAE.Server.LocalAnimation.algae;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import edu.odu.cs.AlgAE.Server.LocalAnimation;
import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.Animations.LocalJavaAnimationApplet;
import edu.odu.cs.AlgAE.Server.MemoryModel.ActivationRecord;
import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;
import edu.odu.cs.AlgAE.Server.Utilities.Index;

public class MinimalDemo extends LocalJavaAnimationApplet {

	public MinimalDemo() {
		super("Minimal Demo");
	}

	@Override
	public String about() {
		return "This is a\nminimal demo.";
	}

	private int c = 0;
	
	
	public class LLNode implements CanBeRendered<LLNode> {
		public class NodeRenderer implements Renderer<LLNode> {

			@Override
			public Color getColor(LLNode obj) {
				return Color.magenta;
			}

			@Override
			public List<Component> getComponents(LLNode obj) {
				return new LinkedList<Component>();
			}

			@Override
			public List<Connection> getConnections(LLNode obj) {
				LinkedList<Connection> links = new LinkedList<Connection>();
				Connection c = new Connection(next, 85, 95);
				c.setLabel("next");
				links.add (c);
				return links;
			}

			@Override
			public int getMaxComponentsPerRow(LLNode obj) {
				return 1;
			}

			@Override
			public String getValue(LLNode obj) {
				return data;
			}

		}
		public String data;
		public LLNode next;
		
		public LLNode (String data, LLNode nxt)
		{
			this.data = data;
			next = nxt;
		}
		
		public String toString() {
			return data;
		}
		
		public String getData()
		{
			return data;
		}
		
		public LLNode getNext()
		{
			return next;
		}

		@Override
		public Renderer<LLNode> getRenderer() {
			return new NodeRenderer();
		}
		
	}

	public class LList implements CanBeRendered<LList> {
		public class ListRenderer implements Renderer<LList> {

			@Override
			public Color getColor(LList obj) {
				return Color.cyan;
			}

			@Override
			public List<Component> getComponents(LList obj) {
				return new LinkedList<Component>();
			}

			@Override
			public List<Connection> getConnections(LList obj) {
				Connection firstC = new Connection(first, 220, 230);
				firstC.setLabel("first");
				Connection lastC = new Connection(last, 130, 140);
				lastC.setLabel("last");
				List<Connection> links = new LinkedList<Connection>();
				links.add (firstC);
				links.add (lastC);
				return links;
			}

			@Override
			public int getMaxComponentsPerRow(LList obj) {
				return 1;
			}

			@Override
			public String getValue(LList obj) {
				return "";
			}

		}

		private LLNode first;
		private LLNode last;
		
		public LList ()
		{
			first = last = null;
		}

		
		public void addFirst (String d)
		{
			ActivationRecord active = LocalAnimation.activate(this);//!
			active.param("d",d).breakHere("starting addFirst");//!
			if (first == null) {
				active.breakHere("adding first node");//!
				first = last = new LLNode (d, null);
			} else {
				active.breakHere("adding node");//!
//!				show_this_instead();
				first = new LLNode (d, first);
			}
			active.breakHere("added first");//!
		}
		
		public boolean contains (String value)
		{
			ActivationRecord active = LocalAnimation.activate(this);
			LLNode current = first;
			active.param("value",value).refVar("current", current).breakHere("contains: start at beginning");//!
			while (current != null) {
				active.refVar("current", current).breakHere("check contents of node");//!
				if (current.data.equals(value))//!
//!				if (current->data == value)  // fake C++
				{
					active.refVar("current", current).breakHere("contains: found it");//!
					return true;
				}
				active.refVar("current", current).breakHere("contains: advance to next node");//!
				current = current.next;
			}
			active.refVar("current", current).breakHere("contains: have looked at all nodes");//!
			return false;
		}
		
		public void clear()
		{
			first = last = null;
		}

		public void addLast (String d)
		{
			if (first == null) {
				first = last = new LLNode (d, null);
			} else {
				last.next = new LLNode (d, null);
				last = last.next;
			}
		}

		//@Override
		public Renderer<LList> getRenderer() {
			return new ListRenderer();
		}
		
	}

	
	private LList list = new LList();

	
	private void findSelected() {
		LocalAnimation.activate(list).breakHere("Prompt for input");
			String value = algae().promptForInput("Search for ...", "[0-9]+");
			boolean found = list.contains (value);
			if (found) {
				out.println ("Found it!");
			} else {
				out.println ("Could not find it.");
			}
		}
	

	
	@Override
	public void buildMenu() {
		
		
		registerStartingAction(new MenuFunction() {

			@Override
			public void selected() {
				list = new LList();
				globalVar("list", list);
			}
			
		});
		
		
		register ("addFirst", new MenuFunction() {
			@Override
			public void selected() {
				++c;
				list.addFirst("" + c);
			}
		});

		register ("find", new MenuFunction() {
			@Override
			public void selected() {
				findSelected();
/*				Animation.activate(list).breakHere("Prompt for input").var("list", list);
				String value = promptForInput("Search for ...", "[0-9]+");
				boolean found = list.contains (value);
				if (found) {
					Animation.out.println ("Found it!");
				} else {
					Animation.out.println ("Could not find it.");
				}*/
			}
		});

		register ("clear", new MenuFunction() {
			@Override
			public void selected() {
				list.clear();
			}
		});

		register ("lots of locals", new MenuFunction() {
			@Override
			public void selected() {
				ActivationRecord arec = LocalAnimation.activate(list);
				//arec.breakHere("lots of locals").refVar("list", list);
				//arec.breakHere("lots of locals").refVar("list", list).var("j", 1);
				int[] array = new int[5];
				for (int n = 0; n < 5; ++n)
					array[n] = n;
				//arec.breakHere("lots of locals").refVar("list", list).var("j", 1).var("array", array);
				Index i = new Index(1, array);
				//arec.breakHere("lots of locals").refVar("list", list).var("j", 1).var("array", array).var("i", i);
				//arec.breakHere("lots of locals").refVar("list", list).var("j", 1).var("array", array).var("i", i).var("k", 4);
				arec.refVar("list", list).var("j", 1).var("array", array).var("i", i).var("k", 4).breakHere("lots of locals");
				for (int n = 0; n < 5; ++n)
					arec.var("array[" + n + "]", array[n]);
				arec.breakHere("local expressions");
			}
		});
		
/*		register ("DoSomethingElse", new MenuFunction() {
			
			@Override
			public void selected()  {
				++c;
				Snapshot snap = new Snapshot("do something else", new SourceLocation(getClass(), "MinimalDemo.java", 35));
				StateObject box = new StateObject("box");
				box.setColor(Color.red).setValue("demo").setMaxComponentsPerRow(4);
				snap.add(box);
				StateObject last = null;
				for (int i = 0; i < c+2; i++) {
					StateObject node = new StateObject("node" + i);
					node.setLabel("node" + i).setValue("" + i).setColor(Color.green);
					if (last == null) {
						box.addConnection(new Connector("first", box, node, 200, 240));
					} else {
						last.addConnection(new Connector("conn"+i, last, node, 89, 91));
					}
					last = node;
				}
				Connector conn = new Connector("last", box, last, 140, 160);
				conn.setPreferredLength(10.0);
				conn.setElasticity(5.0);
				box.addConnection(conn);
				try {
					AlgAE.messages.put (new SnapshotMessage(snap));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		register ("Collision", new MenuFunction() {
			
			@Override
			public void selected()  {
				++c;
				Snapshot snap = new Snapshot("collision", new SourceLocation(getClass(), "MinimalDemo.java", 36));
				StateObject box = new StateObject("box");
				box.setColor(Color.red).setValue("collision").setMaxComponentsPerRow(4);
				snap.add(box);
				box.setLocation(new Point2D.Double(0,0));
				box.setAnchored(true);
				StateObject node0 = new StateObject("node0");
				node0.setLabel("node0").setValue("0").setColor(Color.green);
				node0.setLocation(new Point2D.Double(3.0, 0.0));
				snap.add (node0);
				StateObject node1 = new StateObject("node1");
				node1.setLabel("node1").setValue("1").setColor(Color.blue);
				node1.setLocation(new Point2D.Double(4.0, 1.0));
				snap.add (node1);
				try {
					AlgAE.messages.put (new SnapshotMessage(snap));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		register ("Tension", new MenuFunction() {
			
			@Override
			public void selected()  {
				++c;
				Snapshot snap = new Snapshot("tension", new SourceLocation(getClass(), "MinimalDemo.java", 36));
				StateObject box = new StateObject("box");
				box.setColor(Color.red).setValue("tension").setMaxComponentsPerRow(4);
				snap.add(box);
				box.setLocation(new Point2D.Double(0,0));
				box.setAnchored(true);
				StateObject node0 = new StateObject("node0a");
				node0.setLabel("node0").setValue("0").setColor(Color.green);
				node0.setLocation(new Point2D.Double(20.0, 0.0));
				Connector c1 = new Connector("conn1", box, node0, 90, 90);
				
				StateObject node1 = new StateObject("node1a");
				node1.setLabel("node1").setValue("1").setColor(Color.blue);
				node1.setLocation(new Point2D.Double(0.0, 3.5));
				Connector c2 = new Connector("conn2", box, node1, 180, 180);
				c2.setPreferredLength(5.0);
				box.getConnections().add(c1);
				box.getConnections().add(c2);

				try {
					AlgAE.messages.put (new SnapshotMessage(snap));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		register ("Torsion", new MenuFunction() {
			
			@Override
			public void selected()  {
				++c;
				Snapshot snap = new Snapshot("torsion", new SourceLocation(getClass(), "MinimalDemo.java", 36));
				StateObject box = new StateObject("box");
				box.setColor(Color.red).setValue("torsion").setMaxComponentsPerRow(4);
				snap.add(box);
				box.setLocation(new Point2D.Double(0,0));
				box.setAnchored(true);
				StateObject node0 = new StateObject("node0");
				node0.setLabel("node0").setValue("0").setColor(Color.green);
				node0.setLocation(new Point2D.Double(10.0, 10.0));
				Connector c1 = new Connector("conn1", box, node0, 90, 90);
				c1.setElasticity(1000.0);
				
				StateObject node1 = new StateObject("node1");
				node1.setLabel("node1").setValue("1").setColor(Color.blue);
				node1.setLocation(new Point2D.Double(10.0, 3.5));
				Connector c2 = new Connector("conn2", box, node1, 180, 180);
				c2.setPreferredLength(5.0);
				c2.setElasticity(1000.0);
				box.getConnections().add(c1);
				box.getConnections().add(c2);
			
				try {
					AlgAE.messages.put (new SnapshotMessage(snap));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
*/	}

	
	
	public static void main (String[] args) {
		MinimalDemo demo = new MinimalDemo();
		demo.runAsMain();
	}

}
