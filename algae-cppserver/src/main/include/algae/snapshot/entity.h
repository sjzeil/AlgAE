/**
 * entity.h
 *
 *  An entity represents an object in memory as a labeled box that can contain and connect 
 *  to other entities
 *  
 *  Created on: June 23, 2012
 *      Author: zeil
 */

#ifndef ENTITY_H
#define ENTITY_H

#include <iostream>
#include <list>
#include <string>

#include <algae/snapshot/color.h>

#include <algae/snapshot/connector.h>
#include <algae/snapshot/entityIdentifier.h>
#include <algae/snapshot/identifier.h>


namespace algae {


class Entity  {
private:

	/**
	 * The objects immediately contained within this one
	 */
	std::list<EntityIdentifier> components;
	
	/**
	 * The objects that this one points to
	 */
	std::list<Connector> connections;

	

	EntityIdentifier entityIdentifier;
	std::string label;
	std::string value;
	Color color;
	int maxComponentsPerRow;

public:

	typedef std::list<EntityIdentifier> ComponentsList;
	typedef std::list<Connector> ConnectionsList;

	
	/**
	 * Create an entity representing a named "standalone" object
	 * that is not a component of some larger entity
	 * @param id    object to be represented by this new entity
	 * @param label a descriptive name for this entity
	 */
	Entity (const Identifier& id, std::string theLabel = std::string());

	
	/**
	 * Create an entity representing an object that is a component
	 * of a larger entity.
	 * 
	 * @param id   object to be represented by this new entity
	 * @param container  entity that is considered to contain this one as a component
	 * @param componentLabel  a string differentiating this component from all others of the same container
	 */
	Entity (const Identifier& id, const EntityIdentifier& container, std::string componentLabel);
	
	/**
	 * Create an entity
	 * @param entityIdentifier    object to be represented by this new entity
	 */
	Entity ();

	
	std::string getDescription() const;

	const Identifier& getObjectIdentifier() const {
		return entityIdentifier.getObjectIdentifier();
	}

	EntityIdentifier getContainer() const {
		return entityIdentifier.getContainer();
	}

		
	void printXML (std::ostream& out) const;
	void print (std::ostream& out) const;

	
	bool operator== (const Entity& e) const;
	

	/**
	 * @return the entityIdentifier
	 */
	const EntityIdentifier& getEntityIdentifier() const {
		return entityIdentifier;
	}

	/**
	 * @param entityIdentifier the entityIdentifier to set
	 */
	void setEntityIdentifier(const EntityIdentifier& anEntityIdentifier) {
		entityIdentifier = anEntityIdentifier;
	}

	/**
	 * @return the label
	 */
	std::string getLabel() const {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	void setLabel(std::string theLabel) {
		label = theLabel;
	}

	/**
	 * @return the value
	 */
	std::string getValue() const {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	void setValue(std::string aValue) {
		value = aValue;
	}

	/**
	 * @return the color
	 */
	Color getColor() const {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	void setColor(Color aColor) {
		color = aColor;
	}

	/**
	 * @return the maxComponentsPerRow
	 */
	int getMaxComponentsPerRow() {
		return maxComponentsPerRow;
	}

	/**
	 * @param maxComponentsPerRow the maxComponentsPerRow to set
	 */
	void setMaxComponentsPerRow(int maxComponentsPerRow0) {
		maxComponentsPerRow = maxComponentsPerRow0;
	}

	/**
	 * @return the components
	 */
	const ComponentsList& getComponents() const {
		return components;
	}

	/**
	 * @return the components
	 */
	ComponentsList& getComponents() {
		return components;
	}



	/**
	 * @return the connections
	 */
	const ConnectionsList& getConnections() const{
		return connections;
	}

	/**
	 * @return the connections
	 */
	ConnectionsList& getConnections() {
		return connections;
	}

	/**
	 * @param connections the connections to set
	 */
	void setConnections(const ConnectionsList& connections0) {
		connections = connections0;
	}
	
	
	

};


std::ostream& operator<< (std::ostream& out, const Entity& e);


}
#endif
