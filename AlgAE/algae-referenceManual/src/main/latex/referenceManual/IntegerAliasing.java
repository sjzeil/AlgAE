
public class IntegerAliasing {

	/**
	 * A check to see how often the Integer class
	 * actually returns distinct objects
	 *  
	 * @param args
	 */
	public static void main(String[] args) {
		Integer[] a = new Integer[100];
		Integer[] b = new Integer[100];
		for (int i = 0; i < 100; ++i)
			a[i] = i;
		for (int i = 0; i < 100; ++i)
			b[i] = i;
		
		int count = 0;
		for (int i = 0; i < 100; ++i)
			if (a[i] == b[i]) {
				// a[i] and b[i] contain the same address/reference 
				++count;
			}
		System.out.println ("The two arrays share " + count + " elements.");
	}

	// Output is typically: The two arrays share 100 elements.
}
