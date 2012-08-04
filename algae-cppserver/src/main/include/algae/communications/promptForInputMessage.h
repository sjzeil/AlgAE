/*
 * promptForInputMessage.h
 *
 *  Created on: Jul 11, 2012
 *      Author: zeil
 */

#ifndef PROMPTFORINPUTMESSAGE_H_
#define PROMPTFORINPUTMESSAGE_H_

#include <algae/communications/clientMessage.h>
#include <string>
#include <iostream>

namespace algae {

/**
 * Indicates that animated code is requesting input from the user.
 *
 *
 * @author zeil
 *
 */
class PromptForInputMessage: public ClientMessage
{
	/**
	 * Message to show to user when prompting for input
	 */
	 std::string prompt;

	/**
	 * A regular expression describing the acceptable format for responses from
	 * the user.
	 */
	std::string requiredPattern;

public:

	/**
	 * Construct a new client message
	 *
	 * @param messageKind type of message
	 */
	PromptForInputMessage(std::string aPrompt = std::string("Enter input:"),
				std::string aRequiredPattern = std::string(".*")) :
		ClientMessage ("PromptForInputMessage"), prompt(aPrompt), requiredPattern(aRequiredPattern)
	{ }

	const std::string& getPrompt() const {return prompt;}
	const std::string& getRequiredPattern() const {return requiredPattern;}

	virtual void printXML (std::ostream& out) const;

	virtual ClientMessage* clone() const;


	virtual bool operator== (const ClientMessage& other) const;


};

}
#endif /* CLIENTMESSAGE_H_ */
