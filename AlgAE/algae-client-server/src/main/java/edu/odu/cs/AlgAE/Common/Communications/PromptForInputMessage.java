/**
 *
 */
package edu.odu.cs.AlgAE.Common.Communications;


/**
 * Indicates that animated code is requesting input from the user.
 *
 * @author zeil
 *
 */
public class PromptForInputMessage extends ClientMessage {

	/**
	 * Message to show to user when prompting for input
	 */
	private String prompt;
	
	/**
	 * A regular expression describing the acceptable format for responses from
	 * the user.
	 */
	private String requiredPattern;
	
	
	/**
	 * @param messageKind
	 */
	public PromptForInputMessage(String prompt, String requiredPattern) {
		super("PromptForInput");
		this.prompt = prompt;
		this.requiredPattern = requiredPattern;
	}

	
	public PromptForInputMessage() {
		super("PromptForInput");
		this.prompt = "Enter input:";
		this.requiredPattern = ".*";
	}


	/**
	 * @return the prompt
	 */
	public String getPrompt() {
		return prompt;
	}


	/**
	 * @param prompt the prompt to set
	 */
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}


	/**
	 * @return the requiredPattern
	 */
	public String getRequiredPattern() {
		return requiredPattern;
	}


	/**
	 * @param requiredPattern the requiredPattern to set
	 */
	public void setRequiredPattern(String requiredPattern) {
		this.requiredPattern = requiredPattern;
	}
	
	
	@Override
	public boolean equals(Object clientMessage) {
		if (clientMessage == null)
			return false;
		try {
			PromptForInputMessage msg = (PromptForInputMessage)clientMessage;
			return msg.prompt.equals(prompt)
					&& msg.requiredPattern.equals(requiredPattern);
		} catch (Exception e) {
			return false;
		}
	}


}
