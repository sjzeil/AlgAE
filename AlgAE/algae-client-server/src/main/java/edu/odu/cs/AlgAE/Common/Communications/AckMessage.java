/**
 * 
 */
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
	public boolean equals(Object clientMessage) {
		if (clientMessage == null)
			return false;
		try {
			AckMessage msg = (AckMessage)clientMessage;
			return msg != null;
		} catch (Exception e) {
			return false;
		}
	}

}
