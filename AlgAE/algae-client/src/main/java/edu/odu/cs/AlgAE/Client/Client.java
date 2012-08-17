/**
 * Client.java
 *
 *
 * Created: Sat Apr 25 22:14:01 1998
 *
 * @author Steven J. Zeil
 * @version
 */

package edu.odu.cs.AlgAE.Client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import edu.odu.cs.AlgAE.Client.DataViewer.Animator;
import edu.odu.cs.AlgAE.Client.IOViewer.IOPane;
import edu.odu.cs.AlgAE.Client.SourceViewer.SourcePane;
import edu.odu.cs.AlgAE.Common.Applets.AppletMenuSupport;
import edu.odu.cs.AlgAE.Common.Communications.AckMessage;
import edu.odu.cs.AlgAE.Common.Communications.CapturedOutputMessage;
import edu.odu.cs.AlgAE.Common.Communications.ClientMessage;
import edu.odu.cs.AlgAE.Common.Communications.ForceShutDownMessage;
import edu.odu.cs.AlgAE.Common.Communications.MenuMessage;
import edu.odu.cs.AlgAE.Common.Communications.PromptForInputMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage.ServerMessageTypes;
import edu.odu.cs.AlgAE.Common.Communications.ServerProxy;
import edu.odu.cs.AlgAE.Common.Communications.SnapshotMessage;
import edu.odu.cs.AlgAE.Common.Communications.SourceCodeMessage;
import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;
import edu.odu.cs.AlgAE.Common.Snapshot.SnapshotDiff;


public class Client extends AppletMenuSupport {

	private ViewerPanel dataViewer;
	private Animator animator;
	private List<JMenuItem> algorithmItems;
	
	private JMenu algorithmMenu;
	private ServerProxy server;
	private ClientThread messageReader;
	private IOPane ioPane;
	private SourcePane sourcePane;

	private HelpSet hs;
	private HelpBroker hb;
	
	private String aboutString = "";

	/**
	 * Indicates whether we are running some portion of the
	 * animated code.
	 */
	private boolean running;

	
	/**
	 * Indicates that animation has been shut down
	 */
	private boolean terminated;
	
	/**
	 * Remembers whether we are running this as an applet or as a main program
	 */
	private boolean isAnApplet;

	  
	public Client(ServerProxy server)
	{
		super();
		
		isAnApplet = false;
		
		this.server = server;
		
		running = false;
		terminated = false;
		algorithmItems = new LinkedList<JMenuItem>();
		
		messageReader = null;
	}


	@Override
	public void init (boolean isAnApplet)
	{
		this.isAnApplet = isAnApplet;
		setLayout (new BorderLayout());
		
		
		ioPane = new IOPane (server);
		sourcePane = new SourcePane(server);
		
		animator = new Animator(sourcePane);
		dataViewer = new ViewerPanel ("AlgAE", animator, sourcePane, ioPane);
		
		add (dataViewer, BorderLayout.CENTER);

	}


