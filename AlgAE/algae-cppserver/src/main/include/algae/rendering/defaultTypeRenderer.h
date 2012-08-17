/**
 * typeRenderer.h
 *
 * The default type renderer: value is obtained by using operator<< to
 * write out the object being rendered. The color is randomly assigned
 * (but consistent for all objects of the same type). No components,
 * no connections.
 *
 *  Created on: June 26, 2012
 *      Author: zeil
 */

#ifndef DEFAULTTYPERENDERER_H_
#define DEFAULTTYPERENDERER_H_

#include <string>
#include <sstream>
#include <typeinfo>

#include <algae/snapshot/color.h>
#include <algae/rendering/connection.h>
#include <algae/rendering/renderer.h>
#include <algae/rendering/typeRenderer.h>


namespace algae {



/**
 */
template <typename T>
class DefaultTypeRenderer : public TypeRendererOf<T> {
public:

	DefaultTypeRenderer(const T& t)
	: TypeRendererOf<T>(t) {}

	virtual ~DefaultTypeRenderer() {}

	DefaultTypeRenderer(const DefaultTypeRenderer& dtr)
	 : TypeRendererOf<T>(dtr)
	{}

	/**
	 * What string will be used as the value of this object?
	 *
	 * @param obj: object to be drawn
	 * @return a string or null to yield to other renderers
	 */
	virtual std::string getValue() const
	{
		std::ostringstream out;
		out << *(this->instance);
		return out.str();
	}

	/**
	 * What color will be used to draw this object?
	 *
	 * @param obj: object to be drawn
	 * @return a color or null to yield to other renderers
	 */
	virtual Color getColor() const
	{
		std::string typeName = typeid(T).name();
		unsigned long sum = 0L;
		for (std::string::size_type i = 0; i < typeName.size(); ++i)
		{
			char c = typeName[i];
			sum += 113L * c;
		}
		unsigned red = sum % 256L;
		sum /= 256L;
		unsigned blue = sum % 256L;
		sum /= 256L;
		unsigned green = sum % 256L;
		return Color(red, blue, green);
	}

	/**
	 * Get a list of other objects to be drawn inside the
	 * box portraying this one.
	 *
	 * @param obj: object to be drawn
	 *
	 * @return an array of contained objects or null to yield to other renderers
	 */
	virtual void getComponents(ComponentCollector& components) const {}

	/**
	 * Get a list of other objects to which we will draw
	 * pointers from this one.
	 *
	 * @param obj: object to be drawn
	 *
	 * @return an array of referenced objects or null to yield to other renderers
	 */
	virtual void getConnections(ConnectionCollector& connections) const {}


	/**
	 * Indicates how components will be laid out within the box
	 * representing this object.  A return value of 1 will force all
	 * components to be laid out in a single vertical column. Larger
	 * return values will permit a more horizontal layout.
	 *
	 * A zero value requests that components be laid out in a (more or less) minimal area.
	 *
	 * @param obj
	 * @return max #components per row or a negative value to yield to other renderers
	 */

	virtual int getMaxComponentsPerRow() const {return 1;}


	virtual Renderer* clone() const {return new DefaultTypeRenderer<T>(*(this->instance));}

};

}

#endif
