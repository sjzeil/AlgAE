package edu.odu.cs.AlgAE.Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import edu.odu.cs.AlgAE.Common.Applets.AppletLifetimeSupport;
import edu.odu.cs.AlgAE.Common.Communications.AckMessage;
import edu.odu.cs.AlgAE.Common.Communications.ClientMessage;
import edu.odu.cs.AlgAE.Common.Communications.ClientProxy;
import edu.odu.cs.AlgAE.Common.Communications.MenuMessage;
import edu.odu.cs.AlgAE.Common.Communications.PromptForInputMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage.ServerMessageTypes;
import edu.odu.cs.AlgAE.Common.Communications.SourceCodeMessage;
import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;
import edu.odu.cs.AlgAE.Common.Snapshot.SourceLocation;
import edu.odu.cs.AlgAE.Server.Animations.AnimationContext;
import edu.odu.cs.AlgAE.Server.Animations.ContextAware;
import edu.odu.cs.AlgAE.Server.Animations.LocalJavaAnimation;
import edu.odu.cs.AlgAE.Server.Animations.SimulatedPrintStream;
import edu.odu.cs.AlgAE.Server.MemoryModel.MemoryModel;


public class Server implements ContextAware, AppletLifetimeSupport
{
	/**
	 *  Messages to be sent to the client.
	 */
	private ClientProxy client;

	/**
	 * Animation instance
	 */
	private LocalJavaAnimation animation;

	/**
	 *  Collection of algorithm menu items.
	 **/
	private HashMap<String, MenuFunction> algorithmsMenu;

	/**
	 * Animated code to be performed upon start of the animation
	 */
	private MenuFunction startingAction;


	/**
	 * The algorithms being animated are run from a separate thread.
	 */
	private ServerThread thread;


	/**
	 * The algorithms being animated are run from a separate thread.
	 */
	private LauncherThread launcher;

	/**
	 * Used to force stop of thread
	 */
	private boolean stopped;


	private String promptedInput;

	/**
	 *  Constructor for AlgAE server.
	 *  @param anim  Animation instance for which this is a server
	 */
	public Server(LocalJavaAnimation anim, ClientProxy client)
	{
		animation = anim;
		this.client = client;
		SimulatedPrintStream.setMsgQueue(client);
		algorithmsMenu = new HashMap<String, MenuFunction>();
		startingAction = null;
		thread = new ServerThread();
		launcher = new LauncherThread();
		anim.registerInstance(thread);
		stopped = false;
	}

	/**
	 *  Declare a menu item to appear in the client's Algorithm menu
	 *  and a corresponding function to be executed here on the server
	 *  in response to a selection of that item.
	 *
	 * @param menuItemTitle The string to appear in the client's menu
	 * @param theOperation  code to invoke when the menu item is selected
	 **/
	public void registerMenuItem(String menuItemTitle, MenuFunction theOperation)
	{
		algorithmsMenu.put (menuItemTitle, theOperation);
	}

	public void registerStartingAction(MenuFunction action) {
		startingAction = action;
	}


	@Override
	public void init(boolean isAnApplet)
	{
		animation.buildMenu();
	}

	/**
	 * Start the client and begin running the animation server.
	 *  @param animationTitle   string to place in client's window title bar
	 **/
	public void start()
	{
		//    msgHandler.algorithmName (animationTitle, menuGenerator.about());
		//    
		//
		//    // Main loop: process client's menu selections
		//    String menuSelection = "";
		//    while (!menuSelection.equals("QUIT"))
		//      {
		//	msgHandler.beginAlgorithm();
		//	describeData();
		//	msgHandler.status ("Select an algorithm");
		//	msgHandler.endAlgorithm();
		//	menuSelection = msgHandler.getMenuSelection ();
		//	invoke (menuSelection);
		//      }
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
		launcher.setPriority(Thread.MIN_PRIORITY);
	}


	@Override
	public void stop()
	{

	}

	@Override
	public void destroy()
	{
		shutdown();
	}

