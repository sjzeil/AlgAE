/**
 * 
 */
package edu.odu.cs.AlgAE.Common.Communications;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import edu.odu.cs.AlgAE.Common.Snapshot.Connector;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity;
import edu.odu.cs.AlgAE.Common.Snapshot.Identifier;
import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;
import edu.odu.cs.AlgAE.Common.Snapshot.SnapshotDiff;
import edu.odu.cs.AlgAE.Common.Snapshot.SourceLocation;

/**
 * @author zeil
 *
 */
public class TestClientMessage {

	private void writeReadTest (ClientMessage msg, String mustInclude)
	{
		String className = msg.getClass().getSimpleName();
		String serializedMsg = msg.serialize();
		assertTrue (serializedMsg.contains(className));
		assertTrue (serializedMsg.contains(mustInclude));
		@SuppressWarnings("deprecation")
		InputStream stringIn = new java.io.StringBufferInputStream(serializedMsg);
		ClientMessage msg2 = ClientMessage.load(stringIn);
		assertEquals (className, msg2.getClass().getSimpleName());
		assertEquals (msg, msg2);
	}
	

	@Test
	public void testAckXML() {
		AckMessage message1 = new AckMessage();
		writeReadTest (message1, "Ack");
	}
	
	
	@Test
	public void testCapturedOutputXML() {
		String outp = "foobar";
		writeReadTest (new CapturedOutputMessage(outp), outp);
	}

	@Test
	public void testForceShutDownXML() {
		String param = "foobar";
		writeReadTest (new ForceShutDownMessage(param), param);
	}

	@Test
	public void testMenuXML() {
		String aboutStr = "All about";
		String aboutStr2 = "Nothing about\nthis animation";
		String[] menu1 = {"search", "sort", "insert"};
		String[] menu2 = {"sort", "search", "insert"};
		MenuMessage msg1 = new MenuMessage(aboutStr, menu1);
		MenuMessage msg2 = new MenuMessage(aboutStr2, menu1);
		MenuMessage msg3 = new MenuMessage(aboutStr, menu2);
		
		assertFalse (msg1.equals(msg2));
		assertFalse (msg1.equals(msg3));
		
		writeReadTest (msg1, aboutStr);
		writeReadTest (msg2, menu1[1]);
	}

	@Test
	public void testpromptXML() {
		String prompt1 = "Enter a number:";
		String prompt2 = "Enter a name:";
		String pattern1 = "[0-9][0-9]*";
		String pattern2 = "..*";

		PromptForInputMessage msg1 = new PromptForInputMessage(prompt1, pattern1);
		PromptForInputMessage msg2 = new PromptForInputMessage(prompt2, pattern1);
		PromptForInputMessage msg3 = new PromptForInputMessage(prompt1, pattern2);
		
		assertFalse (msg1.equals(msg2));
		assertFalse (msg1.equals(msg3));
		
		writeReadTest (msg1, prompt1);
		writeReadTest (msg2, pattern1);
	}
	
	
	@Test
	public void testSourceCodeXML() {
		String path1 = "something.h";
		String path2 = "something.cpp";
		String text1 = "#include <string>";
		String text2 = "for (int i = 0; i != 10; i++) {}";

		SourceCodeMessage msg1 = new SourceCodeMessage(path1, text1);
		SourceCodeMessage msg2 = new SourceCodeMessage(path2, text1);
		SourceCodeMessage msg3 = new SourceCodeMessage(path1, text2);
		
		assertFalse (msg1.equals(msg2));
		assertFalse (msg1.equals(msg3));
		
		writeReadTest (msg1, path1);
		writeReadTest (msg3, "for (int i");
	}
	
	
	private Snapshot snap1;
	private Entity entity1a;
	private Entity entity1b;
	private Entity entity2;
	private Entity entity3;
	
	@Before
	public void setup()
	{
		Identifier id1 = new Identifier(1);
		entity1a = new Entity(id1);
		Identifier id2 = new Identifier(2);
		entity2 = new Entity(id2, "label2");
		entity1b = new Entity(id1, entity2, "component1");
		Identifier id3 = new Identifier(3);
		entity3 = new Entity(id3, "labeled");
		entity2.getComponents().add(entity1b.getEntityIdentifier());
		entity3.getConnections().add(new Connector("link", entity3.getEntityIdentifier(),
				entity2.getEntityIdentifier(), 0, 180));
		snap1 = initSnap();
	}
	
	Snapshot initSnap()
	{
		Snapshot snap = new Snapshot();
		snap.add(entity1a);
		snap.add(entity2);
		snap.setGlobal(entity2.getEntityIdentifier(), true);
		snap.add(entity1b);
		snap.add(entity3);
		snap.setActivationStack(entity3.getEntityIdentifier());
		snap.setDescriptor("a breakpoint");
		snap.setBreakpointLocation(new SourceLocation("foo.java", 15));
		return snap;
	}

	@Test
	public void testSnapshotXML() {
		SnapshotDiff sd = new SnapshotDiff(null, snap1);
		writeReadTest (new SnapshotMessage(sd, true), "component1");
		writeReadTest (new SnapshotMessage(sd, false), "link");
	}

	
}
