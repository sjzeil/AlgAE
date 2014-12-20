package edu.odu.cs.AlgAE.Server.Animations;

import edu.odu.cs.AlgAE.Common.Communications.ClientMessage;
import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;
import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.MemoryModel.MemoryModel;

/**
 *  Each animation is characterized by a small set of
 *  "singleton" objects.
 *  
 *  Note that if the animation is being run as an applet,
 *  then there may be several animations in progress simultaneously.
 *  So these are singletons in the sense that there is exactly one of 
 *  these per animation.  The information they contain must be kept distinct
 *  from one animation to another.
 *
 *  @author Steven J Zeil
 **/
public interface AnimationContext
{

	/**
	 * Each animation has a unique activation stack.
	 * @return the activation stack for this animation
	 */
	public MemoryModel getMemoryModel();
	
	/**
	 * Send a message via the communications established for this animation
	 */
	public void sendToClient (ClientMessage msg);
	
	/**
	 * Send a snapshot via the communications established for this animation
	 */
	public void sendToClient (Snapshot snap, boolean completed);
	

	/**
	 * Send source code to client (if not already sent)
	 */
	public void sendSourceToClient(String fileName);

	/**
	 * Provides a replacement for System.out
	 */
	public SimulatedPrintStream sysout();

	/**
	 *  Register an initial action to be
	 *  run at the start of the animation, before any selections
	 *  from the menu.
	 */
	public void registerStartingAction (MenuFunction action);

	/**
	 *  Add an item to the Algorithm menu.
	 */
	public void register(String menuItem, MenuFunction action);

}



