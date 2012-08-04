/**
 * activationRecord.h
 *
 *
 *  Created on: Jul 14, 2012
 *      Author: zeil
 */

#ifndef ACTIVATIONRECORD_H_
#define ACTIVATIONRECORD_H_

#include <string>
#include <iostream>
#include <list>

#include <algae/snapshot/entityIdentifier.h>

namespace algae {

class ActivationRecord
{
	std::string name;
	bool isOnTop;


	std::list<EntityIdentifier> parameters;
	std::list<EntityIdentifier> locals;
	EntityIdentifier* thisParam;


	friend class ActivationRecordRendering;

public:

	typedef std::list<EntityIdentifier>::const_iterator const_iterator;
	typedef std::list<EntityIdentifier>::iterator iterator;

	const_iterator beginParams() const;
	iterator beginParams();

	const_iterator endParams() const;
	iterator endParams();

	const_iterator beginLocals() const;
	iterator beginLocals();

	const_iterator endLocals() const;
	iterator endLocals();

};

}


#endif
