#include <algae/communications/xmlOutput.h>

#include "xmlTester.h"
#include <cstdlib>
#include <cstdio>
#include <map>
#include <string>
#include <fstream>
#include <algorithm>

using namespace std;

//namespace {

XMLTester::XMLTester (bool save, std::string prefix)
: saving(save), fileNamePrefix(prefix)
{}


#ifdef _WIN64
#define isWindows 1
#elif _WIN32
#define isWindows 1
#endif


/**
 * Asks the Java client to read the XML already printed.
 *
 * @return the name of the class recognized by the Java client, or ""
 *      if the attempted input failed.
 */
std::string XMLTester::readXML()
{
	string classPath = "target/test-classes:../algae-common/target/classes:../algae-client/target/classes:target/test-classes";
#ifdef isWindows
    replace (classPath.begin(), classPath.end(), '/', '\\');
    replace (classPath.begin(), classPath.end(), ':', ';');
#endif

	string fileName = fileNamePrefix + ((saving) ? "saved" : "in");
	fields.clear();
	string command = string("java -cp " + classPath + " edu.odu.cs.AlgAE.ReadEncodedXML < ") + fileName + " > xmlTest.out";
	system(command.c_str());
	ifstream in ("xmlTest.out");
	string line;
	while (getline(in, line))
	{
		string::size_type k = line.find('\t');
		string key = line.substr(0, k);
		string value = line.substr(k+1);
		fields[key] = value;
	}
	return fields["Class"];
}

/**
 * Ask the Client to retrieve the indicated attribute
 * and return the string equivalent of its value or "*error*"
 * if the attribute does not exist.
 */
std::string XMLTester::valueOf (std::string attributeName) const
{
	map<string,string>::const_iterator pos = fields.find(attributeName);
	if (pos != fields.end())
		return pos->second;
	else
		return "*error*";
}




void XMLTester::cleanUp()
{
	if (!saving)
		remove ("xmlTest.in");
	remove ("xmlTest.out");
}

//}
