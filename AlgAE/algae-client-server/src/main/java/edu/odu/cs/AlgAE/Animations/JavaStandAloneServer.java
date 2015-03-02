package edu.odu.cs.AlgAE.Animations;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import edu.odu.cs.AlgAE.Common.Communications.ClientMessage;
import edu.odu.cs.AlgAE.Common.Communications.PromptForInputMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage;
import edu.odu.cs.AlgAE.Common.Communications.StandardIOCommunication;
import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;
import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.Server;
import edu.odu.cs.AlgAE.Server.MemoryModel.MemoryModel;

/**
 *  A specialization of Server for Java standalone programs that will exchange messages
 *  with an AlgAE client via standard I/O. 
 *
 *  @author Steven J Zeil
 **/
public abstract class JavaStandAloneServer extends Server implements MenuBuilder, AnimationContext, ContextAware
{

	private static Logger logger = null; 
	
	private InputStream msgsIn;
	private PrintStream msgsOut;
	
	private StandardIOCommunication communications;
	private String theTitle;
	private MemoryModel memoryModel;

	/**
	 *  Collection of algorithm menu items.
	 **/
	private HashMap<String, MenuFunction> algorithmsMenu;

	/**
	 * Animated code to be performed upon start of the animation
	 */
	private MenuFunction startingAction;


	public JavaStandAloneServer (String title, InputStream msgsIn, PrintStream msgsOut)
	{
		DefaultLogSetting.setupLogging(false,  "algae-server%u.log");
		
		this.msgsIn = msgsIn;
		this.msgsOut = msgsOut;
		theTitle = title;
		communications = new StandardIOCommunication(msgsIn, msgsOut);
		memoryModel = new MemoryModel(this);
		SimulatedPrintStream.setMsgQueue(communications);
	}

	public void runAsMain() {
		start();
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



	
	public MemoryModel getMemoryModel() {
		return memoryModel;
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
		memoryModel.globalVar(label, value);
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
		memoryModel.globalVar(label, param);
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
		memoryModel.globalRefVar(label, param);
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
		algorithmsMenu.put (menuItemTitle, theOperation);
	}

	public void registerStartingAction(MenuFunction action) {
		startingAction = action;
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
		String promptedInput = "";
		ClientMessage promptMsg = new PromptForInputMessage(prompt, requiredPattern);
		try {
			getClientCommunications().sendToClient(promptMsg);
			while (promptedInput.equals("")) {
				ServerMessage response = getClientCommunications().getFromClient();
				if (response.getKind().equals("Shutdown")) {
					forceShutdown();
					break;
				} else if (response.getKind().equals("InputSupplied")) {
					promptedInput = response.getDetail();
				} else if (!response.getKind().equals("Ack")) {
					logger.warning("Message protocol error: encountered " + response 
							+ " while awaiting prompted input.\n");
				}
			}
		} catch (InterruptedException ex) {
			// indicates that program is shutting down
		}
		return promptedInput;
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

	@Override
	public AnimationContext context() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendToClient(ClientMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendToClient(Snapshot snap, boolean completed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendSourceToClient(String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SimulatedPrintStream sysout() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void start() {
		communications.start();
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void forceShutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String load(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	
}



