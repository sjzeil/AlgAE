/**
 * componentCollector.h
 *
 *
 *  Created on: June 26, 2012
 *      Author: zeil
 */

#ifndef COMPONENTCOLLECTOR_H_
#define COMPONENTCOLLECTOR_H_

#include <string>
#include <algae/snapshot/identifier.h>


namespace algae {

class Renderer;

class TypeRenderer;

class ComponentCollector
{
public:
	virtual ~ComponentCollector() {}

	template <typename T>
	void add (const T& t, std::string label=std::string());

	virtual void addComponent (const Identifier& t, std::string label) = 0;

};





}

#endif
