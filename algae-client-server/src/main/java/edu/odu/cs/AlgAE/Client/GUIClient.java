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
import java.util.logging.Logger;

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
 * The "normal" Animator for AlgAE animations, this portrays a series of
 * state snapshots via a GUI with controls for selecting algorithms,
 * starting, pausing, and rewinding playback.
 *
 * @author zeil
 *
 */
public class GUIClient extends Client {

    /**
     * Message logging.
     */
    private static final Logger LOG
        = Logger.getLogger(GUIClient.class.getName());

    /**
     * The overall animation view: data, source code, & console.
     */
    private ViewerPanel clientView;
    
    /**
     * The panel portraying data states.
     */
    private AnimatorPanel animator;
    
    /**
     * The list of items to appear in the algorithm. These are
     * the direct controls for launching animated functions.  
     */
    private final List<JMenuItem> algorithmItems;

    /**
     * The menu of algorithm itmes.
     */
    private JMenu algorithmMenu;
    
    /**
     * The thread responsible for processing incoming
     * messages from the server.
     */
    private ClientThread messageReader;
    
    /**
     * A panel portraying System I/O to/from the
     * animated code.
     */
    private IOPane ioPane;
    
    /**
     * A panel for displaying synchronized views of the
     * source code of the naimated functions.
     */
    private SourcePane sourcePane;

    /**
     * Help data.
     */
    private HelpSet hs;
    
    /**
     * Manages the help data.
     */
    private HelpBroker hb;

    /**
     * String to be show when Help->About is selected.
     */
    private String aboutString = "";

    /**
     * Indicates whether we are running some portion of the
     * animated code.
     */
    private boolean running;


    /**
     * Indicates that animation has been shut down.
     */
    private boolean terminated;


    /**
     * Create a GUI client. Communications must be set later. 
     */
    public GUIClient() {
        super(null);

        running = false;
        terminated = false;
        algorithmItems = new LinkedList<JMenuItem>();

        messageReader = null;
    }


    /**
     * Create a GUI client.
     * @param serverComm communications interface to server.
     */
    public GUIClient(final ServerCommunications serverComm) {
        super(serverComm);

        running = false;
        terminated = false;
        algorithmItems = new LinkedList<JMenuItem>();

        messageReader = null;
    }


    /* (non-Javadoc)
     * @see edu.odu.cs.AlgAE.Client.ClientBase#init(boolean)
     */
    @Override
    public final void init () {
        setLayout (new BorderLayout());


        ioPane = new IOPane (getServerAccess());
        sourcePane = new SourcePane(getServerAccess());

        animator = new AnimatorPanel(sourcePane);
        clientView = new ViewerPanel ("AlgAE", animator, sourcePane, ioPane);

        add (clientView, BorderLayout.CENTER);

    }


