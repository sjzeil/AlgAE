#include <algae/snapshot/color.h>
#include <algae/snapshot/entity.h>
#include <algae/snapshot/identifier.h>
#include <algae/rendering/rendering.h>

#include <string>

#include "gtest/gtest.h"

namespace {

using namespace std;
using namespace algae;



TEST (IdentifierTest, emptyID) {
	Identifier id;
	EXPECT_EQ (Identifier::nullID(), id);
}

union NotAGoodIdea {
	int i;
	double d;
};

TEST (IdentifierTest, intTypeRendering) {
	//cout << "Went past here" << endl;
	NotAGoodIdea nagi;
	nagi.i = 23;
	Identifier id(nagi.i);
	EXPECT_NE (Identifier::nullID(), id);

	Identifier id2 (nagi.d);
	EXPECT_EQ (id, id2); // only the addresses are significant

	const TypeRenderer* r = id.getType();

	Entity e (Identifier(nagi.i), "label");
	r->renderInto(e);
	EXPECT_EQ(EntityIdentifier::nullEID(), e.getContainer());
	EXPECT_EQ("23", e.getValue());
	EXPECT_EQ("label", e.getLabel());
	EXPECT_EQ(0U, e.getComponents().size());
	EXPECT_EQ(0U, e.getConnections().size());
}




}  // namespace



