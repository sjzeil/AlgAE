package edu.odu.cs.cs361.animations;//!

import static edu.odu.cs.AlgAE.Server.LocalServer.activate;//!
import edu.odu.cs.AlgAE.Server.MemoryModel.ActivationRecord;//!
import edu.odu.cs.AlgAE.Server.Utilities.DiscreteInteger;//!
import edu.odu.cs.AlgAE.Server.Utilities.Index;//!


public class ArrayOperations {//!
	
	
//!#ifndef ARRAYOPS_H
//!#define ARRAYOPS_H
	

/*
*
* Assume the elements of the array are already in order
* Find the position where value could be added to keep
*    everything in order, and insert it there.
* Return the position where it was inserted
*  - Assumes that we have a separate integer (size) indicating how
*     many elements are in the array
*  - and that the "true" size of the array is at least one larger 
*      than the current value of that counter
*
*  @param array array into which to add an element
*  @param size  number of data elements in hte array. Must be less than
*               the number of elements allocated for the array. Incremented
*               upon output from this function.
*  @param value value to add into the array
*  @return the position where the element was added
*/

//!template <typename Comparable>
public Index addInOrder (DiscreteInteger[] array, int size, int value)//!
//!int addInOrder (Comparable* array, int& size, Comparable value)
{
	 ActivationRecord aRec = activate(ArrayOperations.class);//!
	 aRec.refParam("array", array).refParam("size", size).param("value", value).breakHere("starting addInOrder");//!
	// Make room for the insertion
    //!  int toBeMoved = size - 1;
	 Index toBeMoved = new Index(size - 1, array);//!
	 aRec.var("toBeMoved", toBeMoved).breakHere("start at high end of the data");//!
	//!  while (toBeMoved >= 0 && value < array[toBeMoved]) {
	 while (toBeMoved.get() >= 0 && value < array[toBeMoved.get()].get()) {//!
		aRec.breakHere("in loop: ready to move an element up");//!
	//!    array[toBeMoved+1] = array[toBeMoved];
		  if (toBeMoved.get()+1 >= array.length) {//!
			 aRec.breakHere("array is already full - program may crash");//!
			 return toBeMoved;//!
		   }//!
	  array[toBeMoved.get()+1] = array[toBeMoved.get()];//!
      aRec.breakHere("in loop: Moved the element");//!
	//!    --toBeMoved;
	  toBeMoved.set (toBeMoved.get() - 1);//!
      aRec.breakHere("in loop: decremented");//!
	 }
	// Insert the new value
	 aRec.breakHere("exited loop: insert the new value");//!
	//!  array[toBeMoved+1] = value;
	 array[toBeMoved.get()+1] = new DiscreteInteger(value);//!
	 aRec.breakHere("Inserted new value");//!
	//!  ++size;
	 size = size  + 1;//!
	 aRec.breakHere("Incremented size");//!
	//!  return toBeMoved+1;
	 return new Index(toBeMoved.get()+1, array);//!
}



/*
 * Search an array for a given value, returning the index where 
 *    found or -1 if not found.
 *
 * From Malik, C++ Programming: From Problem Analysis to Program Design
 *
 * @param list the array to be searched
 * @param listLength the number of data elements in the array
 * @param searchItem the value to search for
 * @return the position at which value was found, or -1 if not found
 */

//!template <typename T>
//!int seqSearch(const T list[], int listLength, T searchItem)
public Index seqSearch(DiscreteInteger list[], int listLength, int searchItem)//!
{
	ActivationRecord aRec = activate(ArrayOperations.class);//!
	aRec.refParam("list", list).param("listLength", listLength).param("searchItem", searchItem).breakHere("starting seqSearch");//!
//!    int loc;
  Index loc = new Index(-1, list);//!

//!    for (loc = 0; loc < listLength; loc++)
  for (loc.set(0); loc.get() < listLength; loc.set(loc.get()+1)) {//!
  	aRec.var("loc", loc).breakHere("in loop: see if we have found it");//!
//!        if (list[loc] == searchItem)
      if (list[loc.get()].get() == searchItem) { //!
      	  aRec.breakHere("Found it! Return " + loc.get());//!
          return loc;
      }//!
  }//!
	aRec.breakHere("Couldn't find it. Return -1");//!
//!    return -1;
  return new Index(-1, list);//!
}



/*
 * Search an ordered array for a given value, returning the index where 
 *    found or -1 if not found.
 * @param list the array to be searched. Must be ordered.
 * @param listLength the number of data elements in the array
 * @param searchItem the value to search for
 * @return the position at which value was found, or -1 if not found
 */
//!template <typename Comparable>
//!int seqOrderedSearch(const Comparable list[], int listLength, Comparable searchItem)
public Index seqOrderedSearch(DiscreteInteger list[], int listLength, int searchItem)//!
{
	ActivationRecord aRec = activate(ArrayOperations.class);//!
	aRec.refParam("list", list).param("listLength", listLength).param("searchItem", searchItem).breakHere("starting seqOrderedSearch");//!
//!    int loc = 0;
  Index loc = new Index(0, list);//!

	aRec.var("loc", loc).breakHere("start searching from the beginning");//!
//!    while (loc < listLength && list[loc] < searchItem)
  while (loc.get() < listLength && list[loc.get()].get() < searchItem )//!
    {
  	aRec.breakHere("move forward");//!
//!       ++loc;
     loc.set (loc.get()+1);//!
    }
	aRec.breakHere("Out of the loop: did we find it?");//!
//!    if (loc < listLength && list[loc] == searchItem)
    if (loc.get() < listLength && list[loc.get()].get() == searchItem) { //!
  	     aRec.breakHere("Found It! Return " + loc.get());//!
         return loc;
    }//!
    else
    {//!
  	     aRec.breakHere("Could not find it. Return -1");//!
//!         return -1;
    return new Index(-1, list);//!
  }//!
}



/*
 * Removes an element from the indicated position in the array, moving
 * all elements in higher positions down one to fill in the gap.
 * 
 *  @param array array from which to remove an element
 *  @param size  number of data elements in the array. Decremented
 *               upon output from this function.
 *  @param index position from which to remove the element. Must be < size
 */

//!template <typename T>
//!void removeElement (T* array, int& size, int index)
public void removeElement (DiscreteInteger[] array, int size, Index index)//!
{
 ActivationRecord aRec = activate(ArrayOperations.class);//!
 aRec.refParam("array", array).refParam("size", size).param("index", index).breakHere("starting removeElement");//!
 if (index.get() < 0 || index.get() >= array.length) { //!
	   aRec.breakHere("index is out of bounds - program may crash");//!
	   return; //!
 } //!
//!   int toBeMoved = index + 1;
 Index toBeMoved = new Index(index.get() + 1, array); //!
 aRec.var("toBeMoved",toBeMoved).breakHere("start above index");//!
//!   while (toBeMoved < size) {
 while (toBeMoved.get() < size) {//!
	 aRec.breakHere("move an element down");//!
//!     array[toBeMoved-1] = array[toBeMoved];
   array[toBeMoved.get()-1] = array[toBeMoved.get()]; //!
	 aRec.breakHere("moved");//!
//!     ++toBeMoved;
   toBeMoved.set(toBeMoved.get()+1);//!
 }
	 aRec.breakHere("Done moving elements");//!
//!  --size;
size = size -1;//!
}


/*
 * Performs the standard binary search using two comparisons per level.
 * Returns index where item is found or -1 if not found
 *
 * From Weiss,  Data Structures and Algorithm Analysis, 4e
 * ( modified SJ Zeil)
 *
 * @param a array to search. Must be ordered.
 * @param size number of elements in the array
 * @param x value to search for
 * @return position where found or -1 if not found
 */

//!template <typename Comparable>
//!int binarySearch( const Comparable* a, int size, const Comparable & x )
public int binarySearch(DiscreteInteger[] a, int size, int x)//!
{
	ActivationRecord aRec = activate(getClass());//!
	aRec.refParam("a", a).param("size", size).param("x", x);//!
	aRec.breakHere("starting binarySearch");//!
	
	int NOT_FOUND = -1;//!
    //!    const int NOT_FOUND = -1;
	
	int low = 0;//!
	int high = size-1;//!
    //!    int low = 0, high = a.size( ) - 1;
	aRec.var("low", new Index(low, a)).var("high",new Index(high, a));//!
	aRec.breakHere("start the loop");//!
	while (low <= high) {
		for (int i = low; i <= high; i++) aRec.highlight(a[i]); //!
		aRec.breakHere("in the loop");//!
		
	   int mid = ( low + high ) / 2;
		aRec.var("mid", new Index(mid, a));//!
		if( a[ mid ].get() < x )//!
    //!       if( a[ mid ] < x )
		 {//!
			aRec.breakHere("middle value is too low");//!
            low = mid + 1;
            aRec.var("low", new Index(low, a)); }//!
		 else if( a[ mid ].get() > x )//!
    //!       else if( a[ mid ] > x )
		 {//!
			aRec.breakHere("middle value is too high");//!
			high = mid - 1;
			aRec.var("high", new Index(high, a)); }//!
		  else//!       else
		  {//!
			aRec.breakHere("Found it!");//!
	        return mid;   // Found
		  }//!
		 aRec.clearRenderings();//!
	}
	aRec.breakHere("target is not in the array");//!	 
	return NOT_FOUND;     // NOT_FOUND is defined as -1	
		
	}
	
         
}//!

//!#endif
