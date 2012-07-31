/**
 * 
 */
package edu.odu.cs.AlgAE.Common.Communications;

/**
 * Sent from the server to indicate that something has gone wrong and that the
 * animation is unable to continue.
 * 
 * @author zeil
 *
 */
public class ForceShutDownMessage extends ClientMessage {
	
	private String explanation;

	/**
	 * @param explanation  explanatory string
	 */
	public ForceShutDownMessage(String explanation) {
		super("ForceShutDown");
		this.explanation = explanation;
	}

	/**
	 */
	public ForceShutDownMessage() {
		super("ForceShutDown");
		this.explanation = "Unexpected error in server.";
	}

	
	/**
	 * @return the explanation
	 */
	public String getExplanation() {
		return explanation;
	}

	/**
	 * @param explanation the explanation to set
	 */
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	@Override
	public boolean equals(Object clientMessage) {
		if (clientMessage == null)
			return false;
		try {
			ForceShutDownMessage msg = (ForceShutDownMessage)clientMessage;
			return msg.explanation.equals(explanation);
		} catch (Exception e) {
			return false;
		}
	}
}
