/**
 * 
 */
package edu.odu.cs.AlgAE.Common.Snapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

/**
 * @author zeil
 *
 */
public class TestEntity {
	
	public static void main(String args[]) {
		org.junit.runner.JUnitCore.main(TestEntity.class.getName());
	}


	private Entity entity1a;
	private Entity entity1b;
	private Entity entity2;
	private Entity entity3;
	
	@Before
	public void setup()
	{
		Identifier id1 = new Identifier(1);
		entity1a = new Entity(id1, "");
		Identifier id2 = new Identifier(2);
		entity2 = new Entity(id2, "label2");
		entity1b = new Entity(id1, entity2, "componentA");
		Identifier id3 = new Identifier(3);
		entity3 = new Entity(id3, "labeled");
		entity2.getComponents().add(entity1b.getEntityIdentifier());
		entity2.setValue("foobar");
		entity3.getConnections().add(new Connector("link", entity3.getEntityIdentifier(),
				entity2.getEntityIdentifier(), 0, 180));
	}

	



	void xmlTest (Object x, String mustContain1, String mustContain2)
	{
		ByteArrayOutputStream byOut = new ByteArrayOutputStream();
		XMLEncoder out = new XMLEncoder(new BufferedOutputStream(byOut));
		out.writeObject(x);
		out.close();
		String xmlStr = byOut.toString();
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
	public void testXML()
	{
		xmlTest (entity1a, entity1a.getEntityIdentifier().getObjectIdentifier().toString(), 
				"");
		xmlTest (entity1b, entity2.getEntityIdentifier().getObjectIdentifier().toString(), "componentA");
		xmlTest (entity2, entity2.getEntityIdentifier().getObjectIdentifier().toString(), "foobar");
		xmlTest (entity3, entity2.getEntityIdentifier().getObjectIdentifier().toString(), "link");
		
	}

}
