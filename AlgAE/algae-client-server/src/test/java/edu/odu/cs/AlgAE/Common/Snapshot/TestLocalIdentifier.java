package edu.odu.cs.AlgAE.Common.Snapshot;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestLocalIdentifier {
	
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

	@Test
	public void testLocalIdentifier() {
		A a1 = new A();
		A a2 = new A();
		Identifier id1 = new Identifier(a1);
		Identifier id2 = new Identifier(a2);
		Identifier id3 = new Identifier(a1);
		assertNotEquals (id1, id2);
		assertNotEquals (id2, id3);
		assertEquals (id1, id3);
	}

}
