package edu.odu.cs.AlgAE.Demos;

import static edu.odu.cs.AlgAE.Server.LocalServer.algae;

import java.util.ListIterator;

import edu.odu.cs.AlgAE.Animations.LocalJavaAnimation;
import edu.odu.cs.AlgAE.Server.LocalServer;
import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.MemoryModel.ActivationRecord;
import edu.odu.cs.AlgAE.Server.Utilities.LinkedList;


public class LinkedListDemo extends LocalJavaAnimation {

	public LinkedListDemo() {
		super("LinkedList  Demo");
	}

	@Override
	public String about() {
		return "LinkedList demo.";
	}

	private int c = 0;
	
	


	private LinkedList<String> list = new LinkedList<String>();
	private LinkedList<Integer> list2 = new LinkedList<Integer>();
	


	
	@Override
	public void buildMenu() {
		
		registerStartingAction(new MenuFunction() {

			@Override
			public void selected() {
				globalVar("list", list);
				globalVar("list2", list2);
				list2.showBackLinks(false);
				list2.showFirstLast(false);
			}
			
		});
		
		
		register ("addFirst", new MenuFunction() {
			@Override
			public void selected() {
				++c;
				list.add (0, "" + c);
				list2.add (0, c);
			}
		});

		register ("find", new MenuFunction() {
			@Override
			public void selected() {
				ActivationRecord arec = LocalServer.activate(getClass());
				arec.breakHere("Prompt for input");
				String value = algae().promptForInput("Search for ...", "[0-9]+");
				ListIterator<String> it = list.listIterator();
				arec.var("it", it).var("value", value).breakHere("About to search");
				boolean found = false;
				arec.var("found", found);
				while (it.hasNext() && !found) {
					arec.breakHere("Iterator points to current element");
					arec.pushScope();
					String s = it.next();
					found = value.equals(s);
					arec.var("s", s).var("found", found).breakHere("Examined s");
					arec.popScope();
				}
				if (found) {
					arec.breakHere("Found it!");
					algae().out.println ("Found it!");
				} else {
					arec.breakHere("Could not find it.");
					algae().out.println ("Could not find it.");
				}
			}
		});

		register ("clear", new MenuFunction() {
			@Override
			public void selected() {
				list.clear();
				list2.clear();
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
		LinkedListDemo demo = new LinkedListDemo();
		demo.runAsMain();
	}

}
