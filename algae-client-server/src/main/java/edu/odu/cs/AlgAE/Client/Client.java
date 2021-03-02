package edu.odu.cs.AlgAE.Client;

import edu.odu.cs.AlgAE.Common.Animation.LifetimeSupport;
import edu.odu.cs.AlgAE.Common.Animation.MenuSupport;
import edu.odu.cs.AlgAE.Common.Communications.ServerCommunications;

/**
 * This is the base class for all AlgAE Java animation clients.
 *
 * At its most basic, an algorithm animation consists of a client, a server, and a communication
 * framework linking them.
 *
 * A Client is tasked with portraying the sequence of state snapshots that represent
 * an algorithm animation.  Each Client is presumed to launch a thread that pulls
 * and processes messages from the Server. The pacing at which this occurs is unpredictable,
 * as a GUI Client may include facilities for pausing the animation portrayal for arbitrarily
 * long periods.
 *
 * The Client features init-start-stop-destroy functions inspired by (and sometimes triggered by)
 * the Java Applet life-cycle functions.  These are assumed to follow the usual Applet ordering constraints
 *      program-launch client-constructor init (start stop)* destroy program-shutdown
 *
 * @author Zeil
 *
 */
public abstract class Client extends MenuSupport implements LifetimeSupport {


    private ServerCommunications serverAccess;

    public Client (ServerCommunications serverComm)
    {
        serverAccess = serverComm;
    }


    public Client()
    {
        serverAccess = null;
    }


    /**
     * Called once per execution, prior to start/stop/destroy.
     * For GUI-based clients, this provides a convenient time to build the GUI elements.
     * If a server proxy has already been supplied, the client thread may be started.
     *
     */
    public abstract void init();

    /**
     * Called to indicate that an animation portrayal is active. May restart a paused Client thread.
     */
    public abstract void start();

    /**
     * Called to indicate that an animation portrayal is suspended (typically because it is off-screen or minimized).
     * May pause the Client thread.
     */
    public abstract void stop();

    /**
     * Called once per execution, after any init/start/stop calls, to indicate that final shutdown in imminent,
     */
    public abstract void destroy();

    /**
     * Provides access to the communications paths going to and coming from
     * the server.
     *
     * @return the server or null if no server has been supplied
     */
    public ServerCommunications getServerAccess()
    {
        return serverAccess;
    }

    /**
     * Establishes access to the communications paths going to and coming from
     * the server.  If a server already existed and messages from it had been processed,
     * may result in a loss of some or all state acquired from that server.
     *
     * @param server the server to set
     */
    public void setServerAccess(ServerCommunications server)
    {
        serverAccess = server;
    }


}