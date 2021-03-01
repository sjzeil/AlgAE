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
#include <algae/memoryModel/activationStackRenderer.h>
#include <algae/rendering/typeRendering.h>

namespace algae {

class Renderer;

class ActivationStack
{
	std::vector <ActivationRecord*> activations;
	friend class ActivationStackRenderer;

public:
	typedef std::vector<ActivationRecord>::size_type size_type;
	typedef std::vector<ActivationRecord*>::iterator iterator;
	typedef std::vector<ActivationRecord*>::const_iterator const_iterator;

	void push (std::string functionName);
	void pop();

	size_type size() const;

	ActivationRecord& top();
	const ActivationRecord& top() const;

	/**
	 * Gets the most up-to-date renderer for the supplied object.
	 */
	Renderer* getRenderingOf(const Identifier& ident) const;


	/**
	 * Iterators
	 */
	iterator begin() {return activations.begin();}
	const_iterator begin() const {return activations.begin();}

	iterator end() {return activations.end();}
	const_iterator end() const {return activations.end();}

};


/**
 * Specialization for rendering of types that specialize CanBeRendered
 */
template <typename T>
class TypeRendering<sameClassAs(T, ActivationStack)> {
public:
	const TypeRenderer* getRenderer (const ActivationStack& anObject) const
	{
		return new ActivationStackRenderer(anObject);
	}
};


}


#endif
