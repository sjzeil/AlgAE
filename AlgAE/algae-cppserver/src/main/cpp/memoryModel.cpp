/**
 * memoryModel.cpp
 *
 *
 *  Created on: Aug 15, 2012
 *      Author: zeil
 */


#include <algae/memoryModel/memoryModel.h>
#include <algae/rendering/typeRendering.h>
#include <algae/snapshot/snapshot.h>
#include <algae/snapshot/entityIdentifier.h>
#include <algae/snapshot/entity.h>


#include <list>
#include <map>
#include <sstream>




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



MemoryModel::MemoryModel (Animation& context)
 : animation(context)
{
	activationStack.push("*globals*");
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
			Entity entity = renderObject (newEntityID, c, queue);
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
Entity MemoryModel::renderObject(EntityIdentifier eid, InternalComponent c, std::list<InternalComponent> queue)
{
	Identifier oid = eid.getObjectIdentifier();
	Entity entity (oid, c.container, c.label);

	Identifier obj = c.component;
	Renderer* renderer = activationStack.getRenderingOf(obj);
	renderer->renderInto(entity);
	Entity::ComponentsList& components = entity.getComponents();
	if (components.size() > 0U && eid.depth() < DepthLimit) {
		int componentCount = 0;
		for (Entity::ComponentsList::iterator ci = components.begin(); ci != components.end(); ++ci)
		{
			EntityIdentifier& comp = *ci;
			Identifier cobj = comp.getObjectIdentifier();
			string clabel = comp.getComponentLabel();
			if (clabel.size() == 0U)
				clabel = string("\t") + toString(componentCount);
			++componentCount;
			if (cobj != Identifier::NullID) {
				EntityIdentifier c_eid (cobj, eid, clabel);
				entity.getComponents().push_back(c_eid);
				InternalComponent intComp (eid, cobj, clabel);
				//cerr << entity.getEntityIdentifier() << " has component " << c_eid << endl;
				queue.push_back (intComp);
			}
		}
	}
	Entity::ConnectionsList connections = entity.getConnections();
	for (Entity::ConnectionsList::iterator ci = connections.begin(); ci != connections.end(); ++ci)
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

	Snapshot::EntitiesTable& etable = snap->getEntities();
	Identifier lastOID;
	set<Identifier> keys;
	for (Snapshot::EntitiesTable::iterator i = etable.begin(); i != etable.end(); ++i)
	{
		keys.insert(i->first);
	}
	list<Snapshot::EntitiesTable::iterator> toBeRemoved;
	for (set<Identifier>::iterator k = keys.begin(); k != keys.end(); ++k)
	{
		const Identifier& oid = *k;
		pair<Snapshot::EntitiesTable::iterator, Snapshot::EntitiesTable::iterator> erange = etable.equal_range(oid);
		unsigned numAliases = distance(erange.first, erange.second);

		int maxdepth = -1;
		Entity deepest = (erange.first)->second;
		for (Snapshot::EntitiesTable::iterator it = erange.first; it != erange.second; ++it)
		{
			Entity& entity = it->second;
			unique[entity.getEntityIdentifier()] =  entity;
			if (numAliases > 1) {
				if (entity.getContainer() == EntityIdentifier::nullId() &&
					entity.getLabel().size() == 0U) {
					toBeRemoved.push_back (it);
				}
			}
			int deep = entity.getEntityIdentifier().depth();
			if (deep > maxdepth) {
				maxdepth = deep;
				deepest = entity;
			}
		}
		primaries[oid] = deepest;
		for (list<Snapshot::EntitiesTable::iterator>::iterator j = toBeRemoved.begin(); j != toBeRemoved.end(); ++j)
			etable.erase(*j);
	}

	set<EntityIdentifier> keepThese;
	list<Entity> queue;
	for (set<Identifier>::iterator k = keys.begin(); k != keys.end(); ++k)
	{
		const Identifier& oid = *k;
		pair<Snapshot::EntitiesTable::iterator, Snapshot::EntitiesTable::iterator> aliases = etable.equal_range(oid);
		for (Snapshot::EntitiesTable::iterator j = aliases.first; j != aliases.second; ++j)
		{
			Entity& e = j->second;
			EntityIdentifier ceid = e.getContainer();
			if (ceid == EntityIdentifier::nullId()) {
				queue.push_back(e);
			}
		}
	}
	while (queue.size() > 0) {
		Entity e = *(queue.begin());
		queue.pop_front();
		EntityIdentifier eid = e.getEntityIdentifier();
		keepThese.insert(eid);
		for (Entity::ComponentsList::iterator i = e.getComponents().begin(); i != e.getComponents().end(); ++i)
		{
			EntityIdentifier& ceid = *i;
			queue.push_back (unique[ceid]);
		}
	}

	toBeRemoved.clear();
	Snapshot::EntitiesTable trimmedEntities;
	for (set<Identifier>::iterator k = keys.begin(); k != keys.end(); ++k)
	{
		const Identifier& oid = *k;
		pair<Snapshot::EntitiesTable::iterator, Snapshot::EntitiesTable::iterator> aliases = etable.equal_range(oid);
		for (Snapshot::EntitiesTable::iterator it = aliases.first; it != aliases.second; ++it)
		{
			Entity& entity = it->second;
			if (keepThese.count(entity.getEntityIdentifier()) == 0) {
				toBeRemoved.push_back (it);
			}
		}
	}
	for (list<Snapshot::EntitiesTable::iterator>::iterator j = toBeRemoved.begin(); j != toBeRemoved.end(); ++j)
		etable.erase(*j);
	snap->getEntities().swap (trimmedEntities);

	for (set<Identifier>::iterator k = keys.begin(); k != keys.end(); ++k)
	{
		const Identifier& oid = *k;
		pair<Snapshot::EntitiesTable::iterator, Snapshot::EntitiesTable::iterator> aliases = etable.equal_range(oid);
		Entity primary = primaries[oid];
		for (Snapshot::EntitiesTable::iterator it = aliases.first; it != aliases.second; ++it)
		{
			Entity& e = it->second;
			if (e == primary)
			{
				for (Entity::ConnectionsList::iterator ci = e.getConnections().begin(); ci != e.getConnections().end(); ++ci)
				{
					Connector& conn = *ci;
					EntityIdentifier destID = conn.getDestination();
					if (!(destID == EntityIdentifier::nullId()))
					{
						EntityIdentifier primaryDest = primaries[destID.getObjectIdentifier()].getEntityIdentifier();
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

