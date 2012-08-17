/**
 * activation.h
 *
 *
 *  Created on: June 19, 2012
 *      Author: zeil
 */

#ifndef ACTIVATION_H_
#define ACTIVATION_H_

#include <string>
#include <typeinfo>

#include <algae/memoryModel/activationRecord.h>
#include <algae/snapshot/color.h>
#include <algae/rendering/objectRenderer.h>


/**
 * An Activation records information about a function call being animated.
 *
 * This is the fundamental interface between algorithms being animated
 * and the AlgAE animation system.
 *
 * @author zeil
 *
 */


namespace algae {


class Scope;

class Activation
{
private:
	ActivationRecord* arecord;

	// Do not permit copying...
	Activation (const Activation& a) {}

	// ...or assignment of these objects
	void operator= (const Activation& a) {}

public:

	/**
	 * Create a new activation for a non-member (no "this") function
	 */
	Activation (std::string functionName);


	/**
	 * Create a new activation for a member function with the indicated "this" pointer
	 */
	template <typename T>
	Activation (const T* thisObj, std::string functionName);


	/**
	 * Destruction of the Activation object signals to the animator that we have returned from
	 * a function call being animated.
	 */
	~Activation();


	/**
	 * Establish a rendering for a specific object. This rendering will only
	 * remain in effect until the current activation/scope is exited.
	 */
	Activation& render(ObjectRenderer& newRendering)
	{
		arecord->render (newRendering);
		return *this;
	}



	/**
	 * Show a variable as a parameter of the current activation
	 * Variables portrayed by this call are shown "in-line".
	 *
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 */
	template <typename Object>
	Activation& param(std::string label, const Object& value)
	{
		EntityIdentifier eid(Identifier(value), label);
		arecord->param(label, eid);
		return this;
	}


	/**
	 * Show a variable as a parameter of the current activation.
	 * Variables portrayed by this call are shown as labeled
	 * pointers to the actual value.
	 *
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 * @return a reference to this breakpoint
	 */
	template <typename Object>
	Activation& refParam (std::string  label, const Object& value)
	{
		EntityIdentifier eid(Identifier(value));
		arecord->refParam(label, eid);
		return this;
	}


	/**
	 * Show a variable as a local variable of the current activation
	 * Variables portrayed by this call are shown "in-line".
	 *
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 */
	template <typename Object>
	Activation& var(std::string label, const Object& value)
	{
		EntityIdentifier eid(Identifier(value), label);
		arecord->var(label, eid);
		return this;
	}


	/**
	 * Show a variable as a local variable of the current activation.
	 * Variables portrayed by this call are shown as labeled
	 * pointers to the actual value.
	 *
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 * @return a reference to this breakpoint
	 */
	template <typename Object>
	Activation& refVar (std::string  label, const Object& value)
	{
		EntityIdentifier eid(Identifier(value));
		arecord->refVar(label, eid);
		return this;
	}


	/**
	 * Change the rendering of the indicated object to highlight it.
	 * This stays in effect until the end of the current scope/activation.
	 *
	 */
	template <typename Object>
	Activation& highlight (const Object& value)
	{
		Identifier oid (value);
		arecord->highlight (oid);
		return this;
	}

	template <typename Object>
	Activation& highlight (const Object& value, Color c)
	{
		Identifier oid (value);
		arecord->highlight (oid, c);
		return this;
	}



	/**
	 * Reverse the effect of a prior highlight call
	 *
	 */
	template <typename Object>
	Activation& unhighlight (const Object& value)
	{
		Identifier oid (value);
		arecord->unhighlight (oid);
		return this;
	}



	/**
	 * Take a snapshot of the current program state and send it to the animator.
	 *
	 * This function is not normally called directly, but is invoked via the breakHere macro.
	 *
	 * @param description short message to be displayed by the animator explaining the current circumstances
	 *                       the break
	 * @param fileName  name of the file in which the breakpoint occurs.
	 * @param lineNum   line number in that file where the breakpoint occurs
	 */
	Activation& breakPoint (std::string description, const char* fileName, int lineNumber);




};


#define breakHere(description)  breakPoint(description,__FILE__,__LINE__)

}


#endif
