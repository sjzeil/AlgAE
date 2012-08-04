#include <algae/communications/xmlOutput.h>

#include "xmlTester.h"
#include <cstdlib>
#include <cstdio>
#include <map>
#include <string>
#include <fstream>

using namespace std;

//namespace {

XMLTester::XMLTester (bool save, std::string prefix)
: saving(save), fileNamePrefix(prefix)
{}




void XMLTester::readXML()
{
	string fileName = fileNamePrefix + ((saving) ? "saved" : "in");
	fields.clear();
	string command = string("java -cp AlgAE_Client.4.0.jar edu.odu.cs.AlgAE.Common.ReadEncodedXML < ") + fileName + " > xmlTest.out";
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
}

void XMLTester::cleanUp()
{
	if (!saving)
		remove ("xmlTest.in");
	remove ("xmlTest.out");
}

//}
