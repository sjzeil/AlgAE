/**
 * 
 */
package edu.odu.cs.AlgAE.Common.Snapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import edu.odu.cs.AlgAE.Server.MemoryModel.Identifier;


/**
 * @author zeil
 *
 */
public class TestIdentifier {
	
	public static void main(String args[]) {
		org.junit.runner.JUnitCore.main(TestIdentifier.class.getName());
	}


	public static class A {
		private char c;
		public A() {
			c = 'a';
		}
		
		public int hashCode()
		{
			return 1;
		}
		
		public boolean equals (Object obj) {
			fail("equals invoked instead of ==");
			return true;
		}
		
		public String toString() {
			return "" + c;
		}
	}

	
	private A a1;
	private A a2;
	
	private Identifier id1;
	private Identifier id2;
	private Identifier id3;
	private Identifier id4;

	@Before
	public void setUpOnce()  {
		a1 = new A();
		a2 = new A();
		id1 = new Identifier();
		id2 = new Identifier(a1);
		id3 = new Identifier(a2);
		id4 = new Identifier(a2);
	}
	
	

	@Test
	public void testLocalIdentifier() {
		assertNotEquals (id1, id2);
		assertNotEquals (id2, id3);
		assertEquals (id3, id4);
	}

	/**
	 * Test method for {@link edu.odu.cs.AlgAE.Server.MemoryModel.Identifier#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		assertFalse(id1.hashCode() == id2.hashCode());
		assertFalse(id1.hashCode() == id3.hashCode());
		assertFalse(id2.hashCode() == id3.hashCode());
		assertEquals (id3.hashCode(), id4.hashCode());
	}


	/**
	 * Test method for {@link edu.odu.cs.AlgAE.Server.MemoryModel.Identifier#toString()}.
	 */
	@Test
	public void testToString() {
		assertNotEquals (id1.toString(), id2.toString());
		assertNotEquals (id2.toString(), id3.toString());
		assertEquals (id3.toString(), id4.toString());
	}


}