	@Override
	public JMenuBar buildMenu() {

		JMenuBar menuBar = new JMenuBar();
		if (!isAnApplet) { 
			JMenu fileMenu = new JMenu ("File", false);
			menuBar.add(fileMenu);

			JMenuItem exitItem = new JMenuItem ("Exit");
			fileMenu.add (exitItem);
			exitItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Component src = ioPane;
					while (!(src instanceof JFrame)) {
						src = src.getParent();
					}
					JFrame mainWindow = (JFrame)src;
					mainWindow.dispatchEvent(new WindowEvent(mainWindow, WindowEvent.WINDOW_CLOSING));
					mainWindow.setVisible(false);
				}});
		}

		algorithmMenu = new JMenu ("Algorithm", false);
		menuBar.add (algorithmMenu);


		JMenu helpMenu = new JMenu ("Help", false);
		menuBar.add(helpMenu);


		JMenuItem helpItem = new JMenuItem ("Help");
		helpMenu.add (helpItem);

		String helpHS = "Help/helpset.hs";
		ClassLoader cl = Client.class.getClassLoader();
		try {
			URL hsURL = HelpSet.findHelpSet(cl, helpHS);
			hs = new HelpSet(null, hsURL);
		} catch (Exception ee) {
			System.err.println( "HelpSet " + ee.getMessage());
		}
		if (hs != null) {
			hb = hs.createHelpBroker();

			helpItem.addActionListener(new CSH.DisplayHelpFromSource (hb));
		}
		
		
		JMenuItem aboutItem = new JMenuItem ("About AlgAE");
		helpMenu.add (aboutItem);
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Component src = ioPane;
				while (!(src instanceof JFrame))
					src = src.getParent();
				JFrame mainWindow = (JFrame)src;

				JOptionPane.showMessageDialog(mainWindow,
						aboutString +
						"\n----------------------------\n"
						+ "AlgAE Algorithm Animation Engine, " +
						"version 3.0\n\n" +
						"  Steven J. Zeil\n" +
						"  Old Dominion University\n" +
				"  Dept. of Computer Science");
			}});
		
		return menuBar;
		
	}




	/**
	 * Called from Animation applet to indicate that a start() call
	 * has been issued.
	 */
	public void start() {
		if (messageReader == null) {
			messageReader = new ClientThread(this);
			messageReader.setPriority(Thread.MIN_PRIORITY);
			messageReader.start();
		}
		if (!running) {
			animator.resumeAnimator();
			running = true;
		}
	}

	/**
	 * Called from Animation applet to indicate that a stop() call
	 * has been issued.
	 */
	public void stop() {
		if (running) {
			running = false;
			animator.pauseAnimator();
		}
	}

	/**
	 * Called from Animation applet to indicate that a destroy() call
	 * has been issued.
	 */
	public void destroy() {
		if (!terminated) {
			terminated = true;
			running = false;
			animator.shutdown();
			ServerMessage shutDownMsg = new ServerMessage(ServerMessageTypes.ShutDown);
			try {
				server.sendToServer(shutDownMsg);
			} catch (InterruptedException e) {
			}
			try {
				Thread.sleep(500);
				if (messageReader.isAlive()) {
					messageReader.interrupt();
				}
			} catch (Exception e1) {
			}
		}
	}


	private ActionListener algorithmMenuAction = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent ev) {
			JMenuItem selected = (JMenuItem)ev.getSource();
			String name = selected.getText();
			algorithmMenuItemSelected(name);
		}
	};
	
	public void registerMenuItem(String menuItemTitle) {
		JMenuItem newItem = new JMenuItem (menuItemTitle);
		algorithmMenu.add(newItem);
		algorithmItems.add(newItem);
		newItem.addActionListener(algorithmMenuAction);
	}


	protected void enableAlgorithmMenuItems(boolean b) {
		for (JMenuItem item: algorithmItems) {
			item.setEnabled(b);
		}
		algorithmMenu.setEnabled(b);
	}


	public void showSnapshot(Snapshot snapshot) throws InterruptedException {
		animator.add(snapshot);
	}
	
	
	
	private abstract class MessageAction {
		public abstract void doIt(ClientMessage msg); 
	}

	
	private class ClientThread extends Thread {

		private Client client;
		
		private HashMap<String, MessageAction> msgActions;
		
		private Snapshot lastSnap;

		private MessageAction AckMessageReceived = new MessageAction() {
			
			@Override
			public void doIt(ClientMessage msg) {
			}
		};
		private MessageAction CapturedOutputMessageReceived = new MessageAction() {
			
			@Override
			public void doIt(ClientMessage msg) {
				CapturedOutputMessage m = (CapturedOutputMessage)msg;
				ioPane.print (m.getOutput());
			}
		};

		private MessageAction ForceShutDownMessageReceived = new MessageAction() {
			
			@Override
			public void doIt(ClientMessage msg) {
				ForceShutDownMessage m = (ForceShutDownMessage)msg;
				ioPane.print ("** Animation server is shutting down:\n" + m.getExplanation()+"\n");
			}
		};

		private MessageAction MenuMessageReceived = new MessageAction() {
			
			@Override
			public void doIt(ClientMessage msg) {
				MenuMessage m = (MenuMessage)msg;
				client.aboutString = m.getAbout();
				for (String menuItem: m.getMenuItems()) {
					client.registerMenuItem(menuItem);
				}
			}
		};

		private MessageAction PromptForInputMessageReceived = new MessageAction() {
			
			@Override
			public void doIt(ClientMessage msg) {
				PromptForInputMessage m = (PromptForInputMessage)msg;
				String promptString = m.getPrompt();
				String pattern = m.getRequiredPattern();
				if (pattern == null || pattern.length() == 0)
					pattern = ".*";
				promptForInputDialog(promptString, pattern);
			}
		};

		private MessageAction SnapshotMessageReceived = new MessageAction() {
			
			@Override
			public void doIt(ClientMessage msg) {
				SnapshotMessage m = (SnapshotMessage)msg;
				SnapshotDiff diff = m.getSnapshot();
				boolean completed = m.isMenuItemCompleted();
				Snapshot snapshot = diff.reconstruct(lastSnap);
				lastSnap = snapshot;
				try {
					animator.add(snapshot);
				} catch (InterruptedException e) {
					ioPane.print("Connection to server has failed\n");
				}
				if (completed) {
					menuItemCompleted(snapshot);
				}
			}
		};

		private MessageAction SourceCodeMessageReceived = new MessageAction() {
			
			@Override
			public void doIt(ClientMessage msg) {
				SourceCodeMessage m = (SourceCodeMessage)msg;
				sourcePane.addSourceCode (m.getFilePath(), m.getSourceText());
			}
		};

		public ClientThread(Client client) {
			super("ClientThread");
			this.client = client;
			lastSnap = new Snapshot();
			msgActions = new HashMap<String, MessageAction>();
			msgActions.put (AckMessage.class.getSimpleName(), AckMessageReceived);
			msgActions.put (CapturedOutputMessage.class.getSimpleName(), CapturedOutputMessageReceived);
			msgActions.put (ForceShutDownMessage.class.getSimpleName(), ForceShutDownMessageReceived);
			msgActions.put(MenuMessage.class.getSimpleName(), MenuMessageReceived);
			msgActions.put(PromptForInputMessage.class.getSimpleName(), PromptForInputMessageReceived);
			msgActions.put(SnapshotMessage.class.getSimpleName(), SnapshotMessageReceived);
			msgActions.put(SourceCodeMessage.class.getSimpleName(), SourceCodeMessageReceived);
			
		}


		public void run() {
			synchronized (client) {
				enableAlgorithmMenuItems (false);
				animator.startPlay();
				ServerMessage startMsg = new ServerMessage(ServerMessageTypes.Start);
				try {
					server.sendToServer(startMsg);
				} catch (InterruptedException e) {
					ioPane.print("Connection to server has failed\n");
				}
			}

			while (!terminated) {
				Thread.yield();
				ClientMessage msg;
				try {
					msg = server.getFromServer();
				} catch (InterruptedException e) {
					break;
				}
				interpret (msg);
			}
		}


		private void interpret(ClientMessage msg) {
			//System.err.println ("Client received " + msg);
			MessageAction action = msgActions.get(msg.getClass().getSimpleName());
			if (action != null) {
				action.doIt(msg);
			} else {
				System.err.println ("**Illegal message from server: " + msg);
			}
		}
		
		
		
		
	}

	protected void algorithmMenuItemSelected(String name) {
		enableAlgorithmMenuItems (false);
		animator.clear();
		animator.startStepping();
		ServerMessage menuMsg = new ServerMessage(ServerMessageTypes.MenuItemSelected, name);
		try {
			server.sendToServer(menuMsg);
		} catch (InterruptedException e) {
			ioPane.print("Connection to server has failed\n");
		}
	}


	public void menuItemCompleted(Snapshot snapshot) {
		animator.showStatus("Choose an Algorithm");
		animator.endofSequence();
		enableAlgorithmMenuItems (true);
	}






	public void promptForInputDialog(String prompt, String requiredPattern) {
		String response = JOptionPane.showInputDialog(this, prompt, "Input Requested", JOptionPane.QUESTION_MESSAGE);
		while (!response.matches(requiredPattern)) {
			response = JOptionPane.showInputDialog(this, prompt, "Try again", JOptionPane.WARNING_MESSAGE);
		}
		ServerMessage msg = new ServerMessage(ServerMessageTypes.InputSupplied, response);
		try {
			server.sendToServer(msg);
		} catch (InterruptedException e) {
			ioPane.print("Connection to server has failed\n");
		}
	}





	public IOPane getIOPane() {
		return ioPane;
	}







	/**
	 * Provide access to the animator
	 */
	public Animator getAnimator() {
		return animator;
	}







}
