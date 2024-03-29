package edu.odu.cs.AlgAE.Client;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JFrame;

import edu.odu.cs.AlgAE.Common.Communications.CapturedOutputMessage;
import edu.odu.cs.AlgAE.Common.Communications.ClientMessage;
import edu.odu.cs.AlgAE.Common.Communications.ForceShutDownMessage;
import edu.odu.cs.AlgAE.Common.Communications.MenuMessage;
import edu.odu.cs.AlgAE.Common.Communications.PromptForInputMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage.ServerMessageTypes;
import edu.odu.cs.AlgAE.Common.Communications.ServerCommunications;
import edu.odu.cs.AlgAE.Common.Communications.SnapshotMessage;
import edu.odu.cs.AlgAE.Common.Communications.SourceCodeMessage;
import edu.odu.cs.AlgAE.Common.Snapshot.Connector;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity;
import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;
import edu.odu.cs.AlgAE.Common.Snapshot.SourceLocation;
import edu.odu.cs.AlgAE.Server.MemoryModel.Identifier;

/**
 * Test driver for the AlgAE client - provides a sample of each possible message from a server.
 *  
 * @author zeil
 *
 */
public class DemoClientVisually  implements ServerCommunications {
	
	private GUIClient client;
	private ArrayBlockingQueue<ClientMessage> script;
	
	
	

	public DemoClientVisually() {
		script = new ArrayBlockingQueue<ClientMessage>(40);
	}


	
	
	public void runAsMain () {
			JFrame window = new JFrame("Client Test");
			
			client = new GUIClient (this);
			client.init();
			window.setJMenuBar(client.buildMenu());

			window.getContentPane().add(client);
			
			window.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					client.destroy();
					System.exit (0);
				}
			});
			window.pack();
			window.setVisible(true);
			client.start();
	}
	
	public static void main (String[] args) {
		new DemoClientVisually().runAsMain();
	}


	private Snapshot generateSnapshot1(int k) {
		Entity stk = new Entity(new Identifier(1), "");
		Entity global = new Entity(new Identifier(2), "");
		stk.setColor(Color.magenta);
		stk.setValue("stack");
		global.setLabel("g");
		global.setColor(Color.cyan);
		global.setValue("" + k);
		Snapshot snap = new Snapshot();
		snap.add(stk);
		snap.add(global);
		snap.setDescriptor("heads");
		snap.setBreakpointLocation(new SourceLocation("foo.java", k%5));
		snap.setRootEntity(stk.getEntityIdentifier());
		return snap;
	}


	
	private Snapshot generateSnapshot2(int k) {
		Entity stk = new Entity(new Identifier(1), "");
		Entity global = new Entity(new Identifier(2), "");
		Entity heapObj = new Entity(new Identifier(3), "");
		Entity component = new Entity(new Identifier(4), heapObj.getEntityIdentifier(), "component");
		stk.setColor(Color.magenta);
		stk.setValue("activation stack");
		global.setLabel("g");
		global.setColor(Color.blue);
		global.setValue("" + k);
		heapObj.setColor(Color.yellow);
		heapObj.setValue("on heap");
		component.setValue("" + (1000 + 10*k));
		component.setColor(Color.green);
		
		heapObj.getComponents().add(component.getEntityIdentifier());
		global.getConnections().add(new Connector("next", global.getEntityIdentifier(), heapObj.getEntityIdentifier(),
				0, 180));
		
		
		Snapshot snap = new Snapshot();
		snap.add (stk);
		snap.add (global);
		snap.add (heapObj);
		snap.add (component);
		
		snap.setDescriptor("tails");
		snap.setBreakpointLocation(new SourceLocation("bar.java", k%5));
		snap.setRootEntity(stk.getEntityIdentifier());
		return snap;
	}

	
	private String algorithm = "";
	
	@Override
	public void sendToServer(ServerMessage message) throws InterruptedException {
		System.err.println ("Server received " + message);
		String kind = message.getKind();
		if (kind.equals(ServerMessageTypes.Start.toString())) {
			String[] menu = {"algorithm1", "algorithm2"};
			MenuMessage msg = new MenuMessage("This is a basic visual test of the client", menu);
			script.add(msg);
			SourceCodeMessage msg2 = new SourceCodeMessage("foo.java", 
						"public static void main (String[] args) {\n"
								+ "  new TestClientVisually().runAsMain();\n}");
			script.add(msg2);
			Snapshot snap = new Snapshot();
			SnapshotMessage msg3 = new SnapshotMessage(snap, true);
			script.add (msg3);
		} else if (kind.equals(ServerMessageTypes.MenuItemSelected.toString())) {
			algorithm = message.getDetail();
			for (int i = 0; i < 5; ++i) {
				Snapshot snap = generateSnapshot1(2*i);
				SnapshotMessage msg = new SnapshotMessage(snap, false);
				script.add (msg);
				snap = generateSnapshot2(2*i+1);
				msg = new SnapshotMessage(snap, false);
				script.add (msg);
			}
			PromptForInputMessage pMsg = new PromptForInputMessage("Enter a number", "-?[0-9]+");
			script.add(pMsg);
		} else if (kind.equals(ServerMessageTypes.InputSupplied.toString())) {
			if (message.getDetail().startsWith("-")) {
				ForceShutDownMessage shutdownMsg = new ForceShutDownMessage("I didn't like that number!");
				script.add (shutdownMsg);
			} else {
				for (int i = 6; i < 10; ++i) {
					Snapshot snap = generateSnapshot1(2*i);
					SnapshotMessage msg = new SnapshotMessage(snap, false);
					script.add (msg);
					snap = generateSnapshot2(2*i+1);
					msg = new SnapshotMessage(snap, i < 4);
					script.add (msg);
				}
				CapturedOutputMessage capturedMsg = new CapturedOutputMessage("Completed " + algorithm + "\n");
				script.add (capturedMsg);
			}
		} else if (kind.equals(ServerMessageTypes.ShutDown.toString())) {
			System.err.println ("Shutdown received.");
		}
	}




	@Override
	public ClientMessage getFromServer() throws InterruptedException {
		return script.take();
	}

}
