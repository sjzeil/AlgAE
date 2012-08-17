/**
 * typeRenderer.h
 *
 *  Type renderers capture the default rendering rules for all objects of a
 *  given data type.
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
#include <algae/rendering/specialization.h>


namespace algae {


class TypeRenderer: public Renderer
{
public:
	TypeRenderer(bool canBeCopied = false)
	: Renderer(0, canBeCopied)  {}

	template <typename T>
	static const TypeRenderer* typeRenderer (const T& t);

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
	TypeRendererOf (const T& t, bool canBeCopied = true)
	: TypeRenderer(canBeCopied), instance(&t) {}
};



/**
 * A mix-in type renderer for objects that render themselves
 */
class SelfRenderer: public TypeRenderer
{
public:
	SelfRenderer ()
	: TypeRenderer(false) {}

	virtual Renderer* clone() const {return (Renderer*)this;}
};



}

#endif
