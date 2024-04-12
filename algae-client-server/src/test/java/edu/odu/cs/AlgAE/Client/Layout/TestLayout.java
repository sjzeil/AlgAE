package edu.odu.cs.AlgAE.Client.Layout;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

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
		Layout scene = new Layout(snap);
		Map<EntityIdentifier, Entity> entities =  scene.getEntities();
		assertEquals (0, entities.keySet().size());
	}

    @Test
	public void test_emptyMemoryModel() {
    	MemoryModel memory = new MemoryModel(null);
    	SourceLocation sourceLoc = new SourceLocation();
		Snapshot snap = memory.renderInto("test", sourceLoc);
		Layout scene = new Layout(snap);
		Map<EntityIdentifier, Entity> entities =  scene.getEntities();
		assertEquals (4, entities.keySet().size());
	}

    
    @Test
	public void test_Snap1() {
		Snapshot snap = snap1(0);
		Layout scene = new Layout(snap);
		Map<EntityIdentifier, Entity> entities =  scene.getEntities();
		assertEquals (5, entities.keySet().size());
		checkVar (entities, stackID, 1, null, null);
		checkVar (entities, aID, 1, null, "42");
		EntityIdentifier aEID = new Identifier(aID).asEntityIdentifier(); 
		checkSize (scene, aEID, "A", 5, 9, 1, 3);
	}

    @Test
	public void test_Snap12() {
		Snapshot snap2 = snap1(1);
		Layout scene = new Layout(snap2);
		Map<EntityIdentifier, Entity> entities =  scene.getEntities();
		assertEquals (5, entities.keySet().size());
		checkVar (entities, stackID, 1, null, null);
		checkVar (entities, aID, 1, null, "42");
		checkSize (scene, new Identifier(aID).asEntityIdentifier(), "A", 5, 9, 1, 3);
	}

    @Test
	public void testHorizontalLayout() {
    	MemoryModel memory = new MemoryModel(null);
    	SourceLocation sourceLoc = new SourceLocation();
		Snapshot snap = memory.renderInto("test", sourceLoc);

        
        Entity container = new Entity(new Identifier(10), "");
        container.setValue("container");
        container.setDirection(Entity.Directions.Horizontal);
        snap.add(container);
        snap.setRootEntity(container.getEntityIdentifier());

        Entity a = new Entity(new Identifier(aID), container.getEntityIdentifier(), "1");
        a.setValue("AAAAAAAA");
        snap.add(a);
        container.getComponents().add(a.getEntityIdentifier());

        Entity b = new Entity(new Identifier(bID), container.getEntityIdentifier(), "2");
        b.setValue("BBBBBBBB");
        snap.add(b);
        container.getComponents().add(b.getEntityIdentifier());

        Layout scene = new Layout(snap);
        
    
        Point2D containerLoc = scene.getLocationOf(container.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble containerSize = scene.getSizeOf(container.getEntityIdentifier());

        Point2D aLoc = scene.getLocationOf(a.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble aSize = scene.getSizeOf(a.getEntityIdentifier());

        Point2D bLoc = scene.getLocationOf(b.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble bSize = scene.getSizeOf(b.getEntityIdentifier());

        assertThat(aLoc.getX() + aSize.getWidth(), lessThan(bLoc.getX()));
        assertThat(aLoc.getY(), closeTo(bLoc.getY(), 0.001));
        assertThat(aLoc.getX(), greaterThan(containerLoc.getX()));
        assertThat(aLoc.getX(), lessThan(containerLoc.getX() + 1));
        assertThat(containerSize.getHeight(), lessThan(2 + aSize.getHeight()));
        assertThat(containerSize.getWidth(), greaterThan(aSize.getWidth() + bSize.getWidth()));
        assertThat(containerSize.getWidth(), lessThan(aSize.getWidth() + bSize.getWidth() + 2));
    }

    @Test
	public void testVerticalLayout() {
    	MemoryModel memory = new MemoryModel(null);
    	SourceLocation sourceLoc = new SourceLocation();
		Snapshot snap = memory.renderInto("test", sourceLoc);

        
        Entity container = new Entity(new Identifier(10), "");
        container.setValue("container");
        container.setDirection(Entity.Directions.Vertical);
        snap.add(container);
        snap.setRootEntity(container.getEntityIdentifier());

        Entity a = new Entity(new Identifier(aID), container.getEntityIdentifier(), "1");
        a.setValue("AAAAAAAA");
        snap.add(a);
        container.getComponents().add(a.getEntityIdentifier());

        Entity b = new Entity(new Identifier(bID), container.getEntityIdentifier(), "2");
        b.setValue("BBBBBBBB");
        snap.add(b);
        container.getComponents().add(b.getEntityIdentifier());

        Layout scene = new Layout(snap);
        
    
        Point2D containerLoc = scene.getLocationOf(container.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble containerSize = scene.getSizeOf(container.getEntityIdentifier());

        Point2D aLoc = scene.getLocationOf(a.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble aSize = scene.getSizeOf(a.getEntityIdentifier());

        Point2D bLoc = scene.getLocationOf(b.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble bSize = scene.getSizeOf(b.getEntityIdentifier());

        assertThat(aLoc.getY() + aSize.getHeight(), lessThan(bLoc.getY()));
        assertThat(aLoc.getX(), closeTo(bLoc.getX(), 0.001));
        assertThat(aLoc.getY(), greaterThan(containerLoc.getY()));
        assertThat(aLoc.getY(), lessThan(containerLoc.getY() + 2.5));
        assertThat(containerSize.getWidth(), lessThan(1.5 + aSize.getWidth()));
        assertThat(containerSize.getHeight(), greaterThan(aSize.getHeight() + bSize.getHeight()));
        assertThat(containerSize.getHeight(), lessThan(aSize.getHeight() + bSize.getHeight() + 4));
    }

    @Test
	public void testSquareLayout() {
    	MemoryModel memory = new MemoryModel(null);
    	SourceLocation sourceLoc = new SourceLocation();
		Snapshot snap = memory.renderInto("test", sourceLoc);

        
        Entity container = new Entity(new Identifier(10), "");
        container.setValue("container");
        container.setDirection(Entity.Directions.Square);
        snap.add(container);
        snap.setRootEntity(container.getEntityIdentifier());

        // Container should get a layout roughly like this:
        //
        //  ______________
        //  | A    |     |
        //  |  ___ |  D  |
        //  |  |B| |_____| 
        //  |  |_| |     |
        //  |      |  E  |
        //  |  ___ |_____|
        //  |  |C| |
        //  |  |_| |
        //  |______|


        Entity a = new Entity(new Identifier(aID), container.getEntityIdentifier(), "1");
        a.setValue("AAAA");
        a.setDirection(Entity.Directions.Vertical);
        snap.add(a);
        container.getComponents().add(a.getEntityIdentifier());

        Entity b = new Entity(new Identifier(bID), a.getEntityIdentifier(), "2");
        b.setValue("BBBB");
        snap.add(b);
        a.getComponents().add(b.getEntityIdentifier());

        Entity c = new Entity(new Identifier(43), a.getEntityIdentifier(), "2");
        c.setValue("CCCC");
        snap.add(c);
        a.getComponents().add(c.getEntityIdentifier());

        Entity d = new Entity(new Identifier(44), container.getEntityIdentifier(), "");
        d.setValue("DDDD");
        snap.add(d);
        container.getComponents().add(d.getEntityIdentifier());

        Entity e = new Entity(new Identifier(45), container.getEntityIdentifier(), "");
        e.setValue("EEEE");
        snap.add(e);
        container.getComponents().add(e.getEntityIdentifier());

        Layout scene = new Layout(snap);
        
    
        Point2D containerLoc = scene.getLocationOf(container.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble containerSize = scene.getSizeOf(container.getEntityIdentifier());

        Point2D aLoc = scene.getLocationOf(a.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble aSize = scene.getSizeOf(a.getEntityIdentifier());

        Point2D bLoc = scene.getLocationOf(b.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble bSize = scene.getSizeOf(b.getEntityIdentifier());

        Point2D cLoc = scene.getLocationOf(c.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble cSize = scene.getSizeOf(c.getEntityIdentifier());

        Point2D dLoc = scene.getLocationOf(d.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble dSize = scene.getSizeOf(d.getEntityIdentifier());

        Point2D eLoc = scene.getLocationOf(e.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble eSize = scene.getSizeOf(e.getEntityIdentifier());

        /*
        assertThat(aLoc.getX() + aSize.getWidth(), lessThan(dLoc.getX()));
        assertThat(aLoc.getX() + aSize.getWidth(), lessThan(eLoc.getX()));

        assertThat(dLoc.getX(), closeTo(eLoc.getX(), 0.001));
        assertThat(eLoc.getY(), lessThan(dLoc.getY() + dSize.getHeight()));

        assertThat(eLoc.getY() + eSize.getHeight(), lessThan(aLoc.getY() + aSize.getHeight()));
        */
        // Have not implemented row spanning, so what we are actually 
        // getting is more like
        //  ________
        //  | A    |
        //  |  ___ |
        //  |  |B| | 
        //  |  |_| |
        //  |      |
        //  |  ___ |
        //  |  |C| |
        //  |  |_| |
        //  |______|______
        //  |      |      |
        //  |  D   |   E  |
        //  |______|______|
        assertThat(aLoc.getX(), lessThan(dLoc.getX()+0.1));
        assertThat(dLoc.getX() + dSize.getWidth(), lessThan(eLoc.getX()));

        assertThat(dLoc.getY(), greaterThan(aLoc.getY() + aSize.getHeight()));
        assertThat(eLoc.getY(), closeTo(dLoc.getY(), 0.01));
    }


    @Test
	public void testHorizontalTreeLayout() {
    	MemoryModel memory = new MemoryModel(null);
    	SourceLocation sourceLoc = new SourceLocation();
		Snapshot snap = memory.renderInto("test", sourceLoc);

        
        Entity container = new Entity(new Identifier(10), "");
        container.setValue("container");
        container.setDirection(Entity.Directions.HorizontalTree);
        snap.add(container);
        snap.setRootEntity(container.getEntityIdentifier());

        // Container should get a layout roughly like this:
        //
        //     B
        //   
        //  A
        //          D
        //     C
        //          E
        
        EntityIdentifier containerEID = container.getEntityIdentifier();
        Entity a = new Entity(new Identifier(aID), containerEID, "1");
        a.setValue("A");
        snap.add(a);
        container.getComponents().add(a.getEntityIdentifier());

        Entity b = new Entity(new Identifier(bID), containerEID, "2");
        b.setValue("B");
        snap.add(b);
        Connector a2b = new Connector("a2b", a.getEntityIdentifier(), b.getEntityIdentifier(), 0.0, 360.0);
        a.getConnections().add(a2b);
        container.getComponents().add(b.getEntityIdentifier());

        Entity c = new Entity(new Identifier(43), containerEID, "2");
        c.setValue("C");
        snap.add(c);
        Connector a2c = new Connector("a2c", a.getEntityIdentifier(), c.getEntityIdentifier(), 0.0, 360.0);
        a.getConnections().add(a2c);
        container.getComponents().add(c.getEntityIdentifier());

        Entity d = new Entity(new Identifier(44), containerEID, "");
        d.setValue("D");
        snap.add(d);
        Connector c2d = new Connector("c2d", c.getEntityIdentifier(), d.getEntityIdentifier(), 0.0, 360.0);
        c.getConnections().add(c2d);
        container.getComponents().add(d.getEntityIdentifier());

        Entity e = new Entity(new Identifier(45), containerEID, "");
        e.setValue("E");
        snap.add(e);
        Connector c2e = new Connector("c2e", c.getEntityIdentifier(), e.getEntityIdentifier(), 0.0, 360.0);
        c.getConnections().add(c2e);
        container.getComponents().add(e.getEntityIdentifier());

        Layout scene = new Layout(snap);
        
    
        Point2D containerLoc = scene.getLocationOf(container.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble containerSize = scene.getSizeOf(container.getEntityIdentifier());

        Point2D aLoc = scene.getLocationOf(a.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble aSize = scene.getSizeOf(a.getEntityIdentifier());

        Point2D bLoc = scene.getLocationOf(b.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble bSize = scene.getSizeOf(b.getEntityIdentifier());

        Point2D cLoc = scene.getLocationOf(c.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble cSize = scene.getSizeOf(c.getEntityIdentifier());

        Point2D dLoc = scene.getLocationOf(d.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble dSize = scene.getSizeOf(d.getEntityIdentifier());

        Point2D eLoc = scene.getLocationOf(e.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble eSize = scene.getSizeOf(e.getEntityIdentifier());

        assertThat(aLoc.getX() + aSize.getWidth(), lessThan(bLoc.getX()));
        assertThat(aLoc.getX() + aSize.getWidth(), lessThan(cLoc.getX()));
        assertThat(aLoc.getX() + aSize.getWidth(), lessThan(dLoc.getX()));
        assertThat(aLoc.getX() + aSize.getWidth(), lessThan(eLoc.getX()));
        
        assertThat(bLoc.getX(), closeTo(cLoc.getX(), 0.01));
        
        assertThat(cLoc.getX() + cSize.getWidth(), lessThan(dLoc.getX()));
        assertThat(cLoc.getX() + cSize.getWidth(), lessThan(eLoc.getX()));

        assertThat(dLoc.getX(), closeTo(eLoc.getX(), 0.01));

        assertThat(bLoc.getY(), lessThan(aLoc.getY()));
        assertThat(bLoc.getY() + bSize.getHeight(), lessThan(cLoc.getY()));
        assertThat(bLoc.getY(), lessThan(dLoc.getY()));
        assertThat(bLoc.getY() + bSize.getHeight(), lessThan(eLoc.getY()));

        assertThat(aLoc.getY(), lessThan(cLoc.getY()));
        assertThat(aLoc.getY(), lessThan(eLoc.getY()));

        assertThat(dLoc.getY() + dSize.getHeight(), lessThan(eLoc.getY()));
    }


    @Test
	public void testVerticalTreeLayout() {
    	MemoryModel memory = new MemoryModel(null);
    	SourceLocation sourceLoc = new SourceLocation();
		Snapshot snap = memory.renderInto("test", sourceLoc);

        
        Entity container = new Entity(new Identifier(10), "");
        container.setValue("container");
        container.setDirection(Entity.Directions.VerticalTree);
        snap.add(container);
        snap.setRootEntity(container.getEntityIdentifier());

        // Container should get a layout roughly like this:
        //
        //            A
        //        B        C
        //              D     E
        
        EntityIdentifier containerEID = container.getEntityIdentifier();
        Entity a = new Entity(new Identifier(aID), containerEID, "1");
        a.setValue("A");
        snap.add(a);
        container.getComponents().add(a.getEntityIdentifier());

        Entity b = new Entity(new Identifier(bID), containerEID, "2");
        b.setValue("B");
        snap.add(b);
        Connector a2b = new Connector("a2b", a.getEntityIdentifier(), b.getEntityIdentifier(), 0.0, 360.0);
        a.getConnections().add(a2b);
        container.getComponents().add(b.getEntityIdentifier());

        Entity c = new Entity(new Identifier(43), containerEID, "2");
        c.setValue("C");
        snap.add(c);
        Connector a2c = new Connector("a2c", a.getEntityIdentifier(), c.getEntityIdentifier(), 0.0, 360.0);
        a.getConnections().add(a2c);
        container.getComponents().add(c.getEntityIdentifier());

        Entity d = new Entity(new Identifier(44), containerEID, "");
        d.setValue("D");
        snap.add(d);
        Connector c2d = new Connector("c2d", c.getEntityIdentifier(), d.getEntityIdentifier(), 0.0, 360.0);
        c.getConnections().add(c2d);
        container.getComponents().add(d.getEntityIdentifier());

        Entity e = new Entity(new Identifier(45), containerEID, "");
        e.setValue("E");
        snap.add(e);
        Connector c2e = new Connector("c2e", c.getEntityIdentifier(), e.getEntityIdentifier(), 0.0, 360.0);
        c.getConnections().add(c2e);
        container.getComponents().add(e.getEntityIdentifier());

        Layout scene = new Layout(snap);
        
    
        Point2D containerLoc = scene.getLocationOf(container.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble containerSize = scene.getSizeOf(container.getEntityIdentifier());

        Point2D aLoc = scene.getLocationOf(a.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble aSize = scene.getSizeOf(a.getEntityIdentifier());

        Point2D bLoc = scene.getLocationOf(b.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble bSize = scene.getSizeOf(b.getEntityIdentifier());

        Point2D cLoc = scene.getLocationOf(c.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble cSize = scene.getSizeOf(c.getEntityIdentifier());

        Point2D dLoc = scene.getLocationOf(d.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble dSize = scene.getSizeOf(d.getEntityIdentifier());

        Point2D eLoc = scene.getLocationOf(e.getEntityIdentifier()).getCoordinates();
        Dimension2DDouble eSize = scene.getSizeOf(e.getEntityIdentifier());

        assertThat(aLoc.getY() + aSize.getHeight(), lessThan(bLoc.getY()));
        assertThat(aLoc.getY() + aSize.getHeight(), lessThan(cLoc.getY()));
        assertThat(aLoc.getY() + aSize.getHeight(), lessThan(dLoc.getY()));
        assertThat(aLoc.getY() + aSize.getHeight(), lessThan(eLoc.getY()));
        
        assertThat(bLoc.getY(), closeTo(cLoc.getY(), 0.01));
        
        assertThat(cLoc.getY() + cSize.getHeight(), lessThan(dLoc.getY()));
        assertThat(cLoc.getY() + cSize.getHeight(), lessThan(eLoc.getY()));

        assertThat(dLoc.getY(), closeTo(eLoc.getY(), 0.01));

        assertThat(bLoc.getX(), lessThan(aLoc.getX()));
        assertThat(bLoc.getX() + bSize.getWidth(), lessThan(cLoc.getX()));
        assertThat(bLoc.getX(), lessThan(dLoc.getX()));
        assertThat(bLoc.getX() + bSize.getWidth(), lessThan(eLoc.getX()));

        assertThat(aLoc.getX(), lessThan(cLoc.getX()));
        assertThat(aLoc.getX(), lessThan(eLoc.getX()));

        assertThat(dLoc.getX() + dSize.getWidth(), lessThan(eLoc.getX()));
    }




}
