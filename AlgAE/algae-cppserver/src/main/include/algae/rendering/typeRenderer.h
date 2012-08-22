/**
 * typeRenderer.h
 *
 *
 *  Created on: June 26, 2012
 *      Author: zeil
 */

#ifndef TYPERENDERER_H_
#define TYPERENDERER_H_

#include <list>
#include <string>
#include <sstream>
#include <typeinfo>

#include <algae/snapshot/color.h>
#include <algae/rendering/connection.h>
#include <algae/rendering/renderer.h>


namespace algae {

/**
 * Type renderers provide the basic rendering rules for all objects of some data type.
 * Unlike object renderers, type renderers never defer to other renderers. In fact, the type renderer
 * is often what the object renderers will defer to for the basic rendering rules.
 *
 */
class TypeRenderer: public Renderer
{
public:

	template <typename T>
	static const TypeRenderer* typeRenderer (const T& t);

	/**
	 * Convenience method for cloning type renderers
	 */
	TypeRenderer* cloneTR() const {return (TypeRenderer*)clone();}

};



/**
 * A Type renderer that keeps a pointer to the instance of the object is it supposed
 * to render.  Most programmer-defined type renderers would inherit from this class.
 */
template <typename T>
class TypeRendererOf: public TypeRenderer
{
protected:
	const T* instance;

public:
	TypeRendererOf (const T& t)
	: instance(&t) {}
};






}

#endif
