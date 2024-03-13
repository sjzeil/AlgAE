/**
 * 
 */
package edu.odu.cs.AlgAE.Common.Snapshot;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import static org.hamcrest.MatcherAssert.assertThat; 
//import static org.hamcrest.Matchers.*;

import edu.odu.cs.AlgAE.Server.MemoryModel.Identifier;

/**
 * @author zeil
 *
 */
public class TestSnapshot {
	
	

	private Snapshot snap1;
	private Entity entity1a;
	private Entity entity1b;
	private Entity entity2;
	private Entity entity3;
	
	
	@BeforeEach
	public void setup()
	{
		Identifier id1 = new Identifier(1);
		entity1a = new Entity(id1, "");
		Identifier id2 = new Identifier(2);
		entity2 = new Entity(id2, "label2");
		entity1b = new Entity(id1, entity2.getEntityIdentifier(), "component1");
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
		snap.add(entity3);
		snap.setRootEntity(entity3.getEntityIdentifier());
		snap.setDescriptor("a breakpoint");
		snap.setBreakpointLocation(new SourceLocation("foo.java", 15));
		return snap;
	}
	
	
	boolean canFind (Entity e0)
	{
		boolean found = false;
		for (Entity e: snap1) {
			if (e.getEntityIdentifier().equals(e0.getEntityIdentifier()))
				found = true;
		}
		return found;
	}
	

	/**
	 * Test method for {@link edu.odu.cs.AlgAE.Common.Snapshot.Snapshot#add(edu.odu.cs.AlgAE.Common.Snapshot.Entity, boolean)}.
	 */
	@Test
	public void testAdd() {
		snap1.add (entity3);
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
		snap1.remove(entity1a);
		snap1.remove(entity2);
		assertFalse(canFind(entity1a));
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
	
	
}
