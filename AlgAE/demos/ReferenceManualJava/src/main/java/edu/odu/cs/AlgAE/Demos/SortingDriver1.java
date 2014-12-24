package edu.odu.cs.AlgAE.Demos;

import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.Animations.LocalJavaAnimationApplet;



public class SortingDriver1 extends LocalJavaAnimationApplet {

	public SortingDriver1() {
		super("Sorting Algorithms");
	}

	@Override
	public String about() {
		return "Demonstration of Sorting Algorithms,\n" +
				"prepared for CS 333, Advanced Data Strcutures\n" +
				"and Algorithms, Old Dominion University\n" +
				"Summer 2011";
	}

	
	private int[] array = new int[8];
	
	
	
	@Override
	public void buildMenu() {

		registerStartingAction(new MenuFunction() {
			@Override
			public void selected() {
				generateRandomArray(array.length);
				globalVar("list", array);
			}
		});
		
		register ("Generate a random array", new MenuFunction() {
			@Override
			public void selected() {
				generateRandomArray(array.length);
			}
		});

		register ("Generate a reversed array", new MenuFunction() {
			@Override
			public void selected() {
				generateReverseArray(array.length);
			}
		});
		
		register ("Bubble Sort", new MenuFunction() {
			@Override
			public void selected() {
				Sorting1.bubbleSort (array, array.length);
			}
		});

		register ("Selection Sort", new MenuFunction() {
			@Override
			public void selected() {
				Sorting1.selectionSort (array, array.length);
			}
		});

		register ("Insertion Sort", new MenuFunction() {
			@Override
			public void selected() {
				Sorting1.insertionSort (array, array.length);
			}
		});

	}
	

	private void generateRandomArray(int n)
	{
		if (n != array.length) {
			array = new int[n];
		}
		for (int i = 0; i < n; ++i) {
			array[i] = (int)((double)(2*n) * Math.random());
		}
		
	}

	private void generateReverseArray(int n)
	{
		if (n != array.length) {
			array = new int[n];
		}
		array[n-1] = (int)(3.0 * Math.random());  
		for (int i = n-2; i >= 0; --i) {
			array[i] = array[i+1] + (int)(3.0 * Math.random());
		}
		
	}

	
	
	
	public static void main (String[] args) {
		SortingDriver1 demo = new SortingDriver1();
		demo.runAsMain();
	}

}
