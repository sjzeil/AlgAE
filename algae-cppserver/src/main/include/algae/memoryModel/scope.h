/**
 * scope.h
 *
 *
 *  Created on: Aug 18, 2012
 *      Author: zeil
 */

#ifndef SCOPE_H_
#define SCOPE_H_


#include <algae/memoryModel/activationRecord.h>

namespace algae {

/**
 * A Scope object can be placed at the top of any bracketed ({ }) statement list
 * in animated code.
 *
 * It remembers the number of local variables currently in the topmost activation record.
 * Upon destruction, it removes any local variables in the current activation that were added
 * after its construction.  Such local variables are thereby modeled as having lifetimes bounded
 * by that bracketed statement list;
 */
class Scope {
	bool arWasEmpty;
	ActivationRecord::iterator lastLocalVariable;

public:
	Scope();

	~Scope();

};

}


#endif
