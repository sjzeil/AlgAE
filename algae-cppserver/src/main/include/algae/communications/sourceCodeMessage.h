/*
 * sourceCodeMessage.h
 *
 *  Created on: Jul 11, 2012
 *      Author: zeil
 */

#ifndef SOURCECODEMESSAGE_H_
#define SOURCECODEMESSAGE_H_

#include <algae/communications/clientMessage.h>
#include <string>
#include <iostream>

namespace algae {

/**
 * Server is sending a file of source code to the client for possible display.
 *
 * This message may be sent in response to an earlier getSourceCode message from the
 * client or it may be sent "spontaneously" by the server if it predicts that such a
 * display will likely be required.
 *
 *
 * @author zeil
 *
 */
class SourceCodeMessage: public ClientMessage
{
	/**
	 * identifier of the source code
	 */
	 std::string filePath;

	/**
	 * Full text of the source code file.
	 */
	std::string sourceText;

public:

	/**
	 * Construct a new client message
	 *
	 * @param messageKind type of message
	 */
	SourceCodeMessage(std::string aFilePath = std::string(),
				std::string aSourceText = std::string()) :
		ClientMessage ("SourceCodeMessage"), filePath(aFilePath), sourceText(aSourceText)
	{ }

	const std::string& getFilePath() const {return filePath;}
	const std::string& getSourceText() const {return sourceText;}

	virtual void printXML (std::ostream& out) const;

	virtual ClientMessage* clone() const;


	virtual bool operator== (const ClientMessage& other) const;


};

}
#endif /* CLIENTMESSAGE_H_ */
