package edu.odu.cs.AlgAE.Client.Layout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.util.Map;

import org.junit.Test;

import edu.odu.cs.AlgAE.Client.Layout.Coordinates.Dimension2DDouble;
import edu.odu.cs.AlgAE.Common.Snapshot.Connector;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity;
import edu.odu.cs.AlgAE.Common.Snapshot.EntityIdentifier;
import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;
import edu.odu.cs.AlgAE.Common.Snapshot.SourceLocation;
import edu.odu.cs.AlgAE.Server.MemoryModel.Identifier;
import edu.odu.cs.AlgAE.Server.MemoryModel.MemoryModel;


/**
 * @author zeil
 *
 */
public class TestLayout {

    private int stackID = 1;
    private int mainActID = 2;
    private int fooActID = 3;
    private int aID = 4;
    private int bID = 5;
    
    public Snapshot snap1(int step) {
    	Snapshot s = new Snapshot();
    	s.setDescriptor("first snapshot");
    	s.setBreakpointLocation(new SourceLocation("foo.java", 1));

    	Entity stack = new Entity(new Identifier(stackID), "");
    	stack.setColor(Color.lightGray);
    	stack.setDirection(Entity.Directions.Vertical);
    	s.add(stack);
    	s.setRootEntity(stack.getEntityIdentifier());

    	Entity mainAct = new Entity(new Identifier(mainActID), stack.getEntityIdentifier(), "call0");
    	mainAct.setValue("main()");
    	mainAct.setColor(Color.cyan);
    	s.add(mainAct);
    	stack.getComponents().add(mainAct.getEntityIdentifier());

    	Entity fooAct = new Entity(new Identifier(fooActID), stack.getEntityIdentifier(), "call1");
    	fooAct.setValue("foo(");
    	fooAct.setColor(Color.cyan);
    	fooAct.setDirection(Entity.Directions.Horizontal);
    	s.add(fooAct);
    	stack.getComponents().add(fooAct.getEntityIdentifier());
    	
    	Entity a = new Entity(new Identifier(aID), fooAct.getEntityIdentifier(), "");
    	a.setLabel("A");
    	a.setValue("42");
    	fooAct.getComponents().add(a.getEntityIdentifier());
    	s.add(a);

    	Entity b = new Entity(new Identifier(bID), fooAct.getEntityIdentifier(), "");
    	b.setLabel("B");
    	b.setValue("12");
    	s.add(b);
    	Connector foo2b = new Connector("foo2b", fooAct.getEntityIdentifier(), b.getEntityIdentifier(), 45, 180);
    	fooAct.getConnections().add(foo2b);
    	
    	if (step > 0) {
    		Connector a2b = new Connector("a2b", a.getEntityIdentifier(), b.getEntityIdentifier(), 180, 180);
    		a.getConnections().add(a2b);
    	}

    	
    	return s;
    }

	private static void checkVar (Map<EntityIdentifier, Entity> entities, int objID, 
			int cnt, String label, String value) {
		assertEquals (cnt, countVar(entities, objID));
		Identifier oid = new Identifier(objID);
		EntityIdentifier eid;
		eid = oid.asEntityIdentifier();
		Entity e = entities.get(eid);
		assertNotNull(e);
		if (label != null)
			assertEquals (label, e.getLabel());
		if (value != null)
			assertEquals(value, e.getValue());	
	}

	private static int countVar (Map<EntityIdentifier, Entity> entities, int objID) {
		Identifier id = new Identifier (objID);
		int count = 0;
		for (EntityIdentifier eid: entities.keySet()) {
			if (id.equals(new Identifier(eid))) {
				++count;
			}
		}
		return count;
	}
	
	
	private static void checkSize (Layout scene, EntityIdentifier eid, String label, 
			double minX, double maxX, double minY, double maxY) {
		Dimension2DDouble sz = scene.getSizeOf(eid);
		double w = sz.getWidth();
		double h = sz.getHeight();
		assertTrue (w >= minX);
		assertTrue (w <= maxX);
		assertTrue (h >= minY);
		assertTrue (h <= maxY);
	}

    @Test
	public void test_emptySnap() {
		Snapshot snap = new Snapshot();
		Anchors anchors = new Anchors();
		Layout scene = new Layout(snap, null, anchors);
		Map<EntityIdentifier, Entity> entities =  scene.getEntities();
		assertEquals (0, entities.keySet().size());
	}

    @Test
	public void test_emptyMemoryModel() {
    	MemoryModel memory = new MemoryModel(null);
    	SourceLocation sourceLoc = new SourceLocation();
		Snapshot snap = memory.renderInto("test", sourceLoc);
		Anchors anchors = new Anchors();
		Layout scene = new Layout(snap, null, anchors);
		Map<EntityIdentifier, Entity> entities =  scene.getEntities();
		assertEquals (4, entities.keySet().size());
	}

    
    @Test
	public void test_Snap1() {
		Snapshot snap = snap1(0);
		Anchors anchors = new Anchors();
		Layout scene = new Layout(snap, null, anchors);
		Map<EntityIdentifier, Entity> entities =  scene.getEntities();
		assertEquals (5, entities.keySet().size());
		checkVar (entities, stackID, 1, null, null);
		checkVar (entities, aID, 1, null, "42");
		EntityIdentifier aEID = new Identifier(aID).asEntityIdentifier(); 
		checkSize (scene, aEID, "A", 6, 9, 1, 2);
	}

    @Test
	public void test_Snap12() {
		Snapshot snap = snap1(0);
		Snapshot snap2 = snap1(1);
		Anchors anchors = new Anchors();
		Layout scene0 = new Layout(snap, null, anchors);
		Layout scene = new Layout(snap2, scene0, anchors);
		Map<EntityIdentifier, Entity> entities =  scene.getEntities();
		assertEquals (5, entities.keySet().size());
		checkVar (entities, stackID, 1, null, null);
		checkVar (entities, aID, 1, null, "42");
		checkSize (scene, new EntityIdentifier("", aID), "A", 6, 9, 1, 2);
	}


}
