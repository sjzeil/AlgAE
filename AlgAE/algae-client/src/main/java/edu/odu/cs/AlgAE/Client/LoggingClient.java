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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import edu.odu.cs.AlgAE.Client.DataViewer.Animator;
import edu.odu.cs.AlgAE.Client.IOViewer.IOPane;
import edu.odu.cs.AlgAE.Client.SourceViewer.SourcePane;
import edu.odu.cs.AlgAE.Common.Applets.AppletMenuSupport;
import edu.odu.cs.AlgAE.Common.Communications.AckMessage;
import edu.odu.cs.AlgAE.Common.Communications.CapturedOutputMessage;
import edu.odu.cs.AlgAE.Common.Communications.ClientMessage;
import edu.odu.cs.AlgAE.Common.Communications.ForceShutDownMessage;
import edu.odu.cs.AlgAE.Common.Communications.MenuMessage;
import edu.odu.cs.AlgAE.Common.Communications.PromptForInputMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage.ServerMessageTypes;
import edu.odu.cs.AlgAE.Common.Communications.ServerProxy;
import edu.odu.cs.AlgAE.Common.Communications.SnapshotMessage;
import edu.odu.cs.AlgAE.Common.Communications.SourceCodeMessage;
import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;
import edu.odu.cs.AlgAE.Common.Snapshot.SnapshotDiff;

/**
 * Provided primarily for testing purposes, this client maintains a log of all server messages received. This 
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
public class LoggingClient implements Client {

	private List<String> algorithmItems;
	private int nextAlgorithmItem;
	
	private ServerProxy server;
	private ClientThread messageReader;

	
	private List<ClientMessage> messagesFromServer;
	
	public List<String> inputResponses;
	private int nextInputResponse;
	
	
	  
	public LoggingClient(ServerProxy server)
	{
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
	public ServerProxy getServerProxy() {
		return server;
	}


	/* (non-Javadoc)
	 * @see edu.odu.cs.AlgAE.Client.ClientBase#setServerProxy(edu.odu.cs.AlgAE.Common.Communications.ServerProxy)
	 */
	@Override
	public void setServerProxy(ServerProxy server) {
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
				getServerProxy().sendToServer(msg);
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
					msg = getServerProxy().getFromServer();
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





}
