package edu.odu.cs.AlgAE.Common.Communications;

/**
 * Interface to the Client-Server communications as seen from the Server
 * @author zeil
 *
 */
public interface ClientCommunications {
    
    /**
     * Sends a message to the client - implementations are expected to be synchronized
     * and might block until the client acknowledges receipt.
     *
     * @param message
     * @throws InterruptedException
     */
    public void sendToClient (ClientMessage message) throws InterruptedException;
    
    
    /**
     * Receives a message from the client - implementations are expected to be synchronized
     * and will block if no message is available.
     *
     * @param message
     * @throws InterruptedException
     */
    public ServerMessage getFromClient () throws InterruptedException;

}
