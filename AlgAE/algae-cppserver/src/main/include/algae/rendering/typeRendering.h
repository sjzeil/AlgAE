/**
 * typeRendering.h
 *
 *  Sets up rendering rules for common types and provides a mechanism for
 *  extending the set of renderings to new types.
 *
 *  Created on: June 27, 2012
 *      Author: zeil
 */

#ifndef TYPERENDERING_H_
#define TYPERENDERING_H_

#include <algae/rendering/typeRenderer.h>
#include <algae/rendering/selfRenderer.h>
#include <algae/rendering/proxyForSelfRenderer.h>
#include <algae/rendering/defaultTypeRenderer.h>
#include <algae/rendering/specialization.h>

#include <algae/snapshot/identifier.h>

namespace algae {



/**
 * TypeRendering is a traits-like factory for obtaining a type renderer for
 * an arbitrary object.  Most types will specialize this with specific renderings.
 */


/**
 * General case: use the default renderer
 */
template <typename T, typename = void>
class TypeRendering {
public:
	const TypeRenderer* getRenderer (const T& anObject) const
	{
		return new DefaultTypeRenderer<T>(anObject);
	}
};




/**
 * Specialization for rendering of types that can render themselves
 */
template <typename T>
class TypeRendering<anySubClassOf(T, SelfRenderer)> {
public:
	const TypeRenderer* getRenderer (const SelfRenderer& anObject) const
	{
		return new ProxyForSelfRenderer(anObject);
	}
};


/**
 * One easy way for a type renderer to be associated with a type is
 * to inherit from this class and provide a function that points to
 * a usable renderer.
 */
class CanBeRendered {
public:
	virtual ~CanBeRendered() {}
	virtual const TypeRenderer* getTypeRenderer() const = 0;
};


/**
 * Specialization for rendering of types that specialize CanBeRendered
 */
template <typename T>
class TypeRendering<anySubClassOf(T, CanBeRendered)> {
public:
	const TypeRenderer* getRenderer (const CanBeRendered& anObject) const
	{
		return anObject.getTypeRenderer();
	}
};




/////////////////////////////////////////////////////////////

template <typename T>
const TypeRenderer* TypeRenderer::typeRenderer(const T& t)
{
	return TypeRendering<T>().getRenderer(t);
}

template <class T>
Identifier::Identifier (const T& t)
  : id(&t), type(TypeRenderer::typeRenderer(t))
{}

}

#endif
