package edu.odu.cs.cs361.animations;

import java.awt.Color;
import java.util.List;

import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;
import edu.odu.cs.AlgAE.Server.MemoryModel.ActivationRecord;//!
import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;
import edu.odu.cs.AlgAE.Server.Utilities.ArrayList;
import edu.odu.cs.AlgAE.Server.Utilities.DiscreteInteger;//!
import edu.odu.cs.AlgAE.Server.Utilities.Index;//!
import edu.odu.cs.AlgAE.Server.Utilities.SimpleReference;

import static edu.odu.cs.AlgAE.Server.LocalServer.activate;//!


public class Sorting {

	
/*
* Several sorting routines.
* Arrays are rearranged with smallest item first.
*/


    public static <T> void swap(T[] array, int x, int y) {
        T temp = array[x];
        array[x] = array[y];
        array[y] = temp;
    }

    // From OpenDSA Data Structures and Algorithms, Chapter 13,
    // https://opendsa-server.cs.vt.edu/OpenDSA/Books/Everything/html/InsertionSort.html
    public static <T extends Comparable<T>> void inssort(T[] A) {
        ActivationRecord aRec = activate(Sorting.class);//!
        aRec.refParam("A", A);//!
    	aRec.breakHere("starting insertion sort");//!

        for (int i = 1; i < A.length; i++) { // Insert i'th record
            int lastJ = i; T v = A[i];//!
            Index ii = new Index(i, A);//!
            aRec.var("i", ii).breakHere("Start moving element [i], " + v + " into position.");//!
            aRec.pushScope();//!
            for (int j = i; (j > 0) && (A[j].compareTo(A[j - 1]) < 0); j--) {
                Index jj = new Index(j, A);//!
                aRec.var("j", jj).breakHere("Swap elements " + j + " and " + (j-1));//!
                swap(A, j, j - 1);
                lastJ = j;//!
            }
            aRec.breakHere(v.toString() + " has settled in position " + lastJ);//!
            aRec.popScope();//!
        }
        aRec.breakHere("Completed insertion sort.");//!
    }



/*
* Internal insertion sort routine for subarrays
* that is used by quicksort.
* a is an array of DiscreteInteger.
* left is the left-most index of the subarray.
* right is the right-most index of the subarray.
*/
//!template <typename Comparable>
void insertionSort( ArrayList<DiscreteInteger> a, DiscreteInteger left, DiscreteInteger right )//!
//!void insertionSort( vector<Comparable> & a, int left, int right )
{
	ActivationRecord aRec = activate(getClass());//!
	aRec.param("a","").param("left", left).param("right",right);//!
	for (int i = left.get(); i <= right.get(); ++i) aRec.highlight(a.get(i), Color.blue.brighter().brighter());//!
	aRec.breakHere("begin insertionSort");//!
	
	for( int p = left.get() + 1; p <= right.get(); ++p )
    {
		aRec.var("p", new Index(p, a));//!
		
		int tmp = a.get(p).get();//!
//!        Comparable tmp = std::move( a[ p ] );
        int j = 789;//!
//!        int j;
        aRec.var("j", new Index(j, a));//!
//        aRec.breakHere("look for the position to put tmp");//!
      
        for( j = p; j > left.get() && tmp < a.get( j - 1 ).get(); --j )//!
//!        for( j = p; j > left && tmp < a[ j - 1 ]; --j )
        {
//      	   aRec.breakHere("scan down from j");//!
      	   a.get(j).set(a.get(j-1).get());//!
//!           a[ j ] = std::move( a[ j - 1 ] );
        }
//        aRec.breakHere("Put tmp at a[j]");//!
        a.get(j).set(tmp);//!
//!        a[ j ] = std::move( tmp );
    }
	aRec.breakHere("done sorting");//!
}

/*
* Shellsort, using Shell's (poor) increments.
*/
//!template <typename Comparable>
void shellSort(DiscreteInteger[] a, int n )//!
//!void shellsort( vector<Comparable> & a )
{
	ActivationRecord aRec = activate(getClass());//!
	for (int k = 0; k < n; ++k)//!
		aRec.highlight(a[k], Color.lightGray);//!
	aRec.refParam("a", a).param("n", n);//!
	aRec.breakHere("starting Shell's sort");//!
	
	for(int Gap = n / 2; Gap > 0;Gap = Gap == 2 ? 1 : (int) (Gap / 2.2))//!
//!    for( int gap = a.size( ) / 2; gap > 0; gap /= 2 )
	{
		aRec.pushScope();//!
		aRec.var("Gap", Gap);//!
		aRec.breakHere("Gap has been chosen");//!
		
		for( int i = Gap; i < n; i++ )//!
//!        for( int i = gap; i < a.size( ); ++i )      
        {
			aRec.var("i", new Index(i,a));//!
			for (int j = i; j >= 0; j-=Gap)//!
				aRec.highlight(a[j], Color.green);//!
			aRec.breakHere("'insertion sort' these elements");//!
			
			int Tmp = a[i].get();//!
			aRec.var("Tmp",Tmp);//!
//!            Comparable tmp = std::move( a[ i ] );
			
            int j = i;
            aRec.var("j", new Index(j,a));//!
			aRec.breakHere("where to put Tmp?");//!
			
			
			for(; j >= Gap && Tmp < a[ j - Gap ].get(); j -= Gap)//!
//!            for( ; j >= gap && tmp < a[ j - gap ]; j -= gap )
			{
				aRec.var("j", new Index(j,a));//!
				aRec.breakHere("shift a[j-Gap] up");//!
				a[j].set(a[j - Gap].get());//!
//!                a[ j ] = std::move( a[ j - gap ] );
				aRec.breakHere("drop down Gap positions");//!
			}
			aRec.breakHere("put Tmp in a[j]");//!
			a[j] = new DiscreteInteger(Tmp);//!
//!	        a[ j ] = std::move( tmp );
			for (int k = i; k >= 0; k-=Gap)//!
				aRec.highlight(a[k], Color.lightGray);//!
                           
         }
		 aRec.breakHere("The array is now " + Gap + "-sorted.");//!
		 aRec.popScope();//!
	}
	aRec.breakHere("Finished Shell's sort");//!
}



/*
 * Standard heapsort.
*/
//!template <typename Comparable>
//!void heapsort( vector<Comparable> & a )
//!{
//!    for( int i = a.size( ) / 2 - 1; i >= 0; --i )  /* buildHeap */
//!       percDown( a, i, a.size( ) );
//!    for( int j = a.size( ) - 1; j > 0; --j )
//!    {
//!        std::swap( a[ 0 ], a[ j ] );               /* deleteMax */
//!        percDown( a, 0, j );
//!    }
//!}

/*
 * Internal method for heapsort.
 * i is the index of an item in the heap.
 * Returns the index of the left child.
 */
//!inline int leftChild( int i )
//!{
//!    return 2 * i + 1;
//!}

/*
 * Internal method for heapsort that is used in
 * deleteMax and buildHeap.
 * i is the position from which to percolate down.
 * n is the logical size of the binary heap.
 */
//!template <typename Comparable>
//!void percDown( vector<Comparable> & a, int i, int n )
//!{
//!    int child;
//!    Comparable tmp;

//!    for( tmp = std::move( a[ i ] ); leftChild( i ) < n; i = child )
//!    {
//!        child = leftChild( i );
//!        if( child != n - 1 && a[ child ] < a[ child + 1 ] )
//!            ++child;
//!        if( tmp < a[ child ] )
//!           a[ i ] = std::move( a[ child ] );
//!        else
//!           break;
//!    }
//!    a[ i ] = std::move( tmp );
//!}



