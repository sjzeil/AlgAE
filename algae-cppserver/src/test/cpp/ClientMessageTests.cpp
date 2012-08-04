#include <algae/communications/ackMessage.h>
#include <algae/communications/capturedOutputMessage.h>
#include <algae/communications/forceShutDownMessage.h>
#include <algae/communications/menuMessage.h>
#include <algae/communications/promptForInputMessage.h>
#include <algae/communications/pullMessage.h>
#include <algae/communications/snapshotMessage.h>
#include <algae/communications/sourceCodeMessage.h>

#include <algae/snapshot/snapshotDiff.h>

#include <algorithm>
#include <string>

#include "gtest/gtest.h"
#include "xmlTester.h"



namespace {

using namespace std;
using namespace algae;

// The fixture for testing class Foo.
class ClientMessageTests : public ::testing::Test {
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


	  ClientMessageTests()
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

  virtual ~ClientMessageTests()
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







TEST_F (ClientMessageTests, AckMessage) {
	AckMessage message1;
	
	XMLTester xml;
	xml.printMessage(message1);
	xml.readXML();
	EXPECT_EQ ("AckMessage", xml.fields["Class"]);
}

TEST_F (ClientMessageTests, CapturedOutput) {
	string outp = "foobar";
	XMLTester xml;
	CapturedOutputMessage msg (outp);
	EXPECT_EQ (outp, msg.getOutput());
	xml.printMessage(msg);
	xml.readXML();
	EXPECT_EQ ("CapturedOutputMessage", xml.fields["Class"]);
	EXPECT_EQ (outp, xml.fields["output"]);
}


TEST_F (ClientMessageTests, ForceShutDown) {
	string param = "foobar";
	XMLTester xml;
	ForceShutDownMessage msg(param);
	xml.printMessage(msg);
	EXPECT_EQ (param, msg.getExplanation());
	xml.readXML();
	EXPECT_EQ ("ForceShutDownMessage", xml.fields["Class"]);
	EXPECT_EQ (param, xml.fields["explanation"]);
}


TEST_F (ClientMessageTests, Menu) {

	string aboutStr = "All about\nthis animation";
	string aboutStr2 = "Nothing about\nthis animation";
	string menu1[] = {"search", "sort", "insert"};
	string  menu2[] = {"sort", "search", "insert"};
	MenuMessage msg1 (aboutStr, menu1, menu1+3);
	MenuMessage msg2 (aboutStr2, menu1, menu1+3);
	MenuMessage msg3 (aboutStr, menu2, menu2+3);
	MenuMessage msg4 (aboutStr, menu1, menu1+3);

	EXPECT_EQ (msg1, msg4);
	EXPECT_NE (msg1, msg2);
	EXPECT_NE (msg1, msg3);

	XMLTester xml;
	xml.printMessage(msg1);
	xml.readXML();
	EXPECT_EQ ("MenuMessage", xml.fields["Class"]);
	EXPECT_EQ (string("All about this animation"), xml.fields["about"]);
	/*EXPECT_GT (countOf("search", xml.fields["menu"]), 0);
	EXPECT_GT (countOf("sort", xml.fields["menu"]), 0);
	EXPECT_GT (countOf("insert", xml.fields["menu"]), 0);
	*/
}


TEST_F (ClientMessageTests, Prompt) {
	string prompt1 = "Enter a number:";
	string prompt2 = "Enter a name:";
	string pattern1 = "[0-9][0-9]*";
	string pattern2 = "..*";

	PromptForInputMessage msg1 (prompt1, pattern1);
	PromptForInputMessage msg2 (prompt2, pattern1);
	PromptForInputMessage msg3 (prompt1, pattern2);

	EXPECT_NE (msg1, msg2);
	EXPECT_NE (msg1, msg3);

	XMLTester xml;
	xml.printMessage(msg1);
	xml.readXML();
	EXPECT_EQ ("PromptForInputMessage", xml.fields["Class"]);
	EXPECT_EQ (prompt1, xml.fields["prompt"]);
	EXPECT_EQ (pattern1, xml.fields["requiredpattern"]);
}

TEST_F (ClientMessageTests, PullMessage) {
	PullMessage message1;

	XMLTester xml;
	xml.printMessage(message1);
	xml.readXML();
	EXPECT_EQ ("PullMessage", xml.fields["Class"]);
}


TEST_F (ClientMessageTests, SourceCode) {
	string path1 = "something.h";
	string path2 = "something.cpp";
	string text1 = "#include <string>";
	string text2 = "for (int i = 0; i != 10; i++)\n {}\n";

	SourceCodeMessage msg1 (path1, text1);
	SourceCodeMessage msg2 (path2, text1);
	SourceCodeMessage msg3 (path1, text2);

	EXPECT_NE (msg1, msg2);
	EXPECT_NE (msg1, msg3);

	XMLTester xml (true);
	xml.printMessage(msg1);
	xml.readXML();
	EXPECT_EQ ("SourceCodeMessage", xml.fields["Class"]);
	EXPECT_EQ (path1, xml.fields["filepath"]);
	EXPECT_EQ (text1, xml.fields["sourcetext"]);

	xml.printMessage(msg3);
	xml.readXML();
	EXPECT_EQ ("SourceCodeMessage", xml.fields["Class"]);
	EXPECT_EQ (path1, xml.fields["filepath"]);
	EXPECT_EQ (1, countOf("for (int i = 0; i != 10; i++)", xml.fields["sourcetext"]));
}


TEST_F (ClientMessageTests, Snapshot) {
	SnapshotDiff sd (Snapshot(), snap1);
	SnapshotMessage msg1 (sd, true);
	SnapshotMessage msg2 (sd, false);

	XMLTester xml;
	xml.printMessage(msg1);
	xml.readXML();
	EXPECT_EQ ("SnapshotMessage", xml.fields["Class"]);
	EXPECT_EQ ("true", xml.fields["menuitemcompleted"]);
	EXPECT_GT (countOf("componentA", xml.fields["snapshot"]), 0);
	//EXPECT_GT (countOf("link", xml.fields["snapshot"]), 0);

	xml.printMessage(msg2);
	xml.readXML();
	EXPECT_EQ ("SnapshotMessage", xml.fields["Class"]);
	EXPECT_EQ ("false", xml.fields["menuitemcompleted"]);
}

	
}
