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


namespace algae {


template <typename T>
void ComponentCollector::add (const T& t, std::string label)
{
	addComponent (Identifier(t), label);
}

template <typename T>
void ConnectionCollector::add (const T* destination,
		double theMinAngle, double theMaxAngle,
		int component,
		std::string label,
		std::string ident)
{
	if (destination != 0)
	{
		addConnection (Identifier(*destination),
			theMinAngle, theMaxAngle,
			component,
			label,
			ident	);
	}
	else
	{
		addConnection (Identifier::NullID,
			theMinAngle, theMaxAngle,
			component,
			label,
			ident	);
	}
}

}

#endif
