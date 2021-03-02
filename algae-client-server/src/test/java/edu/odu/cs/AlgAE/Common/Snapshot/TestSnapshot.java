/**
 * 
 */
package edu.odu.cs.AlgAE.Common.Snapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
public class TestSnapshot {
	
	
	void xmlTest (Object x, String mustContain1, String mustContain2)
	{
		ByteArrayOutputStream byout = new ByteArrayOutputStream();
		XMLEncoder out = new XMLEncoder(new BufferedOutputStream(byout));
		out.setPersistenceDelegate(Snapshot.class, new Snapshot.SnapshotPersistenceDelegate());
		out.writeObject(x);
		out.close();
		String xmlStr = byout.toString();
		assertTrue (xmlStr.contains(x.getClass().getSimpleName()));
		if (mustContain1.length() > 0)
			assertTrue (xmlStr.contains(mustContain1));
		if (mustContain2.length() > 0)
			assertTrue (xmlStr.contains(mustContain2));
		
		XMLDecoder in = new XMLDecoder(new ByteArrayInputStream(xmlStr.getBytes()));
		Object y = in.readObject();
		in.close();
		
		assertEquals (x, y);	
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
	
	
	boolean canFind (Entity e0)
	{
		boolean found = false;
		for (Entity e: snap1) {
			if (e.equals(e0))
				found = true;
		}
		return found;
	}
	

	/**
	 * Test method for {@link edu.odu.cs.AlgAE.Common.Snapshot.Snapshot#add(edu.odu.cs.AlgAE.Common.Snapshot.Entity, boolean)}.
	 */
	@Test
	public void testAdd() {
		assertEquals (2, snap1.getEntities().get(entity1a.getEntityIdentifier().getObjectIdentifier()).size());
		assertEquals (2, snap1.getEntities().get(entity1b.getEntityIdentifier().getObjectIdentifier()).size());
		assertEquals (1, snap1.getEntities().get(entity2.getEntityIdentifier().getObjectIdentifier()).size());
		assertEquals (1, snap1.getEntities().get(entity3.getEntityIdentifier().getObjectIdentifier()).size());
		assertTrue (snap1.getGlobals().contains(entity2.getEntityIdentifier()));
		assertFalse (snap1.getGlobals().contains(entity1a.getEntityIdentifier()));
		assertFalse (snap1.getGlobals().contains(entity1b.getEntityIdentifier()));
		assertFalse (snap1.getGlobals().contains(entity3.getEntityIdentifier()));
		snap1.add (entity3);
		assertEquals (1, snap1.getEntities().get(entity3.getEntityIdentifier().getObjectIdentifier()).size());
		assertTrue(canFind(entity1a));
		assertTrue(canFind(entity1b));
		assertTrue(canFind(entity2));
		assertTrue(canFind(entity3));
	}

	/**
	 * Test method for {@link edu.odu.cs.AlgAE.Common.Snapshot.Snapshot#remove(edu.odu.cs.AlgAE.Common.Snapshot.Entity)}.
	 */
	@Test
	public void testRemove() {
		snap1.remove(entity1b);
		assertEquals (1, snap1.getEntities().get(entity1a.getEntityIdentifier().getObjectIdentifier()).size());
		assertEquals (1, snap1.getEntities().get(entity1b.getEntityIdentifier().getObjectIdentifier()).size());
		assertEquals (1, snap1.getEntities().get(entity2.getEntityIdentifier().getObjectIdentifier()).size());
		assertEquals (1, snap1.getEntities().get(entity3.getEntityIdentifier().getObjectIdentifier()).size());
		snap1.remove(entity2);
		assertFalse (snap1.getGlobals().contains(entity2.getEntityIdentifier()));
		assertTrue(canFind(entity1a));
		assertFalse(canFind(entity1b));
		assertFalse(canFind(entity2));
		assertTrue(canFind(entity3));
	}

	/**
	 * Test method for {@link edu.odu.cs.AlgAE.Common.Snapshot.Snapshot#equals(edu.odu.cs.AlgAE.Common.Snapshot.Entity)}.
	 */
	@Test
	public void testEquals() {
		Snapshot snap0 = initSnap();
		
		assertEquals (snap0, snap1);
		snap1.remove(entity2);
		assertFalse (snap0.equals(snap1));
		
		Snapshot snap2 = initSnap();
		assertEquals (snap0, snap2);
		snap2.setDescriptor(snap2.getDescriptor() + "x");
		assertFalse (snap2.equals(snap0));
		
	}
	
	@Test
	public void testXML() {
		xmlTest (snap1, "breakpoint", "foo.java");
		xmlTest (snap1, "component1", "link");
		
	}

}
