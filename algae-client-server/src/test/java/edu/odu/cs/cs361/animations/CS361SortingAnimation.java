package edu.odu.cs.cs361.animations;



import edu.odu.cs.AlgAE.Animations.LocalJavaAnimation;
import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.Utilities.DiscreteInteger;
import edu.odu.cs.AlgAE.Server.Utilities.SimpleReference;


public class CS361SortingAnimation extends LocalJavaAnimation {

	public CS361SortingAnimation() {
		super("Sorting Algorithms");
	}

	@Override
	public String about() {
		return "Demonstration of Sorting Algorithms,\n" +
				"prepared for CS 361, Data Structures\n" +
				"and Algorithms, Old Dominion University"
				+ "\n" +
				"Summer 2024";
	}

	
	//private ArrayList<DiscreteInteger> array = new ArrayList<>();
    private DiscreteInteger[] array = new DiscreteInteger[0];
    private SimpleReference arrayRef = new SimpleReference(array);
	
	
	@Override
	public void buildMenu() {
		
		registerStartingAction(new MenuFunction() {
			
			@Override
			public void selected() {
				generateRandomArray(12);
				globalVar("A", arrayRef);
                //DefaultRenderer.setDefaultArrayDirection(Directions.Vertical);
			}
		});
		
		register ("Generate a random vector", new MenuFunction() {
			@Override
			public void selected() {
				randomArrayGenerated();
			}
		});

		register ("Generate a reversed vector", new MenuFunction() {
			@Override
			public void selected() {
				reverseArrayGenerated();
			}
		});
		
		register ("Insertion Sort", new MenuFunction() {
			@Override
			public void selected() {
				Sorting.inssort (array);
			}
		});
/*
		register ("Shell Sort", new MenuFunction() {
			@Override
			public void selected() {
				new Sorting().shellSort (array, array.size());
			}
		});
*/


		register ("Merge Sort (recursive)", new MenuFunction() {
			@Override
			public void selected() {
				Sorting.mergesort (array);
			}
		});

		register ("Merge Sort (iterative)", new MenuFunction() {
			@Override
			public void selected() {
                //List<DiscreteInteger> list = new ArrayList<>(Arrays.asList(array));
				Sorting.mergesort2 (array);
                //int i = 0;
                //for (DiscreteInteger di: list) array[i] = di;
			}
		});

        /*		register ("Quick Sort", new MenuFunction() {
			@Override
			public void selected() {
				new Sorting().quicksort (array, array.size());
			}
		});
*/
	}
	
	public void randomArrayGenerated()
	{
		String value = promptForInput("How many elements?", "\\d+");
		int n = Integer.parseInt(value);
		generateRandomArray(n);
	}

	public void generateRandomArray(int n)
	{
		array = new DiscreteInteger[n];
		for (int i = 0; i < n; ++i) {
			array[i] = new DiscreteInteger((int)((double)(2*n) * Math.random()));
		}
		arrayRef.set(array);
	}

	
	public void reverseArrayGenerated()
	{
		String value = promptForInput("How many elements?", "\\d+");
		int n = Integer.parseInt(value);
		generateReverseArray(n);
	}

	public void generateReverseArray(int n)
	{
        array = new DiscreteInteger[n];
		array[n-1] = new DiscreteInteger ((int)(3.0 * Math.random()));  
		for (int i = n-2; i >= 0; --i) {
			array[i] = new DiscreteInteger(array[i+1].get() + (int)(3.0 * Math.random()));
		}
		arrayRef.set(array);
	}

	
	
	
	public static void main (String[] args) {
		CS361SortingAnimation demo = new CS361SortingAnimation();
		demo.runAsMain();
	}

}
