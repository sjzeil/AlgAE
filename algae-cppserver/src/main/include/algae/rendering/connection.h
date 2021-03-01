/**
 * connection.h
 *
 *  Connections represent pointers or references to some entity and/or object in memory.
 *  
 *  Created on: June 23, 2012
 *      Author: zeil
 */

#ifndef CONNECTION_H_
#define CONNECTION_H_

#include <iostream>
#include <string>


#include <algae/snapshot/entityIdentifier.h>
#include <algae/snapshot/color.h>

namespace algae {

class Connection
{
private:
	EntityIdentifier destination;
	double minAngle;
	double maxAngle;
	double preferredLength;
	double elasticity;
	std::string value;
	std::string label;
	Color color;
	std::string id;
	int componentIndex;
	
	friend class SimpleReference;

protected:
	Connection (std::string ident, const EntityIdentifier& theDestination,
			double theMinAngle=DefaultMinAngle, double theMaxAngle=DefaultMaxAngle,
			int component = NotAnInternalReference);

public:
	static const double DefaultMinAngle;       // 0.0
	static const double DefaultMaxAngle;       // 360.0
	static const double DefaultPreferredLength;
	static const double DefaultElasticity;
	static const int NotAnInternalReference;

	template <typename T>
	Connection (std::string ident, const T* theDestination,
			double theMinAngle=DefaultMinAngle, double theMaxAngle=DefaultMaxAngle,
			int component = NotAnInternalReference)
	: destination(EntityIdentifier(Identifier(*theDestination))), minAngle(theMinAngle),
		maxAngle(theMaxAngle), preferredLength (DefaultPreferredLength),
		elasticity(DefaultElasticity), value(""), label(""),
		color(Color::Black), id(ident), componentIndex(component)
	{
	}

	Connection ();

	
	
	void print (std::ostream& out) const;
	
	bool operator== (const Connection& c) const;

	/**
	 * @return the destination
	 */
	const EntityIdentifier& getDestination() const {
		return destination;
	}

	/**
	 * @param destination the destination to set
	 */
	void setDestination(const EntityIdentifier& theDestination) {
		destination = theDestination;
	}

	/**
	 * @return the minAngle
	 */
	double getMinAngle() const {
		return minAngle;
	}

	/**
	 * @param minAngle the minAngle to set
	 */
	void setMinAngle(double theMinAngle) {
		minAngle = theMinAngle;
	}

	/**
	 * @return the maxAngle
	 */
	double getMaxAngle() const {
		return maxAngle;
	}

	/**
	 * @param maxAngle the maxAngle to set
	 */
	void setMaxAngle(double theMaxAngle) {
		maxAngle = theMaxAngle;
	}

	/**
	 * @return the preferredLength
	 */
	double getPreferredLength() const {
		return preferredLength;
	}

	/**
	 * @param preferredLength the preferredLength to set
	 */
	void setPreferredLength(double thePreferredLength) {
		preferredLength = thePreferredLength;
	}

	/**
	 * @return the elasticity
	 */
	double getElasticity() const {
		return elasticity;
	}

	/**
	 * @param elasticity the elasticity to set
	 */
	void setElasticity(double theElasticity) {
		elasticity = theElasticity;
	}

	/**
	 * @return the value
	 */
	std::string getValue() const{
		return value;
	}

	/**
	 * @param value the value to set
	 */
	void setValue(std::string theValue) {
		value = theValue;
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
	 * @return the color
	 */
	Color getColor() const {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	void setColor(Color theColor) {
		color = theColor;
	}

	/**
	 * @return the id
	 */
	std::string getId() const {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	void setId(std::string ident) {
		id = ident;
	}

	/**
	 * @return the componentIndex
	 */
	int getComponentIndex() const {
		return componentIndex;
	}

	/**
	 * @param componentIndex the componentIndex to set
	 */
	void setComponentIndex(int theComponentIndex) {
		componentIndex = theComponentIndex;
	}


};

inline
std::ostream& operator<< (std::ostream& out, const Connection& c)
{
	c.print(out);
	return out;
}

}

#endif