    // Based upon OpenDSA Data Structures and Algorithms, Chapter 13,
    // https://opendsa-server.cs.vt.edu/OpenDSA/Books/Everything/html/MergesortImpl.html
    // Changes by S Zeil: made it generic, divided into separate functions
    private static <T extends Comparable<T>> void mergesort(T[] A, Object[] temp, int left, int right) {
        ActivationRecord aRec = activate(Sorting.class);//!
        aRec.param("A", "").param("temp", "").param("left", left).param("right", right);//!
        aRec.clearRenderings();//!
        for (int i = left; i <= right; ++i) aRec.highlight(A[i]); //!
        aRec.breakHere("starting mergesort recursive call on range " + left + ".." + right);//!
        if (left == right) {
            return;
        } // List has one record
        int mid = (left + right) / 2; // Select midpoint
        for (int i = left; i <= mid; ++i) aRec.highlight(A[i], Color.blue); //!
        for (int i = mid+1; i <= right; ++i) aRec.highlight(A[i], Color.yellow); //!
        aRec.var("mid", mid).breakHere("Computed midpoint. Now sort the left half.");//!
        mergesort(A, temp, left, mid); // Mergesort first half
        aRec.breakHere("Now sort the right half.");//!
        mergesort(A, temp, mid + 1, right); // Mergesort second half
        aRec.breakHere("Now merge the two halves.");//!
        merge(A, temp, left, right, mid);
        aRec.breakHere("Merged");//!
    }

