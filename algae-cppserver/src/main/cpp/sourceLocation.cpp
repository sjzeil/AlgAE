/**
 * sourceLocation.cpp
 *
 *
 *  Created on: July 3, 2012
 *      Author: zeil
 */

#include <algae/snapshot/sourceLocation.h>

#include <string>

namespace algae {

SourceLocation::SourceLocation (std::string filename, int line)
: fileName(filename), lineNumber(line)
{
}

SourceLocation::SourceLocation ()
: fileName(""), lineNumber(1)
{
	fileName = "";
	setLineNumber(1);
}


void SourceLocation::print(std::ostream& out) const {
	out << fileName << ":" << lineNumber;
}

bool SourceLocation::operator== (const SourceLocation& other) const {
	if (getLineNumber() != other.getLineNumber())
		return false;
	else return fileName == other.fileName;
}

bool SourceLocation::operator< (const SourceLocation& other) const {
	if (fileName < other.fileName)
		return true;
	if (fileName > other.fileName)
		return false;
	return (getLineNumber() < other.getLineNumber());
}






}

