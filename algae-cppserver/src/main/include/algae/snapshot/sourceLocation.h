/**
 * sourceLocation.h
 *
 *
 *  Created on: June 17, 2012
 *      Author: zeil
 */

#ifndef SOURCELOCATION_H_
#define SOURCELOCATION_H_

#include <iostream>
#include <string>

namespace algae {

class SourceLocation {
private:

	std::string fileName;
	int lineNumber;

public:
	SourceLocation (std::string filename, int line);

	SourceLocation ();


	void print(std::ostream& out) const;

	bool operator== (const SourceLocation& other) const;

	bool operator< (const SourceLocation& other) const;


	/**
	 * @return the fileName
	 */
	std::string getFileName() const {
		return fileName;
	}


	/**
	 * @param fileName the fileName to set
	 */
	void setFileName(std::string theFileName) {
		fileName = theFileName;
	}


	/**
	 * @return the lineNumber
	 */
	int getLineNumber() const {
		return lineNumber;
	}


	/**
	 * @param lineNumber the lineNumber to set
	 */
	void setLineNumber(int aLineNumber) {
		lineNumber = aLineNumber;
	}


};

inline
std::ostream& operator<< (std::ostream& out, const SourceLocation& sl)
{
	sl.print(out);
	return out;
}


}

#endif
