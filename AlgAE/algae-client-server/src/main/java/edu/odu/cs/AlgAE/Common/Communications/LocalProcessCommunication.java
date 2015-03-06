package edu.odu.cs.AlgAE.Common.Communications;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
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
public class LocalProcessCommunication implements ServerCommunications {

    /**
     * Message logging.
     */
    private static final Logger LOG
    = Logger.getLogger(LocalProcessCommunication.class.getName());


    /**
     * Queue of unprocessed (undelivered) messages to the client.
     */
    private BlockingQueue<ClientMessage> clientMessages;

    /**
     * How large should the queue for unprocessed messages be? 
     */
    private static final int QUEUE_CAPACITY = 4;

    /**
     * Stream on which incoming messages from the server
     * will be received.
     */
    private final BufferedInputStream messagesIn;

    /**
     * Stream to which messages for the server can be written.
     */
    private final PrintStream messagesOut;


    /**
     * Thread to process incoming messages from the client.
     */
    private final CommunicationsManager manager;


    /**
     * Create a new communications path between a server and client.
     * 
     * @param msgsIn Stream on which incoming messages from the client
     *                will be received.
     * @param msgsOut Stream to which messages for the client can be written.
     */
    public LocalProcessCommunication (
            final InputStream msgsIn,
            final PrintStream msgsOut) {
        messagesIn = new BufferedInputStream(msgsIn);
        messagesOut = msgsOut;
        clientMessages 
        = new ArrayBlockingQueue<ClientMessage>(QUEUE_CAPACITY);
        manager = new CommunicationsManager();
    }




    /**
     * Sends a message to the server.
     *
     * @param message message to send
     * @throws InterruptedException on unexpected shutdown
     * @see
     *   edu.odu.cs.AlgAE.Common.Communications.ServerCommunications#sendToServer(edu.odu.cs.AlgAE.Common.Communications.ServerMessage)
     */
    @Override
    public final void sendToServer(final ServerMessage message) {
        LOG.fine(message.toString());
        messagesOut.println(message.toString());
        LOG.finer("sent");
    }

    /**
     * Receives a message from the server - implementations are expected 
     * to be synchronized and will block if no message is available.
     *
     * @throws InterruptedException on unexpected shutdown
     * @see
     *   edu.odu.cs.AlgAE.Common.Communications.ServerCommunications#getFromServer()
     */
    @Override
    public final ClientMessage getFromServer() throws InterruptedException {
        LOG.finer("starting");
        ClientMessage msg = clientMessages.take();
        LOG.fine("received: " + msg);
        return msg;
    }





    /**
     * Thread to manage incoming messages from the server.
     * 
     * @author zeil
     *
     */
    private class CommunicationsManager extends Thread {

        /**
         * True if a shutdown has been issued.
         */
        private boolean stopping = false;


        /**
         * Read (and deserialize) a message from the input stream.
         * 
         * @return a message from the server for the client
         * @throws IOException if the read or deserialization fails.
         */
        private ClientMessage readMsgFromServer() throws IOException {
            ClientMessage cmsg = ClientMessage.load(messagesIn);
            return cmsg;
        }

        /**
         * Try to let the thread shut down naturally, but if it is
         * unable to do so (because it is blocked), then interrupt
         * it.
         */
        public void shutdown() {
            final int shortTimeDelay = 100;
            stopping = true;
            try {
                sleep (shortTimeDelay);
            } catch (InterruptedException e) {
                return;
            }
            manager.interrupt();
        }


        @Override
        public void run() {
            try {
                /*
                 * The server drives most communication. It may volunteer
                 * new messages to the client at any time, and we should
                 * try to keep up, but it's OK if the client blocks because
                 * we are falling behind.
                 */
                while (!stopping) {
                    ClientMessage cmsg = readMsgFromServer();
                    clientMessages.put(cmsg); // can block
                    ServerMessage smsg 
                        = new ServerMessage(ServerMessageTypes.Ack);
                    sendToServer (smsg);
                }
            } catch (IOException e) {
                LOG.severe("Problem when reading messages from server: " + e);
            } catch (InterruptedException e) {
                if (!stopping) {
                    LOG.warning("Unexpected shutdown " + e);
                }
            }
        }




    }


    /**
     * Start the threads monitoring communications between the client
     * and server.
     */
    public final void start() {
        manager.start();
    }

    /**
     * Stop the threads monitoring communications between the client and server.
     */
    public final void shutdown() {
        manager.shutdown();
    }

}
