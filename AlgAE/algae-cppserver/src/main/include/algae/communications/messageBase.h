/*
 * messageBase.h
 *
 *  Created on: Jun 10, 2012
 *      Author: zeil
 */

#ifndef MESSAGEBASE_H_
#define MESSAGEBASE_H_

#include <iostream>
#include <string>

namespace algae {


/**
 * Base class for both client and server message
 * @author zeil
 *
 */
class MessageBase {
protected:
	std::string kind;
	
public:

	MessageBase (std::string messageKind)
	: kind(messageKind)
	{
	}

	virtual ~MessageBase() {}

    std::string getKind() const
    {
        return kind;
    }
	

    virtual void print (std::ostream& out) const = 0;

};

}
#endif /* MESSAGEBASE_H_ */
