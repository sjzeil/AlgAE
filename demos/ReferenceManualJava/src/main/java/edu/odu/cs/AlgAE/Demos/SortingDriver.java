package edu.odu.cs.AlgAE.Demos;


import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Animations.LocalJavaAnimation;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;
import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;


public class SortingDriver extends LocalJavaAnimation {

	public SortingDriver() {
		super("Sorting Algorithms");
	}

	@Override
	public String about() {
		return "Demonstration of Sorting Algorithms,\n" +
				"prepared for CS 361, Data Structures\n" +
				"and Algorithms, Old Dominion University\n";
	}

	
	private int[] array = new int[0];
	
	private class ArrayContainer implements CanBeRendered<ArrayContainer>, Renderer<ArrayContainer> {

		@Override
		public Renderer<ArrayContainer> getRenderer() {
			return this;
		}

		@Override
		public Color getColor(ArrayContainer obj) {
			return Color.white;
		}

		@Override
		public List<Component> getComponents(ArrayContainer obj) {
			ArrayList<Component> c = new ArrayList<Component>();
			c.add (new Component(array));
			return c;
		}

		@Override
		public List<Connection> getConnections(ArrayContainer obj) {
			return new ArrayList<Connection>();
		}

		@Override
		public String getValue(ArrayContainer obj) {
			return "";
		}

        @Override
        public Boolean getClosedOnConnections() {
            return true;
        }

        @Override
        public Directions getDirection() {
            return Directions.HorizontalTree;
        }

        @Override
        public Double getSpacing() {
            return 1.0;
        }
		
	}


	
	
	@Override
	public void buildMenu() {
		
		
		registerStartingAction(new MenuFunction() {
			
			@Override
			public void selected() {
				generateRandomArray(8);
				globalVar("list", new ArrayContainer());
			}
		});
		
		register ("Generate a random array", new MenuFunction() {
			@Override
			public void selected() {
				randomArrayGenerated();
			}
		});

		register ("Generate a reversed array", new MenuFunction() {
			@Override
			public void selected() {
				reverseArrayGenerated();
			}
		});
		
		register ("Bubble Sort", new MenuFunction() {
			@Override
			public void selected() {
				Sorting.bubbleSort (array, array.length);
			}
		});

		register ("Selection Sort", new MenuFunction() {
			@Override
			public void selected() {
				Sorting.selectionSort (array, array.length);
			}
		});

		register ("Insertion Sort", new MenuFunction() {
			@Override
			public void selected() {
				Sorting.insertionSort (array, array.length);
			}
		});

	}
	
	public void randomArrayGenerated()
	{
		String value = promptForInput("How many elements?", "\\d+");
		int n = Integer.parseInt(value);
		generateRandomArray(n);
	}

	public void generateRandomArray(int n)
	{
		if (n != array.length) {
			array = new int[n];
		}
		for (int i = 0; i < n; ++i) {
			array[i] = (int)((double)(2*n) * Math.random());
		}
		
	}

	
	public void reverseArrayGenerated()
	{
		String value = promptForInput("How many elements?", "\\d+");
		int n = Integer.parseInt(value);
		generateReverseArray(n);
	}

	public void generateReverseArray(int n)
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
		SortingDriver demo = new SortingDriver();
		demo.runAsMain();
	}

}
