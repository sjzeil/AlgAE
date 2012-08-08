#include <algae/snapshot/snapshotDiff.h>

#include <algorithm>
#include <string>

#include "gtest/gtest.h"
#include "xmlTester.h"



namespace {

using namespace std;
using namespace algae;

// The fixture for testing class Foo.
class SnapshotDiffTests : public ::testing::Test {
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


	SnapshotDiffTests()
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

  virtual ~SnapshotDiffTests()
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

  template <typename Container, typename T>
  bool contains (const Container& c, const T& t)
  {
	  return find(c.begin(), c.end(), t) != c.end();
  }

  int countOf (const string& target, const string& within)
  {
	  int count = 0;
	  string::size_type start = 0;
	  while ((start = within.find(target, start)) != string::npos)
	  {
		  ++count;
		  start += target.size();
	  }
	  return count;
  }


};







TEST_F (SnapshotDiffTests, EmptyConstructor) {
	Snapshot s0;
	SnapshotDiff d0 (s0, s0);
	string d = d0.getDescriptor();
	EXPECT_EQ (string(), d0.getDescriptor());
	EXPECT_EQ (0U, d0.getChangedEntities().size());
	EXPECT_EQ (0U, d0.getNewGlobals().size());
	EXPECT_EQ (0U, d0.getRemovedEntities().size());

	Snapshot s1 = d0.reconstruct(s0);
	EXPECT_EQ (s0, s1);

	XMLTester xml;
	xml.printToXML(d0);
	xml.readXML();
	EXPECT_EQ ("SnapshotDiff", xml.valueOf("Class"));
	EXPECT_EQ ("[]", xml.valueOf("removedentities"));
	EXPECT_EQ ("[]", xml.valueOf("changedentities"));
	EXPECT_EQ ("[]", xml.valueOf("newglobals"));
	EXPECT_EQ ("[]", xml.valueOf("newnonglobals"));
	EXPECT_EQ ("", xml.valueOf("descriptor"));
	EXPECT_EQ (":1", xml.valueOf("breakpointlocation"));
	EXPECT_NE (string::npos, xml.valueOf("activationstack").find("0"));
}


TEST_F (SnapshotDiffTests, ConstructorNullNon) {
	Snapshot s0;
	SnapshotDiff d0 (s0, snap1);
	EXPECT_EQ (snap1.getDescriptor(), d0.getDescriptor());
	EXPECT_EQ (snap1.getEntities().size(), d0.getChangedEntities().size());
	EXPECT_EQ (1U, d0.getNewGlobals().size());
	EXPECT_TRUE (contains(d0.getNewGlobals(), entity2.getEntityIdentifier()));
	EXPECT_EQ (0U, d0.getRemovedEntities().size());

	Snapshot s1 = d0.reconstruct(s0);
	EXPECT_EQ (snap1, s1);

	XMLTester xml;
	xml.printToXML(d0);
	xml.readXML();
	EXPECT_EQ ("SnapshotDiff", xml.valueOf("Class"));
	EXPECT_EQ ("[]", xml.valueOf("removedentities"));
	EXPECT_TRUE (countOf("eid:", xml.valueOf("changedentities")) >= 4);
	EXPECT_EQ (1, countOf("@", xml.valueOf("newglobals")));
	EXPECT_EQ (1, countOf("label2", xml.valueOf("newglobals")));
	EXPECT_EQ (snap1.getDescriptor(), xml.valueOf("descriptor"));
	EXPECT_EQ ("foo.java:15", xml.valueOf("breakpointlocation"));
	EXPECT_EQ (1, countOf("labeled",xml.valueOf("activationstack")));
}

TEST_F (SnapshotDiffTests, ConstructorNonNull) {
		SnapshotDiff d0 (snap1, Snapshot());
		EXPECT_EQ ("", d0.getDescriptor());
		EXPECT_EQ (0U, d0.getChangedEntities().size());
		EXPECT_EQ (0U, d0.getNewGlobals().size());
		EXPECT_TRUE (contains (d0.getNewNonGlobals(), entity2.getEntityIdentifier()));
		EXPECT_EQ (snap1.getEntities().size(), d0.getRemovedEntities().size());

		Snapshot s0;
		Snapshot s1 = d0.reconstruct(snap1);
		EXPECT_EQ (s0, s1);

		XMLTester xml;
		xml.printToXML(d0);
		xml.readXML();
		EXPECT_EQ ("SnapshotDiff", xml.valueOf("Class"));
		EXPECT_EQ ((int)snap1.getEntities().size()-1, countOf(",", xml.valueOf("removedentities")));
		EXPECT_EQ ("[]", xml.valueOf("changedentities"));
		EXPECT_EQ (1, countOf("@", xml.valueOf("newnonglobals")));
		EXPECT_EQ ("[]", xml.valueOf("newglobals"));
		EXPECT_EQ ("", xml.valueOf("descriptor"));
		EXPECT_EQ (":1", xml.valueOf("breakpointlocation"));
		EXPECT_NE (1, countOf("labeled",xml.valueOf("activationstack")));
	}



TEST_F (SnapshotDiffTests, EntityChange) {
		Snapshot empty;
		Snapshot s0 = snap1;


		Entity entity3b (entity3.getEntityIdentifier().getObjectIdentifier(), "labeled");
		entity3b.setColor(Color::Yellow);
		s0.add(entity3b);
		s0.setDescriptor("foo");

		SnapshotDiff d1 (snap1, s0);

		EXPECT_EQ ("foo", d1.getDescriptor());
		EXPECT_EQ (1U, d1.getChangedEntities().size());
		EXPECT_EQ (0U, d1.getNewGlobals().size());
		EXPECT_TRUE (contains(d1.getChangedEntities(), entity3b));
		EXPECT_EQ (0U, d1.getRemovedEntities().size());

		Snapshot s1 = d1.reconstruct(snap1);
		EXPECT_EQ (s0, s1);
	}


TEST_F (SnapshotDiffTests, EntityRemove) {
		Snapshot empty;
		SnapshotDiff d0 (empty, empty);
		Snapshot s0 = d0.reconstruct(snap1);

		s0.remove(entity3);
		s0.setDescriptor("foo");

		SnapshotDiff d1 (snap1, s0);

		EXPECT_EQ ("foo", d1.getDescriptor());
		EXPECT_EQ (0U, d1.getChangedEntities().size());
		EXPECT_EQ (0U, d1.getNewGlobals().size());
		EXPECT_EQ (1U, d1.getRemovedEntities().size());
		EXPECT_TRUE (contains(d1.getRemovedEntities(), entity3.getEntityIdentifier()));

		Snapshot s1 = d1.reconstruct(snap1);
		EXPECT_EQ (s0, s1);
	}



}  // namespace



