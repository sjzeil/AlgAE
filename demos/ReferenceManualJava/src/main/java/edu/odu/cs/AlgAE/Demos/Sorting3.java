package edu.odu.cs.AlgAE.Demos;//!

import static edu.odu.cs.AlgAE.Server.Animations.LocalJavaAnimation.activate;
import edu.odu.cs.AlgAE.Server.MemoryModel.ActivationRecord;

public class Sorting3 {//!


//
//  Based on Malik, "C++ Programming [From Problem Analysis to Program Design]"
//      chapter 10
//

public static//!
	void bubbleSort(int list[], int length)
	{
        ActivationRecord arec = activate(Sorting3.class);//!
        arec.refParam("list", list).param("length", length).breakHere("starting bubble sort");//!
	    int temp;
	    int iteration;
	    int index;

	    for (iteration = 1; iteration < length; iteration++)
	    {
	    	arec.pushScope();//!
	    	arec.var("iteration", iteration);//!
	        arec.breakHere("start a pass over the array");//!
	        for (index = 0; index < length - iteration; index++)
	        {//!
	        	arec.pushScope();//!
		    	arec.var("index", index);//!
	        	arec.breakHere("compare list[index] to list[index+1]");//!
	            if (list[index] > list[index + 1]) 
	            {
			        arec.breakHere("list[index] and list[index+1] are out of order - swap them");//!
	                temp = list[index];
	                list[index] = list[index + 1];
	                list[index + 1] = temp;
	            }
	            arec.popScope();//!
	        }//!
	        arec.breakHere("completed this pass over the array");//!

	        arec.popScope();//!
	    }
	    arec.breakHere("completed bubble sort");//!
	}

	
public static//!
	void selectionSort(int list[], int length)
	{
    ActivationRecord arec = activate(Sorting3.class);//!
    arec.refParam("list", null).param("length", length).breakHere("starting selection sort");//!
	    int index;
	    int smallestIndex;
	    int location;
	    int temp;

	    for (index = 0; index < length - 1; index++)
	    {
	    	arec.pushScope();//!
	    	arec.var("index", index);//!
	    	arec.breakHere("Find the value to go into list[index]");//!
	            //Step a
	        smallestIndex = index; 
	    	arec.var("smallestIndex", smallestIndex);//!
	    	arec.breakHere("smallestIndex holds location of smallest remaining value found so far");//!

	        for (location = index + 1; location < length; location++)
	        {//!
	        	arec.pushScope();//!
		    	arec.var("location", location);//!
		    	arec.breakHere("see if location holds a smaller value");//!

	        	if (list[location] < list[smallestIndex])
	        	{//!
			    	arec.breakHere("Yes, it does.");//!
	                smallestIndex = location;
	    	    	arec.var("smallestIndex", smallestIndex);//!
			    	arec.breakHere("New value for smallestLocation.");//!
	        	}//!
	        	arec.popScope();//!
	        }//!
	            //Step b
	    	arec.breakHere("Swap the values in smallestLocation and index");//!
	        temp = list[smallestIndex];
	        list[smallestIndex] = list[index];
	        list[index] = temp;
	    	arec.breakHere("Ready to move to the next index position");//!
	    	arec.popScope();//!
	    }
	    arec.breakHere("completed selection sort");//!
	}

	
public static//!
	void insertionSort (int list[], int listLength)
	{
	    ActivationRecord arec = activate(Sorting3.class);//!
	    arec.refParam("list", null).param("listLength", listLength);//!
	    arec.breakHere("starting insertion sort");//!
		int firstOutOfOrder, location;
		int temp;
		
		for (firstOutOfOrder = 1; firstOutOfOrder < listLength;
		                          firstOutOfOrder++)
		{//!
			arec.var("firstOutOfOrder", firstOutOfOrder);//!
			arec.breakHere("move list[firstOutOfOrder] into place");//!
	        if (list[firstOutOfOrder] < list[firstOutOfOrder-1])
			{
				temp = list[firstOutOfOrder];
				location = firstOutOfOrder;
				arec.var("temp",temp).var("location", location);//!
				arec.breakHere("temp holds the value we want to insert");//!
				
				do
				{
					arec.breakHere("shift an element up to make room");//!
					list[location] = list[location - 1];
					arec.breakHere("then move to the next lower element");//!
					location--;
					arec.var("location", location);//!
					}
				while (location > 0 && list[location - 1] > temp);
				
				arec.breakHere("Now we know where to insert temp");//!
				list[location] = temp;
			}
			arec.breakHere("Move to the next unordered element");//!
		}//!
		arec.breakHere("Completed insertion sort");//!
	}
	
        
        
}//!
