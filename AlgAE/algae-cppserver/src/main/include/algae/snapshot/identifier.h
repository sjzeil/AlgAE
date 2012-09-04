/**
 * identifier.h
 *
 * Unique identifiers for any value in memory.
 *
 * Identifiers also carry info about the type of that value so that
 * a value can be properly rendered, given only its identifier.
 *
 *  Created on: June 17, 2012
 *      Author: zeil
 */

#ifndef _IDENTIFIER_H_
#define _IDENTIFIER_H_

#include <iostream>


namespace algae {

class TypeRenderer;

class Identifier {
	const void* id;
	const TypeRenderer* type;

	friend std::ostream& operator<< (std::ostream& out, const Identifier& ident);
public:
	template <class T>
	Identifier (const T& t);

	Identifier()
	: id(0), type(0)
	{}

	~Identifier();

	Identifier (const Identifier& ident);

	Identifier& operator= (const Identifier& ident);

	static Identifier nullID() {return Identifier();}

	const TypeRenderer* getType() const {return type;}


	bool operator== (const Identifier& ident) const {return id == ident.id;}
	bool operator!= (const Identifier& ident) const {return id != ident.id;}
	bool operator< (const Identifier& ident) const {return id < ident.id;}

	void printXML (std::ostream& out) const;

	void* getKey() const { return (void*)id; }
};

inline
std::ostream& operator<< (std::ostream& out, const Identifier& ident)
{
	out << ident.id;
	return out;
}

}

#endif
