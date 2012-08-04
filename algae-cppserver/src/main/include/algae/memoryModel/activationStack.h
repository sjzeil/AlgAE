/**
 * activationStack.h
 *
 *
 *  Created on: June 17, 2012
 *      Author: zeil
 */

#ifndef ACTIATIONSTACK_H_
#define ACTIATIONSTACK_H_

#include <string>
#include <iostream>
#include <vector>

#include <algae/memoryModel/activationRecord.h>

namespace algae {

class ActivationStack
{
	std::vector <ActivationRecord> activations;
	friend class ActivationStackRendering;

public:
	void push (std::string functionName);
	void pop();
	std::vector<ActivationRecord>::size_type size() const;

	ActivationRecord& top();
	const ActivationRecord& top() const;

};

}


#endif
