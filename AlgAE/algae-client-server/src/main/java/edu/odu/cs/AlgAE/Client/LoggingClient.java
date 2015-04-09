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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuBar;

import edu.odu.cs.AlgAE.Common.Communications.ClientMessage;
import edu.odu.cs.AlgAE.Common.Communications.ForceShutDownMessage;
import edu.odu.cs.AlgAE.Common.Communications.MenuMessage;
import edu.odu.cs.AlgAE.Common.Communications.PromptForInputMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerCommunications;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage.ServerMessageTypes;
import edu.odu.cs.AlgAE.Common.Communications.SnapshotMessage;

/**
 * Provided primarily for testing purposes, this animator maintains a log of all server messages received. This
 * log can be examined for testing purposes.
 *
 * It provides a stylized interaction with a server:
 *   1. It remembers all menu items provided by the client and invokes them, one at a time, in the order received.
 *      After the last one has been invoked and the server indicates that its animation is completed, this client
 *      signals a shutdown to the server.
 *   2. If the server sends an input request, the client responds with one a sequence of response strings
 *      supplied by the user. The sequence is repeated, as necessary.  By default, the sequence is {"1", "2", "3"}.
 *
 * @author zeil
 *
 */
public class LoggingClient extends Client {

	private List<String> algorithmItems;
	private int nextAlgorithmItem;
	
	private ServerCommunications server;
	private ClientThread messageReader;

	
	private List<ClientMessage> messagesFromServer;
	
	public List<String> inputResponses;
	private int nextInputResponse;
	
	
	
	public LoggingClient(ServerCommunications server)
	{
		super(server);
		this.server = server;
		
		algorithmItems = new ArrayList<>();
		nextAlgorithmItem = 0;
		messagesFromServer = new LinkedList<>();
		inputResponses = new ArrayList<>();
		inputResponses.add("1");
		inputResponses.add("2");
		inputResponses.add("3");
		nextInputResponse = 0;
		
		messageReader = null;
	}


	/* (non-Javadoc)
	 * @see edu.odu.cs.AlgAE.Client.Client#init(boolean)
	 */
	@Override
	public void init (boolean isAnApplet)
	{
	}




	/* (non-Javadoc)
	 * @see edu.odu.cs.AlgAE.Client.Client#start()
	 */
	@Override
	public void start() {
		if (messageReader == null && server != null) {
			messageReader = new ClientThread();
			messageReader.setPriority(Thread.MIN_PRIORITY);
			messageReader.start();
		}
		if (messageReader.isPaused()) {
			messageReader.restart();
		}
	}

	/* (non-Javadoc)
	 * @see edu.odu.cs.AlgAE.Client.ClientBase#stop()
	 */
	@Override
	public void stop() {
		messageReader.pause();
	}

	/* (non-Javadoc)
	 * @see edu.odu.cs.AlgAE.Client.ClientBase#destroy()
	 */
	@Override
	public void destroy() {
		if (messageReader != null) {
			messageReader.shutdown();
			ServerMessage shutDownMsg = new ServerMessage(ServerMessageTypes.ShutDown);
			try {
				server.sendToServer(shutDownMsg);
			} catch (InterruptedException e) {
			}
			try {
				Thread.sleep(100);
				if (messageReader.isAlive()) {
					messageReader.interrupt();
				}
			} catch (Exception e1) {
			}
			messageReader = null;
		}
	}


	/* (non-Javadoc)
	 * @see edu.odu.cs.AlgAE.Client.ClientBase#getServerProxy()
	 */
	@Override
	public ServerCommunications getServerAccess() {
		return server;
	}


	/* (non-Javadoc)
	 * @see edu.odu.cs.AlgAE.Client.ClientBase#setServerProxy(edu.odu.cs.AlgAE.Common.Communications.ServerProxy)
	 */
	@Override
	public void setServerAccess(ServerCommunications server) {
		this.server = server;
		start();
	}



	
	/**
	 * Provides access to the log of all messages received from the server.
	 *
	 * @return the serverMessages
	 */
	public List<ClientMessage> getMessageLog() {
		return messagesFromServer;
	}


	


	private class ClientThread extends Thread {

		private boolean terminated;
		private boolean paused;
		

		public ClientThread() {
			super("ClientThread");
			terminated = false;
			paused = false;
		}

		public synchronized void shutdown() {
			if (!terminated) {
				ServerMessage response = new ServerMessage(ServerMessageTypes.ShutDown);
				attemptToSend(response);
				terminated = true;
				notifyAll ();
			}
		}

		public synchronized void restart() {
			if (paused) {
				paused = false;
				notifyAll();
			}
		}

		private void attemptToSend(ServerMessage msg) {
			try {
				getServerAccess().sendToServer(msg);
			} catch (InterruptedException e) {
				System.err.println ("Connection to server has failed\n");
			}
		}

		public void run() {
			synchronized (this) {
				ServerMessage startMsg = new ServerMessage(ServerMessageTypes.Start);
				attemptToSend(startMsg);
			}
			while (!terminated) {
				try {

					synchronized (this) {
						while (isPaused()) {
							wait();
						}
						if (terminated)
							break;
					}
					Thread.yield();
					ClientMessage msg;
					msg = getServerAccess().getFromServer();
					interpret (msg);
				} catch (InterruptedException e) {
					break;
				}
			}
		}


		private void interpret(ClientMessage msg) {
			//System.err.println ("Client received " + msg);
			messagesFromServer.add(msg);
			if (msg instanceof ForceShutDownMessage) {
				terminated = true;
			} else if (msg instanceof MenuMessage) {
				MenuMessage m = (MenuMessage)msg;
				for (String menuItem: m.getMenuItems()) {
					algorithmItems.add(menuItem);
				}
			} else if (msg instanceof PromptForInputMessage) {
				ServerMessage response = new ServerMessage(ServerMessageTypes.InputSupplied, inputResponses.get(nextInputResponse));
				attemptToSend(response);
				nextInputResponse = (nextInputResponse + 1) % inputResponses.size();
			} else if (msg instanceof SnapshotMessage) {
				SnapshotMessage m = (SnapshotMessage)msg;
				if (m.isMenuItemCompleted()) {
					if (nextAlgorithmItem < algorithmItems.size()) {
						ServerMessage response = new ServerMessage(ServerMessageTypes.MenuItemSelected,
																	algorithmItems.get(nextAlgorithmItem));
						++nextAlgorithmItem;
						attemptToSend(response);
					} else {
						ServerMessage response = new ServerMessage(ServerMessageTypes.ShutDown);
						attemptToSend(response);
						terminated = true;
					}
				}
			}
		}

		/**
		 * @return the paused
		 */
		public synchronized boolean isPaused() {
			return paused;
		}

		/**
		 * Pause the client
		 */
		public synchronized void pause () {
			this.paused = true;
			notifyAll();
		}
		
		
		
		
	}


	@Override
	public JMenuBar buildMenu() {
		return null;
	}





}
