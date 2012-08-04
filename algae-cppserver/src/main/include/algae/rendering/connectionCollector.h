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

class ConnectionCollector
{
public:
	virtual ~ConnectionCollector() {}

	template <typename T>
	void add (const T* destination,
			double theMinAngle = 0.0, double theMaxAngle=360.0,
			int component = -1,
			std::string label=std::string(),
			std::string ident=std::string());

protected:
	virtual void addConnection (
			const Identifier& t,
			double theMinAngle = 0.0, double theMaxAngle=360.0,
			int component = -1,
			std::string label=std::string(),
			std::string ident=std::string()) = 0;

};


}



#endif
