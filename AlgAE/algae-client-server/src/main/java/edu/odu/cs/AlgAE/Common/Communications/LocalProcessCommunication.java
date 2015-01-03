/**
 * 
 */
package edu.odu.cs.AlgAE.Common.Communications;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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
	
	private BlockingQueue<ClientMessage> clientMessages;
	private BlockingQueue<ServerMessage> serverMessages;
	private boolean debugSend = true;
	private boolean debugReceive = true;
	private File pathToExecutable;
	private CommunicationsManager manager;
	private final int QueueCapacity = 4;
	
	
	/**
	 * Create a new communications path between a server and client.
	 */
	public LocalProcessCommunication(File pathToExecutable)
	{
		clientMessages = new ArrayBlockingQueue<ClientMessage>(QueueCapacity);
		serverMessages = new ArrayBlockingQueue<ServerMessage>(QueueCapacity);
		this.pathToExecutable = pathToExecutable;
		manager = null;
	}

	/**
	 * Create a new communications path between a server and client.
	 */
	public LocalProcessCommunication()
	{
		clientMessages = new ArrayBlockingQueue<ClientMessage>(QueueCapacity);
		serverMessages = new ArrayBlockingQueue<ServerMessage>(QueueCapacity);
		this.pathToExecutable = null;
		manager = null;
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



	/**
	 * @return the pathToExecutable
	 */
	public File getPathToExecutable() {
		return pathToExecutable;
	}


	/**
	 * @param pathToExecutable the pathToExecutable to set
	 */
	public void setPathToExecutable(File pathToExecutable) {
		this.pathToExecutable = pathToExecutable;
	}

	
	private class CommunicationsManager extends Thread {
		
		private Process process = null;
		private boolean stopping = false;
		private final String EndOfClientMessageMarker = "</message";
		private BufferedReader fromServer;
		private PrintStream toServer;
		
		
		private ClientMessage readMsgFromServer() throws IOException
		{
			StringBuffer msgBuf = new StringBuffer();
			String line = fromServer.readLine();
			while (line != null && !line.contains(EndOfClientMessageMarker)) {
				msgBuf.append(line);
				line = fromServer.readLine();
			}
			if (line == null) {
				throw new IOException("Lost communication with the server");
			}
			ClientMessage cmsg = ClientMessage.fromXML(msgBuf.toString());
			return cmsg;
		}
		
		public void shutdown() {
			stopping = true;
			try {
				sleep (100);
			} catch (InterruptedException e) {
				return;
			}
			manager.interrupt();
		}

		private void writeMsgToServer(ServerMessage smsg) {
			toServer.println (smsg);
		}
		
		
		public void run()
		{
			ProcessBuilder pb = new ProcessBuilder (pathToExecutable.getAbsolutePath());
			try {
				process = pb.start();
				fromServer = new BufferedReader(new InputStreamReader((process.getInputStream())));
				toServer = new PrintStream(new BufferedOutputStream(process.getOutputStream()));
				
				// Wait for first server message (Start) to appear from client
				while (serverMessages.size() == 0) {
					synchronized (serverMessages) {
						serverMessages.wait();
					}
				}
				
				// From here on, the server drives the communications. It may volunteer new messages to the
			    // client at any time, and we should try to keep up, but it's OK if the server blocks because
				// we are falling behind.
			    // After each message, it will wait for an acknowledgment from us before proceeding.
				// When it's ready for a new Client message, it will send a special Pull request, at which
			    // point we respond with a client message if one is in the queue or block until that time.
				while (!stopping) {
					ClientMessage cmsg = readMsgFromServer();
					ServerMessage smsg;
					if (cmsg instanceof PullMessage) {
						smsg = serverMessages.take(); // can block
					} else {
						clientMessages.put(cmsg); // can block (particularly if a long sequence of snapshots is being sent)
						smsg = new ServerMessage(ServerMessageTypes.Ack);
					}
					writeMsgToServer (smsg);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			catch (InterruptedException e) {
				if (!stopping)
					e.printStackTrace();
			}
		}


		
		
	}
	
	
}
