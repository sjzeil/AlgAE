package edu.odu.cs.AlgAE.Animations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import edu.odu.cs.AlgAE.Common.Communications.AckMessage;
import edu.odu.cs.AlgAE.Common.Communications.ClientMessage;
import edu.odu.cs.AlgAE.Common.Communications.MenuMessage;
import edu.odu.cs.AlgAE.Common.Communications.PromptForInputMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage.ServerMessageTypes;
import edu.odu.cs.AlgAE.Common.Communications.SnapshotMessage;
import edu.odu.cs.AlgAE.Common.Communications.SourceCodeMessage;
import edu.odu.cs.AlgAE.Common.Communications.StandardIOCommunication;
import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;
import edu.odu.cs.AlgAE.Common.Snapshot.SnapshotDiff;
import edu.odu.cs.AlgAE.Common.Snapshot.SourceLocation;
import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.Server;
import edu.odu.cs.AlgAE.Server.MemoryModel.MemoryModel;

/**
 *  A specialization of Server for Java standalone programs that will exchange messages
 *  with an AlgAE client via standard I/O.
 *
 *  @author Steven J Zeil
 **/
public abstract class JavaStandAloneServer extends Server implements MenuBuilder, AnimationContext, ContextAware
{

	private static Logger logger = Logger.getLogger(JavaStandAloneServer.class.getName());
	
	
	private StandardIOCommunication communications;
	private String theTitle;
	private MemoryModel memoryModel;
	
	private ServerThread server;
	private LauncherThread launcher;
	
	/**
     * Used to force stop of thread
     */
    private boolean stopped;

    private HashSet<String> sourceCodeAlreadySent;

	/**
	 *  Collection of algorithm menu items.
	 **/
	private HashMap<String, MenuFunction> algorithmsMenu;

	/**
	 * Animated code to be performed upon start of the animation
	 */
	private MenuFunction startingAction;


	public JavaStandAloneServer (String title, InputStream msgsIn, PrintStream msgsOut)
	{
		DefaultLogSetting.setupLogging(false,  "algae-server%u.log");
		
		theTitle = title;
		communications = new StandardIOCommunication(msgsIn, msgsOut);
		setClientCommunications(communications);
		memoryModel = new MemoryModel(this);
		SimulatedPrintStream.setMsgQueue(communications);
		server = new ServerThread();
		launcher = new LauncherThread();
		sourceCodeAlreadySent = new HashSet<String>();
		algorithmsMenu = new HashMap<String, MenuFunction>();
	}

	public void runAsMain() {
		start();
	}

	/**
	 *  Supply a message to appear in the Help..About dialog.
	 *  Typically, this indicates the origin of the source code
	 *  being animated and the name of the person who prepared the
	 *  animation.
	 **/
	public abstract String about();


	/**
	 * Override this to call register (below) to set up the menu items that will
	 * be displayed in the Algorithms menu and optionally to call registerStartingAction
	 * to set up code to be animated immediately upon launch.
	 */
	public abstract void buildMenu();



	
	public MemoryModel getMemoryModel() {
		return memoryModel;
	}


	/**
	 * Show a variable as a global value in all displays.
	 * Variables portrayed by this call are shown "in-line".
	 *
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 */
	protected void globalVar(String label, int value)
	{
		memoryModel.globalVar(label, value);
	}

	/**
	 * Show a variable as a global value in all displays.
	 * Variables portrayed by this call are shown "in-line".
	 *
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 */
	protected void globalVar (String label, Object param)
	{
		memoryModel.globalVar(label, param);
	}



	/**
	 * Show a variable as a global value in all displays.
	 * Variables portrayed by this call are shown as labeled
	 * pointers to the actual value.
	 *
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 * @return a reference to this breakpoint
	 */
	protected void globalRefVar (String label, Object param)
	{
		memoryModel.globalRefVar(label, param);
	}

	
	/**
	 * A substitute for System.in - reads from the animation GUI
	 */
	public InputStream in;

	/**
	 * A substitute for System.out - writes to the animation GUI
	 */
	public SimulatedPrintStream out;
	
	
	/**
	 *  Declare a menu item to appear in the client's Algorithm menu
	 *  and a corresponding function to be executed here on the server
	 *  in response to a selection of that item.
	 *
	 * @param menuItemTitle The string to appear in the client's menu
	 * @param theOperation  code to invoke when the menu item is selected
	 **/
	public void register(String menuItemTitle, MenuFunction theOperation)
	{
		algorithmsMenu.put (menuItemTitle, theOperation);
	}

