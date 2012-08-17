/**
 * colorChanger.h
 *
 *  An object renderer used to effect color changes in individual objects
 *
 *  Created on: Aug 13, 2012
 *      Author: zeil
 */

#ifndef COLORCHANGER_H_
#define COLORCHANGER_H_

#include <string>

#include <algae/snapshot/color.h>
#include <algae/rendering/objectRenderer.h>


namespace algae {


class ColorChanger: public ObjectRenderer
{
	Color c;
public:
	ColorChanger (const Identifier& renderingOf, Color newColor, const Renderer* deferringTo = 0)
	: ObjectRenderer(renderingOf, deferringTo), c(newColor) {}


	/**
	 * What color will be used to draw this object?
	 *
	 * @return a color or null to yield to other renderers
	 */
	virtual Color getColor() const {return c;}


	virtual Renderer* clone() const
	{
		return new ColorChanger(*this);
	}


};



}

#endif
