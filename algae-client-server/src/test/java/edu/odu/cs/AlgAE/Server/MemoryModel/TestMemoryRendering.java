package edu.odu.cs.AlgAE.Server.MemoryModel;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
//import static org.hamcrest.MatcherAssert.assertThat; 
//import static org.hamcrest.Matchers.*;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import edu.odu.cs.AlgAE.Animations.LocalJavaAnimation;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity;
import edu.odu.cs.AlgAE.Common.Snapshot.EntityIdentifier;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;
import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;
import edu.odu.cs.AlgAE.Common.Snapshot.SourceLocation;
import edu.odu.cs.AlgAE.Server.LocalServer;
import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;

/**
 * @author zeil
 *
 */
public class TestMemoryRendering {

     
    private LocalJavaAnimation applet;
    private LocalServer anim;
    
    @BeforeEach
    public void setUp() {
    	applet = new LocalJavaAnimation("dummy") {
			

            @Override
			public String about() {
				return "";
			}
			

			@Override
			public void buildMenu() {
			}
    	};
    	anim = (LocalServer)applet.getServer();
    	anim.registerInstance(Thread.currentThread());
    	
    }
    
    
    public void registerStartingAction(MenuFunction action) 
    {
    	
    }
    
    
	private void checkVar (Map<EntityIdentifier, Entity> entities, Object obj, int len, String label, String value) {
		Entity e = entities.get(new Identifier(obj).asEntityIdentifier());
		assertNotNull(e);
		if (label != null)
			assertEquals (label, e.getLabel());
		if (value != null)
			assertEquals(value, e.getValue());
		
	}
	

    @Test
	public void test_emptySnap() {
    	MemoryModel memory = anim.getMemoryModel();
		Snapshot snap = memory.renderInto("description", new SourceLocation("foo.java", 23));
		Map<EntityIdentifier, Entity> entities =  snap.getEntities();
		//assertEquals (2, entities.keySet().size());
		checkVar (entities, memory.getActivationStack(), 1, null, null);
	}

    @Test
	public void test_Snap_global() {
		MemoryModel memory = anim.getMemoryModel();
		ActivationStack callStack = memory.getActivationStack();
		String fortyTwo = "42";
		callStack.globalVar("foo", fortyTwo);
		Snapshot snap = memory.renderInto("description", new SourceLocation("foo.java", 23));
		var entities =  snap.getEntities();
		assertEquals (6, entities.keySet().size());
		checkVar (entities, callStack, 1, null, null);
		checkVar (entities, fortyTwo, 1, null, "42");
	}

	private static class String2 implements Renderer<String2>, CanBeRendered<String2> {
		
		public String s;
		
		public String2 (String ss) {
			s = ss;
		}

		@Override
		public Renderer<String2> getRenderer() {
			return this;
		}

		@Override
		public String getValue(String2 obj) {
			return s;
		}

		@Override
		public Color getColor(String2 obj) {
			return Color.blue;
		}

		@Override
		public List<Component> getComponents(String2 obj) {
			LinkedList<Component> c = new LinkedList<Component>();
			c.add(new Component(s, "comp"));
			return c;
		}

		@Override
		public List<Connection> getConnections(String2 obj) {
			return new LinkedList<Connection>();
		}

		@Override
		public Directions getDirection() {
			return Directions.Vertical;
		}

		@Override
		public Double getSpacing() {
			return Renderer.DefaultSpacing;
		}

		@Override
		public Boolean getClosedOnConnections() {
			return false;
		}
		
	}
	
	@Test
	public void test_Snap_components() {
		MemoryModel memory = anim.getMemoryModel();
		ActivationStack callStack = memory.getActivationStack();
		String fortyTwo = "42";
		callStack.globalVar("foo", fortyTwo);
		String twelve = "12";
		String2 dozen = new String2(twelve);
		callStack.globalVar("dozen", dozen);
		Snapshot snap = memory.renderInto("description", new SourceLocation("foo.java", 23));
		var entities =  snap.getEntities();
		assertEquals (9, entities.keySet().size());
		checkVar (entities, callStack, 1, null, null);
		checkVar (entities, fortyTwo, 1, null, "42");
		checkVar (entities, dozen, 1, null, "12");
		checkVar (entities, twelve, 1, "comp", "12");
	}

	@Test
	public void test_Snap_connections() {
		MemoryModel memory = anim.getMemoryModel();
		ActivationStack callStack = memory.getActivationStack();
		String fortyTwo = "42";
		callStack.globalVar("foo", fortyTwo);
		String twelve = "12";
		String2 dozen = new String2(twelve);
		callStack.globalRefVar("dozen", dozen);
		Snapshot snap = memory.renderInto("description", new SourceLocation("foo.java", 23));
		var entities =  snap.getEntities();
		assertEquals (10, entities.keySet().size());
		checkVar (entities, callStack, 1, null, null);
		checkVar (entities, fortyTwo, 1, null, "42");
		checkVar (entities, dozen, 1, null, "12");
		checkVar (entities, twelve, 1, null, "12");
	}

	@Test
	public void test_Snap_alias() {
		MemoryModel memory = anim.getMemoryModel();
		ActivationStack callStack = memory.getActivationStack();
		String fortyTwo = "42";
		callStack.globalVar("foo", fortyTwo);
		String2 dozen = new String2(fortyTwo);
		callStack.globalVar("dozen", dozen);
		Snapshot snap = memory.renderInto("description", new SourceLocation("foo.java", 23));
		var entities =  snap.getEntities();
		assertEquals (9, entities.keySet().size());
		checkVar (entities, callStack, 1, null, null);
		checkVar (entities, fortyTwo, 2, null, "42");
		checkVar (entities, dozen, 1, null, "42");
	}
}
