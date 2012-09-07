#include <algae/memoryModel/memoryModel.h>
#include <algae/activation.h>
#include <algae/animation.h>

#include <algorithm>
#include <string>

#include "gtest/gtest.h"



namespace {

using namespace std;
using namespace algae;

// The fixture for testing class Foo.
class MemoryModelTests : public ::testing::Test {
 protected:
	  int i1, i2, i3;
	  string s1, s2, s3;

	  class MockAnimation: public algae::Animation {
	  public:
		  string about() const {return "about";}

		  void buildMenu() {}
	  };

	  MockAnimation anim;

	MemoryModelTests()
	: i1(111), i2(222), i3(333),
	  s1("s1"), s2("s2"), s3("s3")
	{
	}

  virtual ~MemoryModelTests()
  {
  }

  virtual void SetUp() {
  }

  virtual void TearDown() {
  }



};







TEST_F (MemoryModelTests, ModelAccess) {
	Animation* anim = Animation::algae();
	EXPECT_NE ((Animation*)0, anim);
	MemoryModel& mm = anim->getMemoryModel();
	ActivationStack& stack = mm.getActivationStack();
	EXPECT_EQ (1U, stack.size());
}

TEST_F (MemoryModelTests, StackPushPop) {
	Animation* anim = Animation::algae();
	MemoryModel& mm = anim->getMemoryModel();
	ActivationStack& stack = mm.getActivationStack();

	stack.push("foo");
	stack.push("bar");
	EXPECT_EQ (3U, stack.size());
	EXPECT_EQ ("bar", stack.top().getName());
	stack.pop();
	EXPECT_EQ ("foo", stack.top().getName());
}

TEST_F (MemoryModelTests, GlobalsEmptyStack) {
	Animation* anim = Animation::algae();
	MemoryModel& mm = anim->getMemoryModel();

	mm.globalVar("c", i2);
	mm.globalVar("d", s2);

	ActivationRecord::iterator p1 = mm.beginGlobals();
	ActivationRecord::iterator p2 = mm.endGlobals();
	EXPECT_EQ (2U, distance(p1,p2));
	string expected[] = {string("c"), string("d")};
	string* e = expected;
	for (ActivationRecord::iterator i = p1; i != p2; ++i)
	{
		EXPECT_EQ (*e, i->label);
		++e;
	}
}


TEST_F (MemoryModelTests, Params) {
	Animation* anim = Animation::algae();
	MemoryModel& mm = anim->getMemoryModel();
	ActivationStack& stack = mm.getActivationStack();
	stack.push("foo");
	{
		ActivationRecord& arec = stack.top();

		arec.param("a", Identifier(i1));
		arec.param("b", Identifier(s1));
	}
	stack.push ("bar");
	{
			ActivationRecord& arec = stack.top();

			arec.param("c", Identifier(i2));
			arec.param("d", Identifier(s2));
	}
	ActivationRecord& toprec = stack.top();
	ActivationRecord::iterator p1 = toprec.beginParams();
	ActivationRecord::iterator p2 = toprec.endParams();
	EXPECT_EQ (2U, distance(p1,p2));
	string expected[] = {string("c"), string("d")};
	string* e = expected;
	for (ActivationRecord::iterator i = p1; i != p2; ++i)
	{
		EXPECT_EQ (*e, i->label);
		++e;
	}

	EXPECT_EQ (0U, distance(toprec.beginLocals(), toprec.endLocals()));
}

TEST_F (MemoryModelTests, Vars) {
	Animation* anim = Animation::algae();
	MemoryModel& mm = anim->getMemoryModel();
	ActivationStack& stack = mm.getActivationStack();
	stack.push("foo");
	{
		ActivationRecord& arec = stack.top();

		arec.var("a", Identifier(i1));
		arec.var("b", Identifier(s1));
	}
	stack.push ("bar");
	{
			ActivationRecord& arec = stack.top();

			arec.var("c", Identifier(i2));
			arec.var("d", Identifier(s2));
	}
	ActivationRecord& toprec = stack.top();
	ActivationRecord::iterator p1 = toprec.beginLocals();
	ActivationRecord::iterator p2 = toprec.endLocals();
	EXPECT_EQ (2U, distance(p1,p2));
	string expected[] = {string("c"), string("d")};
	string* e = expected;
	for (ActivationRecord::iterator i = p1; i != p2; ++i)
	{
		EXPECT_EQ (*e, i->label);
		++e;
	}

	EXPECT_EQ (0U, distance(toprec.beginParams(), toprec.endParams()));
	EXPECT_EQ (0U, distance(mm.beginGlobals(), mm.endGlobals()));
}


TEST_F (MemoryModelTests, Globals) {
	Animation* anim = Animation::algae();
	MemoryModel& mm = anim->getMemoryModel();
	ActivationStack& stack = mm.getActivationStack();
	stack.push("foo");
	{
		ActivationRecord& arec = stack.top();

		arec.var("a", Identifier(i1));
		arec.var("b", Identifier(s1));
	}
	stack.push ("bar");


	mm.globalVar("c", i2);
	mm.globalVar("d", s2);

	ActivationRecord& toprec = stack.top();
	ActivationRecord::iterator p1 = mm.beginGlobals();
	ActivationRecord::iterator p2 = mm.endGlobals();
	EXPECT_EQ (2U, distance(p1,p2));
	string expected[] = {string("c"), string("d")};
	string* e = expected;
	for (ActivationRecord::iterator i = p1; i != p2; ++i)
	{
		EXPECT_EQ (*e, i->label);
		++e;
	}

	EXPECT_EQ (0U, distance(toprec.beginParams(), toprec.endParams()));
	EXPECT_EQ (0U, distance(toprec.beginLocals(), toprec.endLocals()));
}


TEST_F (MemoryModelTests, Scopes) {
	Animation* anim = Animation::algae();
	MemoryModel& mm = anim->getMemoryModel();
	ActivationStack& stack = mm.getActivationStack();
	stack.push("foo");
	ActivationRecord& arec = stack.top();

	arec.var("a", Identifier(i1));
	arec.var("b", Identifier(s1));
	string expected[] = {string("a"), string("b"), string("c"), string("d")};

	ActivationRecord& toprec = stack.top();
	ActivationRecord::iterator p1 = toprec.beginLocals();
	ActivationRecord::iterator p2 = toprec.endLocals();
	EXPECT_EQ (2U, distance(p1,p2));
	string* e = expected;
	for (ActivationRecord::iterator i = p1; i != p2; ++i)
	{
		EXPECT_EQ (*e, i->label);
		++e;
	}


	{
		Scope scope1;
		arec.var("c", Identifier(i2));
		ActivationRecord::iterator p1 = toprec.beginLocals();
		ActivationRecord::iterator p2 = toprec.endLocals();

		EXPECT_EQ (3U, distance(p1,p2));
		e = expected;
		for (ActivationRecord::iterator i = p1; i != p2; ++i)
		{
			EXPECT_EQ (*e, i->label);
			++e;
		}

		for (int i = 0; i < 5; ++i)
		{
			Scope scope2;
			arec.var("d", Identifier(s2));
			ActivationRecord::iterator p1 = toprec.beginLocals();
			ActivationRecord::iterator p2 = toprec.endLocals();

			EXPECT_EQ (4U, distance(p1,p2));
			string* e = expected;
			for (ActivationRecord::iterator i = p1; i != p2; ++i)
			{
				EXPECT_EQ (*e, i->label);
				++e;
			}
		}

		EXPECT_EQ (3U, distance(p1,p2));
		e = expected;
		for (ActivationRecord::iterator i = p1; i != p2; ++i)
		{
			EXPECT_EQ (*e, i->label);
			++e;
		}

	}

	EXPECT_EQ (2U, distance(p1,p2));
	e = expected;
	for (ActivationRecord::iterator i = p1; i != p2; ++i)
	{
		EXPECT_EQ (*e, i->label);
		++e;
	}
}




}  // namespace



