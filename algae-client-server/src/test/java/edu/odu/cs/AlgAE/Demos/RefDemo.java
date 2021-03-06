package edu.odu.cs.AlgAE.Demos;

import edu.odu.cs.AlgAE.Animations.LocalJavaAnimation;
import edu.odu.cs.AlgAE.Server.LocalServer;
import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.MemoryModel.ActivationRecord;
import edu.odu.cs.AlgAE.Server.Utilities.SimpleReference;

public class RefDemo extends LocalJavaAnimation {

	public RefDemo() {
		super("Reference Demo");
	}

	@Override
	public String about() {
		return "This is a\nminimal demo.";
	}



	
	public void testRefs()
	{
		ActivationRecord arec = LocalServer.activate(RefDemo.class);
		Integer one = Integer.valueOf(1);
		Integer two = Integer.valueOf(2);
		SimpleReference r1 = new SimpleReference(null, 90, 90);
		SimpleReference r2 = new SimpleReference(null, 90, 90);
		arec.var("ref1", r1).var("ref2", r2).breakHere("start");
		r1.set(one);
		arec.breakHere("1");
		r2.set(two);
		arec.breakHere("2");
		r1.set(two);
		arec.breakHere("3");
		r2.set(null);
		arec.breakHere("4");
	}
	
	
	@Override
	public void buildMenu() {
		
		
		
		
		
		register ("References", new MenuFunction() {
			@Override
			public void selected() {
				testRefs();
			}
		});

	}

	
	
	public static void main (String[] args) {
		RefDemo demo = new RefDemo();
		demo.runAsMain();
	}

}
