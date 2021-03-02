package edu.odu.cs.AlgAE.Demos;


import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Animations.LocalJavaAnimation;

public class SLLInsertionDriver0 extends LocalJavaAnimation {

	public SLLInsertionDriver0() {
		super("Linked List Insertions");
	}

	@Override
	public String about() {
		return "Demonstration of Linked Lists Algorithms,\n" +
				"prepared for CS 333, Programming and Problem\n" +
				"Solving in C++, Old Dominion University\n" +
				"Fall 2010";
	}

	SinglyLinkedLists0 sll = new SinglyLinkedLists0();


	
	
	@Override
	public void buildMenu() {
		register ("Insert a node", new MenuFunction() {

			@Override
			public void selected() {
				generateLL();					
				sll.insert(sll.head.link, 50);
			}
			
		});
		
	}
	
	public void generateLL()
	{
		sll.head = null;
		sll.addToFront(76);
		sll.addToFront(34);
		sll.addToFront(65);
		sll.addToFront(45);
	}


	
	
	
	public static void main (String[] args) {
		SLLInsertionDriver0 demo = new SLLInsertionDriver0();
		demo.runAsMain();
	}

}
