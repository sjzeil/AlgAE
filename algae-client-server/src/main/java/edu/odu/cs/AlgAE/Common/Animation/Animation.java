package edu.odu.cs.AlgAE.Common.Animation;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.odu.cs.AlgAE.Animations.DefaultLogSetting;
import edu.odu.cs.AlgAE.Server.Server;


/**
 *  This is the base class for all AlgAE Java animations.
 *  
 *  An animation combines a client (responsible for displaying the
 *  animation frames) and a server (collecting info about the state
 *  of the program being animated).
 *  
 *
 *  @author Steven J Zeil
 **/
public class Animation extends JPanel
{
    private MenuSupport client;
    private Server server;

    private String theTitle;
    
    
    private boolean serverStarted = false;


    public Animation (String title, MenuSupport client, Server server)
    {
        theTitle = title;
        this.client = client;
        this.server = server;
    }

    public Animation (String title)
    {
        theTitle = title;
        this.client = null;
        this.server = null;
    }


    /**
     * Applet init action - build the GUI and get ready to go
     */
    public void init ()
    {
        DefaultLogSetting.setupLogging(true, "algae%u.log");

        client.init();

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

    /**
     * Start action - begin running the animation
     */
    public void start ()
    {
        client.start();
        if (server != null && !serverStarted) {
            server.start();
            serverStarted = true;
        }
    }


    /**
     * Stop action - pause the animation
     */
    public void stop ()
    {
        client.stop();
    }

    /**
     * Destroy action - shut everything down for good
     */
    public void destroy()
    {
        client.destroy();
        server.shutdown();
    }

    
    /**
     * Used to run this as a standalone application from main()
     */
    public void runAsMain()
    {
        DefaultLogSetting.setupLogging(false, "algae%u.log");
        
        JFrame window = new JFrame(theTitle);
        
        client.init();
        
        
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
    public MenuSupport getClient() {
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
    public void setClient(MenuSupport client) {
        this.client = client;
    }



    /**
     * @param server the server to set
     */
    public void setServer(Server server) {
        this.server = server;
    }



    
}



