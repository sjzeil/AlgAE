package edu.odu.cs.AlgAE.Demos;//!

import static edu.odu.cs.AlgAE.Server.LocalAnimation.activate;
import edu.odu.cs.AlgAE.Server.MemoryModel.ActivationRecord;

public class SinglyLinkedLists0  {


	class nodeType 
	{
		int info;
		nodeType link;

		public nodeType()
		{
			info = -999;//!
			link = null;
		}

	}

	public nodeType head = null;


	public void addToFront (int k)
	{
		nodeType newNode = new nodeType();
		newNode.link = head;
		newNode.info = k;
		head = newNode;
	}



	public void insert (nodeType p, int value)
	{
		ActivationRecord arec = activate(this);//!
		arec.refParam("p", p).param("value", value);//!
		arec.breakHere("starting insertion");//!
		nodeType newNode = new nodeType();//!    nodeType *newNode = new nodeType;
		arec.refVar("newNode", newNode);//!
		arec.breakHere("allocated new node");//!
		newNode.info = value;
		arec.breakHere("inserted data into new node");//!
		newNode.link = p.link;//!    newNode->link = p->link;
		arec.breakHere("make new node point to p's successor");//!
		p.link = newNode;//!    p->link = newNode;
		arec.breakHere("make p point to the new node");//!
		arec.breakHere("Insertion has been completed");//!
		arec.breakHere("Trace the 'next' links and see for yourself that ...");//!
		arec.breakHere("...the new node was inserted right after p.");//!
	}


}
