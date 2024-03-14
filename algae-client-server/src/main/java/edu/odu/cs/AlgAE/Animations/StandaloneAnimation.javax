package edu.odu.cs.AlgAE.Animations;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import edu.odu.cs.AlgAE.Client.GUIClient;
import edu.odu.cs.AlgAE.Common.Communications.LocalProcessCommunication;

/**
 *  An AlgAE animation that works with a server running
 *  in a separate process on the same machine.
 *  Communications is via standard I/O with that process.
 *
 *  @author Steven J Zeil
 **/
public class StandaloneAnimation  {

    /**
     * Message logging.
     */
    private static final Logger LOG
    = Logger.getLogger(StandaloneAnimation.class.getName());

    /**
     * The client (GUI) for this animation.
     */
    private final GUIClient client;

    /**
     * Communications between the client and server.
     */
    private final LocalProcessCommunication communications;
    
    
    /**
     * The window containing the client GUI
     */
    private final JFrame window;

    /**
     * The stream from which messages from the server can be read.
     */
    private final InputStream fromServer;

    /**
     * The stream to which messages can be written to the server.
     */
    private final PrintStream toServer;

    /**
     * The process running the server.
     */
    private Process serverProcess;

    /**
     * Thread to force shutdown of server if the client closes
     * unexpectedly.
     * 
     * @author zeil
     *
     */
    private class ProcessShutdown extends Thread {
        
        /**
         * The process being managed.
         */
        private Process process;
        
        /**
         * Create, but do not start, the shutdown thread.
         * @param processToShutDown process to shut down
         */
        public ProcessShutdown (final Process processToShutDown) {
            process = processToShutDown;
        }
        
        /**
         * Shut down the process.
         */
        public void run() {
            process.destroy();
        }
        
    }

    /**
     * Create a new animation.
     * @param title title string (for display in client window)
     * @param executableCommand the command to launch the server
     */
    public StandaloneAnimation (
            final String title,
            final List<String> executableCommand) {
        DefaultLogSetting.setupLogging(false,  "algae-client%u.log");
        DefaultLogSetting.defaultLevel = Level.FINE;
        final ProcessBuilder pb = new ProcessBuilder (executableCommand);
        try {
            serverProcess = pb.start();
            Runtime.getRuntime().addShutdownHook(
                    new ProcessShutdown(serverProcess));
        } catch (final IOException ex) {
            LOG.severe ("Unable to launch server command: " + ex);
            System.exit (1);
        }
        fromServer = serverProcess.getInputStream();
        toServer = new PrintStream(
                new BufferedOutputStream(serverProcess.getOutputStream()));

        communications = new LocalProcessCommunication(fromServer, toServer);
        client = new GUIClient(communications);
        client.init();
        
        window = new JFrame(title);
        window.setJMenuBar(client.buildMenu());
        window.getContentPane().add(client);
        
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                client.stop();
                client.destroy();
                window.setVisible(false);
            }
        });
        window.pack();
    }


    /**
     * Start the threads making up this animation and show the GUI.
     */
    public final void start() {
        window.setVisible(true);
        client.start();
        communications.start();
    }

    /**
     * Run the animation as a stand-alone program.
     * @param args One command-line parameter - the command to run
     *             the separate server.
     */
    public static void main (final String[] args) {
        if (args.length == 0) {
            System.err.println(
                "Usage: java "
                + "edu.odu.cs.AlgAE.Animation.StandaloneAnimation "
                + "server_command");
            System.exit(2);
        }
        List<String> command = Arrays.asList(args);
        StandaloneAnimation anim =
                new StandaloneAnimation("", command);
        anim.start();
    }


}



