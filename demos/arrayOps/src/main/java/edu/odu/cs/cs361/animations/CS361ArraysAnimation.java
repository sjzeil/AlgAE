package edu.odu.cs.cs361.animations;



import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import edu.odu.cs.AlgAE.Animations.LocalJavaAnimation;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;
import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;
import edu.odu.cs.AlgAE.Server.Utilities.DiscreteInteger;
import edu.odu.cs.AlgAE.Server.Utilities.Index;


public class CS361ArraysAnimation extends LocalJavaAnimation {

public CS361ArraysAnimation() {
    super("Array Operations");
}

@Override
public String about() {
     return "Demonstration of Array Manipulation Algorithms,\n" +
     "prepared for CS 361, Advanced Data Structures\n" +
     "and Algorithms, Old Dominion University\n" +
     "Summer 2014";
}

private DiscreteInteger[] array = new DiscreteInteger[0];

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
public Directions getDirection() {
     return Directions.Vertical;
}

@Override
public Double getSpacing() {
     return null;
}

@Override
public Boolean getClosedOnConnections() {
     return false;
}

}

private int size = 0;



@Override
public void buildMenu() {

registerStartingAction(new MenuFunction() {

@Override
public void selected() {
     generateRandomArray(8);
     globalVar("array", new ArrayContainer());
}
});



register ("binary search", new MenuFunction() {
@Override
public void selected() {
         String value = promptForInput("Value to search for:", "[0-9]+");
         try {
               Integer v = Integer.parseInt(value);
               int k = new ArrayOperations().binarySearch(array, size, v);
                out.println ("binarySearch returned " + k);
             } catch (Exception e) {
// do nothing
}
}
});


register ("sequential ordered search", new MenuFunction() {
@Override
public void selected() {
       String value = promptForInput("Value to search for:", "[0-9]+");
       try {
             Integer v = Integer.parseInt(value);
             Index k = new ArrayOperations().seqOrderedSearch(array, size, v);
             out.println ("seqOrderedSearch returned " + k);
             } catch (Exception e) {
             // do nothing
             }
}
});



register ("sequential search", new MenuFunction() {
@Override
public void selected() {
       String value = promptForInput("Value to search for:", "[0-9]+");
       try {
             Integer v = Integer.parseInt(value);
             Index k = new ArrayOperations().seqSearch(array, size, v);
             out.println ("seqSearch returned " + k);
             } catch (Exception e) {
                System.err.println ("Unexpected exception from animated code: " + e);
             }
}
});


register ("add in order", new MenuFunction() {
@Override
public void selected() {
     String value = promptForInput("Value to add:", "[0-9]+");
     try {
          Integer v = Integer.parseInt(value);
          new ArrayOperations().addInOrder(array, size, v);
         } catch (Exception e) {
         // do nothing
         }
}
});

register ("generate an array", new MenuFunction() {
@Override
public void selected() {
     randomArrayGenerated();
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
     int extraSlots = 4;
     if (n + extraSlots != array.length) {
     array = new DiscreteInteger[n+extraSlots];
}
if (n > 0) {
     array[0] = new DiscreteInteger((int)(5.0 * Math.random()));
}
for (int i = 1; i < n; ++i) {
     array[i] = new DiscreteInteger(array[i-1].get() + ((int)(5.0 * Math.random())));
}
for (int i = n; i < n+extraSlots; ++i) {
     array[i] = new DiscreteInteger(-1);
}
     size= n;
}

 

public static void main (String[] args) {
     CS361ArraysAnimation demo = new CS361ArraysAnimation();
     demo.runAsMain();
}

}