    public static <T extends Comparable<T>> void merge(T[] A, Object[] temp, int left, int right, int mid) {
        ActivationRecord aRec = activate(Sorting.class);//!
        aRec.refParam("A", A).refParam("temp", temp).param("left", left).param("right", right).param("mid", mid);//!
        for (int i = left; i <= mid; ++i) aRec.highlight(A[i], Color.blue); //!
        for (int i = mid+1; i <= right; ++i) aRec.highlight(A[i], Color.yellow); //!
        aRec.breakHere("Starting merge");//!
        // Do the merge operation into temp
        int i1 = left;
        int i2 = mid + 1;
        int curr = left;
        Index ii1 = new Index(i1, A);//!
        Index ii2 = new Index(i2,A);//!
        Index iCurr = new Index(curr, temp);//!
        aRec.var("i1", ii1).var("i2", ii2).var("curr", iCurr).breakHere("Ready to start comparing");//!
        for (; i1 <= mid && i2 <= right; curr++) {
            aRec.breakHere("Compare elements " + i1 + " and " + i2);//!
            ii1.set(i1); ii2.set(i2); iCurr.set(curr);//!
            if (A[i1].compareTo(A[i2]) <= 0) { // Get smaller value
                aRec.breakHere("A[i1] is smaller. Copy it to temp;");//!
                temp[curr] = A[i1++];
            } else {
                aRec.breakHere("A[i2] is smaller. Copy it to temp;");//!
                temp[curr] = A[i2++];
            }
            aRec.breakHere("Smaller value has been copied to temp[curr]");//!
        }
        // Exhausted one sublist or the other. Copy the remaining elements from the
        // non-emptied sublist.
        aRec.breakHere("Copy any remaining elements from left half.");//!
        System.arraycopy(A, i1, temp, curr, mid - i1 + 1);
        aRec.breakHere("Copy any remaining elements from right half.");//!
        System.arraycopy(A, i2, temp, curr, right - i2 + 1);

        // Copy merged data from temp back to A
        aRec.breakHere("Copy merged data from temp back to A.");//!
        System.arraycopy(temp, left, A, left, right - left + 1);
    }

    public static <T extends Comparable<T>> void mergesort(T[] A) {
        ActivationRecord aRec = activate(Sorting.class);//!
        aRec.refParam("A", A);//!
        aRec.breakHere("starting mergesort");//!
        Object[] temp = new Object[A.length];
        for(int i = 0; i < A.length; ++i) temp[i] = new DiscreteInteger(0);//!
        aRec.refVar("temp", temp).breakHere("Allocated temporary array");//!
        mergesort(A, temp, 0, A.length - 1);
        aRec.breakHere("Completed mergesort");//!
    }


