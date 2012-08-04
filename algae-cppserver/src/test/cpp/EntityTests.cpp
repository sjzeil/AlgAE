#include <algae/snapshot/color.h>
#include <algae/snapshot/entity.h>
#include <algae/snapshot/identifier.h>
#include <algae/rendering/rendering.h>
#include <algae/communications/xmlOutput.h>

#include <string>

#include "gtest/gtest.h"

#include "xmlTester.h"


namespace {

using namespace std;
using namespace algae;

// The fixture for testing class Foo.
class EntityTests : public ::testing::Test {
 protected:
	  int i1, i2, i3, i4;

	  Identifier id1;
	  Identifier id2;
	  Identifier id3;

	  Entity entity1a;
	  Entity entity1b;
	  Entity entity2;
	  Entity entity3;


	EntityTests()
	: i1(111), i2(222), i3(333), i4(444),
	  id1(i1), id2(i2), id3(i3),
	  entity1a(id1),
	  entity2(id2, "label2"),
	  //entity1b(id1, entity2, "componentA")
	  entity3(id3, "labeled")
	{
	    entity1b = Entity(id1, entity2, "componentA");
		entity2.getComponents().push_back(entity1b.getEntityIdentifier());
		entity2.setValue("entity2Value");
		entity3.getConnections().push_back(Connector("link", entity3.getEntityIdentifier(),
				entity2.getEntityIdentifier(), 0, 180));
	}

  virtual ~EntityTests()
  {
  }

  virtual void SetUp() {
  }

  virtual void TearDown() {
  }



};




TEST_F (EntityTests, Entity1) {
	EXPECT_EQ (id1, entity1a.getObjectIdentifier());
	EXPECT_EQ(NULL, entity1a.getContainer());
	EXPECT_EQ("", entity1a.getLabel());
	EXPECT_EQ("", entity1a.getValue());
	EXPECT_EQ(0U, entity1a.getComponents().size());
	EXPECT_EQ(0U, entity1a.getConnections().size());

	entity1a.setValue("aValue");
	entity1a.setColor(Color::White);

	EXPECT_EQ ("aValue", entity1a.getValue());
	EXPECT_EQ (Color::White, entity1a.getColor());

	XMLTester xml;
	xml.printToXML(entity1a);
	xml.readXML();
	EXPECT_EQ ("Entity", xml.fields["Class"]);
	EXPECT_EQ ("*null*", xml.fields["container"]);
	EXPECT_EQ ("", xml.fields["componentLabel"]);
	EXPECT_EQ ("aValue", xml.fields["value"]);
	EXPECT_NE (string::npos, xml.fields["color"].find("255"));
}

TEST_F (EntityTests, Entity2) {
	EXPECT_EQ (id2, entity2.getObjectIdentifier());
	EXPECT_EQ(NULL, entity2.getContainer());
	EXPECT_EQ("label2", entity2.getLabel());
	EXPECT_EQ("entity2Value", entity2.getValue());
	EXPECT_EQ(1U, entity2.getComponents().size());
	EXPECT_EQ(0U, entity2.getConnections().size());

	XMLTester xml (true);
	xml.printToXML(entity2);
	xml.readXML();
	EXPECT_EQ ("Entity", xml.fields["Class"]);
	EXPECT_EQ ("*null*", xml.fields["container"]);
	EXPECT_EQ ("", xml.fields["componentLabel"]);
	EXPECT_EQ ("entity2Value", xml.fields["value"]);
	EXPECT_EQ (string::npos, xml.fields["color"].find("255"));
}


TEST_F (EntityTests, Entity1b) {
	EXPECT_EQ (id1, entity1b.getObjectIdentifier());
	EXPECT_EQ(entity2.getEntityIdentifier(), *entity1b.getContainer());
	EXPECT_EQ("componentA", entity1b.getLabel());
	EXPECT_EQ("", entity1b.getValue());
	EXPECT_EQ(0U, entity1b.getComponents().size());
	EXPECT_EQ(0U, entity1b.getConnections().size());

	XMLTester xml;
	xml.printToXML(entity1b);
	xml.readXML();
	EXPECT_EQ ("Entity", xml.fields["Class"]);
	EXPECT_NE ("*null*", xml.fields["container"]);
	EXPECT_EQ ("", xml.fields["componentLabel"]);
	EXPECT_EQ ("", xml.fields["value"]);
}

TEST_F (EntityTests, Entity3) {
	EXPECT_EQ (id3, entity3.getObjectIdentifier());
	EXPECT_EQ(NULL, entity3.getContainer());
	EXPECT_EQ("labeled", entity3.getLabel());
	EXPECT_EQ("", entity3.getValue());
	EXPECT_EQ(0U, entity3.getComponents().size());
	EXPECT_EQ(1U, entity3.getConnections().size());

	XMLTester xml(true);
	xml.printToXML(entity3);
	xml.readXML();
	EXPECT_EQ ("Entity", xml.fields["Class"]);
	EXPECT_EQ ("*null*", xml.fields["container"]);
	EXPECT_EQ ("labeled", xml.fields["label"]);
	EXPECT_EQ ("", xml.fields["value"]);
}






}  // namespace



