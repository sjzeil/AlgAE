/**
 * connector.cpp
 *
 *  Connectors represent pointers or references from one entity to another.
 *  They differ from Connections in two ways:
 *  1) They record the source of the reference as well as the destination
 *  2) They can be rendered as XML
 *  
 *  Created on: June 29, 2012
 *      Author: zeil
 */


#include <iostream>
#include <string>


#include <algae/snapshot/connector.h>
#include <algae/communications/xmlOutput.h>

using namespace std;

namespace algae {


void Connector::printXML (std::ostream& out) const
{
	ObjectTag connector (out, XMLTag::snapshotPackage + ".Connector");
	{
		PropertyTag sourceTag (out, "source");
		source.printXML(out);
		sourceTag.close();
	}
	{
		PropertyTag destinationTag (out, "destination");
		getDestination().printXML(out);
		destinationTag.close();
	}
	{
		PropertyTag idTag (out, "id");
		{
			StringValue idv (out, getId());
		}
		idTag.close();
	}
	{
		PropertyTag pTag (out, "minAngle");
		{
			DoubleValue idv (out, getMinAngle());
		}
		pTag.close();
	}
	{
		PropertyTag pTag (out, "maxAngle");
		{
			DoubleValue idv (out, getMaxAngle());
		}
		pTag.close();
	}
	{
		PropertyTag pTag (out, "preferredLength");
		{
			DoubleValue idv (out, getPreferredLength());
		}
		pTag.close();
	}
	{
		PropertyTag pTag (out, "elasticity");
		{
			DoubleValue idv (out, getElasticity());
		}
		pTag.close();
	}
	{
		PropertyTag pTag (out, "value");
		{
			StringValue idv (out, getValue());
		}
		pTag.close();
	}
	{
		PropertyTag pTag (out, "label");
		{
			StringValue idv (out, getLabel());
		}
		pTag.close();
	}
	{
		PropertyTag pTag (out, "componentIndex");
		{
			IntValue idv (out, getComponentIndex());
		}
		pTag.close();
	}
	{
		PropertyTag colorTag (out, "color");
		getColor().printXML(out);
		colorTag.close();
	}
	connector.close();
}

bool Connector::operator== (const Connector& c) const
{
	return source == c.source && Connection::operator==(c);
}


}

