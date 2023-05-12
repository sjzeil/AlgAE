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
	 ActivationRecord arec = activate(ArrayOperations.class);//!
	 arec.refParam("array", array).refParam("size", size).param("value", value).breakHere("starting addInOrder");//!
	// Make room for the insertion
    //!  int toBeMoved = size - 1;
	 Index toBeMoved = new Index(size - 1, array);//!
	 arec.var("toBeMoved", toBeMoved).breakHere("start at high end of the data");//!
	//!  while (toBeMoved >= 0 && value < array[toBeMoved]) {
	 while (toBeMoved.get() >= 0 && value < array[toBeMoved.get()].get()) {//!
		arec.breakHere("in loop: ready to move an element up");//!
	//!    array[toBeMoved+1] = array[toBeMoved];
		  if (toBeMoved.get()+1 >= array.length) {//!
			 arec.breakHere("array is already full - program may crash");//!
			 return toBeMoved;//!
		   }//!
	  array[toBeMoved.get()+1] = array[toBeMoved.get()];//!
      arec.breakHere("in loop: Moved the element");//!
	//!    --toBeMoved;
	  toBeMoved.set (toBeMoved.get() - 1);//!
      arec.breakHere("in loop: decremented");//!
	 }
	// Insert the new value
	 arec.breakHere("exited loop: insert the new value");//!
	//!  array[toBeMoved+1] = value;
	 array[toBeMoved.get()+1] = new DiscreteInteger(value);//!
	 arec.breakHere("Inserted new value");//!
	//!  ++size;
	 size = size  + 1;//!
	 arec.breakHere("Incremented size");//!
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
	ActivationRecord arec = activate(ArrayOperations.class);//!
	arec.refParam("list", list).param("listLength", listLength).param("searchItem", searchItem).breakHere("starting seqSearch");//!
//!    int loc;
  Index loc = new Index(-1, list);//!

//!    for (loc = 0; loc < listLength; loc++)
  for (loc.set(0); loc.get() < listLength; loc.set(loc.get()+1)) {//!
  	arec.var("loc", loc).breakHere("in loop: see if we have found it");//!
//!        if (list[loc] == searchItem)
      if (list[loc.get()].equals(searchItem)) { //!
      	  arec.breakHere("Found it! Return " + loc.get());//!
          return loc;
      }//!
  }//!
	arec.breakHere("Couldn't find it. Return -1");//!
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
	ActivationRecord arec = activate(ArrayOperations.class);//!
	arec.refParam("list", list).param("listLength", listLength).param("searchItem", searchItem).breakHere("starting seqOrderedSearch");//!
//!    int loc = 0;
  Index loc = new Index(0, list);//!

	arec.var("loc", loc).breakHere("start searching from the beginning");//!
//!    while (loc < listLength && list[loc] < searchItem)
  while (loc.get() < listLength && list[loc.get()].get() < searchItem )//!
    {
  	arec.breakHere("move forward");//!
//!       ++loc;
     loc.set (loc.get()+1);//!
    }
	arec.breakHere("Out of the loop: did we find it?");//!
//!    if (loc < listLength && list[loc] == searchItem)
    if (loc.get() < listLength && list[loc.get()].equals(searchItem)) { //!
  	     arec.breakHere("Found It! Return " + loc.get());//!
         return loc;
    }//!
    else
    {//!
  	     arec.breakHere("Could not find it. Return -1");//!
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
 ActivationRecord arec = activate(ArrayOperations.class);//!
 arec.refParam("array", array).refParam("size", size).param("index", index).breakHere("starting removeElement");//!
 if (index.get() < 0 || index.get() >= array.length) { //!
	   arec.breakHere("index is out of bounds - program may crash");//!
	   return; //!
 } //!
//!   int toBeMoved = index + 1;
 Index toBeMoved = new Index(index.get() + 1, array); //!
 arec.var("toBeMoved",toBeMoved).breakHere("start above index");//!
//!   while (toBeMoved < size) {
 while (toBeMoved.get() < size) {//!
	 arec.breakHere("move an element down");//!
//!     array[toBeMoved-1] = array[toBeMoved];
   array[toBeMoved.get()-1] = array[toBeMoved.get()]; //!
	 arec.breakHere("moved");//!
//!     ++toBeMoved;
   toBeMoved.set(toBeMoved.get()+1);//!
 }
	 arec.breakHere("Done moving elements");//!
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
 * @param size number of elements i nthe array
 * @param x value to search for
 * @return position where found or -1 if not found
 */

//!template <typename Comparable>
//!int binarySearch( const Comparable* a, int size, const Comparable & x )
public int binarySearch(DiscreteInteger[] a, int size, int x)//!
{
	ActivationRecord arec = activate(getClass());//!
	arec.refParam("a", a).param("size", size).param("x", x);//!
	arec.breakHere("starting binarySearch");//!
	
	int NOT_FOUND = -1;//!
    //!    const int NOT_FOUND = -1;
	
	int low = 0;//!
	int high = a.length-1;//!
    //!    int low = 0, high = a.size( ) - 1;
	arec.var("low", new Index(low, a)).var("high",new Index(high, a));//!
	arec.breakHere("start the loop");//!
	while (low <= high) {
		for (int i = low; i < high; i++) arec.highlight(a[i]); //!
		arec.breakHere("in the loop");//!
		
	   int mid = ( low + high ) / 2;
		arec.var("mid", new Index(mid, a));//!
		if( a[ mid ].get() < x )//!
    //!       if( a[ mid ] < x )
		 {//!
			arec.breakHere("middle value is too low");//!
            low = mid + 1;
            arec.var("low", new Index(low, a)); }//!
		 else if( a[ mid ].get() > x )//!
    //!       else if( a[ mid ] > x )
		 {//!
			arec.breakHere("middle value is too high");//!
			high = mid - 1;
			arec.var("high", new Index(high, a)); }//!
		  else//!       else
		  {//!
			arec.breakHere("Found it!");//!
	        return mid;   // Found
		  }//!
		 arec.clearRenderings();//!
	}
	arec.breakHere("target is not in the array");//!	 
	return NOT_FOUND;     // NOT_FOUND is defined as -1	
		
	}
	
         
}//!

//!#endif
