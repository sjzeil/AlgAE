/**
 * renderer.cpp
 *
 *
 *  Created on: June 26, 2012
 *      Author: zeil
 */


#include <string>

#include <algae/snapshot/entity.h>
#include <algae/rendering/connection.h>
#include <algae/rendering/componentCollector.h>
#include <algae/rendering/connectionCollector.h>
#include <algae/rendering/renderer.h>
#include <algae/rendering/typeRenderer.h>


using namespace std;

namespace algae {


class EntityCollector: public ComponentCollector, public ConnectionCollector {
	Entity& e;
public:
	EntityCollector (Entity& entity)
	: e(entity) {}

	virtual void addComponent (const Identifier& t, std::string label);

	virtual void addConnection (
				const Identifier& t,
				double theMinAngle = 0.0, double theMaxAngle=360.0,
				int component = -1,
				std::string label=std::string(),
				std::string ident=std::string());
};

void EntityCollector::addComponent (const Identifier& t, std::string label)
{
	e.getComponents().push_back(EntityIdentifier(t, e.getEntityIdentifier(), label));
}

void EntityCollector::addConnection (
			const Identifier& destID,
			double theMinAngle, double theMaxAngle,
			int component,
			std::string label,
			std::string ident)
{
	EntityIdentifier destEID (destID);
	Connector c (ident, e.getEntityIdentifier(), destEID, theMinAngle, theMaxAngle, component);
	c.setColor(Color::Black);
	c.setLabel(label);
	e.getConnections().push_back(c);
}




/**
 * Apply this renderer to portray an in-memory object as an Entity
 */
void Renderer::renderInto (Entity& e) const
{
	e.setValue(getValue());
	e.setColor(getColor());
	e.setMaxComponentsPerRow(getMaxComponentsPerRow());
	EntityCollector coll (e);
	getComponents(coll);
	getConnections(coll);
}


}
