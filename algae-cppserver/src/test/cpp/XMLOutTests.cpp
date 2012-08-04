#include <algae/snapshot/color.h>
#include <algae/snapshot/entity.h>
#include <algae/snapshot/identifier.h>
#include <algae/rendering/rendering.h>
#include <algae/communications/xmlOutput.h>

#include <cstdlib>
#include <cstdio>
#include <map>
#include <string>
#include <fstream>

#include "gtest/gtest.h"
#include "xmlTester.h"

namespace {

using namespace std;
using namespace algae;



TEST (XMLOutTests, Identifier0) {
	Identifier id;
	XMLTester xml;
	xml.printToXML(id);
	xml.readXML();
	EXPECT_EQ ("RemoteIdentifier", xml.fields["Class"]);
}

TEST (XMLOutTests, Identifier1) {
	int k = 42;
	Identifier id (k);
	XMLTester xml;
	xml.printToXML(id);
	xml.readXML();
	EXPECT_EQ ("RemoteIdentifier", xml.fields["Class"]);
}



}  // namespace



