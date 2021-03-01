package edu.odu.cs.AlgAE.Common.Applets;

import edu.odu.cs.AlgAE.Common.Communications.ClientCommunications;
import edu.odu.cs.AlgAE.Common.Communications.ServerCommunications;

public interface ServerCommunications0 extends ServerCommunications,
        AppletLifetimeSupport {

    /**
     * Provides access to the communications paths going to and coming from
     * the client.
     *
     * @return the server or null if no server has been supplied
     */
    public abstract ClientCommunications getClientAccess();

    /**
     * Establishes access to the communications paths going to and coming from
     * the client.  If a client already existed and messages from it had been processed,
     * may result in a loss of some or all state acquired from that server.
     *
     * @param server the server to set
     */
    public abstract void setClientAccess(ClientCommunications server);

}
