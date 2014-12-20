/**
 * 
 */
package edu.odu.cs.AlgAE.Common.Communications;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * This is an implementation of Client-Server communications for animations
 * where the animated code is Java, running in the same JVM instance as the
 * client.
 * 
 * @author zeil
 *
 */
public class LocalJavaCommunication implements ClientCommunications, ServerCommunications {
	
	private BlockingQueue<ClientMessage> clientMessages;
	private BlockingQueue<ServerMessage> serverMessages;
	private boolean debugSend = false;
	private boolean debugReceive = false;
	
	

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
		if (debugSend)
			System.out.println ("sendToServer: " + message);
		serverMessages.put (message);
		if (debugSend)
			System.out.println ("sendToServer: sent");
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
		if (debugReceive)
			System.out.println ("getFromServer: starting");
		ClientMessage msg = clientMessages.take();
		if (debugReceive)
			System.out.println ("getFromServer: " + msg);
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
		if (debugSend)
			System.out.println ("sendToClient: " + message);
		clientMessages.put(message);
		if (debugSend)
			System.out.println ("sendToClient: sent");
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
		if (debugReceive)
			System.out.println ("getFromClient: starting");
		ServerMessage msg = serverMessages.take();
		if (debugReceive)
			System.out.println ("getFromClient: " + msg);
		return msg;
	}



	/**
	 * Turns message send debugging on and off
	 * 
	 * @param debugSend
	 */
	public void setDebugSend(boolean debugSend) {
		this.debugSend = debugSend;
	}


	/**
	 * Turns message receive debugging on and off
	 * @param debugReceive the debugReceive to set
	 */
	public void setDebugReceive(boolean debugReceive) {
		this.debugReceive = debugReceive;
	}



	
	
}
