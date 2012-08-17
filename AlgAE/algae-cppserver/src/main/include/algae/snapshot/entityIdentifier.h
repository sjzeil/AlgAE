/**
 * entityIdentifier.h
 *
 *
 *  Created on: June 17, 2012
 *      Author: zeil
 */

#ifndef ENTITYIDENTIFIER_H_
#define ENTITYIDENTIFIER_H_

#include <iostream>
#include <string>

#include <algae/snapshot/identifier.h>

namespace algae {

/**
 * Unique identifiers for each entity in the model.
 *
 * One object may map onto several distinct entities to support the
 * illusion that a given object is simultaneously a component of
 * multiple compound objects (or comprises multiple distinct components
 * of a single parent: e.g., arrays of Strings or Integers may contain
 * many duplicate references to one object, but we still prefer to render
 * these as if they were distinc.)
 *
 *  Consequently, the Entity identifier contains the object identifier
 *  but also encodes the parent container and a label or other component
 *  indicator.
 *
 * @author zeil
 *
 */

class EntityIdentifier  {

private:
	/**
	 * The objects immediately contained within this one
	 */

	Identifier id;
	const EntityIdentifier* container;
	std::string componentLabel;

public:
	/**
	 * For objects that are not components of larger objects
	 *
	 * @param id  object identifier\
	 * @param label decorative label/name used for display purposes but is ignored
	 * when doing comparisons
	 */
	EntityIdentifier (const Identifier& oid, std::string label = std::string());



	/**
	 * Used for XML decoding only
	 *
	 */
	EntityIdentifier ();


	/**
	 * For objects that are components of larger objects
	 *
	 * @param id  object identifier
	 * @param container object that contains this one as a component
	 * @param label name that distinguishes this component from others of the same parent
	 *
	 */
	EntityIdentifier (const Identifier& oid, const EntityIdentifier& theContainer, std::string label = std::string());


	EntityIdentifier (const EntityIdentifier& eid);

	EntityIdentifier& operator= (const EntityIdentifier& eid);

	~EntityIdentifier();


	/**
	 * The special ID value used for null pointers
	 *
	 */
	static EntityIdentifier nullId() {
		return EntityIdentifier(Identifier::NullID);
	}


	/**
	 * 	Return a string that differentiates this component from other components of
	 *    the same container. May be null for objects that are not components of others.
	 *
	 */
	std::string getComponentLabel() const {
		return componentLabel;
	}

	void setComponentLabel(std::string label) {
		componentLabel = label;
	}



	/**
	 * The identifier of the object denoted by this entity
	 * @return
	 */
	const Identifier& getObjectIdentifier() const {
		return id;
	}

	void setObjectIdentifier(const Identifier& ident) {
		id = ident;
	}

	/**
	 * The identifier of the entity of which this one is a component
	 *
	 * @return
	 */
	const EntityIdentifier* getContainer() const {
		return container;
	}

	void setContainer(const EntityIdentifier* c) {
		container = c;
	}



	/**
	 * How many nested containers is this considered to be a component of?
	 * @return
	 */
	int depth() const;


	void printXML (std::ostream& out) const;
	void print (std::ostream& out) const;


	bool operator== (const EntityIdentifier& eid) const;


	bool operator< (const EntityIdentifier& eid) const;

};


std::ostream& operator<< (std::ostream& out, const EntityIdentifier& eid);

}
#endif
