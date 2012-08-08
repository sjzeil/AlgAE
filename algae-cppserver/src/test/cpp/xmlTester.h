#ifndef XMLTESTER_H
#define XMLTESTER_H


#include <map>
#include <string>
#include <iostream>
#include <fstream>
#include <sstream>

#include <algae/communications/clientMessage.h>
#include <algae/communications/xmlOutput.h>


//namespace {


class XMLTester {
	bool saving;
	std::string fileNamePrefix;

	std::map<std::string, std::string> fields;
 public:

	  XMLTester (bool save = false, std::string prefix ="xmlTest.");
	  ~XMLTester() {cleanUp();}


	  /**
	   * Print an object to XML, in preparation for checking to see if the
	   * the client can load it.
	   *
	   * If save is true, a copy is written to a file for debugging purposes.
	   */
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

	  /**
	   * Print a message to XML, in preparation for checking to see if the
	   * the client can load it.
	   *
	   * If save is true, a copy is written to a file for debugging purposes.
	   */
	  void printMessage (const algae::ClientMessage& x, bool save = false)
	  {
		  std::string fileName = fileNamePrefix + ((saving) ? "saved" : "in");
		  std::ofstream out (fileName.c_str());
		  x.printXML(out);
		  out.close();
	  }


	  /**
	   * Asks the Java client to read the XML already printed.
	   *
	   * @return the name of the class recognized by the Java client, or ""
	   *      if the attempted input failed.
	   */
	  std::string readXML();

	  /**
	   * Ask the Client to retrieve the indicated attribute
	   * and return the string equivalent of its value or "*error*"
	   * if the attribute does not exist.
	   */
	  std::string valueOf (std::string attributeName) const;

	  void cleanUp();

};

//}  // namespace

#endif



