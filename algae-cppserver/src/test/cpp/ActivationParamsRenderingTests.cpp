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


// The fixture for testing class Foo.
class ActivationParamsRenderingTests : public ::testing::Test {
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


	  ActivationParamsRenderingTests()
	: i1(111), i2(222), i3(333),
	  s1("s1"), s2("s2"), s3("s3"),
	  anim(),
	  mm(anim.getMemoryModel()),
	  stack (mm.getActivationStack())
	  {
		  stack.push("foo");
		  stack.push("bar");
		  stack.push("baz");
	  }

  virtual ~ActivationParamsRenderingTests()
  {
  }

  virtual void SetUp() {
  }

  virtual void TearDown() {
  }


};




TEST_F (ActivationParamsRenderingTests, TopAR) {
	ActivationParams ap ("foo", &stack);
	ap.height = 1;

	Entity e (Identifier(ap), "");
	const TypeRenderer* tren = e.getObjectIdentifier().getType();
	tren->renderInto(e);
	EXPECT_EQ(EntityIdentifier::nullEID(), e.getContainer());
	EXPECT_EQ("", e.getValue());
	EXPECT_EQ("", e.getLabel());

	EXPECT_EQ(2U, e.getComponents().size());
	EXPECT_EQ(0U, e.getConnections().size());
}

TEST_F (ActivationParamsRenderingTests, ThisParam) {
	ActivationParams ap ("foo", &stack);
	ap.height = 1;
	ap.thisParam = new SimpleReference(Identifier(s1));

	Entity e (Identifier(ap), "");
	e.getObjectIdentifier().getType()->renderInto(e);
	EXPECT_EQ(EntityIdentifier::nullEID(), e.getContainer());
	EXPECT_EQ("", e.getValue());
	EXPECT_EQ("", e.getLabel());

	EXPECT_EQ(4U, e.getComponents().size());
	EXPECT_EQ(0U, e.getConnections().size());

	Entity::ComponentsList clist = e.getComponents();
	Identifier firstComponent = clist.front().getObjectIdentifier();
	void* firstComponentObj = firstComponent.getKey();
	SimpleReference* sr = (SimpleReference*)firstComponentObj;
	EXPECT_EQ(Identifier(s1), sr->get());
}

class hasOIDof {
	Identifier id;
public:
	template <typename T>
	hasOIDof(const T& x) : id(x) {}

	bool operator() (const EntityIdentifier& eid)
	{
		return eid.getObjectIdentifier() == id;
	}
};

TEST_F (ActivationParamsRenderingTests, BasicParam) {
	ActivationParams ap ("foo", &stack);
	ap.height = 1;
	ap.parameters.push_back(LabeledComponent(Identifier(s1), "s1"));

	Entity e (Identifier(ap), "");
	const TypeRenderer* tren = e.getObjectIdentifier().getType();
	tren->renderInto(e);

	EXPECT_EQ(3U, e.getComponents().size());
	EXPECT_EQ(0U, e.getConnections().size());

	Entity::ComponentsList clist = e.getComponents();
	Entity::ComponentsList::iterator it = find_if(clist.begin(), clist.end(), hasOIDof(s1));
	EXPECT_NE (clist.end(), it);
	EXPECT_EQ (1, distance(it, clist.begin()));
}


TEST_F (ActivationParamsRenderingTests, BasicParams2) {
	ActivationParams ap ("foo", &stack);
	ap.height = 1;
	ap.parameters.push_back(LabeledComponent(Identifier(s1), "s1"));
	ap.parameters.push_back(LabeledComponent(Identifier(i1), "i1"));

	Entity e (Identifier(ap), "");
	const TypeRenderer* tr = e.getObjectIdentifier().getType();
	tr->renderInto(e);

	Entity::ComponentsList clist = e.getComponents();
	EXPECT_EQ(5U, clist.size());
	EXPECT_NE (clist.end(), find_if(clist.begin(), clist.end(), hasOIDof(s1)));
	EXPECT_NE (clist.end(), find_if(clist.begin(), clist.end(), hasOIDof(i1)));
}


TEST_F (ActivationParamsRenderingTests, RefParamTop) {
	ActivationParams ap ("foo", &stack);
	ap.height = 3;
	Identifier s1id (s1);
	SimpleReference sref (s1id);
	Identifier idSref (sref);
	ap.parameters.push_back(LabeledComponent(idSref, "s1"));

	Entity e (Identifier(ap), "");
	e.getObjectIdentifier().getType()->renderInto(e);

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


TEST_F (ActivationParamsRenderingTests, RefParamBottom) {
	ActivationParams ap ("foo", &stack);
	ap.height = 1;
	Identifier s1id (s1);
	SimpleReference sref (s1id);
	Identifier idSref (sref);
	ap.parameters.push_back(LabeledComponent(idSref, "s1"));

	Entity e (Identifier(ap), "");
	const TypeRenderer* tr = e.getObjectIdentifier().getType();
	tr->renderInto(e);

	EXPECT_EQ(4U, e.getComponents().size());
	EXPECT_EQ(0U, e.getConnections().size());

	Entity::ComponentsList clist = e.getComponents();
	Entity::ComponentsList::iterator it = clist.begin();
	++it;
	++it;
	Identifier component = it->getObjectIdentifier();
	void* componentObj = component.getKey();
	SimpleReference* sr = (SimpleReference*)componentObj;

	EXPECT_EQ(Identifier::nullID(), sr->get());
}






}  // namespace



