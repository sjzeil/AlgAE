/**
 * activationStack.cpp
 *
 *
 *  Created on: Aug 15, 2012
 *      Author: zeil
 */


#include <algae/memoryModel/activationStack.h>
#include <algae/memoryModel/activationRecord.h>
#include <algae/rendering/objectRenderer.h>
#include <algae/rendering/typeRenderer.h>

using namespace std;

namespace algae {

/*
	std::vector <ActivationRecord> activations;
	friend class ActivationStackRendering;

public:
*/

void ActivationStack::push (std::string functionName)
{
	activations.push_back (new ActivationRecord(functionName, this));
}


void ActivationStack::pop()
{
	delete activations[activations.size()-1];
	activations.pop_back();
}


std::vector<ActivationRecord>::size_type ActivationStack::size() const
{
	return activations.size();
}

ActivationRecord& ActivationStack::top()
{
	return *activations[activations.size()-1];
}


const ActivationRecord& ActivationStack::top() const
{
	return *activations[activations.size()-1];
}

/**
 * Gets the most up-to-date renderer for the supplied object.
 */
Renderer* ActivationStack::getRenderingOf(const Identifier& ident) const
{
	Renderer* renderer = 0;
	for (size_type i = activations.size(); renderer == 0 && i > 0 ; --i)
	{
		const ActivationRecord* arec = activations[i-1];
		for (ActivationRecord::const_render_iterator it = arec->beginRenderings(); it != arec->endRenderings(); ++it)
		{
			ObjectRenderer* orend = *it;
			if (orend->getRenders() == ident)
				renderer = orend;
		}
	}
	if (renderer != 0)
		return renderer;
	else
		return (Renderer*)ident.getType();
}




}