    private static class SimpleListNode 
    implements CanBeRendered<SimpleListNode>, Renderer<SimpleListNode>//!
    {
        public Object data;
        public SimpleListNode next;

        public SimpleListNode(Object data) {
            this.data = data;
            this.next = null;
        }

        @Override
        public Boolean getClosedOnConnections() {
            return true;
        }

        @Override
        public Color getColor(SimpleListNode arg0) {
            return Color.yellow;
        }

        @Override
        public List<Component> getComponents(SimpleListNode node) {
            List<Component> components = new ArrayList<>();
            return components;
        }

        @Override
        public List<Connection> getConnections(SimpleListNode arg0) {
            List<Connection> connections = new ArrayList<>();
            connections.add(new Connection(next, 0, 180));
            return connections;
        }

        @Override
        public Directions getDirection() {
            return Directions.HorizontalTree;
        }

        @Override
        public Double getSpacing() {
            return null;
        }

        @Override
        public String getValue(SimpleListNode node) {
            return node.data.toString();
        }

        @Override
        public Renderer<SimpleListNode> getRenderer() {
            return this;
        }
    }

    private static class SimpleList 
    implements Renderer<SimpleList>, CanBeRendered<SimpleList>//!
    {
        private class Header 
        implements Renderer<Header>, CanBeRendered<Header>//!
        {
            private SimpleReference firstRef;//
            private SimpleReference lastRef;//

            public Header() {
                firstRef = new SimpleReference(first);//!
                lastRef = new SimpleReference(last);//!    
            }
    
            @Override
            public Renderer<Header> getRenderer() {
                return this;
            }

            @Override
            public Boolean getClosedOnConnections() {
                return false;
            }

            @Override
            public Color getColor(Header arg0) {
                return Color.black;
            }

            @Override
            public List<Component> getComponents(Header header) {
                List<Component> components = new ArrayList<>();
                header.firstRef.set(first);
                components.add(new Component(firstRef, "first"));
                header.lastRef.set(last);
                components.add(new Component(lastRef, "last"));
                return components;
            }

            @Override
            public List<Connection> getConnections(Header arg0) {
                return new ArrayList<>();
            }

            @Override
            public Directions getDirection() {
                return Directions.Vertical;
            }

            @Override
            public Double getSpacing() {
                return null;
            }

            @Override
            public String getValue(Header arg0) {
                return "";
            }
            
        }

        private SimpleListNode first;
        private SimpleListNode last;
        private Header header;//!
        private int theSize;

        public SimpleList() {
            first = last = null;
            theSize = 0;
            header = new Header();//!
        }

        public boolean isEmpty() {return theSize == 0;}

        public void addToEnd(SimpleListNode nd) {
            if (first == null) {
                first = nd;
            }
            if (last != null) {
                last.next = nd;
            }
            last = nd;
            nd.next = null;
            ++theSize;
            header.firstRef.set(first);//!
            header.lastRef.set(last);//!
        }

        public SimpleListNode removeFront() {
            SimpleListNode front = first;
            first = front.next;
            if (first == null) {
                last = null;
            }
            --theSize;
            front.next = null;
            header.firstRef.set(first);//!
            header.lastRef.set(last);//!
            return front;
        }

        public void swap(SimpleList other) {
            SimpleListNode temp = first;
            first = other.first;
            other.first = temp;
            temp = last;
            last = other.last;
            other.last = temp;
            int tmp = theSize;
            theSize = other.theSize;
            other.theSize = tmp;
            header.firstRef.set(first);//!
            header.lastRef.set(last);//!
            other.header.firstRef.set(other.first);//!
            other.header.lastRef.set(other.last);//!
        }

        @Override
        public Renderer<SimpleList> getRenderer() {
            return this;
        }

        @Override
        public Boolean getClosedOnConnections() {
            return true;
        }

        @Override
        public Color getColor(SimpleList arg0) {
            return new Color(0,0,0,0);
        }

        @Override
        public List<Component> getComponents(SimpleList arg0) {
            List<Component> components = new ArrayList<>();
            header.firstRef.set(first);
            header.lastRef.set(last);
            components.add(new Component(header));
            return components;
        }

        @Override
        public List<Connection> getConnections(SimpleList arg0) {
            return new ArrayList<>();
        }

        @Override
        public Directions getDirection() {
            return Directions.HorizontalTree;
        }

        @Override
        public Double getSpacing() {
            return null;
        }

        @Override
        public String getValue(SimpleList arg0) {
            return "";
        }
    }

