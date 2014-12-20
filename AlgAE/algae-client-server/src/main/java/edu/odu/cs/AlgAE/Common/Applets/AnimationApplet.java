package edu.odu.cs.AlgAE.Common.Applets;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JApplet;
import javax.swing.JFrame;

import edu.odu.cs.AlgAE.Server.Server;


/**
 *  This is the base class for all AlgAE Java animations.
 *  
 *  Animations can be run as applets (the usual method of deployment)
 *  or as main programs (more convenient for testing).  If run as applets, they
 *  can appear with all content inline within an HTML page or can pop up a separate
 *  window.
 *
 *  @author Steven J Zeil
 **/
public class AnimationApplet extends JApplet
{
	private AppletMenuSupport client;
	private Server server;

	private String theTitle;
	
	
	private int inlineDisplay;
	final static int INLINE_DEFAULT = 0;
	private boolean serverStarted = false;
	
	

	public AnimationApplet (String title, AppletMenuSupport client, Server server)
	{
		theTitle = title;
		this.client = client;
		this.server = server;
		inlineDisplay = INLINE_DEFAULT;
	}

	public AnimationApplet (String title)
	{
		theTitle = title;
		this.client = null;
		this.server = null;
		inlineDisplay = INLINE_DEFAULT;
	}


	/**
	 * Applet init action - build the GUI and get ready to go
	 */
	public void init ()
	{
		inlineDisplay = INLINE_DEFAULT;
		String inlineParam = getParameter("inline");
		if (inlineParam != null) {
			try {
				inlineDisplay= Integer.parseInt(inlineParam);
			} catch (NumberFormatException ex) {}
		}
		
		client.init(true);

		if (inlineDisplay > 0) {
			setJMenuBar(client.buildMenu());
			getContentPane().add(client);
		} else {
			JFrame window = new JFrame(theTitle);
			window.setJMenuBar(client.buildMenu());
			window.getContentPane().add(client);
			
			window.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					client.stop();
					client.destroy();
					server.shutdown();
					setVisible(false);
				}
			});
			window.pack();
			window.setVisible(true);
		}
	}

	/**
	 * Applet start action - begin running the animation
	 */
	public void start ()
	{
		client.start();
		if (!serverStarted) {
			server.start();
			serverStarted = true;
		}
	}


	/**
	 * Applet stop action - pause the animation
	 */
	public void stop ()
	{
		client.stop();
	}

	/**
	 * Applet destroy action - shut everything down for good
	 */
	public void destroy()
	{
		client.destroy();
		server.shutdown();
	}

	
	/**
	 * Used to run this as a standalone application from main() rather than as an applet
	 */
	public void runAsMain()
	{
		inlineDisplay = 0;
		JFrame window = new JFrame(theTitle);
		
		client.init(false);
		
		
		window.setJMenuBar(client.buildMenu());

		window.getContentPane().add(client);
		
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				client.stop();
				client.destroy();
				server.shutdown();
				System.exit (0);
			}
		});
		window.pack();
		window.setVisible(true);
		//window.setPreferredSize(new Dimension(800, 600));
		start();
		//client.start();
	}



	/**
	 * @return the client
	 */
	public AppletMenuSupport getClient() {
		return client;
	}




	/**
	 * @return the server
	 */
	public Server getServer() {
		return server;
	}



	/**
	 * @param client the client to set
	 */
	public void setClient(AppletMenuSupport client) {
		this.client = client;
	}



	/**
	 * @param server the server to set
	 */
	public void setServer(Server server) {
		this.server = server;
	}



	
}



