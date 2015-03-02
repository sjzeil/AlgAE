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
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import edu.odu.cs.AlgAE.Client.DataViewer.AnimatorPanel;
import edu.odu.cs.AlgAE.Client.IOViewer.IOPane;
import edu.odu.cs.AlgAE.Client.SourceViewer.SourcePane;
import edu.odu.cs.AlgAE.Common.Communications.AckMessage;
import edu.odu.cs.AlgAE.Common.Communications.CapturedOutputMessage;
import edu.odu.cs.AlgAE.Common.Communications.ClientMessage;
import edu.odu.cs.AlgAE.Common.Communications.ForceShutDownMessage;
import edu.odu.cs.AlgAE.Common.Communications.MenuMessage;
import edu.odu.cs.AlgAE.Common.Communications.PromptForInputMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerCommunications;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage.ServerMessageTypes;
import edu.odu.cs.AlgAE.Common.Communications.SnapshotMessage;
import edu.odu.cs.AlgAE.Common.Communications.SourceCodeMessage;
import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;
import edu.odu.cs.AlgAE.Common.Snapshot.SnapshotDiff;

/**
 * The "normal" Animator for AlgAE animations, this portrays a series of state snapshots
 * via a GUI with controls for selecting algorithms, starting, pausing, and rewinding playback.
 *   
 * @author zeil
 *
 */
public class GUIClient extends Client {

	private ViewerPanel dataViewer;
	private AnimatorPanel animator;
	private List<JMenuItem> algorithmItems;
	
	private JMenu algorithmMenu;
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

	  
	public GUIClient()
	{
		super(null);
		
		isAnApplet = false;
		
		running = false;
		terminated = false;
		algorithmItems = new LinkedList<JMenuItem>();
		
		messageReader = null;
	}


	public GUIClient(ServerCommunications serverComm)
	{
		super(serverComm);
		
		isAnApplet = false;
		
		running = false;
		terminated = false;
		algorithmItems = new LinkedList<JMenuItem>();
		
		messageReader = null;
	}

	
	/* (non-Javadoc)
	 * @see edu.odu.cs.AlgAE.Client.ClientBase#init(boolean)
	 */
	@Override
	public void init (boolean isAnApplet)
	{
		this.isAnApplet = isAnApplet;
		setLayout (new BorderLayout());
		
		
		ioPane = new IOPane (getServerAccess());
		sourcePane = new SourcePane(getServerAccess());
		
		animator = new AnimatorPanel(sourcePane);
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

		String helpHS = "edu/odu/cs/AlgAE/Client/Help/helpset.hs";
		ClassLoader cl = GUIClient.class.getClassLoader();
		URL hsURL = HelpSet.findHelpSet(cl, helpHS);
		if (hsURL == null) {
			// Most likely indicates we are running from an IDE rather than
			// from a fully packaged JAR
			String fileName = "src/main/resources/" + helpHS;
			fileName = fileName.replace('/', File.separatorChar);
			File possibleHelp = new File(fileName);
			if (possibleHelp.exists())
				try {
					hsURL = possibleHelp.toURI().toURL();
				} catch (MalformedURLException e1) {
					System.err.println ("Could not convert " + fileName + " to url: " + e1);
				} catch (Exception e2) {
					System.err.println ("Unexpected exception converting " + fileName + " to url: " + e2);
				}
		}
		if (hsURL != null) {
			try {
				hs = new HelpSet(null, hsURL);
			} catch (HelpSetException e1) {
				System.err.println("Problem loading help set from " + hsURL + ": " + e1);
				e1.printStackTrace();
			}
			if (hs != null) {
				hb = hs.createHelpBroker();

				helpItem.addActionListener(new CSH.DisplayHelpFromSource (hb));
			}
		}
		else 
			System.err.println ("**error: Could not locate help set files");
		
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




	/* (non-Javadoc)
	 * @see edu.odu.cs.AlgAE.Client.ClientBase#start()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see edu.odu.cs.AlgAE.Client.ClientBase#stop()
	 */
	@Override
	public void stop() {
		if (running) {
			running = false;
			animator.pauseAnimator();
		}
	}

	/* (non-Javadoc)
	 * @see edu.odu.cs.AlgAE.Client.ClientBase#destroy()
	 */
	@Override
	public void destroy() {
		if (!terminated) {
			terminated = true;
			running = false;
			animator.shutdown();
			ServerMessage shutDownMsg = new ServerMessage(ServerMessageTypes.ShutDown);
			try {
				getServerAccess().sendToServer(shutDownMsg);
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

		private GUIClient client;
		
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

		public ClientThread(GUIClient client) {
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
					getServerAccess().sendToServer(startMsg);
				} catch (InterruptedException e) {
					ioPane.print("Connection to server has failed\n");
				}
			}

			while (!terminated) {
				Thread.yield();
				ClientMessage msg;
				try {
					msg = getServerAccess().getFromServer();
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
			getServerAccess().sendToServer(menuMsg);
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
			getServerAccess().sendToServer(msg);
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
	public AnimatorPanel getAnimator() {
		return animator;
	}







}
