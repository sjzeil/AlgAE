/**
 *
 */
package edu.odu.cs.AlgAE.Common.Communications;


/**
 * Messages sent to the server.
 *
 * Server messages tend to be have simple structures. For remote servers, these could
 * easily be encoded into an http get.
 *
 * @author zeil
 *
 */
public class ServerMessage extends MessageBase {
	
	public enum ServerMessageTypes {
		Start,				// start an animation, request menu info
		MenuItemSelected,	// user has selected a function from the menu
							//    detail: selected menu item
		InputSupplied, 		// user has entered a response to an input prompt
							//    detail: input string
		GetSourceCode, 		// client wants to display a source code file
							//    detail: path to desired source file
		Pull, 				// generic keep-alive message (for remote servers)
		Ack,                // generic acknowledgement of message receipt from remote/process server
		ShutDown};			// User has requested a shutdown from the client

	private String detail;
	
	/**
	 * Construct a new server message
	 *
	 * @param messageKind type of message
	 * @param detail variant content of server messages
	 */
	public ServerMessage(ServerMessageTypes messageKind, String detail) {
		super(messageKind.toString());
		this.detail = detail;
	}

	/**
	 * Construct a new server message
	 *
	 * @param messageKind type of message
	 * @param detail variant content of server messages
	 */
	public ServerMessage(String messageKind, String detail) {
		super(messageKind);
		this.detail = detail;
	}

	/**
	 * Construct a new server message
	 *
	 */
	public ServerMessage() {
		super("*uninitialized*");
		this.detail = "";
	}

	
	/**
	 * Construct a new server message
	 * Equivalent to ServerMessage(messageKind, "")
	 *
	 * @param messageKind type of message
	 */
	public ServerMessage(ServerMessageTypes messageKind) {
		super(messageKind.toString());
		this.detail = "";
	}

	/**
	 * @return the detail
	 */
	public String getDetail() {
		return detail;
	}
	
	/**
	 * Set the detail
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}

	
	/**
	 * @return the kind
	 */
	public String getKind() {
		return kind;
	}
	
	
	/**
	 * Set the message kind
	 */
	public void setKind(String kind) {
		this.kind= kind ;
	}

	
	
	public boolean equals (Object o) {
		if (o == null)
			return false;
		try {
			ServerMessage msg = (ServerMessage)o;
			return msg.getKind().equals(getKind()) && msg.getDetail().equals(getDetail());
		} catch (Exception e) {
			return false;
		}
	}
	
	
	public String toString()
	{
		return kind + ": " + detail;
	}
}
