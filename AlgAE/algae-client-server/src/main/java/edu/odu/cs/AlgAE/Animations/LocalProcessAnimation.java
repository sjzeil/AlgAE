package edu.odu.cs.AlgAE.Animations;

import java.io.File;
import java.io.InputStream;

import javax.swing.JFileChooser;

import edu.odu.cs.AlgAE.Client.GUIClient;
import edu.odu.cs.AlgAE.Common.Applets.AnimationApplet;
import edu.odu.cs.AlgAE.Common.Communications.LocalJavaCommunication;
import edu.odu.cs.AlgAE.Common.Communications.LocalProcessCommunication;
import edu.odu.cs.AlgAE.Server.LocalServer;
import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.MemoryModel.MemoryModel;

/**
 *  An AlgAE animation that works with a server running in a separate process on the same machine.
 *  Communications is via standard I/O with that process.
 *
 *  @author Steven J Zeil
 **/
public class LocalProcessAnimation extends AnimationApplet 
{

	private GUIClient client;
	private LocalProcessCommunication communications;
	

	public LocalProcessAnimation (String title, File executable)
	{
		super(title);

		communications = new LocalProcessCommunication(executable);
		client = new GUIClient(communications);
		setClient(client);
		client.init(false);
		client.start();
		communications.start();
	}


	/**
	 *  Supply a message to appear in the Help..About dialog.
	 *  Typically, this indicates the origin of the source code
	 *  being animated and the name of the person who prepared the
	 *  animation.
	 **/
	//public abstract String about();


	/**
	 * Override this to call register (below) to set up the menu items that will
	 * be displayed in the Algorithms menu and optionally to call registerStartingAction
	 * to set up code to be animated immediately upon launch.
	 */
	//public abstract void buildMenu();



	
	




	/**
	 * Run the animation as a stand-alone program.  		
	 * @param args One command-line parameter - the path to the executable that runs the server
	 *              If this is not provided, a file dialog is raised to select that executable
	 *              file.  
	 */
	public static void main (String[] args) {
		File executable = null;
		if (args.length > 0) {
			executable = new File(args[0]);
			if (!executable.exists() || !executable.canExecute()) {
				System.err.println ("Cannot launch " + args[0]);
				executable = null;
			}
		}
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select the server executable");
	    int returnVal = chooser.showOpenDialog(null);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	    	executable = chooser.getSelectedFile();
			if (!executable.exists() || !executable.canExecute()) {
				System.err.println ("Cannot launch " + executable);
				executable = null;
			}
	    }
	    
	    if (executable != null) {
	    	new LocalProcessAnimation(executable.getName(), executable).runAsMain();
	    }
	}
	
	
}



