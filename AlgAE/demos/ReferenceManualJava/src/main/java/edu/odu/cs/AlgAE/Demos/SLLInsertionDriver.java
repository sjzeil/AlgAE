package edu.odu.cs.AlgAE.Demos;


import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.Animations.LocalJavaAnimationApplet;

public class SLLInsertionDriver extends LocalJavaAnimationApplet {

	public SLLInsertionDriver() {
		super("Linked List Insertions");
	}

	@Override
	public String about() {
		return "Demonstration of Linked Lists Algorithms,\n" +
				"prepared for CS 333, Programming and Problem\n" +
				"Solving in C++, Old Dominion University\n" +
				"Fall 2010";
	}

	SinglyLinkedLists sll = new SinglyLinkedLists();


	
	
	@Override
	public void buildMenu() {
		
		
/*		
		registerStartingAction(new MenuFunction() {

			@Override
			public void selected() {
				getAnimator().setSpeed(30);
				for (int k = 0; k < 1000; ++k) {
					generateLL();					
					sll.insert(sll.head.link, 50);
					try {
						Thread.sleep(2500);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
			
		});
*/
		
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
		SLLInsertionDriver demo = new SLLInsertionDriver();
		demo.runAsMain();
	}

}
