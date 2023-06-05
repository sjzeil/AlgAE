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

import edu.odu.cs.AlgAE.Server.MemoryModel.Identifier;

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
		entity1b = new Entity(id1, entity2.getEntityIdentifier(), "componentA");
		Identifier id3 = new Identifier(3);
		entity3 = new Entity(id3, "labeled");
		entity2.getComponents().add(entity1b.getEntityIdentifier());
		entity2.setValue("foobar");
		entity3.getConnections().add(new Connector("link", entity3.getEntityIdentifier(),
				entity2.getEntityIdentifier(), 0, 180));
	}

	




}
