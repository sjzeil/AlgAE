/**
 * 
 */
package edu.odu.cs.AlgAE.Animations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import edu.odu.cs.AlgAE.Common.Communications.MenuMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage.ServerMessageTypes;
import edu.odu.cs.AlgAE.Common.Communications.SnapshotMessage;

/**
 * @author zeil
 *
 */
public class TestJavaStandAloneServer {

	// Server uses these
	public InputStream msgsInToServer;
	public PrintStream msgsOutFromServer;
	
	// Test cases use these
	public PrintStream writeToServer;
	public BufferedReader readFromServer;
	
	@Before
	public void setup () throws IOException {
		PipedOutputStream out1 = new PipedOutputStream();
		msgsInToServer = new PipedInputStream(out1);
		writeToServer = new PrintStream(out1, true);
		
		PipedOutputStream out2 = new PipedOutputStream();
		readFromServer = new BufferedReader(
				new InputStreamReader(
						new PipedInputStream(out2)));
		msgsOutFromServer = new PrintStream(out2, true);
		
	}
	
	
	@Test
	public void testPipes() throws Exception {
		msgsOutFromServer.println("foo");
		String line = readFromServer.readLine();
		assertEquals("foo", line);
		writeToServer.println("bar");;
		byte[] buffer = new byte[3];
		int len = msgsInToServer.read(buffer);
		assertTrue(len >= 3);
		assertEquals("bar", new String(buffer, "UTF-8"));
	}
	
	@Test
	public void testShortestAnimation() throws IOException {
		
		JavaStandAloneServer server = new JavaStandAloneServer("test", msgsInToServer, msgsOutFromServer) {
			
			@Override
			public void buildMenu() {
			}
			
			@Override
			public String about() {
				return "test server";
			}
		};
		server.runAsMain();
		writeToServer.println(new ServerMessage(ServerMessageTypes.Start, "starting"));
		JsonReader reader = new JsonReader(readFromServer);
		Gson gson = new Gson();
		String kind = gson.fromJson(reader, String.class);
		assertEquals ("MenuMessage", kind);
		MenuMessage menuMsg = gson.fromJson(reader, MenuMessage.class);
		assertNotNull(menuMsg);
		writeToServer.println(new ServerMessage(ServerMessageTypes.Ack, "ack start"));
        kind = gson.fromJson(reader, String.class);
        assertEquals ("SnapshotMessage", kind);
		SnapshotMessage snapMsg = gson.fromJson(reader, SnapshotMessage.class);
		assertNotNull(snapMsg);
        writeToServer.println(new ServerMessage(ServerMessageTypes.Ack, "ack snap"));
		writeToServer.println(new ServerMessage(ServerMessageTypes.ShutDown, "stopping"));		
	}

}
