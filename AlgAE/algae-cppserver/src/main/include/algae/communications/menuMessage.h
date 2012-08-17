/*
 * menuMessage.h
 *
 *  Created on: Jul 11, 2012
 *      Author: zeil
 */

#ifndef MENUMESSAGE_H_
#define MENUMESSAGE_H_

#include <algae/communications/clientMessage.h>
#include <string>
#include <iostream>
#include <vector>

namespace algae {

/**
 * Sent as an animation is starting, this message tells the client
 * what text should appear in the Help->About menu and what menu items
 * should be placed in the Algorithm menu listing functions that users
 * can select to see animations of.
 *
 * @author zeil
 *
 */
class MenuMessage: public ClientMessage
{

	std::string about;
	std::vector<std::string> menuItems;
public:

	/**
	 * @param about text to appear in the Help menu
	 * @param start: position of first string to be used as menu items
	 * @param stop: position after last string to be used as a menu item
	 */
	template <typename Iterator>
	MenuMessage(std::string aboutString, Iterator start, Iterator stop) :
		ClientMessage ("Menu"), about(aboutString), menuItems(start, stop)
	{ }


	int getNumMenuItems() const {return menuItems.size();}
	const std::string& getMenuItem (int itemNum) const {return menuItems[itemNum];}

	const std::string& getAbout () const {return about;}


	virtual void printXML (std::ostream& out) const;

	virtual ClientMessage* clone() const;


	virtual bool operator== (const ClientMessage& other) const;


};

}
#endif /* CLIENTMESSAGE_H_ */
