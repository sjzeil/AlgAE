/**
 * identifier.cpp
 *
 * Unique identifiers for any value in memory.
 *
 * Identifiers also carry info about the type of that value so that
 * a value can be properly rendered, given only its identifier.
 *
 *  Created on: June 28, 2012
 *      Author: zeil
 */

#include <algae/snapshot/identifier.h>
#include <algae/communications/xmlOutput.h>
#include <algae/rendering/typeRenderer.h>

#include <iostream>

using namespace std;

namespace algae {

Identifier::~Identifier()
{
	if (type != 0)
	{
		delete type;
	}
}

Identifier::Identifier (const Identifier& ident)
: id(ident.id), type(ident.type)
{
	if (type != 0)
	{
		type = type->cloneTR();
	}
}

Identifier& Identifier::operator= (const Identifier& ident)
{
	if (this != &ident)
	{
		id = ident.id;
		if (type != 0)
		{
			delete type;
		}
		type = ident.type;
		if (type != 0)
		{
			type = type->cloneTR();
		}
	}
	return *this;
}




void Identifier::printXML (std::ostream& out) const
{
	ObjectTag obj (out, XMLTag::snapshotPackage + ".RemoteIdentifier");
	{
		PropertyTag pt (out, "id");
		unsigned long value = (unsigned long)(id);
		LongValue tag2 (out, value);
		pt.close();
	}
	obj.close();
}


}
