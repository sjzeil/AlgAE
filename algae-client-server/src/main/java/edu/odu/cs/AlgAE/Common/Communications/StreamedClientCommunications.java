package edu.odu.cs.AlgAE.Common.Communications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import edu.odu.cs.AlgAE.Common.Communications.ServerMessage.ServerMessageTypes;

/**
 * This is an implementation of Client-Server communications for animations
 * where the animated code is in a separate executable running on the same
 * machine. The client and server message streams are multiplexed in a way that
 * allows both sets of messages to be exchanged via the other process's standard
 * input and output streams.
 *
 * @author zeil
 *
 */
public class StreamedClientCommunications implements ClientCommunications {

    /**
     * Message logging.
     */
    private static final Logger LOG
        = Logger.getLogger(StreamedClientCommunications.class.getName());


    /**
     * Queue of unprocessed messages from the client.
     */
    private final BlockingQueue<ServerMessage> serverMessages;
    
    /**
     * Thread to process incoming messages from the client.
     */
    private final CommunicationsManager manager;
    
    /**
     * How large should the queue for unprocessed messages be? 
     */
    private static final int QUEUE_CAPACITY = 4;

    /**
     * Stream on which incoming messages from the client
     * will be received.
     */
    private final BufferedReader messagesIn;
    
    /**
     * Stream to which messages for the client can be written.
     */
    private final PrintStream messagesOut;

    /**
     * True when a message has been sent to the client but an
     * answering Ack has not been received.
     */
    private boolean awaitingAck = false;
    
    /**
     * True when a shutdown message has been received.
     */
    private boolean stopping = false;
    
    /**
     * True when the server threads have been started.
     */
    private boolean started = false;

    /**
     * Create a new communications path between a server and client.
     * 
     * @param msgsIn Stream on which incoming messages from the client
     *                will be received.
     * @param msgsOut Stream to which messages for the client can be written.
     */
    public StreamedClientCommunications(
            final InputStream msgsIn,
            final PrintStream msgsOut) {
        messagesIn = new BufferedReader(new InputStreamReader(msgsIn));
        messagesOut = msgsOut;
        serverMessages = new ArrayBlockingQueue<ServerMessage>(QUEUE_CAPACITY);
        manager = new CommunicationsManager();
    }





    /**
     * Thread to handle incoming messages.
     * 
     * @author zeil
     *
     */
    private class CommunicationsManager extends Thread {

        @Override
        public void run() {
            try {
                /*
                 * The server drives the communications. It may volunteer new
                 * messages to the client at any time, and we should try to
                 * keep up, but it's OK if the server blocks because we are
                 * falling behind.
                 * After each message, it will wait for an acknowledgment from
                 * the client before proceeding. When it's ready for a new
                 * Client message, it will send a special Pull request, at
                 * which point we respond with a client message if one is in
                 * the queue or block until that time.
                 */
                while (!stopping) {
                    LOG.finer("Awaiting message from client");
                    String line = messagesIn.readLine();
                    LOG.fine("Received from client: " + line);
                    if (line == null) {
                        line = "Shutdown:";
                    }
                    if (line.startsWith("Shutdown:")) {
                        stopping = true;
                        serverMessages.put(new ServerMessage(
                                ServerMessageTypes.ShutDown));
                    } else if (line.startsWith("Ack:")) {
                        synchronized (manager) {
                            awaitingAck = false;
                            manager.notifyAll();
                        }
                    } else {
                        final int divider = line.indexOf(':');
                        final String kind 
                            = (divider > 0) ? line.substring(0, divider) : line;
                        String detail 
                            = (divider > 0) ? line.substring(divider + 1) : "";
                        while (detail.length() > 0 && detail.charAt(0) == ' ') {
                            detail = detail.substring(1);
                        }
                        serverMessages.put(new ServerMessage(kind, detail));
                    }
                }
            } catch (final IOException e) {
                LOG.severe("Error in communications: " + e);
            }
            catch (final InterruptedException e) {
                if (!stopping) {
                    LOG.warning("Irregular shutdown of communications: " + e);
                }
            }
        }
    }


    /**
     * Start the threads monitoring communications between the client
     * and server.
     */
    public final void start() {
        started = true;
        manager.start();
    }




    @Override
    public final void sendToClient(final ClientMessage message)
            throws InterruptedException {
        if (!started) {
            start();
        }
        synchronized (manager) {
            while (awaitingAck) {
                LOG.fine("Server is awaiting ack of prior message before sending: " + message);
                manager.wait();
            }
            awaitingAck = true;
        }
        LOG.fine("About to send to client: " + message);
        messagesOut.println(message.serialize());
        LOG.fine("Sent to client: " + message);
    }




    @Override
    public final ServerMessage getFromClient() 
            throws InterruptedException {
        if (!started) {
            start();
        }
        return serverMessages.take();
    }


}
