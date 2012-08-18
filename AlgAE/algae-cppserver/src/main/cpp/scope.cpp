/**
 * scope.cpp
 *
 *
 *  Created on: Aug 18, 2012
 *      Author: zeil
 */


#include <algae/memoryModel/scope.h>
#include <algae/animation.h>
#include <algae/memoryModel/memoryModel.h>

using namespace algae;

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


Scope::Scope()
{
	Animation* anim = Animation::algae();
	MemoryModel& mem = anim->getMemoryModel();
	ActivationStack& stack = mem.getActivationStack();
	ActivationRecord& arecord = stack.top();
	lastLocalVariable = arecord.endLocals();
	if (lastLocalVariable != arecord.beginLocals())
	{
		arWasEmpty = false;
		--lastLocalVariable;
	}
	else
	{
		arWasEmpty = true;
	}
}

Scope::~Scope()
{
	Animation* anim = Animation::algae();
	MemoryModel& mem = anim->getMemoryModel();
	ActivationStack& stack = mem.getActivationStack();
	ActivationRecord& arecord = stack.top();
	if (arWasEmpty)
	{
		arecord.locals.clear();
	}
	else
	{
		ActivationRecord::iterator after = lastLocalVariable;
		++after;
		arecord.locals.erase (after, arecord.locals.end());
	}

}



}

