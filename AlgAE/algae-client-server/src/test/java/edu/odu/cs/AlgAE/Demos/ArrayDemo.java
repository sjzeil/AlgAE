package edu.odu.cs.AlgAE.Demos;

import java.util.ArrayList;

import edu.odu.cs.AlgAE.Server.LocalAnimation;
import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.Animations.LocalJavaAnimationApplet;
import edu.odu.cs.AlgAE.Server.MemoryModel.ActivationRecord;
import edu.odu.cs.AlgAE.Server.Utilities.Index;

public class ArrayDemo extends LocalJavaAnimationApplet {

	public ArrayDemo() {
		super("Array Demo");
	}

	@Override
	public String about() {
		return "This is a\n demo of arrays and array-like structures.";
	}

	private Integer[] a1;
	private String[] a2;
	private double[][] a3;
	


	void animatedFunction (Integer[] param1) {
		ActivationRecord arec = LocalAnimation.activate(ArrayDemo.class);
		arec.refParam("param1", param1);
		arec.breakHere("about to call");
		animatedFunction2(param1);
		arec.breakHere("called");
	}

	void animatedFunction2 (Integer[] param1) {
		ActivationRecord arec = LocalAnimation.activate(ArrayDemo.class);
		arec.refParam("param1", a1);
		arec.breakHere("show arrays 1");
		arec.var("a2",a2);
		arec.breakHere("show arrays 2");
		arec.pushScope();
		arec.var("a3",a3);
		arec.breakHere("show arrays 3");
		Index index = new Index(0, a1, a2);
		for (int i = 0; i < Math.min(a1.length, a2.length); ++i) {
			index.set(i);
			arec.var("i",index);
			arec.breakHere("looking at " + i);
		}
		arec.popScope();
		arec.breakHere("show arrays 2 again");
	}
	
	
	
	@Override
	public void buildMenu() {
		
		
		
		registerStartingAction(new MenuFunction() {

			@Override
			public void selected() {
				a1 = new Integer[8];
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
				ActivationRecord arec = LocalAnimation.activate(ArrayDemo.class);
				arec.var("a1", a1).var("i",new Index(1,a1)).breakHere("show arrays 1");			
			}
		});
		
		register ("one array", new MenuFunction() {
			@Override
			public void selected() {
				globalVar("a1g", a1);
				ActivationRecord arec = LocalAnimation.activate(ArrayDemo.class);
				//arec.var("a1", a1);
				arec.breakHere("show arrays 1");
				arec.breakHere("show arrays 2");
				arec.breakHere("show arrays 3");
			}
		});


		register ("arrays", new MenuFunction() {
			@Override
			public void selected() {
				animatedFunction(a1);
			}
		});

		
		register ("arrayLists", new MenuFunction() {
			@Override
			public void selected() {
				ActivationRecord arec = LocalAnimation.activate(ArrayDemo.class);
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
				ActivationRecord arec = LocalAnimation.activate(ArrayDemo.class);
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
