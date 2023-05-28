/**
 * 
 */
package edu.odu.cs.AlgAE.Common.Snapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

/**
 * @author zeil
 *
 */
public class TestSnapshotDiff {
	
	
	void xmlTest (Object x, String mustContain1, String mustContain2)
	{
		ByteArrayOutputStream byOut = new ByteArrayOutputStream();
		XMLEncoder out = new XMLEncoder(new BufferedOutputStream(byOut));
		out.writeObject(x);
		out.close();
		String xmlStr = byOut.toString();
		assertTrue (xmlStr.contains(x.getClass().getSimpleName()));
		if (mustContain1.length() > 0)
			assertTrue (xmlStr.contains(mustContain1));
		if (mustContain2.length() > 0)
			assertTrue (xmlStr.contains(mustContain2));
		
		XMLDecoder in = new XMLDecoder(new ByteArrayInputStream(xmlStr.getBytes()));
		Object y = in.readObject();
		
		assertEquals (x, y);	
		in.close();
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
		entity1a = new Entity(id1, "");
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
	
	
	boolean canFind (Entity e0)
	{
		boolean found = false;
		for (Entity e: snap1) {
			if (e.equals(e0))
				found = true;
		}
		return found;
	}
	
	@Test
	public void testEmptyConstructor() {
		SnapshotDiff d0 = new SnapshotDiff();
		assertEquals ("", d0.getDescriptor());
		assertEquals (0, d0.getChangedEntities().size());
		assertEquals (0, d0.getNewGlobals().size());
		assertEquals (0, d0.getRemovedEntities().size());
		
		Snapshot s0 = new Snapshot();
		Snapshot s1 = d0.reconstruct(s0);
		assertEquals (s0, s1);
		
		xmlTest (d0, "", "");
	}

	@Test
	public void testConstructorNullNon() {
		SnapshotDiff d0 = new SnapshotDiff(null, snap1);
		assertEquals (snap1.getDescriptor(), d0.getDescriptor());
		assertEquals (snap1.getEntities().keySet().size()+1, d0.getChangedEntities().size());
		assertEquals (1, d0.getNewGlobals().size());
		assertTrue (d0.getNewGlobals().contains(entity2.getEntityIdentifier()));
		assertEquals (0, d0.getRemovedEntities().size());
		
		Snapshot s0 = new Snapshot();
		Snapshot s1 = d0.reconstruct(s0);
		assertEquals (snap1, s1);
		
		xmlTest (d0, "link", "component");
	}


	@Test
	public void testConstructorNonNull() {
		SnapshotDiff d0 = new SnapshotDiff(snap1, null);
		assertEquals ("", d0.getDescriptor());
		assertEquals (0, d0.getChangedEntities().size());
		assertEquals (0, d0.getNewGlobals().size());
		assertTrue (d0.getNewNonGlobals().contains(entity2.getEntityIdentifier()));
		assertEquals (snap1.getEntities().keySet().size()+1, d0.getRemovedEntities().size());
		
		Snapshot s0 = new Snapshot();
		Snapshot s1 = d0.reconstruct(snap1);
		assertEquals (s0, s1);
		
		xmlTest (d0, "", "");
	}


	
	@Test
	public void testEntityChange() {
		SnapshotDiff d0 = new SnapshotDiff(null, null);
		Snapshot s0 = d0.reconstruct(snap1);
		s0.setActivationStack(snap1.getActivationStack());
		s0.setBreakpointLocation(snap1.getBreakpointLocation());
		s0.setDescriptor(snap1.getDescriptor());
		
		Entity entity3b = new Entity(entity3.getEntityIdentifier().getObjectIdentifier(), "labeled");
		entity3b.setColor(Color.yellow);
		s0.add(entity3b);
		s0.setDescriptor("foo");
		
		SnapshotDiff d1 = new SnapshotDiff(snap1, s0);
		
		assertEquals ("foo", d1.getDescriptor());
		assertEquals (1, d1.getChangedEntities().size());
		assertEquals (0, d1.getNewGlobals().size());
		assertTrue (d1.getChangedEntities().contains(entity3b));
		assertEquals (0, d1.getRemovedEntities().size());
		
		Snapshot s1 = d1.reconstruct(snap1);
		assertEquals (s0, s1);
		
		xmlTest (d1, "foo", "");
	}


	@Test
	public void testEntityRemove() {
		SnapshotDiff d0 = new SnapshotDiff(null, null);
		Snapshot s0 = d0.reconstruct(snap1);
		
		s0.remove(entity3);
		s0.setDescriptor("foo");
		
		SnapshotDiff d1 = new SnapshotDiff(snap1, s0);
		
		assertEquals ("foo", d1.getDescriptor());
		assertEquals (0, d1.getChangedEntities().size());
		assertEquals (0, d1.getNewGlobals().size());
		assertEquals (1, d1.getRemovedEntities().size());
		assertTrue (d1.getRemovedEntities().contains(entity3.getEntityIdentifier()));
		
		Snapshot s1 = d1.reconstruct(snap1);
		assertEquals (s0, s1);
		
		xmlTest (d1, "foo", "");
	}


}
