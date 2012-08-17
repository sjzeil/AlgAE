package edu.odu.cs.AlgAE.Server.Animations;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;

import edu.odu.cs.AlgAE.Common.Communications.ClientMessage;
import edu.odu.cs.AlgAE.Common.Communications.LocalJavaCommunication;
import edu.odu.cs.AlgAE.Common.Communications.SnapshotMessage;
import edu.odu.cs.AlgAE.Common.Communications.SourceCodeMessage;
import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;
import edu.odu.cs.AlgAE.Common.Snapshot.SnapshotDiff;
import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.Server;
import edu.odu.cs.AlgAE.Server.MemoryModel.ActivationRecord;
import edu.odu.cs.AlgAE.Server.MemoryModel.MemoryModel;

/**
 *  This is the base class for all AlgAE Java animations.
 *
 *  @author Steven J Zeil
 **/
public abstract class LocalJavaAnimation extends Animation implements AnimationContext
{

	private MemoryModel memoryModel;
	private Server server;
	private LocalJavaCommunication communications;
	
	private static HashMap<Thread, WeakReference<LocalJavaAnimation> > instances
	 = new HashMap<Thread, WeakReference<LocalJavaAnimation> >();
	
	private HashSet<String> sourceCodeAlreadySent;

	public LocalJavaAnimation (String title)
	{
		super(title, new LocalJavaCommunication());
		memoryModel = new MemoryModel(this);
		communications = (LocalJavaCommunication)getServerCommunications();
		server = new Server(this, communications);
		communications.setLocalServer(server);
		sourceCodeAlreadySent = new HashSet<String>();
	}


	/**
	 *  Supply a message to appear in the Help..About dialog.
	 *  Typically, this indicates the origin of the source code
	 *  being animated and the name of the person who prepared the
	 *  animation.
	 **/
	public abstract String about();
	
	
	/**
	 * Override this to call register (below) to set up the menu items that will
	 * be displayed in the Algorithms menu and optionally to call registerStartingAction
	 * to set up code to be animated immediately upon launch.
	 */
	public abstract void buildMenu();

	  /**
	   *  Called from buildMenu to register an initial action to be
	   *  run at the start of the animation, before any selections
	   *  from the menu.
	   */
	  public void registerStartingAction (MenuFunction action)
	  {
		  server.registerStartingAction (action);
	  }

	  /**
	   *  Called from buildMenu to add an item to the Algorithm menu.
	   */
	  public void register(String menuItem, MenuFunction action) {
		  server.registerMenuItem (menuItem, action);
	  }


	

	/**
	 * Each animation has a unique activation stack.
	 * @return the activation stack for this animation
	 */
	public MemoryModel getMemoryModel() {
		return memoryModel;
	}
	

	/**
	 * Each animation has a unique server.
	 * @return the server for this animation
	 */
	public Server getServer ()
	{
		return server;
	}


	/**
	 * Animated code must be able to access the relevant Animation instance
	 * even though such code was written independently of the animation system.
	 * This function returns that animation instance, under the assumption that the
	 * server (which launches the animated code) will have registered its thread
	 * for that purpose.
	 *  
	 * @return the animation associated with a thread
	 */
	public static LocalJavaAnimation algae()
	{
		return instances.get(Thread.currentThread()).get();
	}
	
	/**
	 * Animated code must be able to access the relevant Animation instance
	 * even though such code was written independently of the animation system.
	 * This function associates this animation instance with a thread, usually
	 * the thread used by the server to launch the code being animated.
	 */
	public void registerInstance(Thread t)
	{
		instances.put(t, new WeakReference<LocalJavaAnimation>(this));
	}

	
	/**
	 * This must be called at the beginning of each new function to signal
	 * that a new empty record should be pushed onto the top of the stack.
	 * 
	 * @param thisObj - A reference to an object of the class of
	 *    which the current function is a member. Normally, "this"
	 *    will do just fine.  thisObj is used to help locate
	 *    the source code being animated, so, in a pinch (e.g., if
	 *    animating a static function) another object whose source
	 *    code lies in the same directory/folder will do.
	 *    
	 * @return ActivationRecord for the new function call
	 */

	public static ActivationRecord activate (Object thisObject) {
		return algae().getMemoryModel().getActivationStack().activate (thisObject);
	}


	/**
	 * Show a variable as a global value in all displays.
	 * Variables portrayed by this call are shown "in-line".
	 * 
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 */
	public void globalVar(String label, int value)
	{
		getMemoryModel().globalVar(label, value);
	}

	/**
	 * Show a variable as a global value in all displays.
	 * Variables portrayed by this call are shown "in-line".
	 * 
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 */
	public void globalVar (String label, Object param)
	{
		getMemoryModel().globalVar(label, param);
	}

	
	
	/**
	 * Show a variable as a global value in all displays.
	 * Variables portrayed by this call are shown as labeled
	 * pointers to the actual value.
	 * 
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 * @return a reference to this breakpoint
	 */
	public void globalRefVar (String label, Object param)
	{
		getMemoryModel().globalRefVar(label, param);
	}


	/**
	 * Pops up a dialog box prompting for an input, pausing the
	 * animation until a satisfactory input value is obtained from the
	 * human operator.
	 * 
	 * @param prompt  Text of the prompt message to be displayed
	 * @param requiredPattern regular expression describing an acceptable input value
	 * @return a human-entered string matching the requiredPattern
	 */
	public String promptForInput(String prompt, String requiredPattern) {
		return server.promptForInput(prompt, requiredPattern);
	}
	
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
	 * Send a message via the communications established for this animation
	 */
	public void sendToClient (ClientMessage msg)
	{
		try {
			communications.sendToClient(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	private Snapshot lastSnapshot = null;
	
	/**
	 * Send a snapshot via the communications established for this animation
	 */
	public void sendToClient (Snapshot snap, boolean completed)
	{
		SnapshotDiff diff = new SnapshotDiff(lastSnapshot, snap);
		lastSnapshot = snap;
		SnapshotMessage msg = new SnapshotMessage(diff, completed);
		sendToClient(msg);
	}
	
	
	/**
	 * Send source code to client for display. Code is sent only once for any given fileName.
	 */
	public void sendSourceToClient (String fileName)
	{
		if (!sourceCodeAlreadySent.contains(fileName)) {
			sourceCodeAlreadySent.add (fileName);
			SourceCodeMessage msg = new SourceCodeMessage(fileName, server.load(fileName));
			sendToClient(msg);
		}
	}

	
	
	
	/**
	 * Provides a replacement for System.out
	 */
	public SimulatedPrintStream sysout()
	{
		return out;
	}
	
	
	
	
	/**
	 * A substitute for System.in - reads from the animation GUI
	 */
	public InputStream in;
	
	/**
	 * A substitute for System.out - writes to the animation GUI
	 */
	public SimulatedPrintStream out = new SimulatedPrintStream();
	
}