    // Iterative Merge Sort
    public static <T extends Comparable<T>> void mergesort2(T[] array) {
        ActivationRecord aRec = activate(Sorting.class);//!
        aRec.refParam("list", array).breakHere("Starting iterative mergesort");//!

        ArrayList<SimpleList> temps = setUpTempLists(array.length);

        aRec.refVar("temps", temps).breakHere("Temporary lists have been set up");//!

        mergeDataFromList(array, temps);
        aRec.breakHere("All data has been processed from the input list");//!

        SimpleList sorted = mergeAllTempLists(temps);
        aRec.refVar("sorted",sorted).breakHere("All temporary lists have been merged");//!
        copyToArray(array, sorted);
        aRec.breakHere("Completed mergesort");//!
    }

    private static ArrayList<SimpleList> setUpTempLists(int inputSize) {
        ArrayList<SimpleList> results = new ArrayList<>();
        // Compute ceil(log_2(inputSize))
        int size = 1;
        while (size <= inputSize) {
            size *= 2;
            results.add(new SimpleList());
        }
        results.setDirection(Directions.Vertical);//!
        return results;
    }



    private static <T extends Comparable<T>>
    void mergeDataFromList(T[] array, ArrayList<SimpleList> temps) {
        ActivationRecord aRec = activate(Sorting.class);//!
        aRec.refParam("array", array).refParam("temps", temps).breakHere("starting mergeDataFromList");//!
        for (T data: array) {
            aRec.pushScope();//!
            SimpleList inHand = new SimpleList();
            aRec.var("data", data).refVar("inHand",inHand).breakHere("Add next data item from array to inHand");//!
            inHand.addToEnd(new SimpleListNode(data));
            int k = 0;
            aRec.var("k", k).breakHere("Begin merging inHand with the temps lists");//!
            while (!temps.get(k).isEmpty()) {
                inHand.swap(merge(temps.get(k), inHand));
                aRec.refParam("inHand", inHand).breakHere("Merged temps[" + k + "] into inHand");//!
                ++k;
            }
            aRec.breakHere("Swap empty temps[" + k + "] with inHand");//!
            temps.set(k, inHand);
            aRec.popScope();//!
            aRec.highlight(data, Color.gray);//!
        }
    }

