/**
 * 
 */
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
public class StandardIOCommunication implements ClientCommunications {
	
	private final static Logger logger = Logger.getLogger(StandardIOCommunication.class.getName()); 
	
	
	
	private BlockingQueue<ServerMessage> serverMessages;
	private CommunicationsManager manager;
	private final int QueueCapacity = 4;
	
	private BufferedReader messagesIn;
	private PrintStream messagesOut;
	
	private boolean awaitingAck = false;
	private boolean stopping = false;
	private boolean started = false;
	
	/**
	 * Create a new communications path between a server and client.
	 */
	public StandardIOCommunication(InputStream msgsIn, PrintStream msgsOut)
	{
		messagesIn = new BufferedReader(new InputStreamReader(msgsIn));
		messagesOut = msgsOut;
		serverMessages = new ArrayBlockingQueue<ServerMessage>(QueueCapacity);
		manager = new CommunicationsManager();
	}





	
	private class CommunicationsManager extends Thread {
						
		public void run()
		{
			try {
				// The server drives the communications. It may volunteer new messages to the
				// client at any time, and we should try to keep up, but it's OK if the server blocks because
				// we are falling behind.
				// After each message, it will wait for an acknowledgment from the client before
				// proceeding.
				// When it's ready for a new Client message, it will send a special Pull request, at which
				// point we respond with a client message if one is in the queue or block until that time.
				while (!stopping) {
					String line = messagesIn.readLine();
					if (line == null) {
						line = "Shutdown:";
					}
					if (line.startsWith("Shutdown:")) {
						stopping = true;
						serverMessages.put(new ServerMessage(ServerMessageTypes.ShutDown));
					} else if (line.startsWith("Ack:")) {
						synchronized (manager) {
							awaitingAck = false;
							manager.notifyAll();
						}
					} else {
						int divider = line.indexOf(':');
						String kind = (divider > 0) ? line.substring(0, divider) : line;
						String detail = (divider > 0) ? line.substring(divider+1) : "";
						serverMessages.put(new ServerMessage(kind, detail));
					}
				}
			} catch (IOException e) {
				logger.severe("Error in communications: " + e);
			}
			catch (InterruptedException e) {
				if (!stopping)
					logger.warning("Irregular shutwodn of communications: " + e);
			}
		}
	}


	/**
	 * Start the threads monitoring communications between the client and server
	 */
	public void start() {
		started = true;
		manager.start();
	}




	@Override
	public void sendToClient(ClientMessage message) throws InterruptedException {
		if (!started) {
			start();
		}
		synchronized (manager) {
			while (awaitingAck) {
				manager.wait();
			}
			awaitingAck = true;
		}
		messagesOut.println(message.serialize());
	}




	@Override
	public ServerMessage getFromClient() throws InterruptedException {
		if (!started) {
			start();
		}
		return serverMessages.take();
	}
	
	
}
