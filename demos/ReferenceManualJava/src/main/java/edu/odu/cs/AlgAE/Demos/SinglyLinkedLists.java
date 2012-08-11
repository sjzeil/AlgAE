package edu.odu.cs.AlgAE.Demos;//!

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import static edu.odu.cs.AlgAE.Server.Animations.LocalJavaAnimation.activate;
import edu.odu.cs.AlgAE.Server.MemoryModel.ActivationRecord;
import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;
import edu.odu.cs.AlgAE.Server.Utilities.SimpleReference;


public class SinglyLinkedLists implements CanBeRendered<SinglyLinkedLists>, Renderer<SinglyLinkedLists> {//!


class nodeType implements CanBeRendered<nodeType>, Renderer<nodeType>
{
    int info;
    nodeType link;
    
    public nodeType()
    {
    	info = -999;//!
    	link = null;
    }

	public Renderer<nodeType> getRenderer() {//!
		return this;//!
	}//!
	public Color getColor(nodeType obj) {//!
		return Color.green.darker();//!
	}//!
	public List<Component> getComponents(nodeType obj) {
		LinkedList<Component> data = new LinkedList<Component>();//!
		data.add (new Component(info, "info"));//!
		return data;//!
	}//!
	public List<Connection> getConnections(nodeType obj) {//!
		LinkedList<Connection> links = new LinkedList<Connection>();//!
		Connection c =  new Connection(link, 85.0, 95.0);//!
		c.setLabel("link");
		links.add(c);//!
		return links;//!
	}//!
	public int getMaxComponentsPerRow(nodeType obj) {//!
		return 100;//!
	}//!
	public String getValue(nodeType obj) {//!
		return "";//!
	}//!
}

nodeType head = null;


public void addToFront (int k)
{
   nodeType newNode = new nodeType();
   newNode.link = head;
   newNode.info = k;
   head = newNode;
}

public void setUp ()
{
	head = null;
	addToFront(45);
	addToFront(63);
	addToFront(92);
	addToFront(17);
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

@Override
public Renderer<SinglyLinkedLists> getRenderer() {
	return this;
}

@Override
public Color getColor(SinglyLinkedLists obj) {
	return null;
}

@Override
public List<Component> getComponents(SinglyLinkedLists obj) {
	LinkedList<Component> components = new LinkedList<Component>();
	components.add (new Component(new SimpleReference(head, 180.0, 180.0), "head"));
	return components;
}

@Override
public List<Connection> getConnections(SinglyLinkedLists obj) {
	return new LinkedList<Connection>();
}

@Override
public int getMaxComponentsPerRow(SinglyLinkedLists obj) {
	return 100;
}

@Override
public String getValue(SinglyLinkedLists obj) {
	return "";
}

        
}
