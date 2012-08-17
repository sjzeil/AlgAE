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

	MemoryModelTests()
	: i1(111), i2(222), i3(333),
	  s1("s1"), s2("s2"), s3("s3")
	{
	}

  virtual ~MemoryModelTests()
  {
  }

  virtual void SetUp() {
	  Animation::reset();
  }

  virtual void TearDown() {
  }


};







TEST_F (MemoryModelTests, ModelAccess) {
	Animation* anim = Animation::algae();
	EXPECT_NE ((Animation*)0, anim);
	MemoryModel& mm = anim->getMemoryModel();
	ActivationStack& stack = mm.getActivationStack();
	EXPECT_EQ (0U, stack.size());
}



}  // namespace



