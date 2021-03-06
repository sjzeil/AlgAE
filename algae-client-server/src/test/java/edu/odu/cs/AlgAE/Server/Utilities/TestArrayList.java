/**
 * 
 */
package edu.odu.cs.AlgAE.Server.Utilities;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

import org.junit.Test;

/**
 * @author zeil
 *
 */
public class TestArrayList {

    /**
     * Test method for {@link edu.odu.cs.AlgAE.Server.Utilities.ArrayList#ArrayList()}.
     */
    @Test
    public void testArrayList() {
        Integer two = Integer.valueOf(2);
        ArrayList<Integer> al = new ArrayList<>();
        assertFalse (al.contains(two));
        al.forEach((x) -> fail("forEach failure"));
        try {
            al.get(0);
            fail("did not detect out-of-range access");
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        assertEquals (-1, al.indexOf(two));
        assertEquals (-1, al.lastIndexOf(two));
        assertTrue (al.isEmpty());
        
        Iterator<Integer> it = al.iterator();
        assertFalse(it.hasNext());
        
        ListIterator<Integer> lit = al.listIterator();
        assertFalse(lit.hasNext());
        
        try {
            al.listIterator(1);
            fail("did not detect out-of-range access");
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        
        assertEquals (0, al.size());
        
        try {
            al.subList(0, 1);
            fail("did not detect out-of-range access");
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        
        Object[] arr1 = al.toArray();
        assertEquals(0, arr1.length);
        
        Integer[] arr2 = new Integer[1];
        arr2[0] = 1;
        Integer[] arr3 = al.toArray(arr2);
        assertSame (arr2, arr3);
        assertNull(arr2[0]);
        
        al.toString();
        al.trimToSize();
    }

    /**
     * Test method for {@link edu.odu.cs.AlgAE.Server.Utilities.ArrayList#ArrayList(int)}.
     */
    @Test
    public void testArrayListInt() {
        Integer two = Integer.valueOf(2);
        ArrayList<Integer> al = new ArrayList<>(12);
        assertFalse (al.contains(two));
        al.forEach((x) -> fail("forEach failure"));
        try {
            al.get(0);
            fail("did not detect out-of-range access");
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        assertEquals (-1, al.indexOf(two));
        assertEquals (-1, al.lastIndexOf(two));
        assertTrue (al.isEmpty());
        
        Iterator<Integer> it = al.iterator();
        assertFalse(it.hasNext());
        
        ListIterator<Integer> lit = al.listIterator();
        assertFalse(lit.hasNext());
        
        try {
            al.listIterator(1);
            fail("did not detect out-of-range access");
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        
        assertEquals (0, al.size());
        
        try {
            al.subList(0, 1);
            fail("did not detect out-of-range access");
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
        
        Object[] arr1 = al.toArray();
        assertEquals(0, arr1.length);
        
        Integer[] arr2 = new Integer[1];
        arr2[0] = 1;
        Integer[] arr3 = al.toArray(arr2);
        assertSame (arr2, arr3);
        assertNull(arr2[0]);
        
        al.toString();
        al.trimToSize();
    }

    class Summation implements Consumer<Integer> {

        public int sum;
        
        public Summation() {
            sum = 0;
        }
        
        @Override
        public void accept(Integer t) {
            sum += t;
        }
        
    }
    
    /**
     * Test method for {@link edu.odu.cs.AlgAE.Server.Utilities.ArrayList#ArrayList(java.util.Collection)}.
     */
    @Test
    public void testArrayListCollectionOfQextendsT() {
        List<Integer> in = new java.util.ArrayList<Integer>();
        Integer zero = Integer.valueOf(0);
        Integer one = Integer.valueOf(1);
        Integer two = Integer.valueOf(2);
        in.add(zero);
        in.add(one);
        in.add(two);
        ArrayList<Integer> al = new ArrayList<>(in);
        assertTrue (al.contains(two));
        Summation sum = new Summation();;
        al.forEach(sum);
        assertEquals (3, sum.sum);
        assertEquals (zero, al.get(0));
        assertEquals (two, al.get(2));
        assertEquals (2, al.indexOf(two));
        assertEquals (1, al.lastIndexOf(one));
        assertFalse (al.isEmpty());
        
        Iterator<Integer> it = al.iterator();
        assertTrue(it.hasNext());
        assertEquals (zero, it.next());
        assertEquals (one, it.next());
        assertEquals (two, it.next());
        assertFalse(it.hasNext());
        
        assertEquals (3, al.size());
        
        List<Integer> subl = al.subList(1, 3);
        assertEquals (2, subl.size());
        assertEquals (one, subl.get(0));
        
        Object[] arr1 = al.toArray();
        assertEquals(3, arr1.length);
        assertSame (arr1[0], zero);
        
        Integer[] arr2 = new Integer[1];
        arr2[0] = 1;
        Integer[] arr3 = al.toArray(arr2);
        assertNotSame (arr2, arr3);
        assertSame(arr3[2], two);
        
        
        assertTrue (al.toString().contains("0"));
        assertTrue (al.toString().contains("1"));
        assertTrue (al.toString().contains("2"));

        ListIterator<Integer> lit = al.listIterator();
        assertTrue(lit.hasNext());
        assertEquals (zero, lit.next());
        
        ListIterator<Integer> lit2 = al.listIterator(1);
        assertTrue(lit2.hasNext());
        assertEquals (one, lit2.next());

        Integer four = Integer.valueOf(4);
        lit.add(4);
        assertEquals (4, al.size());
        assertEquals (zero, al.get(0));
        assertEquals (four, al.get(1));
        assertEquals (one, al.get(2));
        assertEquals (two, al.get(3));
        assertEquals (one, lit.next());
        
        lit.remove();
        assertEquals (3, al.size());
        assertEquals (zero, al.get(0));
        assertEquals (four, al.get(1));
        assertEquals (two, al.get(2));
        assertEquals (two, lit.next());
        
        
        
    }

    /**
     * Test method for {@link edu.odu.cs.AlgAE.Server.Utilities.ArrayList#set(int, java.lang.Object)}.
     */
    @Test
    public void testSetIntT() {
        List<Integer> in = new java.util.ArrayList<Integer>();
        Integer zero = Integer.valueOf(0);
        Integer one = Integer.valueOf(1);
        Integer two = Integer.valueOf(2);
        in.add(zero);
        in.add(zero);
        in.add(two);
        ArrayList<Integer> al = new ArrayList<>(in);
        al.set(1, one);
        assertTrue (al.contains(one));
        Summation sum = new Summation();;
        al.forEach(sum);
        assertEquals (3, sum.sum);
        assertEquals (zero, al.get(0));
        assertEquals (two, al.get(2));
        assertEquals (2, al.indexOf(two));
        assertEquals (1, al.lastIndexOf(one));
        assertFalse (al.isEmpty());
        
        Iterator<Integer> it = al.iterator();
        assertTrue(it.hasNext());
        assertEquals (zero, it.next());
        assertEquals (one, it.next());
        assertEquals (two, it.next());
        assertFalse(it.hasNext());
        
        assertEquals (3, al.size());
        
        List<Integer> subl = al.subList(1, 3);
        assertEquals (2, subl.size());
        assertEquals (one, subl.get(0));
        
        Object[] arr1 = al.toArray();
        assertEquals(3, arr1.length);
        assertSame (arr1[0], zero);
        
        Integer[] arr2 = new Integer[1];
        arr2[0] = 1;
        Integer[] arr3 = al.toArray(arr2);
        assertNotSame (arr2, arr3);
        assertSame(arr3[2], two);
        
        
        assertTrue (al.toString().contains("0"));
        assertTrue (al.toString().contains("1"));
        assertTrue (al.toString().contains("2"));

        ListIterator<Integer> lit = al.listIterator();
        assertTrue(lit.hasNext());
        assertEquals (zero, lit.next());
        
        ListIterator<Integer> lit2 = al.listIterator(1);
        assertTrue(lit2.hasNext());
        assertEquals (one, lit2.next());

        Integer four = Integer.valueOf(4);
        lit.add(4);
        assertEquals (4, al.size());
        assertEquals (zero, al.get(0));
        assertEquals (four, al.get(1));
        assertEquals (one, al.get(2));
        assertEquals (two, al.get(3));
        assertEquals (one, lit.next());
        
        lit.remove();
        assertEquals (3, al.size());
        assertEquals (zero, al.get(0));
        assertEquals (four, al.get(1));
        assertEquals (two, al.get(2));
        assertEquals (two, lit.next());
        
        
        
    }

    /**
     * Test method for {@link java.util.AbstractList#add(int, java.lang.Object)}.
     */
    @Test
    public void testAddIntE() {
        List<Integer> in = new java.util.ArrayList<Integer>();
        Integer zero = Integer.valueOf(0);
        Integer one = Integer.valueOf(1);
        Integer two = Integer.valueOf(2);
        in.add(zero);
        in.add(two);
        ArrayList<Integer> al = new ArrayList<>(in);
        al.add(1, one);
        assertTrue (al.contains(one));
        assertTrue (al.contains(two));
        Summation sum = new Summation();;
        al.forEach(sum);
        assertEquals (3, sum.sum);
        assertEquals (zero, al.get(0));
        assertEquals (two, al.get(2));
        assertEquals (2, al.indexOf(two));
        assertEquals (1, al.lastIndexOf(one));
        assertFalse (al.isEmpty());
        
        Iterator<Integer> it = al.iterator();
        assertTrue(it.hasNext());
        assertEquals (zero, it.next());
        assertEquals (one, it.next());
        assertEquals (two, it.next());
        assertFalse(it.hasNext());
        
        assertEquals (3, al.size());
        
        List<Integer> subl = al.subList(1, 3);
        assertEquals (2, subl.size());
        assertEquals (one, subl.get(0));
        
        Object[] arr1 = al.toArray();
        assertEquals(3, arr1.length);
        assertSame (arr1[0], zero);
        
        Integer[] arr2 = new Integer[1];
        arr2[0] = 1;
        Integer[] arr3 = al.toArray(arr2);
        assertNotSame (arr2, arr3);
        assertSame(arr3[2], two);
        
        
        assertTrue (al.toString().contains("0"));
        assertTrue (al.toString().contains("1"));
        assertTrue (al.toString().contains("2"));

        ListIterator<Integer> lit = al.listIterator();
        assertTrue(lit.hasNext());
        assertEquals (zero, lit.next());
        
        ListIterator<Integer> lit2 = al.listIterator(1);
        assertTrue(lit2.hasNext());
        assertEquals (one, lit2.next());

        Integer four = Integer.valueOf(4);
        lit.add(4);
        assertEquals (4, al.size());
        assertEquals (zero, al.get(0));
        assertEquals (four, al.get(1));
        assertEquals (one, al.get(2));
        assertEquals (two, al.get(3));
        assertEquals (one, lit.next());
        
        lit.remove();
        assertEquals (3, al.size());
        assertEquals (zero, al.get(0));
        assertEquals (four, al.get(1));
        assertEquals (two, al.get(2));
        assertEquals (two, lit.next());
        
        
        
    }

    /**
     * Test method for {@link java.util.AbstractList#remove(int)}.
     */
    @Test
    public void testRemoveInt() {
        List<Integer> in = new java.util.ArrayList<Integer>();
        Integer zero = Integer.valueOf(0);
        Integer one = Integer.valueOf(1);
        Integer two = Integer.valueOf(2);
        in.add(zero);
        in.add(one);
        in.add(4);
        in.add(two);
        ArrayList<Integer> al = new ArrayList<>(in);
        al.remove(2);
        assertTrue (al.contains(one));
        assertTrue (al.contains(two));
        Summation sum = new Summation();;
        al.forEach(sum);
        assertEquals (3, sum.sum);
        assertEquals (zero, al.get(0));
        assertEquals (two, al.get(2));
        assertEquals (2, al.indexOf(two));
        assertEquals (1, al.lastIndexOf(one));
        assertFalse (al.isEmpty());
        
        Iterator<Integer> it = al.iterator();
        assertTrue(it.hasNext());
        assertEquals (zero, it.next());
        assertEquals (one, it.next());
        assertEquals (two, it.next());
        assertFalse(it.hasNext());
        
        assertEquals (3, al.size());
        
        List<Integer> subl = al.subList(1, 3);
        assertEquals (2, subl.size());
        assertEquals (one, subl.get(0));
        
        Object[] arr1 = al.toArray();
        assertEquals(3, arr1.length);
        assertSame (arr1[0], zero);
        
        Integer[] arr2 = new Integer[1];
        arr2[0] = 1;
        Integer[] arr3 = al.toArray(arr2);
        assertNotSame (arr2, arr3);
        assertSame(arr3[2], two);
        
        
        assertTrue (al.toString().contains("0"));
        assertTrue (al.toString().contains("1"));
        assertTrue (al.toString().contains("2"));

        ListIterator<Integer> lit = al.listIterator();
        assertTrue(lit.hasNext());
        assertEquals (zero, lit.next());
        
        ListIterator<Integer> lit2 = al.listIterator(1);
        assertTrue(lit2.hasNext());
        assertEquals (one, lit2.next());

        Integer four = Integer.valueOf(4);
        lit.add(4);
        assertEquals (4, al.size());
        assertEquals (zero, al.get(0));
        assertEquals (four, al.get(1));
        assertEquals (one, al.get(2));
        assertEquals (two, al.get(3));
        assertEquals (one, lit.next());
        
        lit.remove();
        assertEquals (3, al.size());
        assertEquals (zero, al.get(0));
        assertEquals (four, al.get(1));
        assertEquals (two, al.get(2));
        assertEquals (two, lit.next());
        
        
        
    }

    /**
     * Test method for {@link java.util.AbstractList#addAll(int, java.util.Collection)}.
     */
    @Test
    public void testAddAllIntCollectionOfQextendsE() {
        List<Integer> in = new java.util.ArrayList<Integer>();
        Integer zero = Integer.valueOf(0);
        Integer one = Integer.valueOf(1);
        Integer two = Integer.valueOf(2);
        in.add(one);
        in.add(two);
        ArrayList<Integer> al = new ArrayList<>();
        al.add(zero);
        al.addAll(in);
        assertTrue (al.contains(two));
        Summation sum = new Summation();;
        al.forEach(sum);
        assertEquals (3, sum.sum);
        assertEquals (zero, al.get(0));
        assertEquals (two, al.get(2));
        assertEquals (2, al.indexOf(two));
        assertEquals (1, al.lastIndexOf(one));
        assertFalse (al.isEmpty());
        
        Iterator<Integer> it = al.iterator();
        assertTrue(it.hasNext());
        assertEquals (zero, it.next());
        assertEquals (one, it.next());
        assertEquals (two, it.next());
        assertFalse(it.hasNext());
        
        assertEquals (3, al.size());
        
        List<Integer> subl = al.subList(1, 3);
        assertEquals (2, subl.size());
        assertEquals (one, subl.get(0));
        
        Object[] arr1 = al.toArray();
        assertEquals(3, arr1.length);
        assertSame (arr1[0], zero);
        
        Integer[] arr2 = new Integer[1];
        arr2[0] = 1;
        Integer[] arr3 = al.toArray(arr2);
        assertNotSame (arr2, arr3);
        assertSame(arr3[2], two);
        
        
        assertTrue (al.toString().contains("0"));
        assertTrue (al.toString().contains("1"));
        assertTrue (al.toString().contains("2"));

        ListIterator<Integer> lit = al.listIterator();
        assertTrue(lit.hasNext());
        assertEquals (zero, lit.next());
        
        ListIterator<Integer> lit2 = al.listIterator(1);
        assertTrue(lit2.hasNext());
        assertEquals (one, lit2.next());

        Integer four = Integer.valueOf(4);
        lit.add(4);
        assertEquals (4, al.size());
        assertEquals (zero, al.get(0));
        assertEquals (four, al.get(1));
        assertEquals (one, al.get(2));
        assertEquals (two, al.get(3));
        assertEquals (one, lit.next());
        
        lit.remove();
        assertEquals (3, al.size());
        assertEquals (zero, al.get(0));
        assertEquals (four, al.get(1));
        assertEquals (two, al.get(2));
        assertEquals (two, lit.next());
                
    }



    /**
     * Test method for {@link java.util.AbstractCollection#remove(java.lang.Object)}.
     */
    @Test
    public void testRemoveObject() {
        List<Integer> in = new java.util.ArrayList<Integer>();
        Integer zero = Integer.valueOf(0);
        Integer one = Integer.valueOf(1);
        Integer two = Integer.valueOf(2);
        Integer five = Integer.valueOf(5);
        in.add(zero);
        in.add(one);
        in.add(five);
        in.add(two);
        ArrayList<Integer> al = new ArrayList<>(in);
        al.remove(five);
        assertTrue (al.contains(one));
        assertTrue (al.contains(two));
        Summation sum = new Summation();;
        al.forEach(sum);
        assertEquals (3, sum.sum);
        assertEquals (zero, al.get(0));
        assertEquals (two, al.get(2));
        assertEquals (2, al.indexOf(two));
        assertEquals (1, al.lastIndexOf(one));
        assertFalse (al.isEmpty());
        
        Iterator<Integer> it = al.iterator();
        assertTrue(it.hasNext());
        assertEquals (zero, it.next());
        assertEquals (one, it.next());
        assertEquals (two, it.next());
        assertFalse(it.hasNext());
        
        assertEquals (3, al.size());
        
        List<Integer> subl = al.subList(1, 3);
        assertEquals (2, subl.size());
        assertEquals (one, subl.get(0));
        
        Object[] arr1 = al.toArray();
        assertEquals(3, arr1.length);
        assertSame (arr1[0], zero);
        
        Integer[] arr2 = new Integer[1];
        arr2[0] = 1;
        Integer[] arr3 = al.toArray(arr2);
        assertNotSame (arr2, arr3);
        assertSame(arr3[2], two);
        
        
        assertTrue (al.toString().contains("0"));
        assertTrue (al.toString().contains("1"));
        assertTrue (al.toString().contains("2"));

        ListIterator<Integer> lit = al.listIterator();
        assertTrue(lit.hasNext());
        assertEquals (zero, lit.next());
        
        ListIterator<Integer> lit2 = al.listIterator(1);
        assertTrue(lit2.hasNext());
        assertEquals (one, lit2.next());

        Integer four = Integer.valueOf(4);
        lit.add(4);
        assertEquals (4, al.size());
        assertEquals (zero, al.get(0));
        assertEquals (four, al.get(1));
        assertEquals (one, al.get(2));
        assertEquals (two, al.get(3));
        assertEquals (one, lit.next());
        
        lit.remove();
        assertEquals (3, al.size());
        assertEquals (zero, al.get(0));
        assertEquals (four, al.get(1));
        assertEquals (two, al.get(2));
        assertEquals (two, lit.next());
        
        
        
   }

}
