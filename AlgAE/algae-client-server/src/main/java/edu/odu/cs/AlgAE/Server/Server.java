package edu.odu.cs.AlgAE.Server;

import edu.odu.cs.AlgAE.Common.Communications.ClientCommunications;


/**
 * This is the base class for all AlgAE Java animation clients.
 *  
 * At its most basic, an algorithm animation consists of a client, a server, and a communication
 * framework linking them.
 *  
 * A Server provides a sequence of snapshots (descriptions of data states at breakpoints)
 * from a running algorithm. 
 *
 *  @author Steven J Zeil
 **/
public abstract class Server
{

	private ClientCommunications clientCommunications;

	/**
	 * Create a Server connected to some Client via the given communications path.
	 * 
	 * @param animation  a class that portrays animation states (snapshots)
	 * @param communications a connector to a local or remote Client
	 */
	public Server (ClientCommunications communications)
	{
		setClientCommunications(communications);
	}

	/**
	 * Create a Server connected to some Client via the given communications path.
	 * 
	 * @param animation  a class that portrays animation states (snapshots)
	 * @param communications a connector to a local or remote Client
	 */
	public Server ()
	{
		clientCommunications = null;
	}

	
	
	/**
	 * @return the ClientCommunications
	 */
	public ClientCommunications getClientCommunications() {
		return clientCommunications;
	}

	/**
	 * @param ClientCommunications the ClientCommunications to set
	 */
	public void setClientCommunications(ClientCommunications ClientCommunications) {
		this.clientCommunications = ClientCommunications;
	}


	/**
	 * When started, the server composes and sends a message describing the available algorithms
	 * (usually shown by the client to the human operator via a menu), then sends one or more
	 * source code listings and data snapshots.   Selection of algorithms will be enabled only after
	 * a data snapshot that is marked as "completed". 
	 */
	public abstract void start();


	/**
	 * Terminate the animation normally.  
	 */
	public abstract void shutdown();

	/**
	 * Send a shutdown message to the client and then terminate the animation.  
	 */
	public abstract void forceShutdown();


	  /**
	   *  Called from buildMenu to register an initial action to be
	   *  run at the start of the animation, before any selections
	   *  from the menu.
	   */
	  public abstract void registerStartingAction (MenuFunction action);

	  /**
	   *  Called from buildMenu to add an item to the Algorithm menu.
	   */
	  public abstract void register(String menuItem, MenuFunction action);

		/**
		 * Pops up a dialog box prompting for an input, pausing the
		 * animation until a satisfactory input value is obtained from the
		 * human operator.
		 * 
		 * @param prompt  Text of the prompt message to be displayed
		 * @param requiredPattern regular expression describing an acceptable input value
		 * @return a human-entered string matching the requiredPattern
		 */
		public abstract String promptForInput(String prompt, String requiredPattern);
		
		/**
		 * Pops up a dialog box prompting for an input, pausing the
		 * animation until a satisfactory input value is obtained from the
		 * human operator.
		 * 
		 * @param prompt  Text of the prompt message to be displayed
		 * @return a human-entered string
		 */
		public String promptForInput(String prompt) {
			return promptForInput(prompt, ".*");
		}

		/**
		 * Finds the source code associated with a given source file name
		 * and loads it into a string.
		 * 
		 * @param fileName a file name (not necessarily a full path)
		 * @return source code contained in that file.
		 */
		public abstract String load(String fileName);



}



