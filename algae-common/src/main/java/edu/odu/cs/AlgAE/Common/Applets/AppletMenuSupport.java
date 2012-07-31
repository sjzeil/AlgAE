/**
 * 
 */
package edu.odu.cs.AlgAE.Common.Applets;

import javax.swing.JMenuBar;
import javax.swing.JPanel;

/**
 * Describes a class supporting a lifetime protocol comparable to that of an applet
 * and allowing the construction of a menu bar.
 * 
 * @author zeil
 *
 */
public abstract class AppletMenuSupport extends JPanel implements AppletLifetimeSupport {

	/**
	 * Construct a menu bar.
	 * 
	 * Should be called after init(isAnApplet), so that the object knows whether to include a
	 * File->exit menu item.
	 * 
	 * @return a menu bar
	 */
	public abstract JMenuBar buildMenu();
	
}
