package edu.odu.cs.AlgAE.Demos;

import java.util.ArrayList;

import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.Animations.LocalJavaAnimation;
import edu.odu.cs.AlgAE.Server.MemoryModel.ActivationRecord;
import edu.odu.cs.AlgAE.Server.MemoryModel.ActivationStack;
import edu.odu.cs.AlgAE.Server.Utilities.Index;

public class ArrayDemo extends LocalJavaAnimation {

	public ArrayDemo() {
		super("Array Demo");
	}

	@Override
	public String about() {
		return "This is a\n demo of arrays and array-like structures.";
	}

	private int[] a1;
	private String[] a2;
	private double[][] a3;
	


	
	
	@Override
	public void buildMenu() {
		
		
		
		registerStartingAction(new MenuFunction() {

			@Override
			public void selected() {
				a1 = new int[8];
				for (int i = 0; i < a1.length; ++i)
					a1[i] = i;
				a2 = new String[4];
				for (int i = 0; i < a2.length; ++i)
					a2[i] = "" + (char)('A' + i);
				a3 = new double[3][4];
				for (int i = 0; i < 3; ++i) {
					for (int j = 0; j < 4; ++j)
						a3[i][j] = ((double)(10*i + j))/10.0;
				}
			}
			
		});
		
		
		register ("index", new MenuFunction() {
			@Override
			public void selected() {
				ActivationStack stack = getMemoryModel().getActivationStack();
				ActivationRecord arec = stack.activate(ArrayDemo.class);
				arec.var("a1", a1).var("i",new Index(1,a1)).breakHere("show arrays 1");			
			}
		});

		register ("arrays", new MenuFunction() {
			@Override
			public void selected() {
				ActivationRecord arec = LocalJavaAnimation.activate(ArrayDemo.class);
				arec.var("a1", a1);
				arec.breakHere("show arrays 1");
				arec.var("a2",a2);
				arec.breakHere("show arrays 2");
				arec.pushScope();
				arec.var("a3",a3);
				arec.breakHere("show arrays 3");
				for (int i = 0; i < Math.min(a1.length, a2.length); ++i) {
					arec.var("i",new Index(i,a1,a2));
					arec.breakHere("looking at " + i);
				}
				arec.popScope();
				arec.breakHere("show arrays 2 again");
			}
		});

		
		register ("arrayLists", new MenuFunction() {
			@Override
			public void selected() {
				ActivationRecord arec = LocalJavaAnimation.activate(ArrayDemo.class);
				ArrayList<Integer> a = new ArrayList<Integer>();
				arec.var("a", a).breakHere("show list");
				for (int i = 0; i < 3; ++i) {
					a.add(i);
					arec.highlight(a.get(i));
					arec.var("i",i).breakHere("added " + i);
					arec.highlight(a.get(i));
				}
				for (int i = 0; i < 3; ++i) {
					arec.var("i",new Index(i,a)).breakHere("looking at " + i);
				}
			}
		});

		

		register ("highlight", new MenuFunction() {
			@Override
			public void selected() {
				ActivationRecord arec = LocalJavaAnimation.activate(ArrayDemo.class);
				arec.var("a2", a2).breakHere("show array");
				for (int i = 0; i < 3; ++i) {
					arec.highlight(a2[i]);
					arec.var("i",i).breakHere("highlight " + i);
				}
			}
		});
		
	}

	
	
	public static void main (String[] args) {
		ArrayDemo demo = new ArrayDemo();
		demo.runAsMain();
	}

}
