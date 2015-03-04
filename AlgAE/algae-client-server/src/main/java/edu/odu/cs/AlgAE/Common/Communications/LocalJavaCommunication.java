/**
 * 
 */
package edu.odu.cs.AlgAE.Common.Communications;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import edu.odu.cs.AlgAE.Common.Communications.ServerMessage.ServerMessageTypes;

/**
 * This is an implementation of Client-Server communications for animations
 * where the animated code is Java, running in the same JVM instance as the
 * client.
 * 
 * @author zeil
 *
 */
public class LocalJavaCommunication implements ClientCommunications, ServerCommunications {
	
	private final static Logger logger = Logger.getLogger(LocalJavaCommunication.class.getName()); 
	
	private BlockingQueue<ClientMessage> clientMessages;
	private BlockingQueue<ServerMessage> serverMessages;
	
	

	/**
	 * Create a new communications path between a server and client.
	 */
	public LocalJavaCommunication()
	{
		final int QueueCapacity = 4;
		clientMessages = new ArrayBlockingQueue<ClientMessage>(QueueCapacity);
		serverMessages = new ArrayBlockingQueue<ServerMessage>(QueueCapacity);
	}

	/**
	 * Sends a message to the server - implementations are expected to be synchronized
	 * and might block until the server acknowledges receipt.
	 * 
	 * @param message
	 * @throws InterruptedException 
	 * @see edu.odu.cs.AlgAE.Common.Communications.ServerCommunications#sendToServer(edu.odu.cs.AlgAE.Common.Communications.ServerMessage)
	 */
	@Override
	public void sendToServer(ServerMessage message) throws InterruptedException {
		logger.fine("sendToServer: " + message);
		serverMessages.put (message);
		logger.finer("sendToServer: sent");
	}

	/**
	 * Receives a message from the server - implementations are expected to be synchronized
	 * and will block if no message is available.
	 * 
	 * @param message
	 * @throws InterruptedException 
	 * @see edu.odu.cs.AlgAE.Common.Communications.ServerCommunications#getFromServer()
	 */
	@Override
	public ClientMessage getFromServer() throws InterruptedException {
		logger.finer("getFromServer: starting");
		ClientMessage msg = clientMessages.take();
		logger.fine("getFromServer: " + msg);
	    serverMessages.put(new ServerMessage(ServerMessageTypes.Ack, msg.getClass().getName()));
		return msg;
	}

	/**
	 * Sends a message to the client - implementations are expected to be synchronized
	 * and might block until the client acknowledges receipt.
	 * 
	 * @param message
	 * @throws InterruptedException 
	 * @see edu.odu.cs.AlgAE.Common.Communications.ClientCommunications#sendToClient(edu.odu.cs.AlgAE.Common.Communications.ClientMessage)
	 */
	@Override
	public void sendToClient(ClientMessage message) throws InterruptedException {
		logger.fine("sendToClient: " + message);
		clientMessages.put(message);
		logger.finer("sendToClient: sent");
	}

	/**
	 * Receives a message from the client - implementations are expected to be synchronized
	 * and will block if no message is available.
	 * 
	 * @param message
	 * @throws InterruptedException 
	 * @see edu.odu.cs.AlgAE.Common.Communications.ClientCommunications#getFromClient()
	 */
	@Override
	public ServerMessage getFromClient() throws InterruptedException {
		logger.finer("getFromClient: starting");
		ServerMessage msg = serverMessages.take();
		logger.fine("getFromClient: " + msg);
		return msg;
	}



	
	
}
