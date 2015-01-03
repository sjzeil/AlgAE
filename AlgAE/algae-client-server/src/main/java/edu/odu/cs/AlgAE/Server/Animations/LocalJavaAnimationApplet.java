package edu.odu.cs.AlgAE.Server.Animations;

import java.io.InputStream;

import edu.odu.cs.AlgAE.Client.GUIClient;
import edu.odu.cs.AlgAE.Common.Applets.AnimationApplet;
import edu.odu.cs.AlgAE.Server.LocalAnimation;
import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.MemoryModel.MemoryModel;


/**
 *  This is the base class for all AlgAE Java animations.
 *  
 *  Like all algorithm animations, it combines a client, a server, and a communications
 *  path between them.
 *  
 *  It also implements a mechanism by which multiple instances can be running in the same JVM
 *  (common if a web page includes two or more animations as applets) with the animated algorithm code
 *  able to identify its own instance and obtain access to the appropriate animation state.
 *
 *  @author Steven J Zeil
 **/
public abstract class LocalJavaAnimationApplet extends AnimationApplet implements MenuBuilder
{

	private LocalAnimation server;
	private GUIClient client;

	public LocalJavaAnimationApplet (String title)
	{
		super(title);

		client = new GUIClient();
		setClient(client);
		server = new LocalAnimation(client, this);
		super.setServer(server);
		out = server.out;
		in = server.in;
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



	
	protected MemoryModel getMemoryModel() {
		return server.getMemoryModel();
	}


	/**
	 * Show a variable as a global value in all displays.
	 * Variables portrayed by this call are shown "in-line".
	 * 
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 */
	protected void globalVar(String label, int value)
	{
		server.globalVar(label, value);
	}

	/**
	 * Show a variable as a global value in all displays.
	 * Variables portrayed by this call are shown "in-line".
	 * 
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 */
	protected void globalVar (String label, Object param)
	{
		server.globalVar(label, param);
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
	protected void globalRefVar (String label, Object param)
	{
		server.globalRefVar(label, param);
	}

	
	/**
	 * A substitute for System.in - reads from the animation GUI
	 */
	public InputStream in;

	/**
	 * A substitute for System.out - writes to the animation GUI
	 */
	public SimulatedPrintStream out;
	
	
	/**
	 *  Declare a menu item to appear in the client's Algorithm menu
	 *  and a corresponding function to be executed here on the server
	 *  in response to a selection of that item.
	 *
	 * @param menuItemTitle The string to appear in the client's menu
	 * @param theOperation  code to invoke when the menu item is selected
	 **/
	public void register(String menuItemTitle, MenuFunction theOperation)
	{
		server.register (menuItemTitle, theOperation);
	}

	public void registerStartingAction(MenuFunction action) {
		server.registerStartingAction(action);
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

	
}



