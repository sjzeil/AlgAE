#include <algae/rendering/rendering.h>
#include <algae/snapshot/color.h>
#include <algae/snapshot/entity.h>

#include <algorithm>
#include <string>

#include "gtest/gtest.h"


namespace {

using namespace std;
using namespace algae;

// The fixture for testing class Foo.


TEST (TypeRendering, emptyT) {
	EXPECT_EQ (0, 0);
}


TEST (TypeRendering, intTypeRendering) {
	int k = 23;
	const TypeRenderer* r = TypeRenderer::typeRenderer(k);
	Entity e (Identifier(k), "label");
	r->renderInto(e);
	EXPECT_EQ(EntityIdentifier::nullEID(), e.getContainer());
	EXPECT_EQ("23", e.getValue());
	EXPECT_EQ("label", e.getLabel());
	EXPECT_EQ(0U, e.getComponents().size());
	EXPECT_EQ(0U, e.getConnections().size());

}


TEST (TypeRendering, charTypeRendering) {
	char c = 'Q';
	const Renderer* r = TypeRenderer::typeRenderer(c);
	Entity e (Identifier(c), "");
	r->renderInto(e);
	EXPECT_EQ(EntityIdentifier::nullEID(), e.getContainer());
	EXPECT_EQ("Q", e.getValue());
	EXPECT_EQ("", e.getLabel());
	EXPECT_EQ(0U, e.getComponents().size());
	EXPECT_EQ(0U, e.getConnections().size());
}


TEST (TypeRendering, stringTypeRendering) {
	string s = "abc";
	const Renderer* r = TypeRenderer::typeRenderer(s);
	Entity e (Identifier(s), "");
	r->renderInto(e);
	EXPECT_EQ(EntityIdentifier::nullEID(), e.getContainer());
	EXPECT_EQ("abc", e.getValue());
	EXPECT_EQ("", e.getLabel());
	EXPECT_EQ(0U, e.getComponents().size());
	EXPECT_EQ(0U, e.getConnections().size());
}


struct IntStr: public SelfRenderer
{
	int k;
	string s;
	string* c;

	virtual ~IntStr() {}

	IntStr(int kk, string ss) : k(kk), s(ss), c(0) {}

	virtual std::string getValue() const;
	virtual Color getColor() const;
	virtual void getComponents(ComponentCollector& components) const;
	virtual void getConnections(ConnectionCollector& connections) const;
	virtual int getMaxComponentsPerRow() const;
};

ostream& operator<< (ostream& out, const IntStr& is)
{
	out << "Bogus!";
	return out;
}

std::string IntStr::getValue() const
{
	return "@@@";
}

Color IntStr::getColor() const
{
	return Color::Green;
}

void IntStr::getComponents(ComponentCollector& components) const
{
	components.add(k, "k0");
	components.add(s, "s0");
}

void IntStr::getConnections(ConnectionCollector& connections) const
{
	if (c != 0) {
		connections.add(c, 0, 45, -1, "next");
		connections.add((string*)0, 180, 225, -1, "prev");
		connections.add(this, 90, 95, -1, "self");
	}
}

int IntStr::getMaxComponentsPerRow() const
{
	return 1;
}



TEST (TypeRendering, selfRendering) {
	int k = 23;
	string s = "abc";
	IntStr x (k,s);
	const Renderer* r = TypeRenderer::typeRenderer(x);
	Entity e (Identifier(x), "");
	r->renderInto(e);
	EXPECT_EQ(EntityIdentifier::nullEID(), e.getContainer());
	EXPECT_EQ("@@@", e.getValue());
	EXPECT_EQ(Color::Green, e.getColor());
	EXPECT_EQ("", e.getLabel());
	EXPECT_EQ(2U, e.getComponents().size());
	EXPECT_EQ(0U, e.getConnections().size());

	EntityIdentifier eid1 = e.getComponents().front();
	EXPECT_NE(EntityIdentifier::nullEID(), eid1.getContainer());
	EXPECT_EQ (e.getEntityIdentifier(), eid1.getContainer());



	EntityIdentifier eid2 = e.getComponents().front();
	EXPECT_NE(EntityIdentifier::nullEID(), eid2.getContainer());
	EXPECT_EQ (e.getEntityIdentifier(), eid2.getContainer());
}

class destinationIs {
	EntityIdentifier eid;
public:
	destinationIs (const EntityIdentifier& e): eid(e) {}

