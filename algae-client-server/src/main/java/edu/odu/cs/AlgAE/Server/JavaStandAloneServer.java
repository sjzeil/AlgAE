package edu.odu.cs.AlgAE.Server;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.odu.cs.AlgAE.Animations.AnimationContext;
import edu.odu.cs.AlgAE.Animations.ContextAware;
import edu.odu.cs.AlgAE.Animations.DefaultLogSetting;
import edu.odu.cs.AlgAE.Animations.MenuBuilder;
import edu.odu.cs.AlgAE.Animations.SimulatedPrintStream;
import edu.odu.cs.AlgAE.Common.Communications.AckMessage;
import edu.odu.cs.AlgAE.Common.Communications.ClientMessage;
import edu.odu.cs.AlgAE.Common.Communications.MenuMessage;
import edu.odu.cs.AlgAE.Common.Communications.PromptForInputMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage.ServerMessageTypes;
import edu.odu.cs.AlgAE.Common.Communications.SnapshotMessage;
import edu.odu.cs.AlgAE.Common.Communications.SourceCodeMessage;
import edu.odu.cs.AlgAE.Common.Communications.StreamedClientCommunications;
import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;
import edu.odu.cs.AlgAE.Common.Snapshot.SourceLocation;
import edu.odu.cs.AlgAE.Server.MemoryModel.ActivationRecord;
import edu.odu.cs.AlgAE.Server.MemoryModel.MemoryModel;

/**
 *  A specialization of Server for Java standalone programs that will exchange
 *  messages with an AlgAE client via standard I/O.
 *
 *  @author Steven J Zeil
 **/