    private static <T extends Comparable<T>> 
    SimpleList mergeAllTempLists(ArrayList<SimpleList> temps) {
        ActivationRecord aRec = activate(Sorting.class);//!
        SimpleList result = temps.get(0);
        aRec.refVar("result", result).breakHere("Merge each temp list into result");//!
        for (int i = 1; i < temps.size(); ++i) {
            result.swap(merge(result, temps.get(i)));
            aRec.var("i", i).breakHere("Merged temps[" + i + "] into result");//!
        }
        aRec.breakHere("done with mergeAllTempLists");//!
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> void copyToArray(T[] array, SimpleList inHand) {
        int i = 0;
        for (SimpleListNode nd = inHand.first; nd != null; nd = nd.next) {
            array[i] = (T)nd.data;
            ++i;
        }
    }




    private static <T extends Comparable<T>>
    SimpleList merge(SimpleList list1, SimpleList list2) {
        ActivationRecord aRec = activate(Sorting.class);//!
        aRec.refParam("list1", list1).refParam("list2", list2);//!
        SimpleList result = new SimpleList();
        aRec.var("result",result).breakHere("merge the lists into result");//!
        while ((!list1.isEmpty()) && (!list2.isEmpty())) {
            @SuppressWarnings("unchecked")
            T t1 = (T)list1.first.data;
            @SuppressWarnings("unchecked")
            T t2 = (T)list2.first.data;
            aRec.var("t1",  t1).var("t2", t2).breakHere("Compare t1 and t2");//!
            if (t1.compareTo(t2) <= 0) {
                result.addToEnd(list1.removeFront());
                aRec.breakHere("t1 was smaller. Added to result");//!
            } else {
                result.addToEnd(list2.removeFront());
                aRec.breakHere("t2 was smaller. Added to result");//!
            }
        }
        aRec.breakHere("Copy any data remaining in list1");//!
        while (!list1.isEmpty()) {
            result.addToEnd(list1.removeFront());
        }
        aRec.breakHere("Copy any data remaining in list2");//!
        while (!list2.isEmpty()) {
            result.addToEnd(list2.removeFront());
        }
        aRec.breakHere("done with merge");//!
        return result;
    }


/*
* Return median of left, center, and right.
* Order these and hide the pivot.
*/
//!template <typename Comparable>
final DiscreteInteger median3( ArrayList<DiscreteInteger> a, DiscreteInteger left, DiscreteInteger right )//!
//!const Comparable & median3( vector<Comparable> & a, int left, int right )
{
	ActivationRecord aRec = activate(getClass());//!
	aRec.param("a","").param("left", left).param("right",right);//!

	DiscreteInteger center = new DiscreteInteger( (left.get() + right.get() ) / 2);
//!    int center = ( left + right ) / 2;
	aRec.var("center", center);//!
	a.pushIndices();//!
	a.indexedBy(left, "left");//!
	a.indexedBy(right, "right");//!
	a.indexedBy(center, "center");//!
 
	if( a.get( center ).get() < a.get( left ).get() ) //!
//!    if( a[ center ] < a[ left ] )
	{
		aRec.breakHere("swap  a[ left ] and  a[ center ]");//!
//!        std::swap( a[ left ], a[ center ] );
		a.get( left ).swap( a.get(center));//!
		aRec.breakHere("done with swapping left and center");//!
		
	}
	if( a.get( right ).get() < a.get( left ).get() )//!
//!    if( a[ right ] < a[ left ] )
	{
		aRec.breakHere("swap  a[ right ] and  a[ left ]");//!
//!        std::swap( a[ left ], a[ right ] );
		a.get( left ).swap( a.get(right) );//!
		aRec.breakHere("done with swapping right and left");//!
	}
	if( a.get( right ).get() < a.get( center ).get() )//!
//!    if( a[ right ] < a[ center ] )
	{
		aRec.breakHere("swap  a[ right ] and  a[ center ]");//!
//!        std::swap( a[ right ], a[ center ] );
		a.get( right ).swap(a.get( center ));//!
		aRec.breakHere("done with swapping right and center");//!
	}

 // Place pivot at position right - 1
	aRec.breakHere("Place pivot at position right - 1");//!
	aRec.breakHere("swap  a[ center ] and  a[ right - 1 ]");//!
//!    std::swap( a[ center ], a[ right - 1 ] );
	a.get( center ).swap( a.get( right.get() - 1 ));//!
	aRec.breakHere("done with swapping center and right - 1");//!
 
    return a.get( right.get() - 1 );//!
//!    return a[ right - 1 ];
}





/*
* Internal quicksort method that makes recursive calls.
* Uses median-of-three partitioning and a cutoff of 10.
* a is an array of DiscreteInteger
* left is the left-most index of the subarray.
* right is the right-most index of the subarray.
*/
//!template <typename Comparable>
void quicksort(ArrayList<DiscreteInteger> a, int left0, int right0) {//!
//!void quicksort( vector<Comparable> & a, int left, int right ) {
	ActivationRecord aRec = activate(getClass());//!
	DiscreteInteger left = new DiscreteInteger(left0);//!
	DiscreteInteger right = new DiscreteInteger(right0);//!
	a.pushIndices();//!
	a.indexedBy(left, "left");//!
	a.indexedBy(right, "right");//!
	aRec.refParam("a", a).param("left", left).param("right",right);//!
	for (int i = left0; i <= right0; ++i)
		aRec.highlight(a.get(i), Color.blue.brighter().brighter());
	
	if( left.get() + 4 <= right.get() )//!
//!	if( left + 4 <= right )
    {
		aRec.breakHere("Choose the pivot.");//!
		DiscreteInteger pivot = median3( a, left, right );//!
//!        const Comparable & pivot = median3( a, left, right );
		aRec.var("pivot", pivot);//!
        // Begin partitioning
		aRec.breakHere("begin partitioning");//!
        DiscreteInteger i = new DiscreteInteger(left.get());//!
        DiscreteInteger j = new DiscreteInteger(right.get() - 1);//!
        a.indexedBy(i, "i");//!
        a.indexedBy(j, "j");//!
//!        int i = left, j = right - 1;
        aRec.var("i", i).var("j", j);//!
        aRec.breakHere("look for elements to swap");//!
        for( ; ; )
        {
        	i.incr();//!
        	while( a.get(i).get() < pivot.get() ) {i.incr();//! 
        		aRec.breakHere("scan up from the left");//!
//!            while( a[ ++i ] < pivot ) { }
        	}//!
        	j.decr();//!
            while( pivot.get() < a.get(j).get() ) {j.decr();//!
        	aRec.breakHere("scan down from the right");//!
//!            while( pivot < a[ --j ] ) { }
            }//!
            aRec.breakHere("Either we are ready to swap or we are done pivoting");//!
            if( i.get() < j.get() )//!
//!            if( i < j )
            {
            	aRec.breakHere("Swap the out-of-position elements");//!
//!                std::swap( a[ i ], a[ j ] );
            	a.get(i).swap(a.get(j));//!
            }
            else
                break;
        }

        aRec.breakHere("Restore pivot");//!
//!        std::swap( a[ i ], a[ right - 1 ] ); // Restore pivot
    	a.get(i).swap(a.get(right.get() - 1));//!
        
    	aRec.breakHere("Sort small elements");//!
    	quicksort( a, left.get(), i.get() - 1 );//!
//!    	quicksort( a, left, i - 1 );        // Sort small elements
    	
    	aRec.breakHere("Sort large elements");//!
        quicksort( a, i.get() + 1, right.get() );//!
//!        quicksort( a, i + 1, right );       // Sort large elements
    }
    else  
    {// Do an insertion sort on the subarray
    	aRec.breakHere("Do an insertion sort on the subarray");//!
        insertionSort( a, left, right );
    }
	a.popIndices();//!
}






/*
*  Quicksort algorithm (driver).
*/
//!template <typename Comparable>
void quicksort( ArrayList<DiscreteInteger> a, int size)//!
//!void quicksort( vector<Comparable> & a )
{
	ActivationRecord aRec = activate(getClass());//!
	aRec.refParam("a",a);//!
	aRec.breakHere("start quicksorting");//!
	
	quicksort( a, 0, size - 1 );//!
//!    quicksort( a, 0, a.size( ) - 1 );
}


/*
 * Internal selection method that makes recursive calls.
 * Uses median-of-three partitioning and a cutoff of 10.
 * Places the kth smallest item in a[k-1].
 * a is an array of Comparable items.
 * left is the left-most index of the subarray.
 * right is the right-most index of the subarray.
 * k is the desired rank (1 is minimum) in the entire array.
 */
//!template <typename Comparable>
//!void quickSelect( vector<Comparable> & a, int left, int right, int k )
//!{
//!    if( left + 10 <= right )
//!    {
//!        const Comparable & pivot = median3( a, left, right );

            // Begin partitioning
//!        int i = left, j = right - 1;
//!        for( ; ; )
//!        {
//!            while( a[ ++i ] < pivot ) { }
//!            while( pivot < a[ --j ] ) { }
//!            if( i < j )
//!                std::swap( a[ i ], a[ j ] );
//!            else
//!                break;
//!        }

//!        std::swap( a[ i ], a[ right - 1 ] );  // Restore pivot

            // Recurse; only this part changes
//!        if( k <= i )
//!            quickSelect( a, left, i - 1, k );
//!        else if( k > i + 1 )
//!            quickSelect( a, i + 1, right, k );
//!    }
//!    else  // Do an insertion sort on the subarray
//!        insertionSort( a, left, right );
//!}

/*
 * Quick selection algorithm.
 * Places the kth smallest item in a[k-1].
 * a is an array of Comparable items.
 * k is the desired rank (1 is minimum) in the entire array.
 */
//!template <typename Comparable>
//!void quickSelect( vector<Comparable> & a, int k )
//!{
//!    quickSelect( a, 0, a.size( ) - 1, k );
//!}


//!template <typename Comparable>
//!void SORT( vector<Comparable> & items )
//!{
//!    if( items.size( ) > 1 )
//!    {
//!        vector<Comparable> smaller;
//!        vector<Comparable> same;
//!        vector<Comparable> larger;
        
//!        auto chosenItem = items[ items.size( ) / 2 ];
        
//!        for( auto & i : items )
//!        {
//!            if( i < chosenItem )
//!                smaller.push_back( std::move( i ) );
//!            else if( chosenItem < i )
//!                larger.push_back( std::move( i ) );
//!            else
//!                same.push_back( std::move( i ) );
//!        }
        
//!        SORT( smaller );     // Recursive call!
//!        SORT( larger );      // Recursive call!
        
//!        std::move( begin( smaller ), end( smaller ), begin( items ) );
//!        std::move( begin( same ), end( same ), begin( items ) + smaller.size( ) );
//!        std::move( begin( larger ), end( larger ), end( items ) - larger.size( ) );

/*
        items.clear( );
        items.insert( end( items ), begin( smaller ), end( smaller ) );
        items.insert( end( items ), begin( same ), end( same ) );
        items.insert( end( items ), begin( larger ), end( larger ) );
*/
//!    }
//!}

/*
 * This is the more public version of insertion sort.
 * It requires a pair of iterators and a comparison
 * function object.
 */
//!template <typename RandomIterator, typename Comparator>
//!void insertionSort( const RandomIterator & begin,
//!                    const RandomIterator & end,
//!                    Comparator lessThan )
//!{
//!    if( begin == end )
//!        return;
        
//!    RandomIterator j;

//!    for( RandomIterator p = begin+1; p != end; ++p )
//!    {
//!        auto tmp = std::move( *p );
//!        for( j = p; j != begin && lessThan( tmp, *( j-1 ) ); --j )
//!            *j = std::move( *(j-1) );
//!        *j = std::move( tmp );
//!    }
//!}

/*
 * The two-parameter version calls the three parameter version, using C++11 decltype
 */
//!template <typename RandomIterator>
//!void insertionSort( const RandomIterator & begin,
//!                    const RandomIterator & end )
//!{
//!    insertionSort( begin, end, less<decltype(*begin )>{ } );
//!}


//!#endif  
        
        
}//!
