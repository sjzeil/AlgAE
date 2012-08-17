/**
 * colorInverter.h
 *
 *  An object renderer used to change the color of an object to a complementary color
 *    (generally used in highlighting)
 *
 *  Created on: Aug 13, 2012
 *      Author: zeil
 */

#ifndef COLORINVERTER_H_
#define COLORINVERTER_H_

#include <string>

#include <algae/snapshot/color.h>
#include <algae/rendering/objectRenderer.h>


namespace algae {


class ColorInverter: public ObjectRenderer
{
public:
	ColorInverter (const Identifier& renderingOf, const Renderer* deferringTo = 0)
	: ObjectRenderer(renderingOf, deferringTo) {}


	/**
	 * What color will be used to draw this object?
	 *
	 * @return a color or null to yield to other renderers
	 */
	virtual Color getColor() const;

	virtual Renderer* clone() const;

};



}

#endif