public abstract class JavaStandAloneServer extends Server
    implements MenuBuilder, AnimationContext, ContextAware {

    /**
     * Error logging.
     */
    private static Logger logger
        = Logger.getLogger(JavaStandAloneServer.class.getName());

    /**
     * This is actually a singleton class. The created instance is stored here
     * to facilitate static access.
     */
    private static JavaStandAloneServer instance = null;
    
    
    /**
     * Communications between here and the client.
     */
    private final StreamedClientCommunications communications;

    /**
     * A model of the activation stack and heap of the code
     * being animated.
     */
    private final MemoryModel memoryModel;

    /**
     * Thread that processes messages from the client.
     */
    private final ServerThread server;

    /**
     * Thread in which the animated code itself is run.
     */
    private final LauncherThread launcher;

    /**
     * Used to force stop of thread.
     */
    private boolean stopped;

    /**
     * A list of source code files already sent to the client.
     */
    private final HashSet<String> sourceCodeAlreadySent;

    /**
     *  Collection of algorithm menu items.
     **/
    private final HashMap<String, MenuFunction> algorithmsMenu;

    /**
     * Animated code to be performed upon start of the animation.
     */
    private MenuFunction startingAction;


    /**
     * Create a new server.
     * @param title a string that client should display as a window title.
     * @param msgsIn stream on which messages from the client will appear.
     * @param msgsOut stream to which messages to the client can be written.
     */
    public JavaStandAloneServer (
            final String title,
            final InputStream msgsIn,
            final PrintStream msgsOut) {
        DefaultLogSetting.setupLogging(false,  "algae-server%u.log");
        DefaultLogSetting.defaultLevel = Level.FINE;

        communications = new StreamedClientCommunications(msgsIn, msgsOut);
        setClientCommunications(communications);
        memoryModel = new MemoryModel(this);
        SimulatedPrintStream.setMsgQueue(communications);
        server = new ServerThread();
        launcher = new LauncherThread();
        sourceCodeAlreadySent = new HashSet<String>();
        algorithmsMenu = new HashMap<String, MenuFunction>();
        buildMenu();
        instance = this;
    }

    /**
     * Launch the server (in a non-applet context).
     */
    public final void runAsMain() {
        start();
    }

    /**
     *  Supply a message to appear in the Help..About dialog.
     *  Typically, this indicates the origin of the source code
     *  being animated and the name of the person who prepared the
     *  animation.
     **/
    @Override
    public abstract String about();


    /**
     * Override this to call register (below) to set up the menu items that will
     * be displayed in the Algorithms menu and optionally to call
     * registerStartingAction to set up code to be animated immediately upon
     * launch.
     */
    @Override
    public abstract void buildMenu();



    @Override
    public final MemoryModel getMemoryModel() {
        return memoryModel;
    }
    
    
    /**
     * Animated code must be able to access the relevant Animation instance
     * even though such code was written independently of the animation system.
     * This function returns that animation instance, under the assumption that the
     * server (which launches the animated code) will have registered its thread
     * for that purpose.
     *
     * @return the animation associated with a thread
     */
    public static JavaStandAloneServer algae()
    {
        return instance;
    }

    
    
    /**
     * This must be called at the beginning of each new function to signal
     * that a new empty record should be pushed onto the top of the stack.
     *
     * @param thisObj - A reference to an object of the class of
     *    which the current function is a member. Normally, "this"
     *    will do just fine.  thisObj is used to help locate
     *    the source code being animated, so, in a pinch (e.g., if
     *    animating a static function) another object whose source
     *    code lies in the same directory/folder will do.
     *
     * @return ActivationRecord for the new function call
     */

    public static ActivationRecord activate (Object thisObject) {
        return algae().memoryModel.getActivationStack().activate (thisObject);
    }




    /**
     * Show a variable as a global value in all displays.
     * Variables portrayed by this call are shown "in-line".
     *
     * @param label  the variable name (optional, can be "" or null)
     * @param value  the variable/value
     */
    protected final void globalVar(final String label, final int value) {
        memoryModel.globalVar(label, value);
    }

    /**
     * Show a variable as a global value in all displays.
     * Variables portrayed by this call are shown "in-line".
     *
     * @param label  the variable name (optional, can be "" or null)
     * @param var  the variable/value
     */
    protected final void globalVar (final String label, final Object var) {
        memoryModel.globalVar(label, var);
    }



    /**
     * Show a variable as a global value in all displays.
     * Variables portrayed by this call are shown as labeled
     * pointers to the actual value.
     *
     * @param label  the variable name (optional, can be "" or null)
     * @param var  the variable/value
     */
    protected final void globalRefVar (final String label, final Object var) {
        memoryModel.globalRefVar(label, var);
    }


    /**
     * A substitute for System.in - reads from the animation GUI.
     */
    public InputStream in;

    /**
     * A substitute for System.out - writes to the animation GUI.
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
    @Override
    public final void register(
            final String menuItemTitle,
            final MenuFunction theOperation) {
        algorithmsMenu.put (menuItemTitle, theOperation);
    }

    @Override
    public final void registerStartingAction(final MenuFunction action) {
        startingAction = action;
    }


    /**
     * Pops up a dialog box prompting for an input, pausing the
     * animation until a satisfactory input value is obtained from the
     * human operator.
     *
     * @param prompt  Text of the prompt message to be displayed
     * @param requiredPattern regular expression describing an
     *                        acceptable input value
     * @return a human-entered string matching the requiredPattern
     */
    @Override
    public final String promptForInput(
            final String prompt,
            final String requiredPattern) {
        String promptedInput = "";
        final ClientMessage promptMsg
            = new PromptForInputMessage(prompt, requiredPattern);
        try {
            getClientCommunications().sendToClient(promptMsg);
            while (promptedInput.equals("")) {
                final ServerMessage response
                    = getClientCommunications().getFromClient();
                if (response.getKind().equals("Shutdown")) {
                    forceShutdown();
                    break;
                } else if (response.getKind().equals("InputSupplied")) {
                    promptedInput = response.getDetail();
                } else if (!response.getKind().equals("Ack")) {
                    logger.warning("Message protocol error: encountered "
                            + response
                            + " while awaiting prompted input.\n");
                }
            }
        } catch (final InterruptedException ex) {
            logger.fine("Shutdown occurred while waiting for prompted input: "
                    + ex);
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
    @Override
    public final String promptForInput(final String prompt) {
        return promptForInput(prompt, ".*");
    }

    @Override
    public final AnimationContext context() {
        return memoryModel.context();
    }

    @Override
    public final void sendToClient(final ClientMessage msg) {
        try {
            communications.sendToClient(msg);
        } catch (final InterruptedException e) {
            logger.fine (
                    "Thread closed while waiting for message from client: "
                    + e);
        }
    }

    @Override
    public final void sendToClient(
            final Snapshot snap,
            final boolean completed) {
        final SnapshotMessage msg = new SnapshotMessage(snap, completed);
        sendToClient(msg);
    }

    @Override
    public final void sendSourceToClient(final String fileName) {
        if (!sourceCodeAlreadySent.contains(fileName)) {
            sourceCodeAlreadySent.add (fileName);
            final SourceCodeMessage msg
                = new SourceCodeMessage(fileName, load(fileName));
            sendToClient(msg);
        }
    }

    @Override
    public final SimulatedPrintStream sysout() {
        return out;
    }

    @Override
    public final void start() {
        communications.start();
        server.start();
    }

    /**
     * Thread used to actually drive and run the animated code.
     *
     * @author zeil
     */
    private class LauncherThread extends Thread {

        /**
         * Most recently selected menu action.
         */
        private MenuFunction selectedAction;


        /**
         * Create a new thread.
         */
        public LauncherThread() {
            super("Animation Code Launcher");
            selectedAction = null;
        }

        /**
         * Run animated code bound to a selected menu item.
         *
         * @param action the selected menu binding.
         */
        public synchronized void runFunction (final MenuFunction action) {
            while (selectedAction != null) {
                try {
                    wait ();
                } catch (final InterruptedException e) {
                    if (!stopped) {
                        e.printStackTrace();
                    }
                }
            }
            selectedAction = action;
            notifyAll();
        }


        @Override
        public void run() {
            logger.fine("Started server's launcher thread");
            if (startingAction != null) {
                selectedAction = startingAction;
            } else {
                final MemoryModel memory = getMemoryModel();
                memory.getActivationStack().clear();
                final Snapshot snap = memory.renderInto(
                        "Choose an Algorithm",
                        new SourceLocation("", 0));
                sendToClient(snap, true);
            }
            while (!stopped) {
                Thread.yield();
                synchronized (this) {
                    while (selectedAction == null) {
                        try {
                            wait ();
                        } catch (final InterruptedException e) {
                            if (!stopped) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                selectedAction.selected();
                synchronized (this) {
                    selectedAction = null;
                    notifyAll();
                }
                final MemoryModel memory = getMemoryModel();
                memory.getActivationStack().clear();
                final Snapshot snap = memory.renderInto(
                        "Choose an Algorithm",
                        new SourceLocation("", 0));
                sendToClient(snap, true);
            }

        }
    }




    @Override
    public final void shutdown() {
        stopped = true;
        final int delayTimeInMilliSeconds = 230;
        try {
            Thread.sleep(delayTimeInMilliSeconds);
            if (server.isAlive()) {
                server.interrupt();
            }
            if (launcher.isAlive()) {
                launcher.interrupt();
            }
        } catch (final Exception e) {
            logger.warning("Difficulty shutting down server: " + e);
        }
    }

    @Override
    public final void forceShutdown() {
        shutdown();
    }

    /**
     * Attempt to find and load the source code indicated by the given
     * file name.
     * @param fileName name where we can expect to find this source code
     * @return text of the source code
     */
    @Override
    public final String load (final String fileName)
    {
        String className = fileName.replace(".java", "");
        className = className.replaceAll("[/\\\\]", ".");
        className = className.replaceFirst("\\$.*$", "");
        Class<?> container;
        try {
            container = Class.forName(className);
        } catch (final ClassNotFoundException e) {
            container = this.getClass();
            // Why not?  It might work, and couldn't hurt.
        }
        return load (container, fileName);
    }

    /**
     * Attempt to find and load the source code indicated by the given
     * file name.
     * @container a class whose loader can be used to retrieve the data.
     * @param fileName name where we can expect to find this source code
     * @return text of the source code
     */
    private final String load(
            final Class<?> container,
            final String fileName)
    {
        String contents = "Could not load " + fileName + "\n";

        final String resourceName = "/" + fileName.replace('\\', '/');

        InputStream resourceIn = container.getResourceAsStream(resourceName);
        //System.err.println ("resourceIn: " + resourceIn);

        if (resourceIn == null) {
            // Should not happen in actual operation (running from a Jar), but
            // is common when running/debugging from Eclipse or other IDEs
            final String classPath = System.getProperty("java.class.path");
            final String[] cpDirectories
                = classPath.split(System.getProperty("path.separator"));
            final ArrayList<String> directories
                = new ArrayList<String>(Arrays.asList(cpDirectories));
            directories.add("src/main/java".replace('/', File.separatorChar));
            directories.add("src/test/java".replace('/', File.separatorChar));
            for (final String dir: directories) {
                final File base = new File(dir);
                final File sourceFile = new File(base, fileName);
                if (sourceFile.exists()) {
                    try {
                        resourceIn = new FileInputStream(sourceFile);
                        break;
                    } catch (final FileNotFoundException e) {
                        resourceIn = null;
                    }
                }
            }
        }

        if (resourceIn != null) {
            final StringBuffer contentBuf = new StringBuffer();
            final BufferedReader in
                = new BufferedReader (new InputStreamReader(resourceIn));
            try {
                while (in.ready()) {
                    final String line = in.readLine();
                    contentBuf.append(line);
                    contentBuf.append("\n");
                }
                contents = contentBuf.toString();
            } catch (final IOException ex) {
                ex.printStackTrace();
                contents = contentBuf.toString() + "\n" + ex.toString();
            } finally {
                try {
                    in.close();
                } catch (final IOException e) {
                    logger.warning("Problem closing source file "
                            + fileName + ": " + e);
                }
            }
        }
        return contents;
    }


    /**
     * Provide an input from a dialog box popped up in a separate thread.
     *
     * @param value  input text
     */
    public final void inputSupplied(final String value) {
        // TODO
    }



    /**
     * Thread used to obtain messages from the client and dispatch appropriate
     * actions in response.
     *
     * @author zeil
     */
    private class ServerThread extends Thread {

        /**
         * An action in response  to a message from the client.
         *
         * @author zeil
         */
        private abstract class MessageAction {
            /**
             * Perform the action.
             * @param msgDetail contents of the "detail" field of the message
             *                  from the client
             * @throws InterruptedException on unexpected shutdown
             */
            public abstract void doIt(String msgDetail)
                    throws InterruptedException;
        }

        /**
         * Registered message actions.
         */
        private final HashMap<String, MessageAction> msgActions;

        /**
         * Action for GetSourceCode message.
         */
        private final MessageAction getSourceCodeAction = new MessageAction() {

            @Override
            public final void doIt(final String fileName)
                    throws InterruptedException {
                final String sourceCode = load(fileName);
                final SourceCodeMessage msg
                    = new SourceCodeMessage(fileName, sourceCode);
                getClientCommunications().sendToClient(msg);
            }
        };

        /**
         * Action for InputSupplied message.
         */
        private final MessageAction inputSuppliedAction
            = new MessageAction() {

            @Override
            public final void doIt(final String inputText)
                    throws InterruptedException {
                inputSupplied(inputText);
            }
        };

        /**
         * Action for Menu message.
         */
        private final MessageAction menuAction = new MessageAction() {

            @Override
            public final void doIt(final String msgDetail)
                    throws InterruptedException {
                final MenuFunction selected = algorithmsMenu.get(msgDetail);
                if (selected != null) {
                    launcher.runFunction(selected);
                } else {
                    logger.warning ("Unexpected menu selection: " + msgDetail);
                }
            }
        };

        /**
         * Action for Pull message.
         */
        private final MessageAction pullAction = new MessageAction() {

            @Override
            public final void doIt(final String msgDetail)
                    throws InterruptedException {
                final AckMessage ack = new AckMessage();
                getClientCommunications().sendToClient(ack);
            }
        };

        /**
         * Action for ShutDown message.
         */
        private final MessageAction shutDownAction = new MessageAction() {

            @Override
            public final void doIt(final String msgDetail)
                    throws InterruptedException {
                shutdown();
            }
        };

        /**
         * Action for Start message.
         */
        private final MessageAction startAction = new MessageAction() {

            @Override
            public final void doIt(final String msgDetail)
                    throws InterruptedException {
                final String[] menuItemTitles
                    = algorithmsMenu.keySet().toArray(new String[0]);
                String aboutStr = about();
                if (aboutStr == null) {
                    aboutStr = "";
                }
                final MenuMessage msg
                    = new MenuMessage(aboutStr, menuItemTitles);
                getClientCommunications().sendToClient(msg);
                launcher.start();
            }
        };

        /**
         * Create the server thread with bindings for each
         * client message type.
         */
        public ServerThread() {
            super("Server Message Handler");
            msgActions = new HashMap<String, MessageAction>();
            msgActions.put(ServerMessageTypes.GetSourceCode .toString(),
                    getSourceCodeAction);
            msgActions.put(ServerMessageTypes.InputSupplied.toString(),
                    inputSuppliedAction);
            msgActions.put(ServerMessageTypes.MenuItemSelected.toString(),
                    menuAction);
            msgActions.put(ServerMessageTypes.Pull.toString(),
                    pullAction);
            msgActions.put(ServerMessageTypes.ShutDown.toString(),
                    shutDownAction);
            msgActions.put(ServerMessageTypes.Start.toString(),
                    startAction);

        }


        @Override
        public final void run() {
            //System.err.println ("Started server thread");
            while (!stopped) {
                Thread.yield();
                ServerMessage msg;
                try {
                    msg = getClientCommunications()
                            .getFromClient();  // may block
                } catch (final InterruptedException e) {
                    stopped = true;
                    break;
                }
                final MessageAction action = msgActions.get(msg.getKind());
                if (action != null) {
                    try {
                        action.doIt(msg.getDetail());
                    } catch (final InterruptedException e) {
                        stopped = true;
                        break;
                    }
                } else {
                    logger.warning("Unexpected message from client: " + msg);
                }
            }
        }


    }

}



