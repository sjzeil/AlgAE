/*
 * capturedOurputMessage.h
 *
 *  Created on: July 8, 2012
 *      Author: zeil
 */

#ifndef CAPTUREDOUTPUTMESSAGE_H_
#define CAPTUREDOUTPUTMESSAGE_H_

#include <algae/communications/clientMessage.h>
#include <string>
#include <iostream>

namespace algae {

/**
 * Indicates that animated code has written some text to standard output
 *
 * @author zeil
 *
 */
class CapturedOutputMessage: public ClientMessage
{
	std::string output;
public:

	/**
	 * Construct a new client message
	 *
	 * @param messageKind type of message
	 */
	CapturedOutputMessage(std::string outputString = std::string()) :
		ClientMessage ("CapturedOutput"), output(outputString)
	{ }


	const std::string& getOutput() const {return output;}
	void setOutput(const std::string& outputString) {output = outputString;}

	virtual void printXML (std::ostream& out) const;

	virtual ClientMessage* clone() const;


	virtual bool operator== (const ClientMessage& other) const;


};

}
#endif /* CLIENTMESSAGE_H_ */
