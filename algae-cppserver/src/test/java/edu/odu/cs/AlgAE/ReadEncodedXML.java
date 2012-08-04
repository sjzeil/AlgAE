/**
 * Support class for testing XML inter-operability between Java client and
 * C++ server.
 * 
 */
package edu.odu.cs.AlgAE;


import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zeil
 *
 */
public class ReadEncodedXML {
	
	public static void main(String args[]) {
		Object x = readXML();
		listAttributes (x);
	}

	private static void listAttributes(Object x) {
		Class<?> c = x.getClass();
		System.out.println ("Class\t" + c.getSimpleName());
		Method[] methods = c.getMethods();
		for (Method m: methods) {
			String name = m.getName();
			//System.err.println (name + " 1");
			if (name.startsWith("get") || name.startsWith("is")) {
				if (m.getParameterTypes().length == 0) {
					try {
						Object result = m.invoke(x);
						if (result == null)
							result = "*null*";
						//System.err.println ("got " + name + " result: " + result + " in " + result.getClass().getSimpleName());
						String resultStr = result.toString().replaceAll("[^ -z]", " ");
						if (name.startsWith("get"))
							name = name.substring(3).toLowerCase();
						else
							name = name.substring(2).toLowerCase();
						System.out.println (name + "\t" + resultStr);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static Object readXML() {
		XMLDecoder in = new XMLDecoder(new BufferedInputStream(System.in));
		Object x = in.readObject();
		in.close();
		return x;
	}


}
