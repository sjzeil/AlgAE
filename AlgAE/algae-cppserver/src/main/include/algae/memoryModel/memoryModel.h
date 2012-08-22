/**
 * memoryModel.h
 *
 *
 *  Created on: June 17, 2012
 *      Author: zeil
 */

#ifndef MEMORYMODEL_H_
#define MEMORYMODEL_H_

#include <list>
#include <set>
#include <string>
#include <typeinfo>

#include <algae/memoryModel/activationStack.h>
#include <algae/memoryModel/component.h>
#include <algae/snapshot/entityIdentifier.h>
#include <algae/snapshot/identifier.h>
#include <algae/rendering/renderer.h>
#include <algae/snapshot/sourceLocation.h>



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


namespace algae {


class Animation;
class Entity;
class Snapshot;

class MemoryModel
{
private:
	ActivationStack activationStack;
	Animation& animation;


	/**
	 * All known objects in the code being animated, indexed by unique Identifiers
	 * assigned by this class.
	 */
	std::set<Identifier> knownObjects;


public:

	MemoryModel (Animation& context);



	/**
	 * Try to find a known identifier for this object. If none is found, register
	 * a new, arbitrary identifier for the purpose.
	 *
	 * @param c
	 * @return the unique ID for this object
	 */
	template <typename Object>
	static Identifier getIdentifierFor(const Object& obj, Animation& context);

	template <typename Object>
	static Identifier getIdentifierFor(const Object* obj, Animation& context);



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
	 *   5) default rendering (displays tostd::string () with no components or connections)
	 *
	 * @param obj
	 * @return a list of renderers, in the order they should be consulted.
	 */
	template <typename T>
	Renderer* getRenderer(const T& obj);


	/**
	 * Establish a rendering for all objects of the indicated class.
	 * Note that there are several ways to establish renderings, and that
	 * these are resolved as describedi getRenderer(), above.
	 *
	 * If a prior rendering has been established for this class, it is replaced by this call.
	 * Unlike object renderings, class renderings are "global" and do not lose effect when
	 * we return from an activation.
	 *
	 */
	template <typename T>
	void render(const T* type, const Renderer& newRendering);



	/**
	 * Show a variable as a global value in all displays.
	 * Variables portrayed by this call are shown "in-line".
	 *
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 */
	template <typename Object>
	void globalVar(std::string label, const Object& value)
	{
		ActivationRecord* arec = *(activationStack.begin());
		arec->var(label, value);
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
	template <typename Object>
	void globalRefVar (std::string  label, const Object& value)
	{
		ActivationRecord& arec = *(activationStack.begin());
		arec.refVar(label, value);
	}



	/**
	 * Iterators over global variables
	 */

	typedef ActivationRecord::const_iterator const_iterator;
	typedef ActivationRecord::iterator iterator;

	const_iterator beginGlobals() const {return (*activationStack.begin())->beginLocals();}
	iterator beginGlobals() {return (*activationStack.begin())->beginLocals();}

	const_iterator endGlobals() const {return (*activationStack.begin())->endLocals();}
	iterator endGlobals() {return (*activationStack.begin())->endLocals();}



	Animation& context() {
		return animation;
	}

	Snapshot* renderInto(std::string description, SourceLocation sourceLocation);

	/**
	 * @return the activationStack
	 */
	ActivationStack& getActivationStack() {
		return activationStack;
	}

	/**
	 * @return the activationStack
	 */
	const ActivationStack& getActivationStack() const {
		return activationStack;
	}




private:

	/**
	 * Guards against infinite recursion in the componentOf relation
	 */
	static const int DepthLimit = 25;


	class InternalComponent {
	public:
		EntityIdentifier container;
		Identifier component;
		std::string label;

		InternalComponent (EntityIdentifier acontainer, Identifier acomponent, std::string aLabel = std::string())
		{
			container = acontainer;
			component = acomponent;
			label = aLabel;
		}

		void print (std::ostream& out) const
		{
			out << "IC[" << component << "]@" << container;
			if (label.size() > 0L)
				out << ":" << label;
		}

	};

	/**
	 * Adds to the snapshot all objects that can be reached in one or more
	 * steps along the component and connection from the activation stack. This
	 * will include all global variables, parameters and locals of the current call,
	 * and non-ref parameters of the older calls.
	 *
	 * @param ob
	 * @param tobeProcessed
	 */
	void formClosure(Snapshot* snap);

	// Create an entity describing the rendering of this component;
	void renderObject(Entity& entity, std::list<InternalComponent>& queue);


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
	void normalize(Snapshot* snap);


	std::string toString(int i) const;


};


}


#endif
