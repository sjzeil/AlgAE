/**
 * 
 */
package edu.odu.cs.AlgAE.Common.Snapshot;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
//import static org.hamcrest.MatcherAssert.assertThat; 
//import static org.hamcrest.Matchers.*;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


import com.google.gson.Gson;

import edu.odu.cs.AlgAE.Server.MemoryModel.Identifier;

/**
 * @author zeil
 *
 */
public class TestEntityIdentifier {
	
	

	private static EntityIdentifier id1;
	private static EntityIdentifier id2;

	@BeforeAll
	public static void setUpOnce()  {
		id1 = new Identifier(1).asEntityIdentifier();
		id2 = new Identifier(2).asEntityIdentifier();
	}

	@Test
	public void testBasics() {
		//assertEquals ("entityA", id1.getComponentLabel());
		//assertEquals ("entityB", id2.getComponentLabel());
		//assertEquals (Identifier.nullID, id1.getContainer());
		
		assertNotEquals(id1.toString(), id2.toString());
	}



	void xmlTest (Object x, String mustContain1, String mustContain2)
	{
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		XMLEncoder out = new XMLEncoder(new BufferedOutputStream(bytesOut));
		out.writeObject(x);
		out.close();
		String xmlStr = bytesOut.toString();
		assertTrue (xmlStr.contains(x.getClass().getSimpleName()));
		if (mustContain1.length() > 0)
			assertTrue (xmlStr.contains(mustContain1));
		if (mustContain2.length() > 0)
			assertTrue (xmlStr.contains(mustContain2));
		
		XMLDecoder in = new XMLDecoder(new ByteArrayInputStream(xmlStr.getBytes()));
		Object y = in.readObject();
		
		assertEquals (x, y);	
		in.close();
	}
	
	@Test
	public void testSerialization()
	{
		Gson gson = new Gson();
		String json1 = gson.toJson(id1);
		String json2 = gson.toJson(id2);
		assertNotEquals (json1, json2);
		
		EntityIdentifier eid1 = gson.fromJson(json1, EntityIdentifier.class);
		assertEquals (id1, eid1);
		assertEquals (id1.toString(), eid1.toString());
		EntityIdentifier eid2 = gson.fromJson(json2, EntityIdentifier.class);
		assertEquals (id2, eid2);
		assertEquals (id2.toString(), eid2.toString());
	}

}
