/**
 * color.h
 *
 *
 *  Created on: June 19, 2012
 *      Author: zeil
 */

#ifndef COLOR_H_
#define COLOR_H_

#include <iostream>

namespace algae {

struct Color
{
	unsigned char red;
	unsigned char green;
	unsigned char blue;
	unsigned char alpha;

	Color (unsigned char r, unsigned char g, unsigned char b, unsigned char a = 0)
	: red(r), green(g), blue(b), alpha(a)
	{}

	static const Color Black;
	static const Color White;

	static const Color Blue;
	static const Color Cyan;
	static const Color Green;
	static const Color Magenta;
	static const Color Red;
	static const Color Yellow;

	static const Color PaleBlue;
	static const Color PaleCyan;
	static const Color PaleGreen;
	static const Color PaleMagenta;
	static const Color PaleRed;
	static const Color PaleYellow;

	static const Color DarkGray;
	static const Color MedGray;
	static const Color LightGray;

	void printXML (std::ostream& out) const;

	bool operator== (const Color& c) const
	{
		return red == c.red && blue == c.blue
				&& green == c.green && alpha == c.alpha;
	}

};

std::ostream& operator<< (std::ostream& out, const Color& c);

}

#endif
