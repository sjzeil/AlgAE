#include <algae/memoryModel/memoryModel.h>
#include <algae/memoryModel/activationRecord.h>
#include <algae/snapshot/snapshot.h>
#include <algae/activation.h>
#include <algae/animation.h>

#include <algorithm>
#include <string>

#include "gtest/gtest.h"



namespace {

using namespace std;
using namespace algae;

// The fixture for testing class Foo.
class MemoryModelRenderingTests : public ::testing::Test {
 protected:
	  int i1, i2, i3;
	  string s1, s2, s3;

	  class MockAnimation: public algae::Animation {
	  public:
		  string about() const {return "about";}

		  void buildMenu() {}
	  };

	  MockAnimation anim;
	  MemoryModel& mm;
	  ActivationStack& stack;
	  ActivationRecord* fooAR;
	  ActivationRecord* barAR;

	  Snapshot* snap;


	  MemoryModelRenderingTests()
	: i1(111), i2(222), i3(333),
	  s1("s1"), s2("s2"), s3("s3"),
	  anim(),
	  mm(anim.getMemoryModel()),
	  stack (mm.getActivationStack())
	  {
		stack.push("foo");
		fooAR = &stack.top();
		fooAR->param("i2", i2);
		fooAR->var("s2", s2);

		stack.push("bar");
		barAR = &stack.top();
		barAR->param("i3", i3);
		barAR->var("s3", s3);

		mm.globalVar("i1", i1);
		mm.globalVar("s1", s1);
	}

  virtual ~MemoryModelRenderingTests()
  {
  }

  virtual void SetUp() {
		Animation* anim = Animation::algae();
		MemoryModel& mm = anim->getMemoryModel();
		ActivationStack& stack = mm.getActivationStack();
		stack.push ("bar");
		{
				ActivationRecord& arec = stack.top();

				arec.param("c", i2);
				arec.param("d", s2);
		}

  }

  virtual void TearDown() {
  }

  EntityIdentifier getParent(const Identifier& ident)
  {
	  return snap->getEntities().lower_bound(ident)->second.getEntityIdentifier().getContainer();
  }

  bool hasAncestor(const Identifier& ident, const Identifier& expectedAncestor)
  {
	  Identifier ancestor = ident;
	  while (ancestor != Identifier::NullID && ancestor != expectedAncestor)
	     ancestor = snap->getEntities().lower_bound(ancestor)->second.getEntityIdentifier().getContainer();
	  return (ancestor == expectedAncestor);
  }

};







TEST_F (MemoryModelRenderingTests, ClosureTaken) {
	string description = "why are we stopping here?";
	string fileName = "filename.cpp";
	int lineNumber = 314;
	snap = mm.renderInto(description, SourceLocation(fileName, lineNumber));

	EXPECT_EQ(description, snap->getDescriptor());
	EXPECT_EQ(SourceLocation(fileName, lineNumber), snap->getBreakpointLocation());
	EXPECT_EQ(Identifier(stack), snap->getActivationStack().getObjectIdentifier());
	EXPECT_EQ (1, snap->getEntities().count(Identifier(stack)));
	EXPECT_EQ (1, snap->getEntities().count(Identifier(s1)));
	EXPECT_EQ (1, snap->getEntities().count(Identifier(s2)));
	EXPECT_EQ (1, snap->getEntities().count(Identifier(s3)));
	EXPECT_EQ (1, snap->getEntities().count(Identifier(i1)));
	EXPECT_EQ (1, snap->getEntities().count(Identifier(i2)));
	EXPECT_EQ (1, snap->getEntities().count(Identifier(i3)));
	EXPECT_EQ (1, snap->getEntities().count(Identifier(*fooAR)));
	EXPECT_EQ (1, snap->getEntities().count(Identifier(*barAR)));
}

TEST_F (MemoryModelRenderingTests, GlobalsMarked) {
	string description = "why are we stopping here?";
	string fileName = "filename.cpp";
	int lineNumber = 314;
	snap = mm.renderInto(description, SourceLocation(fileName, lineNumber));

	EXPECT_EQ (0U, snap->getGlobals().count(Identifier(stack)));
	EXPECT_EQ (1U, snap->getGlobals().count(Identifier(s1)));
	EXPECT_EQ (0U, snap->getGlobals().count(Identifier(s2)));
	EXPECT_EQ (0U, snap->getGlobals().count(Identifier(s3)));
	EXPECT_EQ (1U, snap->getGlobals().count(Identifier(i1)));
	EXPECT_EQ (0U, snap->getGlobals().count(Identifier(i2)));
	EXPECT_EQ (0U, snap->getGlobals().count(Identifier(i3)));
	EXPECT_EQ (0U, snap->getGlobals().count(Identifier(*fooAR)));
	EXPECT_EQ (0U, snap->getGlobals().count(Identifier(*barAR)));
}

TEST_F (MemoryModelRenderingTests, Components) {
	string description = "why are we stopping here?";
	string fileName = "filename.cpp";
	int lineNumber = 314;
	snap = mm.renderInto(description, SourceLocation(fileName, lineNumber));

	EXPECT_NE (Identifier::NullID, getParent(Identifier(stack)));
	EXPECT_TRUE (hasAncestor(Identifier(*fooAR), Identifier(stack)));
	EXPECT_TRUE (hasAncestor(Identifier(*barAR), Identifier(stack)));

	EXPECT_EQ (EntityIdentifier::nullId(), getParent(Identifier(i1)));
	EXPECT_EQ (EntityIdentifier::nullId(), getParent(Identifier(s1)));

	EXPECT_TRUE (hasAncestor(Identifier(*fooAR), Identifier(s2)));
	EXPECT_TRUE (hasAncestor(Identifier(*fooAR), Identifier(i2)));

	EXPECT_TRUE (hasAncestor(Identifier(*barAR), Identifier(s3)));
	EXPECT_TRUE (hasAncestor(Identifier(*barAR), Identifier(i3)));

}



}  // namespace



