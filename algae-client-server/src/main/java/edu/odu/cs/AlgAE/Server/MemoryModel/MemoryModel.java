package edu.odu.cs.AlgAE.Server.MemoryModel;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import edu.odu.cs.AlgAE.Animations.AnimationContext;
import edu.odu.cs.AlgAE.Animations.ContextAware;
import edu.odu.cs.AlgAE.Common.Snapshot.Connector;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;
import edu.odu.cs.AlgAE.Common.Snapshot.EntityIdentifier;
import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;
import edu.odu.cs.AlgAE.Common.Snapshot.SourceLocation;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;
import edu.odu.cs.AlgAE.Server.Utilities.Index;
import edu.odu.cs.AlgAE.Server.Utilities.SimpleReference;

/**
 * The fundamental memory model supported by an animation server.
 *
 * Memory is represented as a
 * - activation stack
 * - a set of global objects
 * - the collection of all objects that can be reached starting from the
 * activation stack and the global variables.
 * - a collection of rendering mechanisms for different data types or for
 * specific objects
 *
 * The memory model can be rendered at a lower level as a collection of
 * components and connections.
 * Components can contain other components. Connections link one component to
 * another.
 *
 * Together, the containment and connection relations present a directed graph.
 *
 * A snapshot can be taken of that graph at any time. That snapshot is a
 * linearized representation of
 * all components and connections that can be reached starting from the
 * rendering of the activation stack
 * object and from the globals.
 *
 * @author zeil
 *
 */
