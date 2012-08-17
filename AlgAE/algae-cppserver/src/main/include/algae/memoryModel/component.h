/**
 * component.h
 *
 *
 *  Created on: June 17, 2012
 *      Author: zeil
 */

#ifndef COMPONENT_H_
#define COMPONENT_H_

#include <ostream>

namespace algae {

struct Component
{
	int tbd;
};

std::ostream& operator<< (std::ostream& out, const Component& c);

}

#endif
