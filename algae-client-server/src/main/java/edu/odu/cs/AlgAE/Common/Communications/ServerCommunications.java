package edu.odu.cs.AlgAE.Common.Communications;

/**
 * Interface to the Client-Server communications as seen from the Client
 * @author zeil
 *
 */
public interface ServerCommunications {
    
    /**
     * Sends a message to the server - implementations are expected to be synchronized
     * and might block until the server acknowledges receipt.
     *
     * @param message
     * @throws InterruptedException
     */
    public void sendToServer (ServerMessage message) throws InterruptedException;
    
    
    /**
     * Receives a message from the server - implementations are expected to be synchronized
     * and will block if no message is available.
     *
     * @param message
     * @throws InterruptedException
     */
    public ClientMessage getFromServer () throws InterruptedException;

}