	public void registerStartingAction(MenuFunction action) {
		startingAction = action;
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
		String promptedInput = "";
		ClientMessage promptMsg = new PromptForInputMessage(prompt, requiredPattern);
		try {
			getClientCommunications().sendToClient(promptMsg);
			while (promptedInput.equals("")) {
				ServerMessage response = getClientCommunications().getFromClient();
				if (response.getKind().equals("Shutdown")) {
					forceShutdown();
					break;
				} else if (response.getKind().equals("InputSupplied")) {
					promptedInput = response.getDetail();
				} else if (!response.getKind().equals("Ack")) {
					logger.warning("Message protocol error: encountered " + response
							+ " while awaiting prompted input.\n");
				}
			}
		} catch (InterruptedException ex) {
			// indicates that program is shutting down
		}
		return promptedInput;
	}


	/**
	 * Pops up a dialog box prompting for an input, pausing the
	 * animation until a satisfactory input value is obtained from the
	 * human operator.
	 *
	 * @param prompt  Text of the prompt message to be displayed
	 * @return a human-entered string
	 */
	public String promptForInput(String prompt) {
		return promptForInput(prompt, ".*");
	}

	@Override
	public AnimationContext context() {
		return memoryModel.context();
	}

	@Override
	public void sendToClient(ClientMessage msg) {
        try {
            communications.sendToClient(msg);
        } catch (InterruptedException e) {
            logger.warning ("Thread closed while waiting for message from client: " + e);
        }
	}

	private Snapshot lastSnapshot = null;

	@Override
	public void sendToClient(Snapshot snap, boolean completed) {
	    SnapshotDiff diff = new SnapshotDiff(lastSnapshot, snap);
	    lastSnapshot = snap;
	    SnapshotMessage msg = new SnapshotMessage(diff, completed);
	    sendToClient(msg);
	}

	@Override
	public void sendSourceToClient(String fileName) {
        if (!sourceCodeAlreadySent.contains(fileName)) {
            sourceCodeAlreadySent.add (fileName);
            SourceCodeMessage msg = new SourceCodeMessage(fileName, load(fileName));
            sendToClient(msg);
        }
	}

	@Override
	public SimulatedPrintStream sysout() {
        return out;
	}

	@Override
	public void start() {
		communications.start();
		server.start();
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
            if (startingAction != null)
                selectedAction = startingAction;
            else {
                MemoryModel memory = getMemoryModel();
                memory.getActivationStack().clear();
                Snapshot snap = memory.renderInto("Choose an Algorithm", new SourceLocation("", 0));
                sendToClient(snap, true);
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
                MemoryModel memory = getMemoryModel();
                memory.getActivationStack().clear();
                Snapshot snap = memory.renderInto("Choose an Algorithm", new SourceLocation("", 0));
                sendToClient(snap, true);
            }

        }
    }




    public void shutdown() {
        stopped = true;
        try {
            Thread.sleep(250);
            if (server.isAlive()) {
                server.interrupt();
            }
            if (launcher.isAlive()) {
                launcher.interrupt();
            }
        } catch (Exception e) {
            logger.warning("Difficulty shutting down server: " + e);
        }
	}

	@Override
	public void forceShutdown() {
		shutdown();
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
                    logger.warning("Problem closing source file " + fileName + ": " + e);
                }
            }
        }
        return contents;
    }


    /**
     * Provide an input from a dialog box popped up in a separate thread.ops up a dialog box prompting for an input, pausing the
     * animation until a satisfactory input value is obtained from the
     * human operator.
     *
     * @param value  input text
     */
    public void inputSupplied(String value) {
        // TODO
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
                getClientCommunications().sendToClient(msg);
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
                getClientCommunications().sendToClient(ack);
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
                String[] menuItemTitles = algorithmsMenu.keySet().toArray(new String[0]);
                String aboutStr = about();
                if (aboutStr == null)
                    aboutStr = "";
                MenuMessage msg = new MenuMessage(aboutStr, menuItemTitles);
                getClientCommunications().sendToClient(msg);
                launcher.start();
            }
        };

        public ServerThread() {
            super("Server Message Handler");
            msgActions = new HashMap<String, MessageAction>();
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
                    msg = getClientCommunications().getFromClient();  // may block
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

}



