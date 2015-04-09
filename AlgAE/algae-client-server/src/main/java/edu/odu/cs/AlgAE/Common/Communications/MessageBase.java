/**
 *
 */
package edu.odu.cs.AlgAE.Common.Communications;

import java.io.Serializable;

/**
 * @author zeil
 *
 */
public class MessageBase implements Serializable {
	
	protected String kind;
	
	public MessageBase (String messageKind)
	{
		kind = messageKind;
	}

}