    @Override
    public final JMenuBar buildMenu() {

        final JMenuBar menuBar = new JMenuBar();

        final JMenu fileMenu = new JMenu ("File", false);
        menuBar.add(fileMenu);

        final JMenuItem exitItem = new JMenuItem ("Exit");
        fileMenu.add (exitItem);
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Component src = ioPane;
                while (!(src instanceof JFrame)) {
                    src = src.getParent();
                }
                final JFrame mainWindow = (JFrame) src;
                mainWindow.dispatchEvent(
                        new WindowEvent(mainWindow, 
                                WindowEvent.WINDOW_CLOSING));
                mainWindow.setVisible(false);
            }
        });


        algorithmMenu = new JMenu ("Algorithm", false);
        menuBar.add (algorithmMenu);


        final JMenu helpMenu = new JMenu ("Help", false);
        menuBar.add(helpMenu);


        final JMenuItem helpItem = new JMenuItem ("Help");
        helpMenu.add (helpItem);

        final String helpHS = "edu/odu/cs/AlgAE/Client/Help/helpset.hs";
        final ClassLoader cl = GUIClient.class.getClassLoader();
        URL hsURL = HelpSet.findHelpSet(cl, helpHS);
        if (hsURL == null) {
            // Most likely indicates we are running from an IDE rather than
            // from a fully packaged JAR
            String fileName = "src/main/resources/" + helpHS;
            fileName = fileName.replace('/', File.separatorChar);
            final File possibleHelp = new File(fileName);
            if (possibleHelp.exists()) {
                try {
                    hsURL = possibleHelp.toURI().toURL();
                } catch (final MalformedURLException e1) {
                    LOG.warning("Could not convert " + fileName
                            + " to url: " + e1);
                } catch (final Exception e2) {
                    LOG.warning ("Unexpected exception converting " 
                            + fileName + " to url: " + e2);
                }
            }
        }
        if (hsURL != null) {
            try {
                hs = new HelpSet(null, hsURL);
            } catch (final HelpSetException e1) {
                LOG.warning ("Problem loading help set from " 
                        + hsURL + ": " + e1);
            }
            if (hs != null) {
                hb = hs.createHelpBroker();

                helpItem.addActionListener(new CSH.DisplayHelpFromSource (hb));
            }
        } else {
            LOG.warning("**error: Could not locate help set files");
        }
        final JMenuItem aboutItem = new JMenuItem ("About AlgAE");
        helpMenu.add (aboutItem);
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Component src = ioPane;
                while (!(src instanceof JFrame)) {
                    src = src.getParent();
                }
                final JFrame mainWindow = (JFrame) src;

                JOptionPane.showMessageDialog(mainWindow,
                        aboutString
                        + "\n----------------------------\n"
                        + "AlgAE Algorithm Animation Engine, "
                        + "version 3.0\n\n"
                        + "  Steven J. Zeil\n"
                        + "  Old Dominion University\n"
                        + "  Dept. of Computer Science");
            }
        });

        return menuBar;

    }




    /* (non-Javadoc)
     * @see edu.odu.cs.AlgAE.Client.ClientBase#start()
     */
    @Override
    public final void start() {
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
    public final void stop() {
        if (running) {
            running = false;
            animator.pauseAnimator();
        }
    }

    /* (non-Javadoc)
     * @see edu.odu.cs.AlgAE.Client.ClientBase#destroy()
     */
    @Override
    public final void destroy() {
        if (!terminated) {
            terminated = true;
            running = false;
            animator.shutdown();
            final ServerMessage shutDownMsg 
                = new ServerMessage(ServerMessageTypes.ShutDown);
            try {
                getServerAccess().sendToServer(shutDownMsg);
            } catch (final InterruptedException e) {
                LOG.finer ("Unexpected shutdown: " + e);
            }
            try {
                final int timeDelay = 500;
                Thread.sleep(timeDelay);
                if (messageReader.isAlive()) {
                    messageReader.interrupt();
                }
            } catch (final Exception e1) {
                LOG.finer ("Unexpected error during shutdown: " + e1);
            }
        }
    }



    /**
     * How to process algorithm menu items.
     */
    private final ActionListener algorithmMenuAction = new ActionListener() {

        @Override
        public void actionPerformed(final ActionEvent ev) {
            final JMenuItem selected = (JMenuItem) ev.getSource();
            final String name = selected.getText();
            algorithmMenuItemSelected(name);
        }
    };

    /**
     * Register an algorithm menu item name.
     * 
     * @param menuItemTitle item name
     */
    public final void registerMenuItem(final String menuItemTitle) {
        final JMenuItem newItem = new JMenuItem (menuItemTitle);
        algorithmMenu.add(newItem);
        algorithmItems.add(newItem);
        newItem.addActionListener(algorithmMenuAction);
    }


    /**
     * Set all algorithm items to an enabled/disabled state.
     * @param enabled true to enable, false to disable.
     */
    protected final void enableAlgorithmMenuItems(final boolean enabled) {
        for (final JMenuItem item: algorithmItems) {
            item.setEnabled(enabled);
        }
        algorithmMenu.setEnabled(enabled);
    }


    /**
     * Pass a snapshot to the animator for eventual display.
     * @param snapshot data state (snapshot) denoting a future animation frame
     * @throws InterruptedException on unexpected shutdown
     */
    public final void showSnapshot(final Snapshot snapshot)
            throws InterruptedException {
        animator.add(snapshot);
    }


    /**
     * Actions taken in response to incoming messages from the server.
     * @author zeil
     */
    private abstract class MessageAction {
        /**
         * Perform an action.
         * @param msg message from server
         */
        public abstract void doIt(ClientMessage msg);
    }


    /**
     * Thread to process messages from server as they arrive.
     * 
     * @author zeil
     *
     */
    private class ClientThread extends Thread {
        
        /**
         * The client served by this thread.
         */
        private final GUIClient client;

        /**
         * Registered actions for various message types.
         */
        private final HashMap<String, MessageAction> msgActions;

        /**
         * Last snapshot received. Used to reverse incoming snapshot diffs.
         */
        private Snapshot lastSnap;

        /**
         * Action for Ack messages.
         */
        private final MessageAction ackMessageReceived = new MessageAction() {

            @Override
            public void doIt(final ClientMessage msg) {
            }
        };
        
        /**
         * Action for a captured output message: display output in I/O Pane.
         */
        private final MessageAction capturedOutputMessageReceived 
            = new MessageAction() {

            @Override
            public void doIt(final ClientMessage msg) {
                final CapturedOutputMessage m = (CapturedOutputMessage)msg;
                ioPane.print (m.getOutput());
            }
        };

        /**
         * Action for forced shutdown message: signal that the animation
         * has ended.
         */
        private final MessageAction forceShutDownMessageReceived
            = new MessageAction() {

            @Override
            public void doIt(final ClientMessage msg) {
                final ForceShutDownMessage m = (ForceShutDownMessage) msg;
                ioPane.print ("** Animation server is shutting down:\n" 
                        + m.getExplanation() + "\n");
            }
        };

        /**
         * Action for menu message: register items to appear in algorithm menu.
         */
        private final MessageAction menuMessageReceived = new MessageAction() {

            @Override
            public void doIt(final ClientMessage msg) {
                final MenuMessage m = (MenuMessage) msg;
                client.aboutString = m.getAbout();
                for (final String menuItem: m.getMenuItems()) {
                    client.registerMenuItem(menuItem);
                }
            }
        };

        /**
         * Action for PromptForInput message: prompt the user for interactive 
         * input requested by the animated code.
         */
        private final MessageAction promptForInputMessageReceived 
            = new MessageAction() {

            @Override
            public void doIt(final ClientMessage msg) {
                final PromptForInputMessage m = (PromptForInputMessage) msg;
                final String promptString = m.getPrompt();
                String pattern = m.getRequiredPattern();
                if (pattern == null || pattern.length() == 0) {
                    pattern = ".*";
                }
                promptForInputDialog(promptString, pattern);
            }
        };

        /**
         * Action for snapshot message: prepare a new frame for 
         * the animation.
         */
        private final MessageAction snapshotMessageReceived
            = new MessageAction() {

            @Override
            public void doIt(final ClientMessage msg) {
                final SnapshotMessage m = (SnapshotMessage) msg;
                final SnapshotDiff diff = m.getSnapshot();
                final boolean completed = m.isMenuItemCompleted();
                final Snapshot snapshot = diff.reconstruct(lastSnap);
                lastSnap = snapshot;
                try {
                    animator.add(snapshot);
                } catch (final InterruptedException e) {
                    ioPane.print("Connection to server has failed\n");
                }
                if (completed) {
                    menuItemCompleted(snapshot);
                }
            }
        };

        /**
         * Action for source code message: add source code to Source Pane.
         */
        private final MessageAction sourceCodeMessageReceived
            = new MessageAction() {

            @Override
            public void doIt(final ClientMessage msg) {
                final SourceCodeMessage m = (SourceCodeMessage) msg;
                sourcePane.addSourceCode (m.getFilePath(), m.getSourceText());
            }
        };

        /**
         * Create a new client thread.
         * @param theClient client served by this thread. 
         */
        public ClientThread(final GUIClient theClient) {
            super("ClientThread");
            this.client = theClient;
            lastSnap = new Snapshot();
            msgActions = new HashMap<String, MessageAction>();
            msgActions.put (AckMessage.class.getSimpleName(), 
                            ackMessageReceived);
            msgActions.put (CapturedOutputMessage.class.getSimpleName(), 
                            capturedOutputMessageReceived);
            msgActions.put (ForceShutDownMessage.class.getSimpleName(), 
                            forceShutDownMessageReceived);
            msgActions.put(MenuMessage.class.getSimpleName(), 
                            menuMessageReceived);
            msgActions.put(PromptForInputMessage.class.getSimpleName(), 
                            promptForInputMessageReceived);
            msgActions.put(SnapshotMessage.class.getSimpleName(),
                            snapshotMessageReceived);
            msgActions.put(SourceCodeMessage.class.getSimpleName(),
                            sourceCodeMessageReceived);

        }


        /**
         * Send a Start message to the server and then begin processing
         * client messages received form the server.
         */
        @Override
        public void run() {
            synchronized (client) {
                enableAlgorithmMenuItems (false);
                animator.startPlay();
                final ServerMessage startMsg
                    = new ServerMessage(ServerMessageTypes.Start);
                try {
                    getServerAccess().sendToServer(startMsg);
                } catch (final InterruptedException e) {
                    ioPane.print("Connection to server has failed\n");
                }
            }

            while (!terminated) {
                Thread.yield();
                ClientMessage msg;
                try {
                    msg = getServerAccess().getFromServer();
                } catch (final InterruptedException e) {
                    break;
                }
                interpret (msg);
            }
        }


        /**
         * Select an perform the appropriate action for a message from the
         * server.
         * @param msg a message from the server.
         */
        private void interpret(final ClientMessage msg) {
            LOG.finer("Client received " + msg);
            final MessageAction action 
                = msgActions.get(msg.getClass().getSimpleName());
            if (action != null) {
                action.doIt(msg);
            } else {
                LOG.severe ("**Illegal message from server: " + msg);
            }
        }




    }

    /**
     * GUI action when an item is selected from the algorithm menu.
     * A message is sent to the server indicating the selected item.
     * @param name name of the menu item
     */
    protected final void algorithmMenuItemSelected(final String name) {
        enableAlgorithmMenuItems (false);
        animator.clear();
        animator.startStepping();
        final ServerMessage menuMsg 
            = new ServerMessage(ServerMessageTypes.MenuItemSelected, name);
        try {
            getServerAccess().sendToServer(menuMsg);
        } catch (final InterruptedException e) {
            ioPane.print("Connection to server has failed\n");
        }
    }

    /**
     * Notify the animator that a function being animated has
     * completed, and the user may not select another.
     * @param snapshot data state upon completion.
     */
    public void menuItemCompleted(final Snapshot snapshot) {
        animator.showStatus("Choose an Algorithm");
        animator.endofSequence();
        enableAlgorithmMenuItems (true);
    }





    /**
     * Display a dialog box with a prompt for interactive input.
     * @param prompt prompt string to display
     * @param requiredPattern a regular expression governing input that
     *         will be accepted as legal.
     */
    public final void promptForInputDialog(final String prompt, 
                                            final String requiredPattern) {
        String response = JOptionPane.showInputDialog(
                this,
                prompt, 
                "Input Requested", 
                JOptionPane.QUESTION_MESSAGE);
        while (!response.matches(requiredPattern)) {
            response = JOptionPane.showInputDialog(
                    this, 
                    prompt, 
                    "Try again", 
                    JOptionPane.WARNING_MESSAGE);
        }
        final ServerMessage msg 
            = new ServerMessage(ServerMessageTypes.InputSupplied, response);
        try {
            getServerAccess().sendToServer(msg);
        } catch (final InterruptedException e) {
            ioPane.print("Connection to server has failed\n");
        }
    }




    /**
     * Provide access to the I/O panel.
     * @return the I/O panel
     */
    public final IOPane getIOPane() {
        return ioPane;
    }







    /**
     * Provide access to the animator.
     * @return the animator pane.
     */
    public final AnimatorPanel getAnimator() {
        return animator;
    }







}
