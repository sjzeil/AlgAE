/**
 * 
 */
package edu.odu.cs.AlgAE.Common.Communications;

/**
 * Server is sending a file of source code to the client for possible display. 
 * 
 * This message may be sent in response to an earlier getSourceCode message from the
 * client or it may be sent "spontaneously" by the server if it predicts that such a
 * display will likely be required.  
 * 
 * @author zeil
 *
 */
public class SourceCodeMessage extends ClientMessage {

	/**
	 * identifier of the source code
	 */
	private String filePath;
	
	/**
	 * Full text of the source code file.
	 */
	private String sourceText;
	
	
	/**
	 * @param messageKind
	 */
	public SourceCodeMessage(String filePath, String sourceText) {
		super("SourceCode");
		this.filePath = filePath;
		this.sourceText = sourceText;
	}

	public SourceCodeMessage() {
		super("SourceCode");
		this.filePath = "";
		this.sourceText = "";
	}

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}


	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}


	/**
	 * @return the sourceText
	 */
	public String getSourceText() {
		return sourceText;
	}


	/**
	 * @param sourceText the sourceText to set
	 */
	public void setSourceText(String sourceText) {
		this.sourceText = sourceText;
	}

	@Override
	public boolean equals(Object clientMessage) {
		if (clientMessage == null)
			return false;
		try {
			SourceCodeMessage msg = (SourceCodeMessage)clientMessage;
			return msg.filePath.equals(filePath) 
					&& msg.sourceText.equals(sourceText);
		} catch (Exception e) {
			return false;
		}
	}

}
