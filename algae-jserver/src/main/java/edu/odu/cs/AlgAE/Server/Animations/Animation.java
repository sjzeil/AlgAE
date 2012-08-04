package edu.odu.cs.AlgAE.Server.Animations;

import edu.odu.cs.AlgAE.Client.Client;
import edu.odu.cs.AlgAE.Common.Applets.AnimationApplet;
import edu.odu.cs.AlgAE.Common.Applets.ServerCommunications;


/**
 *  This is the base class for all AlgAE Java animations.
 *  
 *  An animation is an applet that can be run as a main program.
 *  
 *  At its most basic, an animation consists of a client (a GUI panel that can be presented in an applet or a window),
 *  a server that manages the actual code being animated, and a communications object that connects them and
 *  allows them to send messages to one another.
 *  
 *  The server can be local (from the perspective of the machine displaying the client) or remote. Normally, animations
 *  if Java code will be deployed locally, while animations of C++ (or other languages) will be deployed remotely. (It's
 *  possible, if only for testing purposes, to run C++ animations locally as well.) The communications object can
 *  therefore range from simple in-memory queues for local Java animations to code designed to interact with a remote
 *  servlet for remote animations.
 *
 *  @author Steven J Zeil
 **/
public abstract class Animation extends AnimationApplet
{

	private ServerCommunications serverCommunications;

	/**
	 * Create an applet with a client connected to some server via the given communications path.
	 * 
	 * @param title  title to display in GUI window
	 * @param communications a connector to a local or remote server
	 */
	public Animation (String title, ServerCommunications communications)
	{
		super (title, new Client(communications), communications);
		serverCommunications = communications;
	}


	/**
	 * @return the serverCommunications
	 */
	public ServerCommunications getServerCommunications() {
		return serverCommunications;
	}




}



