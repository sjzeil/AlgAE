/**
 * 
 */
package edu.odu.cs.AlgAE.Animations;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferCreator;

import edu.odu.cs.AlgAE.Common.Communications.ForceShutDownMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage.ServerMessageTypes;

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
		writeToServer.println(new ServerMessage(ServerMessageTypes.ShutDown, "stopping"));
		
		String line = readFromServer.readLine();
		assertTrue (line.contains("<start"));
		
	}

}
