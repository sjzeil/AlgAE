/*
 * SnapshotMessage.h
 *
 *  Created on: Jun 10, 2012
 *      Author: zeil
 */

#ifndef SNAPSHOTMESSAGE_H_
#define SNAPSHOTMESSAGE_H_

#include <algae/communications/clientMessage.h>
#include <algae/snapshot/snapshotDiff.h>
#include <string>
#include <iostream>

namespace algae {

/**
 * Indicates that the animation has reached a breakpoint and has prepared a snapshot
 * of the current memory state for display by the client.
 *
 *
 * @author zeil
 *
 */
class SnapshotMessage: public ClientMessage
{

	SnapshotDiff snapshot;
	bool menuItemCompleted;

public:

	/**
	 * Construct a new client message
	 *
	 * @param messageKind type of message
	 */
	SnapshotMessage() :
		ClientMessage ("Snapshot"), snapshot(Snapshot(), Snapshot()), menuItemCompleted(false)
	{ }

	SnapshotMessage(const SnapshotDiff& snap, bool menuItemIsCompleted) :
			ClientMessage ("Snapshot"), snapshot(snap), menuItemCompleted(menuItemIsCompleted)
		{ }


	bool getMenuItemCompleted () const {return menuItemCompleted;}
	const SnapshotDiff& getSnapshot() const {return snapshot;}


	virtual void printXML (std::ostream& out) const;

	virtual ClientMessage* clone() const;


	virtual bool operator== (const ClientMessage& other) const;


};

}
#endif /* CLIENTMESSAGE_H_ */
