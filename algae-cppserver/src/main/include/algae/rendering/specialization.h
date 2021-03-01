/**
 * specialization.h
 *
 *  Support for template specialization
 *
 *  Created on: June 27, 2012
 *      Author: zeil
 */

#ifndef SPECIALIZATION_H_
#define SPECIALIZATION_H_


namespace algae {

template<bool C, typename T = void>
struct enable_if {
	typedef T type;
};

template<typename T>
struct enable_if<false, T> { };

template<typename, typename>
struct is_same {
	static bool const value = false;
};

template<typename A>
struct is_same<A, A> {
	static bool const value = true;
};

template<typename B, typename D>
struct is_base_of {
	static D * create_d();
	static char (& chk(B *))[1];
	static char (& chk(...))[2];
	static bool const value =
			sizeof chk(create_d()) == 1 &&
			!is_same<B    volatile const,
			void volatile const>::value;
};

/*
 * E.g.,

struct ABaseClass { };
struct InheritingClass : public ABaseClass { };
struct SomeOtherClass { };

template<typename T, typename = void>
struct Specializable {  }; // General, unspecialized declaration

template<typename T>
struct Specializable <T, typename enable_if<is_base_of<ABaseClass, T>::value>::type> {
	// specialized content
    typedef int isSpecialized;
};

void foo() {
    Specializable<ABaseClass>::isSpecialized test1;        // ok
    Specializable<InheritingClass>::isSpecialized test2;   // ok
    Specializable<SomeOtherClass> test3;                   // ok
    Specializable<SomeOtherClass>::isSpecialized test4;    // will not compile
}
*/


#define anySubClassOf(T, U) T, typename enable_if<is_base_of<U, T>::value>::type

#define sameClassAs(T, U) T, typename enable_if<is_same<U, T>::value>::type


}

#endif
