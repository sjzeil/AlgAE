package edu.odu.cs.AlgAE.Server.MemoryModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.odu.cs.AlgAE.Animations.AnimationContext;
import edu.odu.cs.AlgAE.Animations.ContextAware;
import edu.odu.cs.AlgAE.Common.Snapshot.Connector;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity;
import edu.odu.cs.AlgAE.Common.Snapshot.EntityIdentifier;
import edu.odu.cs.AlgAE.Common.Snapshot.Identifier;
import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;
import edu.odu.cs.AlgAE.Common.Snapshot.SourceLocation;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;
import edu.odu.cs.AlgAE.Server.Utilities.Index;
import edu.odu.cs.AlgAE.Server.Utilities.SimpleReference;


/**
 * The fundamental memory model supported by an animation server.
 *
 * Memory is represented as a
 *   - activation stack
 *   - a set of global objects
 *   - the collection of all objects that can be reached starting from the
 *       activation stack and the global variables.
 *   - a collection of rendering mechanisms for different data types or for specific objects
 *
 * The memory model can be rendered at a lower level as a collection of components and connections.
 * Components can contain other components. Connections link one component to another.
 *
 * Together, the containment and connection relations present a directed graph.
 *
 * A snapshot can be taken of that graph at any time.  That snapshot is a linearized representation of
 * all components and connections that can be reached starting from the rendering of the activation stack
 * object and from the globals.
 *
 * @author zeil
 *
 */
public class MemoryModel implements ContextAware
{

    private ActivationStack activationStack;
    private LinkedList<Component> globals;
    private AnimationContext animation;
    
    
    
    
    public MemoryModel (AnimationContext context) {
        animation = context;
        activationStack = new ActivationStack(this);
        globals = new LinkedList<Component>();
    }
    
    
    



    /**
     * Get the collection of rules for rendering an object. Although this returns
     * a single rendering object, this object may represent the combination of
     * several distinct renderers applicable to the indicated object. The combination
     * is obtained by consultation with available renderers as follows (from highest to
     * lowest precedence):
     *   1) renderings established for specific objects
     *   2) getRendering(), for classes that implement CanBeRendered
     *   3) class renderings (see render(), below))
     *   4) class renderings established for superclasses of this one
     *   5) default rendering (displays toString() with no components or connections)
     *
     * @param obj
     * @return a list of renderers, in the order they should be consulted.
     */
    public <T> Renderer<T> getRenderer(T obj) {
        return activationStack.getRenderer(obj);
    }


    /**
     * Establish a rendering for all objects of the indicated class.
     * Note that there are several ways to establish renderings, and that
     * these are resolved as described in getRenderer(), above.
     *
     * If a prior rendering has been established for this class, it is replaced by this call.
     * Unlike object renderings, class renderings are "global" and do not lose effect when
     * we return from an activation.
     *
     */
    public <T> MemoryModel render(Class<?> aClass, Renderer<T> newRendering) {
        activationStack.render(aClass, newRendering);
        return this;
    }

    

    /**
     * Show a variable as a global value in all displays.
     * Variables portrayed by this call are shown "in-line".
     *
     * @param label  the variable name (optional, can be "" or null)
     * @param param  the variable/value
     */
    public void globalVar(String label, int value) {
        globalVar(label, new Index(value));
    }

    /**
     * Show a variable as a global value in all displays.
     * Variables portrayed by this call are shown "in-line".
     *
     * @param label  the variable name (optional, can be "" or null)
     * @param param  the variable/value
     */
    public void globalVar (String label, Object param)
    {
        globals.add(new Component(param, label));
    }

    /**
     * Show a variable as a global value in all displays.
     * Variables portrayed by this call are shown as labeled
     * pointers to the actual value.
     *
     * @param label  the variable name (optional, can be "" or null)
     * @param param  the variable/value
     * @return a reference to this breakpoint
     */
    public void globalRefVar (String label, Object param)
    {
        SimpleReference ref = new SimpleReference(param);
        ref.setMinAngle(90.0);
        ref.setMaxAngle(180.0);
        globalVar (label, ref);
    }



    
    /**
     * Get a list of all global objects shared by all activations
     * @return list of global objects
     */
    public List<Component> getGlobalComponents() {
        return globals;
    }

    
    public String toString() {
        return activationStack.toString();
    }
    


    
    
    

    @Override
    public AnimationContext context() {
        return animation;
    }