public class MemoryModel implements ContextAware,
        CanBeRendered<MemoryModel>, Renderer<MemoryModel> {

    private ActivationStack activationStack;
    private LinkedList<Component> globals;
    private AnimationContext animation;
    private GlobalList globalList;
    private Map<EntityIdentifier, Entity> entities;
    private Map<Identifier, Component> components;
    private Identifier globalsID;

    private class GlobalList implements CanBeRendered<GlobalList>, Renderer<GlobalList> {
        private Entity entity;

        public GlobalList() {
            entity = new Entity(new Identifier(this), "*globals*");
        }

        public Entity getEntity() {
            return entity;
        }

        @Override
        public String getValue(GlobalList obj) {
            return "";
        }

        @Override
        public Color getColor(GlobalList obj) {
            return Color.white;
        }

        @Override
        public List<Component> getComponents(GlobalList obj) {
            List<Component> components = new LinkedList<>();
            return components;
        }

        @Override
        public List<Connection> getConnections(GlobalList obj) {
            return new LinkedList<>();
        }

        @Override
        public Directions getDirection() {
            return Directions.Square;
        }

        @Override
        public Double getSpacing() {
            return 2 * Renderer.DefaultSpacing;
        }

        @Override
        public Boolean getClosedOnConnections() {
            return true;
        }

        @Override
        public Renderer<GlobalList> getRenderer() {
            return this;
        }
    }

    public MemoryModel(AnimationContext context) {
        animation = context;
        activationStack = new ActivationStack(this);
        globals = new LinkedList<Component>();
        globalList = new GlobalList();
        entities = new HashMap<>();
        components = new HashMap<>();
    }

    /**
     * Get the collection of rules for rendering an object. Although this returns
     * a single rendering object, this object may represent the combination of
     * several distinct renderers applicable to the indicated object. The
     * combination
     * is obtained by consultation with available renderers as follows (from highest
     * to
     * lowest precedence):
     * 1) renderings established for specific objects
     * 2) getRendering(), for classes that implement CanBeRendered
     * 3) class renderings (see render(), below))
     * 4) class renderings established for superclasses of this one
     * 5) default rendering (displays toString() with no components or connections)
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
     * If a prior rendering has been established for this class, it is replaced by
     * this call.
     * Unlike object renderings, class renderings are "global" and do not lose
     * effect when
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
     * @param label the variable name (optional, can be "" or null)
     * @param param the variable/value
     */
    public void globalVar(String label, int value) {
        globalVar(label, new Index(value));
    }

    /**
     * Show a variable as a global value in all displays.
     * Variables portrayed by this call are shown "in-line".
     *
     * @param label the variable name (optional, can be "" or null)
     * @param param the variable/value
     */
    public void globalVar(String label, Object param) {
        globals.add(new Component(param, null, label));
    }

    /**
     * Show a variable as a global value in all displays.
     * Variables portrayed by this call are shown as labeled
     * pointers to the actual value.
     *
     * @param label the variable name (optional, can be "" or null)
     * @param param the variable/value
     * @return a reference to this breakpoint
     */
    public void globalRefVar(String label, Object param) {
        SimpleReference ref = new SimpleReference(param);
        ref.setMinAngle(90.0);
        ref.setMaxAngle(180.0);
        globalVar(label, ref);
    }

    /**
     * Get a list of all global objects shared by all activations
     * 
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
        snap.setRootEntity(new Identifier(this).asEntityIdentifier());
        formClosure(snap);
        // normalize(snap);
        return snap;
    }

    private class InternalComponent {
        public Component container;
        public Component component;

        public InternalComponent(Component container, Component component) {
            this.container = container;
            this.component = component;

        }

        public String toString() {
            return "IC[" + component + "]@" + container;
        }
    }

    /**
     * Adds to the snapshot all objects that can be reached in one or more
     * steps along the components and connections from the activation stack.
     * This will include all global variables, parameters and locals of the
     * current call, and non-ref parameters of the older calls.
     *
     * @param snap the snapshot to which to add the referenced objects
     */
    private void formClosure(Snapshot snap) {
        // Get the top three components
        Identifier rootID = new Identifier(this);
        EntityIdentifier rootEID = rootID.asEntityIdentifier();
        Component rootComponent = new Component(this, "root");

        Identifier stackID = new Identifier(activationStack);
        EntityIdentifier stackEID = stackID.asEntityIdentifier();
        Component stackComponent = new Component(activationStack, rootComponent, "astack");

        globalsID = new Identifier(globalList);
        EntityIdentifier globalsEID = globalsID.asEntityIdentifier();
        Component globalsComponent = new Component(globalList, rootComponent, "globals");

        Entity rootEntity = new Entity(rootID, "root");

        entities.clear();
        entities.put(rootEID, rootEntity);

        // Initialize the queue with the memory model
        LinkedList<Component> queue = new LinkedList<>();
        queue.add(stackComponent);
        queue.add(globalsComponent);

        components.clear();
        components.put(rootID, rootComponent);
        


        for (Component gC : globals) {
            gC.setContainer(globalsComponent);
            queue.add(gC);
        }

        while (!queue.isEmpty()) {
            // For each component in the queue, use the renderer for that object
            // to create an entity and add its components and connections to the queue
            // for future processing.
            Component c = queue.pop();
            System.err.println("formClosure on " + c);
            renderIfUnknownSoFar(snap, queue, c);
        }
    }

    private void renderIfUnknownSoFar(Snapshot snap, LinkedList<Component> queue, Component c) {
        Identifier oid = new Identifier(c.getComponentObject());

        Component container = c.getContainer();

        if (components.get(oid) != null) {
            Renderer<Object> containerRenderer = activationStack.getRenderer(container.getComponentObject());
            if (!containerRenderer.getClosedOnConnections()) {
                // This object appears to be a component of multiple larger
                // objects. We can't render it in two different places.
                Object obj = c.getComponentObject();
                Renderer<Object> renderer = activationStack.getRenderer(obj);
                Alias alias = new Alias(renderer);
                queue.push(new Component(alias, c.getContainer()));
            }  else {
                // We have processed this object elsewhere and don't need to
                // do it again.
                System.err.println("Closure: not re-rendering " + oid);
            }
        } else { // rendering this for the first time
                components.put(oid, c);
                Object containerObj = container.getComponentObject();
                Entity entity = renderObject(oid.asEntityIdentifier(), c, queue);
                snap.add(entity);
                // System.err.println ("Closure: new entity " + entity);
        }
    }

    // Create an entity describing the rendering of this component;
    private Entity renderObject(EntityIdentifier eid, Component c, LinkedList<Component> queue) {
        Identifier oid = new Identifier(eid);
        Entity entity = createNewEntity(c, oid);
        entities.put(eid, entity);
        EntityIdentifier containerEID = entity.getContainer();
        if (containerEID != null) {
            Entity container = entities.get(containerEID);
            container.getComponents().add(eid);
        } else {
            globalList.getEntity().getComponents().add(eid);
        }
        Object obj = c.getComponentObject();
        Renderer<Object> render = activationStack.getRenderer(obj);
        renderBasicEntityAttributes(entity, obj, render);
        renderComponents(eid, queue, entity, obj, render);
        renderConnections(eid, queue, entity, obj, render);
        return entity;
    }

    private void renderConnections(EntityIdentifier eid, LinkedList<Component> queue, Entity entity, Object obj,
            Renderer<Object> render) {
        List<Connection> connections = render.getConnections(obj);
        if (connections != null) {
            renderAllConnections(eid, queue, entity, connections);
        }
    }

    private void renderAllConnections(EntityIdentifier eid, LinkedList<Component> queue, Entity entity,
            List<Connection> connections) {
        for (Connection conn : connections) {
            Object destObj = conn.getDestination();
            Identifier destID = null;
            destID = new Identifier(destObj);
            Connector connector = new Connector(conn.getID(), eid, 
                    destID.asEntityIdentifier(),
                    conn.getMinAngle(), conn.getMaxAngle(), conn.getComponentIndex());
            if (conn.getColor() != null)
                connector.setColor(conn.getColor());
            connector.setLabel(conn.getLabel());
            connector.setValue(conn.getValue());
            connector.setElasticity(conn.getElasticity());
            connector.setPreferredLength(conn.getPreferredLength());
            entity.getConnections().add(connector);
            if (destObj != null) {
                Component destComponent = new Component(destObj, components.get(globalsID), "");
                // System.err.println ("" + entity.getEntityIdentifier() + " connects to " +
                // destID);

                queue.add(destComponent);
            }
        }
    }

    private void renderComponents(EntityIdentifier eid, LinkedList<Component> queue, Entity entity, Object obj,
            Renderer<Object> render) {
        Component container = components.get(new Identifier(obj));
        List<Component> componentList = render.getComponents(obj);
        if (componentList != null) {
            for (Component c: componentList) {
                if (c.getContainer() == null) {
                    c.setContainer(container);
                }
            }
            int componentCount = 0;
            ListIterator<Component> reversed = componentList.listIterator(components.size());
            while (reversed.hasPrevious()) {
                Component comp = reversed.previous();
                comp.setContainer(container);
                String cLabel = comp.getLabel();
                if (cLabel == null || cLabel.length() == 0)
                    cLabel = "\t" + componentCount;
                ++componentCount;
                if (container != null) {
                    if (render.getClosedOnConnections()) {
                        SpanTree spanTree = new SpanTree(container.getComponentObject(), 
                        render.getDirection(),   
                        components,
                        activationStack);
                        Component spanComponent = new Component(spanTree, container, cLabel);
                        queue.push(spanComponent);
                    } else {
                        // EntityIdentifier c_eid = new EntityIdentifier(new Identifier(cObj), eid,
                        // cLabel);
                        // entity.getComponents().add(c_eid);
                        Component c = new Component(comp, container, cLabel);
                        // System.err.println ("" + entity.getEntityIdentifier() + " has component " +
                        // c_eid);
                        queue.push(c);
                    }
                }
            }
        }
    }

    private void renderBasicEntityAttributes(Entity entity, Object obj, Renderer<Object> render) {
        entity.setColor(render.getColor(obj));
        entity.setDirection(render.getDirection());
        entity.setSpacing(render.getSpacing());
        entity.setClosedOnConnections(render.getClosedOnConnections());
        entity.setValue(render.getValue(obj));
    }

    private Entity createNewEntity(Component c, Identifier oid) {
        Entity entity = null;
        if (c.getContainer() == null) {
            String label = c.getLabel();
            entity = new Entity(oid, label);
        } else {
            entity = new Entity(oid, oid.asEntityIdentifier(), c.getLabel());
        }
        return entity;
    }

    /**
     * Attempts to resolve duplications caused by objects that map onto several
     * discrete entities.
     *
     * 1) If two entities exist for the same object and one is not a component of a
     * larger entity
     * and is also unlabeled, then that one is removed.
     * 2) For each remaining object with multiple renderings, the most deeply nested
     * one is considered as
     * the primary occurrence. If there is a tie for most deeply nested, the tie is
     * broken arbitrarily.
     * 3) All connectors incoming to an entity are re-routed to the primary
     * occurrence of that object.
     * 4) All connectors outgoing from a non-primary entity are dropped.
     */
    private void normalize(Snapshot snap) {
        HashMap<Identifier, Entity> primaries = new HashMap<Identifier, Entity>();
        HashMap<EntityIdentifier, Entity> unique = new HashMap<EntityIdentifier, Entity>();
        for (Identifier oid : snap.getEntities().keySet()) {
            LinkedList<Entity> aliases = snap.getEntities().get(oid);
            Entity deepest = null;
            Iterator<Entity> it = aliases.iterator();
            while (it.hasNext()) {
                Entity entity = it.next();
                unique.put(entity.getEntityIdentifier(), entity);
                if (aliases.size() > 1) {
                    if (entity.getContainer() == null &&
                            (entity.getLabel() == null || entity.getLabel().length() == 0)) {
                        it.remove();
                    }
                }
            }
            primaries.put(oid, deepest);
        }

        HashSet<EntityIdentifier> keepThese = new HashSet<EntityIdentifier>();
        LinkedList<Entity> queue = new LinkedList<Entity>();
        for (Identifier oid : snap.getEntities().keySet()) {
            LinkedList<Entity> aliases = snap.getEntities().get(oid);
            for (Entity e : aliases) {
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
            for (EntityIdentifier cEID : e.getComponents()) {
                queue.add(unique.get(cEID));
            }
        }
        HashMap<Identifier, LinkedList<Entity>> trimmedEntities = new HashMap<Identifier, LinkedList<Entity>>();
        for (Identifier oid : snap.getEntities().keySet()) {
            LinkedList<Entity> aliases = snap.getEntities().get(oid);
            Iterator<Entity> it = aliases.iterator();
            while (it.hasNext()) {
                Entity entity = it.next();
                if (!keepThese.contains(entity.getEntityIdentifier())) {
                    it.remove();
                }
            }
            if (aliases.size() > 0) {
                trimmedEntities.put(oid, aliases);
            }
        }
        snap.getEntities().clear();
        snap.getEntities().putAll(trimmedEntities);

        for (Identifier oid : snap.getEntities().keySet()) {
            LinkedList<Entity> aliases = snap.getEntities().get(oid);
            Entity primary = primaries.get(oid);
            for (Entity e : aliases) {
                if (e == primary) {
                    for (Connector conn : e.getConnections()) {
                        EntityIdentifier destID = conn.getDestination();
                        if (!destID.isNull()) {
                            EntityIdentifier primaryDest = primaries.get(new Identifier(destID)).getEntityIdentifier();
                            conn.setDestination(primaryDest);
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

    @Override
    public String getValue(MemoryModel obj) {
        return "";
    }

    @Override
    public Color getColor(MemoryModel obj) {
        return Color.white;
    }

    @Override
    public List<Component> getComponents(MemoryModel obj) {
        java.util.ArrayList<Component> components = new java.util.ArrayList<Component>();
        components.add(new Component(activationStack));
        components.add(new Component(globalList));
        return components;
    }

    @Override
    public List<Connection> getConnections(MemoryModel obj) {
        return new LinkedList<>();
    }

    @Override
    public Directions getDirection() {
        return Directions.Horizontal;
    }

    @Override
    public Double getSpacing() {
        return 2.0 * Renderer.DefaultSpacing;
    }

    @Override
    public Boolean getClosedOnConnections() {
        return false;
    }

    @Override
    public Renderer<MemoryModel> getRenderer() {
        return this;
    }

}