	/**
	 * Pops up a dialog box prompting for an input, pausing the
	 * animation until a satisfactory input value is obtained from the
	 * human operator.
	 * 
	 * @param prompt  Text of the prompt message to be displayed
	 * @param requiredPattern regular expression describing an acceptable input value
	 * @return a human-entered string matching the requiredPattern
	 */
	public String promptForInput(String prompt, String requiredPattern) {
		promptedInput = "";
		ClientMessage promptMsg = new PromptForInputMessage(prompt, requiredPattern);
		try {
			client.sendToClient(promptMsg);
			synchronized (client) {
				client.wait();
				return promptedInput;
			}
		} catch (InterruptedException e) {
			return "";
		}

	}

	/**
	 * Provide an input from a dialog box popped up in a separate thread.ops up a dialog box prompting for an input, pausing the
	 * animation until a satisfactory input value is obtained from the
	 * human operator.
	 * 
	 * @param value  input text
	 */
	public void inputSupplied(String value) {
		synchronized (client) {
			promptedInput = value;
			client.notifyAll();
		}
	}





	/**
	 * Attempt to find and load the source code indicated by the given file name.
	 * @param fileName name where we can expect to find this source code
	 * @return text of the source code
	 */
	public String load (String fileName)
	{
		String className = fileName.replace(".java", "");
		className = className.replaceAll("[/\\\\]", ".");
		className = className.replaceFirst("\\$.*$", "");
		Class<?> container;
		try {
			container = Class.forName(className);
		} catch (ClassNotFoundException e) {
			container = this.getClass(); // Why not?  It might work, and couldn't hurt. 
		}
		return load (container, fileName); 
	}
	
