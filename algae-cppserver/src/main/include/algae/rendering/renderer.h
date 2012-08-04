/**
 * renderer.h
 *
 *
 *  Created on: June 17, 2012
 *      Author: zeil
 */

#ifndef RENDERER_H_
#define RENDERER_H_

#include <list>
#include <string>

#include <algae/snapshot/color.h>
#include <algae/snapshot/entity.h>
#include <algae/snapshot/identifier.h>
#include <algae/rendering/connection.h>
#include <algae/rendering/componentCollector.h>
#include <algae/rendering/connectionCollector.h>


namespace algae {


class Renderer
{
	const Renderer* prior;
	bool thisCanBeCopied;

public:
	Renderer (const Renderer* deferringTo = 0, bool copy = true)
	: prior(deferringTo), thisCanBeCopied(copy) {}

	virtual ~Renderer() {if (prior != 0 && prior->thisCanBeCopied) delete prior;}

	Renderer (const Renderer& r)
	: prior(r.prior), thisCanBeCopied(r.thisCanBeCopied)
	{
		if (prior != 0 && prior->thisCanBeCopied)
		{
			prior = prior->clone();
		}
	}

	Renderer& operator= (const Renderer& r)
	{
		if (this != &r)
		{
			if (prior != 0 && prior->thisCanBeCopied)
			{
				delete prior;
			}
			thisCanBeCopied = r.thisCanBeCopied;
			prior = r.prior;
			if (prior != 0 && prior->thisCanBeCopied)
			{
				prior = prior->clone();
			}
		}
		return *this;
	}


	virtual Renderer* clone() const = 0;



	/**
	 * What string will be used as the value of this object?
	 *
	 * @param obj: object to be drawn
	 * @return a string or null to yield to other renderers
	 */
	virtual std::string getValue() const = 0;

	/**
	 * What color will be used to draw this object?
	 *
	 * @param obj: object to be drawn
	 * @return a color or null to yield to other renderers
	 */
	virtual Color getColor() const = 0;

	/**
	 * Get a list of other objects to be drawn inside the
	 * box portraying this one.
	 *
	 * @param obj: object to be drawn
	 *
	 * @return an array of contained objects or null to yield to other renderers
	 */
	virtual void getComponents(ComponentCollector& components) const = 0;

	/**
	 * Get a list of other objects to which we will draw
	 * pointers from this one.
	 *
	 * @param obj: object to be drawn
	 *
	 * @return an array of referenced objects or null to yield to other renderers
	 */
	virtual void getConnections(ConnectionCollector& connections) const = 0;


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

	virtual int getMaxComponentsPerRow() const = 0;


	const Renderer* deferTo() const {return prior;}

	bool canBeCopied() const {return thisCanBeCopied;}
	void setCanBeCopied (bool copyable) {thisCanBeCopied = copyable;}


	/**
	 * Apply this renderer to portray an in-memory object as an Entity
	 */
	template <typename T>
	Entity render(const T& x, std::string label = std::string()) const;

private:
	void renderInto (Entity& e) const;


};

/**
 * Apply this renderer to portray an in-memory object as an Entity
 */
template <typename T>
Entity Renderer::render(const T& x, std::string label) const
{
	Identifier oid (x);
	Entity e (oid, label);
	renderInto (e);
	return e;
}




}

#endif
