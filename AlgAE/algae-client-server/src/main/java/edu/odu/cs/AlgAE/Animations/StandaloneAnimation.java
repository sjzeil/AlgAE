package edu.odu.cs.AlgAE.Animations;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import edu.odu.cs.AlgAE.Client.GUIClient;
import edu.odu.cs.AlgAE.Common.Applets.AnimationApplet;
import edu.odu.cs.AlgAE.Common.Communications.LocalProcessCommunication;

/**
 *  An AlgAE animation that works with a server running
 *  in a separate process on the same machine.
 *  Communications is via standard I/O with that process.
 *
 *  @author Steven J Zeil
 **/
public class StandaloneAnimation extends AnimationApplet {

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
     * Communications between the cleint and server.
     */
    private final LocalProcessCommunication communications;

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
     * Create a new animation.
     * @param title title string (for display in client window)
     * @param executableCommand the command to launch the server
     */
    public StandaloneAnimation (
            final String title,
            final List<String> executableCommand) {
        super(title);

        final ProcessBuilder pb = new ProcessBuilder (executableCommand);
        try {
            serverProcess = pb.start();
        } catch (final IOException ex) {
            LOG.severe ("Unable to launch server command: " + ex);
            System.exit (1);
        }
        fromServer = serverProcess.getInputStream();
        toServer = new PrintStream(
                new BufferedOutputStream(serverProcess.getOutputStream()));

        communications = new LocalProcessCommunication(fromServer, toServer);
        client = new GUIClient(communications);
        setClient(client);
        client.init(false);
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



