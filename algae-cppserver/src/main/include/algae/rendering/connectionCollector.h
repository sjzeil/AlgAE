/**
 * connectionCollector.h
 *
 *
 *  Created on: June 26, 2012
 *      Author: zeil
 */

#ifndef CONNECTIONCOLLECTOR_H_
#define CONNECTIONCOLLECTOR_H_

#include <algae/snapshot/color.h>
#include <algae/rendering/connection.h>


namespace algae {

class Renderer;
class TypeRenderer;
class Connection;

class ConnectionCollector
{
protected:
	int counter;
	double elasticity;
	double defaultLength;
	Color color;
public:
	ConnectionCollector()
	: counter(0), elasticity(1.0), defaultLength(1.0), color(Color::Black)
	{}


	virtual ~ConnectionCollector() {}

	template <typename T>
	void add (const T* destination,
			double theMinAngle = 0.0, double theMaxAngle=360.0,
			int component = -1,
			std::string label=std::string());

	void setColor (Color c) {color = c;}
	void setElasticity (double e) {elasticity = e;}
	void setDefaultLength (double len) {defaultLength = len;}

	virtual void addConnection (const Connection& conn) = 0;



};


}



#endif
