#include <algae/snapshot/snapshot.h>
#include <algae/communications/xmlOutput.h>

#include <algorithm>
#include <string>

#include "gtest/gtest.h"



namespace {

using namespace std;
using namespace algae;

// The fixture for testing class Foo.
class SnapshotTests : public ::testing::Test {
 protected:
	  int i1, i2, i3, i4;

	  Identifier id1;
	  Identifier id2;
	  Identifier id3;

	  Entity entity1a;
	  Entity entity1b;
	  Entity entity2;
	  Entity entity3;

	  Snapshot snap1;


	SnapshotTests()
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

  virtual ~SnapshotTests()
  {
  }

  virtual void SetUp() {
	  snap1 = Snapshot();
	  initSnap(snap1);
  }

  virtual void TearDown() {
  }

  void initSnap(Snapshot& snap)
  {
	  snap.add(entity1a);
	  snap.add(entity2);
	  snap.setGlobal(entity2.getEntityIdentifier(), true);
	  snap.add(entity1b);
	  snap.add(entity3);
	  snap.setActivationStack(entity3.getEntityIdentifier());
	  snap.setDescriptor("a breakpoint");
	  snap.setBreakpointLocation(SourceLocation("foo.java", 15));
  }

  bool canFind (const Entity& e0)
  {
	  for (Snapshot::iterator it = snap1.begin(); it != snap1.end(); ++it)
		  if (e0 == *it)
			  return true;
	  return false;
  }


};







	/**
	 * Test method for {@link edu.odu.cs.AlgAE.Common.Snapshot.Snapshot#add(edu.odu.cs.AlgAE.Common.Snapshot.Entity, boolean)}.
	 */
TEST_F (SnapshotTests, Add) {
		EXPECT_EQ (2U, snap1.getEntities().count(entity1a.getEntityIdentifier().getObjectIdentifier()));
		EXPECT_EQ (2U, snap1.getEntities().count(entity1b.getEntityIdentifier().getObjectIdentifier()));
		EXPECT_EQ (1U, snap1.getEntities().count(entity2.getEntityIdentifier().getObjectIdentifier()));
		EXPECT_EQ (1U, snap1.getEntities().count(entity3.getEntityIdentifier().getObjectIdentifier()));
		EXPECT_EQ (1U, snap1.getGlobals().count(entity2.getEntityIdentifier()));
		EXPECT_EQ (0U, snap1.getGlobals().count(entity1a.getEntityIdentifier()));
		EXPECT_EQ (0U, snap1.getGlobals().count(entity1b.getEntityIdentifier()));
		EXPECT_EQ (0U, snap1.getGlobals().count(entity3.getEntityIdentifier()));
		snap1.add (entity3);
		EXPECT_EQ (1U, snap1.getEntities().count(entity3.getEntityIdentifier().getObjectIdentifier()));
		EXPECT_TRUE(canFind(entity1a));
		EXPECT_TRUE(canFind(entity1b));
		EXPECT_TRUE(canFind(entity2));
		EXPECT_TRUE(canFind(entity3));
	}

	/**
	 * Test method for {@link edu.odu.cs.AlgAE.Common.Snapshot.Snapshot#remove(edu.odu.cs.AlgAE.Common.Snapshot.Entity)}.
	 */
TEST_F (SnapshotTests, Remove) {
		snap1.remove(entity1b);
		EXPECT_EQ (1U, snap1.getEntities().count(entity1a.getEntityIdentifier().getObjectIdentifier()));
		EXPECT_EQ (1U, snap1.getEntities().count(entity1b.getEntityIdentifier().getObjectIdentifier()));
		EXPECT_EQ (1U, snap1.getEntities().count(entity2.getEntityIdentifier().getObjectIdentifier()));
		EXPECT_EQ (1U, snap1.getEntities().count(entity3.getEntityIdentifier().getObjectIdentifier()));
		snap1.remove(entity2);
		EXPECT_EQ (0U, snap1.getGlobals().count(entity2.getEntityIdentifier()));
		EXPECT_TRUE(canFind(entity1a));
		EXPECT_FALSE(canFind(entity1b));
		EXPECT_FALSE(canFind(entity2));
		EXPECT_TRUE(canFind(entity3));
	}

	/**
	 * Test method for {@link edu.odu.cs.AlgAE.Common.Snapshot.Snapshot#equals(edu.odu.cs.AlgAE.Common.Snapshot.Entity)}.
	 */
TEST_F (SnapshotTests, Equals) {
		Snapshot snap0;
		initSnap(snap0);

		EXPECT_EQ (snap0, snap1);
		snap1.remove(entity2);
		EXPECT_NE (snap0, snap1);

		Snapshot snap2;
		initSnap(snap2);
		EXPECT_EQ (snap0, snap2);
		snap2.setDescriptor(snap2.getDescriptor() + "x");
		EXPECT_NE (snap2, snap0);

	}






}  // namespace



