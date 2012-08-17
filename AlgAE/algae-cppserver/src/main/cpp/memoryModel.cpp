/**
 * memoryModel.cpp
 *
 *
 *  Created on: Aug 15, 2012
 *      Author: zeil
 */


#include <algae/memoryModel/memoryModel.h>
#include <algae/snapshot/snapshot.h>
#include <algae/snapshot/entityIdentifier.h>
#include <algae/snapshot/entity.h>


#include <list>
#include <map>




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

using namespace std;

namespace algae {

/*
class Animation;
class Entity;
class Snapshot;

class MemoryModel
{
private:
	ActivationStack activationStack;
	std::list<Component> globals;
	Animation& animation;


	std::set<Identifier> knownObjects;
*/

MemoryModel::MemoryModel (Animation& context)
 : animation(context)
{

}




Snapshot* MemoryModel::renderInto(std::string description, SourceLocation sourceLocation)
{
	Snapshot* snap = new Snapshot(description, sourceLocation);
	Identifier stackID (activationStack);
	EntityIdentifier stackEID (stackID);
	snap->setActivationStack(stackEID);
	formClosure(snap);
	normalize(snap);
	return snap;
}




	/**
	 * Guards against infinite recursion in the componentOf relation
	static const int DepthLimit = 25;


	class InternalComponent {
	public:
		EntityIdentifier container;
		Component component;

		InternalComponent (EntityIdentifier acontainer, Component acomponent)
		{
			container = acontainer;
			component = acomponent;
		}

		void print (std::ostream& out) const
		{
			out << "IC[" << component << "]@" << container;
		}

	};
*/


/**
 * Adds to the snapshot all objects that can be reached in one or more
 * steps along the component and connection from the activation stack. This
 * will include all global variables, parameters and locals of the current call,
 * and non-ref parameters of the older calls.
 *
 * @param ob
 * @param tobeProcessed
 */
void MemoryModel::formClosure(Snapshot* snap)
{
	// Initialize the queue with the activation stack and with the global and
	// local objects
	list<InternalComponent> queue;
	queue.push_back (InternalComponent(EntityIdentifier::nullId(), Identifier(activationStack)));

	// globals are stored in record 0 of the stack
	ActivationRecord& globals = *(activationStack.begin());
	for (ActivationRecord::iterator g = globals.beginLocals(); g != globals.endLocals(); ++g) {
		queue.push_back(InternalComponent(EntityIdentifier::nullId(), g->oid, g->label));
	}

	while (queue.size() > 0U) {
		// For each component in the queue, use the renderer for that object
		// to create an entity and add its components and connections to the queue
		// for future processing.
		InternalComponent& c = *(queue.begin());
		queue.pop_front();

		Identifier oid = c.component;
		Snapshot::EntitiesTable& etable = snap->getEntities();
		pair<Snapshot::EntitiesTable::iterator, Snapshot::EntitiesTable::iterator> erange = etable.equal_range(oid);
		bool found = false;
		EntityIdentifier newEntityID (oid, c.container, c.label);
		//cerr << "Snapshot closure adds " << newEntityID << endl;
		for (Snapshot::EntitiesTable::iterator i = erange.first; (!found) && i != erange.second; ++i)
		{
			if (newEntityID == i->second.getEntityIdentifier())
			{
				found = true;
				break;
			}
		}
		if (!found)
		{
			Entity* entity = renderObject (newEntityID, c, queue);
			//cerr << "Closure: new entity " << entity << endl;
			etable.insert(Snapshot::EntitiesTable::value_type(oid, entity));
		}
	}

}

string MemoryModel::toString(int i) const
{
	ostringstream out;
	out << i;
	return out.str();
}

// Create an entity describing the rendering of this component;
Entity* MemoryModel::renderObject(EntityIdentifier eid, InternalComponent c, std::list<InternalComponent> queue)
{
	Identifier oid = eid.getObjectIdentifier();
	Entity entity (oid, c.container, c.label);

	Identifier obj = c.component;
	Renderer* renderer = activationStack.getRenderingOf(obj);
	renderer->renderInto(entity);
	list<EntityIdentifier>& components = entity.getComponents();
	if (components.size() > 0U && eid.depth() < DepthLimit) {
		int componentCount = 0;
		for (list<EntityIdentifier>::iterator ci = components.begin(); ci != components.end(); ++ci)
		{
			EntityIdentifier& comp = *ci;
			Identifier cobj = comp.getObjectIdentifier();
			string clabel = comp.getComponentLabel();
			if (clabel.size() == 0U)
				clabel = string("\t") + toString(componentCount);
			++componentCount;
			if (cobj != Identifier::NullID) {
				EntityIdentifier c_eid = new EntityIdentifier(cobj, eid, clabel);
				entity.getComponents().push_back(c_eid);
				InternalComponent intComp (eid, cobj, clabel);
				//cerr << entity.getEntityIdentifier() << " has component " << c_eid << endl;
				queue.push_back (intComp);
			}
		}
	}
	list<Connector> connections = entity.getConnections();
	for (list<Connector>::iterator ci = connections.begin(); ci != connections.end(); ++ci)
	{
		Connector& conn = *ci;
		Identifier destID = conn.getDestination();
		if (destID != Identifier::NullID) {
			InternalComponent intComp (EntityIdentifier::nullId(), destID, "");
			//cerr << entity.getEntityIdentifier() << " connects to " << destID << endl;
			queue.push_back (intComp);
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
void MemoryModel::normalize(Snapshot* snap)
{
	map<Identifier, Entity> primaries;
	map<EntityIdentifier, Entity> unique;
	for (Identifier oid: snap.getEntities().keySet()) {
		LinkedList<Entity> aliases = snap.getEntities().get(oid);
		int maxdepth = -1;
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
			if (deep > maxdepth) {
				maxdepth = deep;
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
			EntityIdentifier ceid = e.getContainer();
			if (ceid == null || ceid.equals(EntityIdentifier.nullID())) {
				queue.add(e);
			}
		}
	}
	while (!queue.isEmpty()) {
		Entity e = queue.getFirst();
		queue.removeFirst();
		EntityIdentifier eid = e.getEntityIdentifier();
		keepThese.add(eid);
		for (EntityIdentifier ceid: e.getComponents()) {
			queue.add (unique.get(ceid));
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




}

