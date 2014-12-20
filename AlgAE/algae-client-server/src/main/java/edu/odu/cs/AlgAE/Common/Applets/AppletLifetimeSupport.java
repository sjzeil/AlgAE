/**
 * 
 */
package edu.odu.cs.AlgAE.Common.Applets;

/**
 * Describes a class supporting a lifetime protocol comparable to that of an applet
 * 
 * @author zeil
 *
 */
public interface AppletLifetimeSupport {

	public void init(boolean isAnApplet);
	public void start();
	public void stop();
	public void destroy();
}
