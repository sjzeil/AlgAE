/*
 * xmlOutput.cpp
 *
 *  Created on: Jun 28, 2012
 *      Author: zeil
 */


#include <algae/communications/xmlOutput.h>

#include <iostream>
#include <string>


using namespace std;

namespace algae {

int XMLTag::depth = 0;
int XMLTag::idNum = 0;

const std::string XMLTag::javaXMLTag = "<java version=\"1.6.0_24\" class=\"java.beans.XMLDecoder\">";
const std::string XMLTag::communicationsPackage = "edu.odu.cs.AlgAE.Common.Communications";
const std::string XMLTag::snapshotPackage = "edu.odu.cs.AlgAE.Common.Snapshot";


XMLTag::XMLTag (std::ostream& output, std::string tagName)
 : out(output), isClosed(false), trueTagName(tagName)
{
	++depth;
}



XMLTag::~XMLTag()
{
	close();
}

void XMLTag::close()
{
	if (!isClosed) {
		--depth;
		out << string(3*depth, ' ') << "</" << trueTagName << ">\n";
		isClosed = true;
	}
}

JavaTag::JavaTag (std::ostream& output)
: XMLTag(output, "java")
{
	out << javaXMLTag;
	depth = 1;
}


ObjectTag::ObjectTag (std::ostream& output, std::string className)
	: XMLTag(output, "object")
{
	++idNum;
	out << string(3*(depth-1), ' ');
	out << "<object id='obj" << idNum << "' class='" << className << "'>\n";
}


PropertyTag::PropertyTag (std::ostream& output, std::string propertyName)
	: XMLTag(output, "void")
{
	out << string(3*(depth-1), ' ');
	out << "<void property='" << propertyName << "'>\n";
}



IndexTag::IndexTag (std::ostream& output, int index)
: XMLTag(output, "void")
{
out << string(3*(depth-1), ' ');
out << "<void index='" << index << "'>\n";
}


MethodTag::MethodTag (std::ostream& output, std::string methodName)
: XMLTag(output, "void")
{
out << string(3*(depth-1), ' ');
out << "<void method='" << methodName << "'>\n";
}



ArrayTag::ArrayTag (std::ostream& output, std::string elementType, int arraySize)
: XMLTag(output, "array")
{
	out << string(3*(depth-1), ' ');
	out << "<array class='" << elementType << "' length='" << arraySize << "'>\n";
}



StringValue::StringValue (std::ostream& output, std::string value)
: XMLTag(output, "string")
{
	out << string(3*(depth-1), ' ');
	out << "<string>" << xmlEncode(value) << "</string>\n";
	isClosed = true;
	--depth;
}




std::string StringValue::xmlEncode (const std::string text) const
{
	string result;
	for (unsigned int i = 0; i < text.size(); ++i)
	{
		char c = text[i];
		if (c == '&')
			result += "&amp;";
		else if (c == '<')
			result += "&lt;";
		else if (c == '>')
			result += "&gt;";
		else if (c < ' ')
		{
			int d1 = c % 10;
			int d2 = c / 10;
			char c1 = '0' + d1;
			char c2 = '0' + d2;
			result += "&#";
			result += c2;
			result += c1;
			result += ';';
		}
		else
			result += c;
	}
	return result;
}


IntValue::IntValue (std::ostream& output, int value)
  : XMLTag(output, "int")
{
	out << string(3*(depth-1), ' ');
	out << "<int>" << value << "</int>\n";
	isClosed = true;
	--depth;
}

LongValue::LongValue (std::ostream& output, long value)
  : XMLTag(output, "int")
{
	out << string(3*(depth-1), ' ');
	out << "<long>" << value << "</long>\n";
	isClosed = true;
	--depth;
}


DoubleValue::DoubleValue (std::ostream& output, double value)
  : XMLTag(output, "double")
{
	out << string(3*(depth-1), ' ');
	out << "<double>" << value << "</double>\n";
	isClosed = true;
	--depth;
}


BoolValue::BoolValue (std::ostream& output, bool value)
  : XMLTag(output, "boolean")
{
	out << string(3*(depth-1), ' ');
	out << "<boolean>" << (value ? "true" : "false") << "</boolean>\n";
	isClosed = true;
	--depth;
}


}