	private String load(Class<?> container, String fileName)
	{
		String contents = "Could not load " + fileName + "\n";
		
		String resourceName = "/" + fileName.replace('\\', '/');
		
		InputStream resourceIn = container.getResourceAsStream(resourceName);
		//System.err.println ("resourceIn: " + resourceIn);
		
		if (resourceIn == null) {
			// Should not happen in actual operation (running from a Jar), but
			// is common when running/debugging from Eclipse or other IDEs
			String classPath = System.getProperty("java.class.path");
			String[] cpDirectories = classPath.split(System.getProperty("path.separator"));
			ArrayList<String> directories = new ArrayList<String>(Arrays.asList(cpDirectories));
			directories.add("src/main/java".replace('/', File.separatorChar));
			directories.add("src/test/java".replace('/', File.separatorChar));
			for (String dir: directories) {
				File base = new File(dir);
				File sourceFile = new File(base, fileName);
				if (sourceFile.exists()) {
					try {
						resourceIn = new FileInputStream(sourceFile);
						break;
					} catch (FileNotFoundException e) {
						resourceIn = null;
					}
				}
			}
		}
		
		if (resourceIn != null) {
			StringBuffer contentBuf = new StringBuffer();
			BufferedReader in = new BufferedReader (new InputStreamReader(resourceIn));
			try {
				while (in.ready()) {
					String line = in.readLine();
					contentBuf.append(line);
					contentBuf.append("\n");
				}
				contents = contentBuf.toString();
			} catch (IOException ex) {
				ex.printStackTrace();
				contents = contentBuf.toString() + "\n" + ex.toString();
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} 
		return contents;
	}



	

	private class ServerThread extends Thread {


		private abstract class MessageAction {
			public abstract void doIt(String msgDetail) throws InterruptedException; 
		}

		private HashMap<String, MessageAction> msgActions;

		private MessageAction GetSourceCodeAction = new MessageAction() {
			
			@Override
			public void doIt(String fileName) throws InterruptedException {
				String sourceCode = load(fileName);
				SourceCodeMessage msg = new SourceCodeMessage(fileName, sourceCode);
				client.sendToClient(msg);
			}
		};

		private MessageAction InputSuppliedAction = new MessageAction() {
			
			@Override
			public void doIt(String inputText) throws InterruptedException {
				inputSupplied(inputText);
			}
		};

		private MessageAction MenuAction = new MessageAction() {
			
			@Override
			public void doIt(String msgDetail) throws InterruptedException {
				MenuFunction selected = algorithmsMenu.get(msgDetail);
				if (selected != null)
					launcher.runFunction(selected);
				else
					System.err.println ("Unexpected menu selection: " + msgDetail);
			}
		};

		private MessageAction PullAction = new MessageAction() {

			@Override
			public void doIt(String msgDetail) throws InterruptedException {
				AckMessage ack = new AckMessage();
				client.sendToClient(ack);
			}
		};

		private MessageAction ShutDownAction = new MessageAction() {

			@Override
			public void doIt(String msgDetail) throws InterruptedException {
				shutdown();
			}
		};

		private MessageAction StartAction = new MessageAction() {

			@Override
			public void doIt(String msgDetail) throws InterruptedException {
				String[] menuItemTitles = new String[algorithmsMenu.size()];
				int i = 0;
				for (String item: algorithmsMenu.keySet()) {
					menuItemTitles[i] = item;
					i++;
				}
				String aboutStr = animation.about();
				if (aboutStr == null)
					aboutStr = "";
				MenuMessage msg = new MenuMessage(aboutStr, menuItemTitles);
				client.sendToClient(msg);
				launcher.start();
			}
		};

		public ServerThread() {
			super("Server Message Handler");
			msgActions = new HashMap<String, Server.ServerThread.MessageAction>();
			msgActions.put(ServerMessageTypes.GetSourceCode .toString(), GetSourceCodeAction);
			msgActions.put(ServerMessageTypes.InputSupplied.toString(), InputSuppliedAction);
			msgActions.put(ServerMessageTypes.MenuItemSelected.toString(), MenuAction);
			msgActions.put(ServerMessageTypes.Pull.toString(), PullAction);
			msgActions.put(ServerMessageTypes.ShutDown.toString(), ShutDownAction);
			msgActions.put(ServerMessageTypes.Start.toString(), StartAction);
			
		}


		public void run() {
			//System.err.println ("Started server thread");
			while (!stopped) {
				Thread.yield();
				ServerMessage msg;
				try {
					msg = client.getFromClient();  // may block
				} catch (InterruptedException e) {
					stopped = true;
					break;
				}
				MessageAction action = msgActions.get(msg.getKind());
				if (action != null) {
					try {
						action.doIt(msg.getDetail());
					} catch (InterruptedException e) {
						stopped = true;
						break;
					}
				} else {
					System.err.println ("Unexpected message from client: " + msg);
				}
			}
		}

		
	}
	
	private class LauncherThread extends Thread {


		private MenuFunction selectedAction;
		

		public LauncherThread() {
			super("Animation Code Launcher");
			selectedAction = null;
		}
		
		
		public synchronized void runFunction (MenuFunction action)
		{
			while (selectedAction != null) {
				try {
					wait ();
				} catch (InterruptedException e) {
					if (!stopped)
						e.printStackTrace();
				}
			}
			selectedAction = action;
			notifyAll();
		}


		public void run() {
			//System.err.println ("Started server thread");
			animation.registerInstance(this);
			if (startingAction != null)
				selectedAction = startingAction;
			else {
				MemoryModel memory = animation.getMemoryModel();
				memory.getActivationStack().clear();
				Snapshot snap = memory.renderInto("Choose an Algorithm", new SourceLocation("", 0));
				animation.sendToClient(snap, true);
			}
			while (!stopped) {
				Thread.yield();
				synchronized (this) {
					while (selectedAction == null) {
						try {
							wait ();
						} catch (InterruptedException e) {
							if (!stopped)
								e.printStackTrace();
						}
					}
				}				
				selectedAction.selected();
				synchronized (this) {
					selectedAction = null;
					notifyAll();
				}
				MemoryModel memory = animation.getMemoryModel();
				memory.getActivationStack().clear();
				Snapshot snap = memory.renderInto("Choose an Algorithm", new SourceLocation("", 0));
				animation.sendToClient(snap, true);
			}

		}
	}
	



	public void shutdown() {
		stopped = true;
		try {
			Thread.sleep(500);
			if (thread.isAlive()) {
				thread.interrupt();
			}
			if (launcher.isAlive()) {
				launcher.interrupt();
			}
		} catch (Exception e) {

		}

	}

	@Override
	public AnimationContext context() {
		return animation;
	}






}
