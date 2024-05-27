package edu.odu.cs.AlgAE.Demos;

import java.awt.Color;
import java.util.ArrayList;

import edu.odu.cs.AlgAE.Animations.LocalJavaAnimation;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;
import edu.odu.cs.AlgAE.Server.LocalServer;
import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.MemoryModel.ActivationRecord;
import edu.odu.cs.AlgAE.Server.Rendering.DefaultRenderer;
import edu.odu.cs.AlgAE.Server.Rendering.VerticalRenderer;
import edu.odu.cs.AlgAE.Server.Utilities.Index;
import edu.odu.cs.AlgAE.Server.Utilities.IndexedArray;

public class IndexedArrayDemo extends LocalJavaAnimation {


    public IndexedArrayDemo() {
		super("Indexed Array Demo");
	}

	@Override
	public String about() {
		return "This is a\n demo of arrays and array-like structures.";
	}

	private Integer[] a1;
	private String[] a2;
	private double[][] a3;
    private IndexedArray<Integer> a1Shadow;
	private IndexedArray<String> a2Shadow;
	


	void animatedFunction (Integer[] param1) {
		ActivationRecord aRec = LocalServer.activate(IndexedArrayDemo.class);
		aRec.refParam("param1", param1);
		aRec.breakHere("about to call");
		animatedFunction2(param1);
		aRec.breakHere("called");
	}

	void animatedFunction2 (Integer[] param1) {
		ActivationRecord aRec = LocalServer.activate(IndexedArrayDemo.class);
		aRec.refParam("param1", a1);
		aRec.breakHere("show arrays 1");
		aRec.var("a2",a2);
		aRec.breakHere("show arrays 2");
		aRec.pushScope();
		aRec.var("a3",a3);
		aRec.breakHere("show arrays 3");
		Index index = new Index(0, a1, a2);
		for (int i = 0; i < Math.min(a1.length, a2.length); ++i) {
			index.set(i);
			aRec.var("i",index);
			aRec.breakHere("looking at " + i);
		}
		aRec.popScope();
		aRec.breakHere("show arrays 2 again");
	}
	
    static private int dirCounter = 0;

	
	@Override
	public void buildMenu() {
		
		
		
		registerStartingAction(new MenuFunction() {

			@Override
			public void selected() {
				a1 = new Integer[8];
				for (int i = 0; i < a1.length; ++i)
					a1[i] = i;
                a1Shadow = new IndexedArray<>(a1);
				a2 = new String[4];
				for (int i = 0; i < a2.length; ++i)
					a2[i] = "" + (char)('A' + i);
                a2Shadow = new IndexedArray<>(a2);
                a2Shadow.setColor(null);
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
				ActivationRecord aRec = LocalServer.activate(IndexedArrayDemo.class);
                int i = 1;
                a1Shadow.indexedBy(i, "i");
				aRec.var("a1", a1Shadow).var("i",i).breakHere("show arrays 1");			
			}
		});
		
		register ("one array", new MenuFunction() {
			@Override
			public void selected() {
				globalVar("a1g", a1);
				ActivationRecord aRec = LocalServer.activate(IndexedArrayDemo.class);
				//aRec.var("a1", a1);
				aRec.breakHere("show arrays 1");
				aRec.breakHere("show arrays 2");
				aRec.breakHere("show arrays 3");
			}
		});


		register ("vertical array", new MenuFunction() {
			@Override
			public void selected() {
                ++dirCounter;
                DefaultRenderer.setDefaultArrayDirection(
                    (dirCounter % 2 == 0) 
                    ? Directions.Horizontal : Directions.Vertical);
				//globalVar("a1g", a1);
				ActivationRecord aRec = LocalServer.activate(IndexedArrayDemo.class);
				//aRec.var("a1", a1);
                aRec.render(new VerticalRenderer<>(a1));
				aRec.breakHere("show arrays 1");
				aRec.breakHere("show arrays 2");
				aRec.breakHere("show arrays 3");
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
				ActivationRecord aRec = LocalServer.activate(IndexedArrayDemo.class);
				ArrayList<Integer> a = new ArrayList<Integer>();
				aRec.var("a", a).breakHere("show list");
				for (int i = 0; i < 3; ++i) {
					a.add(i);
					aRec.highlight(a.get(i));
					aRec.var("i",i).breakHere("added " + i);
					aRec.highlight(a.get(i));
				}
				for (int i = 0; i < 3; ++i) {
					aRec.var("i",new Index(i,a)).breakHere("looking at " + i);
				}
			}
		});

		

		register ("highlight", new MenuFunction() {
			@Override
			public void selected() {
				ActivationRecord aRec = LocalServer.activate(IndexedArrayDemo.class);
				aRec.var("a2", a2Shadow).breakHere("show array");
				for (int i = 0; i < 3; ++i) {
					aRec.highlight(a2[i]);
                    a2Shadow.indexedBy(i, "i");
					aRec.var("i",i).breakHere("highlight " + i);
				}
			}
		});
		
	}

	
	
	public static void main (String[] args) {
		IndexedArrayDemo demo = new IndexedArrayDemo();
		demo.runAsMain();
	}

}
