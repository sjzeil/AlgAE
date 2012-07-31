/**
 * 
 */
package edu.odu.cs.AlgAE.Common.Communications;

/**
 * A generic message, used by a remote/process server to indicate
 * that it is prepared to receive a server message
 * 
 * @author zeil
 *
 */
public class PullMessage extends ClientMessage {

	/**
	 */
	public PullMessage() {
		super("Ack");
	}

	@Override
	public boolean equals(Object clientMessage) {
		if (clientMessage == null)
			return false;
		try {
			PullMessage msg = (PullMessage)clientMessage;
			return msg != null;
		} catch (Exception e) {
			return false;
		}
	}

}
