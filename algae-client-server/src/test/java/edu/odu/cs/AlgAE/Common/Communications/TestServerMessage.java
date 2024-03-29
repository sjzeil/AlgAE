/**
 * 
 */
package edu.odu.cs.AlgAE.Common.Communications;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
//import static org.hamcrest.MatcherAssert.assertThat; 
//import static org.hamcrest.Matchers.*;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


/**
 * @author zeil
 *
 */
public class TestServerMessage {

	@Test
	public void testEq() {
		for (ServerMessage.ServerMessageTypes kind: ServerMessage.ServerMessageTypes.values()) {
			ServerMessage message1 = new ServerMessage(kind, "a");
			ServerMessage message2 = new ServerMessage(kind, "b");
			assertFalse (message1.equals(message2));
			for (ServerMessage.ServerMessageTypes kind2: ServerMessage.ServerMessageTypes.values()) {
				ServerMessage message3 = new ServerMessage(kind2, "a");
				assertEquals (kind==kind2, message3.equals(message1));
			}
					
		}
	}

	@Test
	public void testXML() {
		for (ServerMessage.ServerMessageTypes kind: ServerMessage.ServerMessageTypes.values()) {
			ServerMessage message1 = new ServerMessage(kind, "message1.x");
			ByteArrayOutputStream byOut = new ByteArrayOutputStream();
			XMLEncoder out = new XMLEncoder(new BufferedOutputStream(byOut));
			out.writeObject(message1);
			out.close();
			String inXML = byOut.toString();
			System.out.println ("Kind: " + kind);
			assertTrue (inXML.contains(kind.toString()));
			assertTrue (inXML.contains("message1.x"));
			
			XMLDecoder in = new XMLDecoder(new ByteArrayInputStream(inXML.getBytes()));
			ServerMessage message2 = (ServerMessage)in.readObject();
			
			assertEquals (message1, message2);
			in.close();
		}
	}
}
