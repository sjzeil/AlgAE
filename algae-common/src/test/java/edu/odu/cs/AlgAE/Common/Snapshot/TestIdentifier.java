/**
 * 
 */
package edu.odu.cs.AlgAE.Common.Snapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author zeil
 *
 */
public class TestIdentifier {
	
	public static void main(String args[]) {
		org.junit.runner.JUnitCore.main(TestIdentifier.class.getName());
	}


	private static Identifier id1;
	private static Identifier id2;
	private static Identifier id3;
	private static Identifier id4;

	@BeforeClass
	public static void setUpOnce()  {
		id1 = new RemoteIdentifier();
		id2 = new LocalIdentifier(id1);
		id3 = new RemoteIdentifier(123);
		id4 = new RemoteIdentifier(123);
	}

	/**
	 * Test method for {@link edu.odu.cs.AlgAE.Common.Snapshot.Identifier#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		assertFalse(id1.hashCode() == id2.hashCode());
		assertFalse(id1.hashCode() == id3.hashCode());
		assertFalse(id2.hashCode() == id3.hashCode());
		assertEquals (id3.hashCode(), id4.hashCode());
	}


	/**
	 * Test method for {@link edu.odu.cs.AlgAE.Common.Snapshot.Identifier#toString()}.
	 */
	@Test
	public void testToString() {
		assertTrue (id1.toString().contains("0"));
		assertTrue (id2.toString().contains("Identifier"));
		assertTrue (id3.toString().contains("123"));
		assertEquals (id3.toString(), id4.toString());
	}

	/**
	 * Test method for {@link edu.odu.cs.AlgAE.Common.Snapshot.Identifier#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		assertEquals(id3, id4);
		assertFalse(id1.equals(id2));
		assertFalse(id1.equals(id3));
		assertFalse(id2.equals(id3));
		assertFalse(id2.equals(id1));
		assertFalse(id2.equals(id3));
	}

	void xmlTest (Object x, String mustContain1, String mustContain2)
	{
		ByteArrayOutputStream byout = new ByteArrayOutputStream();
		XMLEncoder out = new XMLEncoder(new BufferedOutputStream(byout));
		out.writeObject(x);
		out.close();
		String xmlStr = byout.toString();
		assertTrue (xmlStr.contains(x.getClass().getSimpleName()));
		if (mustContain1.length() > 0)
			assertTrue (xmlStr.contains(mustContain1));
		if (mustContain2.length() > 0)
			assertTrue (xmlStr.contains(mustContain2));
		
		XMLDecoder in = new XMLDecoder(new ByteArrayInputStream(xmlStr.getBytes()));
		Object y = in.readObject();
		
		assertEquals (x, y);		
	}
	
	@Test
	public void testXML()
	{
		xmlTest (id1, "0", "Identifier");
		xmlTest (id3, "123", "Identifier");
	}

}
