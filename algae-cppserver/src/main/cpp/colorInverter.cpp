/**
 * colorInverer.cpp
 *
 *  An object renderer used to change the color of an object to a complementary color
 *    (generally used in highlighting)
 *
 *  Created on: Aug 13, 2012
 *      Author: zeil
 */


#include <algae/memoryModel/activationStack.h>
#include <algae/memoryModel/memoryModel.h>
#include <algae/rendering/colorInverter.h>

using namespace std;

namespace algae {



/**
 * What color will be used to draw this object?
 *
 * @return a color or null to yield to other renderers
 */
Color ColorInverter::getColor() const
{
	Color oldColor = deferTo()->getColor();
	Color c (256-oldColor.red, 256-oldColor.green, 256-oldColor.blue, oldColor.alpha);
	if (c == oldColor)
		return Color::Yellow;
	else
		return c;
}


Renderer* ColorInverter::clone() const
{
	return new ColorInverter(*this);
}


}