    public Snapshot renderInto(String description, SourceLocation sourceLocation) {
        Snapshot snap = new Snapshot(description, sourceLocation);
        snap.setActivationStack(new EntityIdentifier(new Identifier(activationStack)));
        formClosure(snap);
        normalize(snap);
        return snap;
    }


    /**
     * Guards against infinite recursion in the componentOf relation
     */
    private static final int DepthLimit = 25;
    
    
    private class InternalComponent {
        public EntityIdentifier container;
        public Component component;
        
        public InternalComponent (EntityIdentifier container, Component component)
        {
            this.container = container;
            this.component = component;
        }
        
        public String toString() {
            return "IC[" + component + "]@" + container;
        }
    }
    
    /**
     * Adds to the snapshot all objects that can be reached in one or more
     * steps along the component and connection from the activation stack. This
     * will include all global variables, parameters and locals of the current call,
     * and non-ref parameters of the older calls.
     *
     * @param snap the snapshot to which to add the referenced objects
     */
    private void formClosure(Snapshot snap)
    {
        // Initialize the queue with the activation stack and with the global and
        // local objects
        LinkedList<InternalComponent> queue = new LinkedList<InternalComponent>();
        queue.add (new InternalComponent(null, new Component(activationStack)));
        
        for (Component gC: globals) {
            queue.add(new InternalComponent(null, gC));
        }

        while (!queue.isEmpty()) {
            // For each component in the queue, use the renderer for that object
            // to create an entity and add its components and connections to the queue
            // for future processing.
            InternalComponent c = queue.pop();
            Identifier oid = new Identifier(c.component.getComponentObject());
            LinkedList<Entity> aliasesForEntity = snap.getEntities().get(oid);
            if (aliasesForEntity == null) {
                aliasesForEntity = new LinkedList<Entity>();
                snap.getEntities().put(oid, aliasesForEntity);
            }
            boolean found = false;
            EntityIdentifier newEntityID = new EntityIdentifier(oid, c.container, c.component.getLabel());
            //System.err.println ("Snapshot closure adds " + newEntityID);

            for (Entity e: aliasesForEntity) {
                if (newEntityID.equals(e.getEntityIdentifier())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                Entity entity = renderObject (newEntityID, c, queue);
                //System.err.println ("Closure: new entity " + entity);
                aliasesForEntity.add(entity);
            }
        }
    }

    // Create an entity describing the rendering of this component;
    private Entity renderObject(EntityIdentifier eid, InternalComponent c, LinkedList<InternalComponent> queue) {
        Identifier oid = eid.getObjectIdentifier();
        Entity entity = null;
        if (c.container == null) {
            String label = c.component.getLabel();
            entity = new Entity(oid, label);
        } else {
            entity = new Entity(oid, c.container, c.component.getLabel());
        }
        Object obj = c.component.getComponentObject();
        Renderer<Object> render = activationStack.getRenderer(obj);
        entity.setColor(render.getColor(obj));

        entity.setDirection(render.getDirection());
        entity.setSpacing(render.getSpacing());
        entity.setClosedOnConnections(render.getClosedOnConnections());
        
        entity.setValue(render.getValue(obj));
        List<Component> components = render.getComponents(obj);
        if (components != null && eid.depth() < DepthLimit) {
            int componentCount = 0;
            for (Component comp: components) {
                Object cObj = comp.getComponentObject();
                String cLabel = comp.getLabel();
                if (cLabel == null || cLabel.length() == 0)
                    cLabel = "\t" + componentCount;
                ++componentCount;
                if (cObj != null) {
                    EntityIdentifier c_eid = new EntityIdentifier(new Identifier(cObj), eid, cLabel);
                    entity.getComponents().add(c_eid);
                    InternalComponent intComp = new InternalComponent(eid, new Component(cObj, cLabel));
                    //System.err.println ("" + entity.getEntityIdentifier() + " has component " + c_eid);
                    queue.add (intComp);
                }
            }
        }
        List<Connection> connections = render.getConnections(obj);
        if (connections != null) {
            for (Connection conn: connections) {
                Object destObj = conn.getDestination();
                Identifier destID = null;
                destID = new Identifier(destObj);
                Connector connector = new Connector(conn.getID(), eid, new EntityIdentifier(destID),
                        conn.getMinAngle(), conn.getMaxAngle(), conn.getComponentIndex());
                if (conn.getColor() != null)
                    connector.setColor(conn.getColor());
                connector.setLabel(conn.getLabel());
                connector.setValue(conn.getValue());
                connector.setElasticity(conn.getElasticity());
                connector.setPreferredLength(conn.getPreferredLength());
                entity.getConnections().add(connector);
                if (destObj != null) {
                    InternalComponent intComp = new InternalComponent(null, new Component(destObj));
                    //System.err.println ("" + entity.getEntityIdentifier() + " connects to " + destID);
                    queue.add (intComp);
                }
            }
        }
        return entity;
    }


    
    /**
     * Attempts to resolve duplications caused by objects that map onto several discrete entities.
     *
     * 1) If two entities exist for the same object and one is not a component of a larger entity
     *    and is also unlabeled, then that one is removed.
     * 2) For each remaining object with multiple renderings, the most deeply nested one is considered as
     *      the primary occurrence. If there is a tie for most deeply nested, the tie is broken arbitrarily.
     * 3) All connectors incoming to an entity are re-routed to the primary occurrence of that object.
     * 4) All connectors outgoing from a non-primary entity are dropped.
     */
    private void normalize(Snapshot snap)
    {
        HashMap<Identifier, Entity> primaries = new HashMap<Identifier, Entity>();
        HashMap<EntityIdentifier, Entity> unique = new HashMap<EntityIdentifier, Entity>();
        for (Identifier oid: snap.getEntities().keySet()) {
            LinkedList<Entity> aliases = snap.getEntities().get(oid);
            int maxDepth = -1;
            Entity deepest = null;
            Iterator<Entity> it = aliases.iterator();
            while (it.hasNext()) {
                Entity entity = it.next();
                unique.put (entity.getEntityIdentifier(), entity);
                if (aliases.size() > 1) {
                    if (entity.getContainer() == null &&
                            (entity.getLabel() == null ||  entity.getLabel().length() == 0)) {
                        it.remove();
                    }        
                }
                int deep = entity.getEntityIdentifier().depth();
                if (deep > maxDepth) {
                    maxDepth = deep;
                    deepest = entity;
                }
            }
            primaries.put(oid, deepest);
        }
        
        HashSet<EntityIdentifier> keepThese = new HashSet<EntityIdentifier>();
        LinkedList<Entity> queue = new LinkedList<Entity>();
        for (Identifier oid: snap.getEntities().keySet()) {
            LinkedList<Entity> aliases = snap.getEntities().get(oid);
            for (Entity e: aliases) {
                EntityIdentifier cEID = e.getContainer();
                if (cEID == null || cEID.equals(EntityIdentifier.nullID())) {
                    queue.add(e);
                }
            }
        }
        while (!queue.isEmpty()) {
            Entity e = queue.getFirst();
            queue.removeFirst();
            EntityIdentifier eid = e.getEntityIdentifier();
            keepThese.add(eid);
            for (EntityIdentifier cEID: e.getComponents()) {
                queue.add (unique.get(cEID));
            }
        }
        HashMap<Identifier, LinkedList<Entity>> trimmedEntities = new HashMap<Identifier, LinkedList<Entity>>();
        for (Identifier oid: snap.getEntities().keySet()) {
            LinkedList<Entity> aliases = snap.getEntities().get(oid);
            Iterator<Entity> it = aliases.iterator();
            while (it.hasNext()) {
                Entity entity = it.next();
                if (!keepThese.contains(entity.getEntityIdentifier())) {
                    it.remove();        
                }
            }
            if (aliases.size() > 0) {
                trimmedEntities.put (oid, aliases);
            }
        }
        snap.getEntities().clear();
        snap.getEntities().putAll (trimmedEntities);
        
        for (Identifier oid: snap.getEntities().keySet()) {
            LinkedList<Entity> aliases = snap.getEntities().get(oid);
            Entity primary = primaries.get(oid);
            for (Entity e: aliases) {
                if (e == primary) {
                    for (Connector conn: e.getConnections()) {
                        EntityIdentifier destID = conn.getDestination();
                        if (!destID.equals(EntityIdentifier.nullID())) {
                            EntityIdentifier primaryDest = primaries.get(destID.getObjectIdentifier()).getEntityIdentifier();
                            conn.setDestination (primaryDest);
                        }
                    }
                } else {
                    e.getConnections().clear();
                }
            }
        }        
    }



    /**
     * @return the activationStack
     */
    public ActivationStack getActivationStack() {
        return activationStack;
    }



    
    
    
    
}
