#ifndef XMLTESTER_H
#define XMLTESTER_H


#include <map>
#include <string>
#include <fstream>

#include <algae/communications/clientMessage.h>
#include <algae/communications/xmlOutput.h>


//namespace {


// The fixture for testing class Foo.
class XMLTester {
	bool saving;
	std::string fileNamePrefix;
 public:

	  std::map<std::string, std::string> fields;

	  XMLTester (bool save = false, std::string prefix ="xmlTest.");
	  ~XMLTester() {cleanUp();}



	  template <typename T>
	  void printToXML (const T& x, bool save = false)
	  {
		  std::string fileName = fileNamePrefix + ((saving) ? "saved" : "in");
		  std::ofstream out (fileName.c_str());
		  algae::JavaTag jt (out);
		  x.printXML(out);
		  jt.close();
		  out.close();
	  }

	  void printMessage (const algae::ClientMessage& x, bool save = false)
	  	  {
	  		  std::string fileName = fileNamePrefix + ((saving) ? "saved" : "in");
	  		  std::ofstream out (fileName.c_str());
	  		  x.printXML(out);
	  		  out.close();
	  	  }



	  void readXML();

	  void cleanUp();

};

//}  // namespace

#endif



