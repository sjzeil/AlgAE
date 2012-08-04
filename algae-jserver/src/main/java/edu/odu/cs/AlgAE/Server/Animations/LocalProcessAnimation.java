package edu.odu.cs.AlgAE.Server.Animations;

import java.io.File;

import javax.swing.JFileChooser;

import edu.odu.cs.AlgAE.Common.Communications.LocalProcessCommunication;

/**
 *  An AlgAE animation that works with a server running in a separate process on the same machine.
 *
 *  @author Steven J Zeil
 **/
public class LocalProcessAnimation extends Animation
{

	public LocalProcessAnimation (String title, File server)
	{
		super(title, new LocalProcessCommunication(server));
	}

	
	
	
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



