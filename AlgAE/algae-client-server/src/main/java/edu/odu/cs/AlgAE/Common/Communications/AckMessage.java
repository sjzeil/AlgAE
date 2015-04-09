package edu.odu.cs.AlgAE.Common.Communications;

/**
 * A generic acknowledgment message, used as a keep-alive in remote servers.
 *
 * @author zeil
 *
 */
public class AckMessage extends ClientMessage {

    /**
     */
    public AckMessage() {
        super("Ack");
    }

    @Override
    public final boolean equals(final Object clientMessage) {
        return (clientMessage != null) && (clientMessage instanceof AckMessage);
    }

    @Override
    public final int hashCode() {
        return getClass().getName().hashCode();
    }

}
