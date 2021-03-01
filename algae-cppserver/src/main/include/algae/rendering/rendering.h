/**
 * rendering.h
 *
 *
 *  Created on: June 17, 2012
 *      Author: zeil
 */

#ifndef RENDERING_H_
#define RENDERING_H_

#include <algae/rendering/componentCollector.h>
#include <algae/rendering/connectionCollector.h>
#include <algae/rendering/renderer.h>
#include <algae/rendering/typeRendering.h>

using namespace std;


namespace algae {


template <typename T>
void ComponentCollector::add (const T& t, std::string label)
{
	addComponent (Identifier(t), label);
}

template <typename T>
void ConnectionCollector::add (
		const T* destination,
		double theMinAngle, double theMaxAngle,
		int component,
		std::string label)
{
	++counter;
	char ch = ' ' + counter;
	string ident (1, ch);
	Connection c (ident, destination, theMinAngle, theMaxAngle, component);
	c.setLabel(label);
	addConnection (c);
}

}

#endif
