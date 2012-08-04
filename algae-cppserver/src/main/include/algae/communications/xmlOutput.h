/*
 * xmlOutput.h
 *
 *  Created on: Jun 28, 2012
 *      Author: zeil
 */

#ifndef XMLOUTPUT_H_
#define XMLOUTPUT_H_

#include <iostream>
#include <string>

namespace algae {

class XMLTag {
protected:
	std::ostream& out;
	bool isClosed;
	std::string trueTagName;

	static int depth;
	static int idNum;

	static const std::string javaXMLTag;

	XMLTag (std::ostream& output, std::string trueTagName);

public:
	virtual ~XMLTag();

	virtual void close();

	static const std::string communicationsPackage;
	static const std::string snapshotPackage;
};



class JavaTag: public XMLTag {
public:
	JavaTag (std::ostream& output);
};


class ObjectTag: public XMLTag {
public:
	ObjectTag (std::ostream& output, std::string className);
};

class PropertyTag: public XMLTag {
public:
	PropertyTag (std::ostream& output, std::string propertyName);
};

class IndexTag: public XMLTag {
public:
	IndexTag (std::ostream& output, int index);
};


class MethodTag: public XMLTag {
public:
	MethodTag (std::ostream& output, std::string methodName);
};

class ArrayTag: public XMLTag {
public:
	ArrayTag (std::ostream& output, std::string elementType, int arraySize);
};

class StringValue: public XMLTag {
public:
	StringValue (std::ostream& output, std::string value);

	std::string xmlEncode (const std::string text) const;
};


class IntValue: public XMLTag {
public:
	IntValue (std::ostream& output, int value);
};

class LongValue: public XMLTag {
public:
	LongValue (std::ostream& output, long value);
};


class DoubleValue: public XMLTag {
public:
	DoubleValue (std::ostream& output, double value);
};

class BoolValue: public XMLTag {
public:
	BoolValue (std::ostream& output, bool value);
};






}

#endif /* XMLOUTPUT_H_ */