	bool operator() (const Connector& c) const;
};
bool destinationIs::operator() (const Connector& c) const
{
	return c.getDestination() == eid;
}

TEST (TypeRendering, connections) {
	int k = 23;
	string s = "abc";
	IntStr x (k,s);
	x.c = new string("foo");

	const Renderer* r = TypeRenderer::typeRenderer(x);
	Entity e (Identifier(x), "");
	r->renderInto(e);
	EXPECT_EQ(EntityIdentifier::nullEID(), e.getContainer());
	EXPECT_EQ("@@@", e.getValue());
	EXPECT_EQ(Color::Green, e.getColor());
	EXPECT_EQ("", e.getLabel());
	EXPECT_EQ(2U, e.getComponents().size());
	EXPECT_EQ(3U, e.getConnections().size());

	EntityIdentifier eid1 = e.getComponents().front();
	EXPECT_NE(EntityIdentifier::nullEID(), eid1.getContainer());
	EXPECT_EQ (e.getEntityIdentifier(), eid1.getContainer());



	EntityIdentifier eid2 = e.getComponents().front();
	EXPECT_NE(EntityIdentifier::nullEID(), eid2.getContainer());
	EXPECT_EQ (e.getEntityIdentifier(), eid2.getContainer());

	list<Connector> conn = e.getConnections();
	EXPECT_EQ (conn.end(), find_if(conn.begin(), conn.end(), destinationIs(eid1)));
	EXPECT_NE (conn.end(), find_if(conn.begin(), conn.end(), destinationIs(e.getEntityIdentifier())));
	EXPECT_NE (conn.end(), find_if(conn.begin(), conn.end(), destinationIs(EntityIdentifier())));

}




class IntStr2: public CanBeRendered
{
	int k;
	string s;

	friend class IntStrRenderer;
public:
	virtual ~IntStr2() {}

	IntStr2(int kk, string ss) : k(kk), s(ss) {}

	const TypeRenderer* getTypeRenderer() const;
};

class IntStrRenderer: public TypeRendererOf<IntStr2>
{
public:
	IntStrRenderer (const IntStr2& instance)
	: TypeRendererOf<IntStr2>(instance)
	  {}

	virtual const TypeRenderer* getTypeRenderer() const {return this;}
	virtual std::string getValue() const;
	virtual Color getColor() const;
	virtual void getComponents(ComponentCollector& components) const;
	virtual void getConnections(ConnectionCollector& connections) const;
	virtual int getMaxComponentsPerRow() const;

	Renderer* clone() const {return new IntStrRenderer(*instance);}
};

ostream& operator<< (ostream& out, const IntStr2& is)
{
	out << "Still Bogus!";
	return out;
}

std::string IntStrRenderer::getValue() const
{
	return instance->s;
}

Color IntStrRenderer::getColor() const
{
	return Color::Blue;
}

void IntStrRenderer::getComponents(ComponentCollector& components) const
{
	components.add(instance->k, "k0");
	components.add(instance->s, "s0");
}

void IntStrRenderer::getConnections(ConnectionCollector& connections) const
{}

int IntStrRenderer::getMaxComponentsPerRow() const
{
	return 1;
}

const TypeRenderer* IntStr2::getTypeRenderer() const
{
	return new IntStrRenderer(*this);
}





TEST (TypeRendering, canBeRendered) {
	int k = 47;
	string s = "def";
	IntStr2 x (k,s);
	const Renderer* r = TypeRenderer::typeRenderer(x);
	Entity e (Identifier(x), "");
	r->renderInto(e);
	EXPECT_EQ(EntityIdentifier::nullEID(), e.getContainer());
	EXPECT_EQ(s, e.getValue());
	EXPECT_EQ(Color::Blue, e.getColor());
	EXPECT_EQ("", e.getLabel());
	EXPECT_EQ(2U, e.getComponents().size());
	EXPECT_EQ(0U, e.getConnections().size());

	EntityIdentifier eid1 = e.getComponents().front();
	EXPECT_NE(EntityIdentifier::nullEID(), eid1.getContainer());
	EXPECT_EQ (e.getEntityIdentifier(), eid1.getContainer());



	EntityIdentifier eid2 = e.getComponents().front();
	EXPECT_NE(EntityIdentifier::nullEID(), eid2.getContainer());
	EXPECT_EQ (e.getEntityIdentifier(), eid2.getContainer());


}



}  // namespace



