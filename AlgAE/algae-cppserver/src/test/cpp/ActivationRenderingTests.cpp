#include <algae/impl/activationRecordImpl.h>
#include <algae/memoryModel/memoryModel.h>
#include <algae/memoryModel/activationRecord.h>
#include <algae/memoryModel/simpleReference.h>
#include <algae/snapshot/snapshot.h>
#include <algae/activation.h>
#include <algae/animation.h>

#include <algorithm>
#include <string>

#include "gtest/gtest.h"



namespace {

using namespace std;
using namespace algae;

class ActivationRecordAccess: public algae::ActivationRecord
{
public:
	using algae::ActivationRecord::impl;
};

// The fixture for testing class Foo.
class ActivationRenderingTests : public ::testing::Test {
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
	  ActivationRecordAccess* fooAR;
	  ActivationRecordAccess* barAR;

	  Snapshot* snap;


	  ActivationRenderingTests()
	: i1(111), i2(222), i3(333),
	  s1("s1"), s2("s2"), s3("s3"),
	  anim(),
	  mm(anim.getMemoryModel()),
	  stack (mm.getActivationStack())
	  {
		  /*
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
		*/
	}

  virtual ~ActivationRenderingTests()
  {
  }

  virtual void SetUp() {
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
	  while (ancestor != Identifier::nullID() && ancestor != expectedAncestor)
	     ancestor = snap->getEntities().lower_bound(ancestor)->second.getEntityIdentifier().getContainer().getObjectIdentifier();
	  return (ancestor == expectedAncestor);
  }

};




TEST_F (ActivationRenderingTests, TopAR) {
	stack.push("foo");
	fooAR = (ActivationRecordAccess*)(&stack.top());
	Entity e (Identifier(*fooAR), "");
	e.getObjectIdentifier().getType()->renderInto(e);
	EXPECT_EQ(EntityIdentifier::nullEID(), e.getContainer());
	EXPECT_EQ("", e.getValue());
	EXPECT_EQ("", e.getLabel());
	EXPECT_EQ(2U, e.getComponents().size());
	EXPECT_EQ(0U, e.getConnections().size());
}

TEST_F (ActivationRenderingTests, InnerAR) {
	stack.push("foo");
	fooAR = (ActivationRecordAccess*)(&stack.top());
	stack.push ("bar");
	Entity e (Identifier(*fooAR), "");
	e.getObjectIdentifier().getType()->renderInto(e);
	EXPECT_EQ(EntityIdentifier::nullEID(), e.getContainer());
	EXPECT_EQ("", e.getValue());
	EXPECT_EQ("", e.getLabel());
	// Only the top AR shows its local variables
	EXPECT_EQ(1U, e.getComponents().size());
	EXPECT_EQ(0U, e.getConnections().size());
}

TEST_F (ActivationRenderingTests, EmptyParams) {
	stack.push("foo");
	fooAR = (ActivationRecordAccess*)(&stack.top());
	Entity e (Identifier(fooAR->impl->params), "");
	e.getObjectIdentifier().getType()->renderInto(e);
	EXPECT_EQ(EntityIdentifier::nullEID(), e.getContainer());
	EXPECT_EQ("", e.getValue());
	EXPECT_EQ("", e.getLabel());
	EXPECT_EQ(3U, e.getComponents().size());
	EXPECT_EQ(0U, e.getConnections().size());
}

TEST_F (ActivationRenderingTests, ThisParam) {
	stack.push("foo");
	fooAR = (ActivationRecordAccess*)(&stack.top());
	fooAR->thisParam(Identifier(s1));
	Entity e (Identifier(fooAR->impl->params), "");
	e.getObjectIdentifier().getType()->renderInto(e);
	EXPECT_EQ(EntityIdentifier::nullEID(), e.getContainer());
	EXPECT_EQ("", e.getValue());
	EXPECT_EQ("", e.getLabel());
	EXPECT_EQ(5U, e.getComponents().size());
	EXPECT_EQ(0U, e.getConnections().size());

	Entity::ComponentsList clist = e.getComponents();
	Identifier firstComponent = clist.front().getObjectIdentifier();
	void* firstComponentObj = firstComponent.getKey();
	SimpleReference* sr = (SimpleReference*)firstComponentObj;
	EXPECT_EQ(Identifier(s1), sr->get());
}


TEST_F (ActivationRenderingTests, BasicParam) {
	stack.push("foo");
	fooAR = (ActivationRecordAccess*)(&stack.top());
	fooAR->param("s1", Identifier(s1));
	Entity e (Identifier(fooAR->impl->params), "");
	e.getObjectIdentifier().getType()->renderInto(e);
	EXPECT_EQ(EntityIdentifier::nullEID(), e.getContainer());
	EXPECT_EQ("", e.getValue());
	EXPECT_EQ("", e.getLabel());
	EXPECT_EQ(4U, e.getComponents().size());
	EXPECT_EQ(0U, e.getConnections().size());

	Entity::ComponentsList clist = e.getComponents();
	Entity::ComponentsList::iterator it = clist.begin();
	++it;
	++it;
	Identifier component = it->getObjectIdentifier();
	EXPECT_EQ(Identifier(s1), component);

	fooAR->param("i1", Identifier(i1));
	Entity e2 (Identifier(fooAR->impl->params), "");
	e2.getObjectIdentifier().getType()->renderInto(e);
	EXPECT_EQ(6U, e.getComponents().size());
}


TEST_F (ActivationRenderingTests, RefParam) {
	stack.push("foo");
	fooAR = (ActivationRecordAccess*)(&stack.top());
	fooAR->refParam("s1", Identifier(s1));
	Entity e (Identifier(fooAR->impl->params), "");
	e.getObjectIdentifier().getType()->renderInto(e);
	EXPECT_EQ(EntityIdentifier::nullEID(), e.getContainer());
	EXPECT_EQ("", e.getValue());
	EXPECT_EQ("", e.getLabel());
	EXPECT_EQ(4U, e.getComponents().size());
	EXPECT_EQ(0U, e.getConnections().size());

	Entity::ComponentsList clist = e.getComponents();
	Entity::ComponentsList::iterator it = clist.begin();
	++it;
	++it;
	Identifier component = it->getObjectIdentifier();
	void* componentObj = component.getKey();
	SimpleReference* sr = (SimpleReference*)componentObj;

	EXPECT_EQ(Identifier(s1), sr->get());
}




TEST_F (ActivationRenderingTests, EmptyLocals) {
	stack.push("foo");
	fooAR = (ActivationRecordAccess*)(&stack.top());
	Entity e (Identifier(fooAR->impl->locals), "");
	e.getObjectIdentifier().getType()->renderInto(e);
	EXPECT_EQ(EntityIdentifier::nullEID(), e.getContainer());
	EXPECT_EQ("", e.getValue());
	EXPECT_EQ("", e.getLabel());
	EXPECT_EQ(0U, e.getComponents().size());
	EXPECT_EQ(0U, e.getConnections().size());
}






}  // namespace



