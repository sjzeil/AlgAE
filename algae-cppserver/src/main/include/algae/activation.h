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

#include <algae/snapshot/color.h>
#include <algae/rendering/renderer.h>


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


class ActivationRecord;
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
	template <typename T>
	Activation& render(const T& object, const Renderer& newRendering);



	/**
	 * Show a variable as a parameter of the current activation
	 * Variables portrayed by this call are shown "in-line".
	 *
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 */
	template <typename Object>
	Activation& param(std::string label, const Object& value);


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
	void refParam (std::string  label, const Object& value);


	/**
	 * Show a variable as a local variable of the current activation
	 * Variables portrayed by this call are shown "in-line".
	 *
	 * @param label  the variable name (optional, can be "" or null)
	 * @param param  the variable/value
	 */
	template <typename Object>
	Activation& var(std::string label, const Object& value);


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
	Activation& refVar (std::string  label, const Object& value);


	/**
	 * Change the rendering of the indicated object to highlight it.
	 * This stays in effect until the end of the current scope/activation.
	 *
	 */
	template <typename Object>
	Activation& highlight (const Object& value);

	template <typename Object>
	Activation& highlight (const Object& value, Color c);


	/**
	 * Reverse the effect of a prior highlight call
	 *
	 */
	template <typename Object>
	Activation& unhighlight (const Object& value);



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
