/**
 * 
 */
package edu.odu.cs.AlgAE.Animations;

import edu.odu.cs.AlgAE.Common.Communications.CapturedOutputMessage;
import edu.odu.cs.AlgAE.Common.Communications.ClientCommunications;


/**
 * @author zeil
 *
 */
public class SimulatedPrintStream {
	
	private static ClientCommunications messages;
	

	public SimulatedPrintStream()
	{
	}
	
	public static void setMsgQueue (ClientCommunications msgQueue) 
	{
		messages = msgQueue;
	}
	
	public void print (boolean b)
	{
		print ("" + b);
	}

	public void print (char c)
	{
		print ("" + c);
	}

	public void print (char[] c)
	{
		StringBuffer sb = new StringBuffer();
		sb.append (c);
		print (sb);
	}
	
	public void print (int i)
	{
		print ("" + i);
	}

	public void print (float f)
	{
		print ("" + f);
	}

	public void print (double d)
	{
		print ("" + d);
	}

	public void print (long l)
	{
		print ("" + l);
	}

	public void print (Object o)
	{
		flush (o.toString());
	}

	public void println ()
	{
		flush ("\n");
	}

	public void println (boolean b)
	{
		println ("" + b);
	}

	public void println (char c)
	{
		println ("" + c);
	}

	public void println (char[] c)
	{
		StringBuffer sb = new StringBuffer();
		sb.append (c);
		println (sb);
	}
	
	public void println (int i)
	{
		println ("" + i);
	}

	public void println (float f)
	{
		println ("" + f);
	}

	public void println (double d)
	{
		println ("" + d);
	}

	public void println (long l)
	{
		println ("" + l);
	}

	public void println (Object o)
	{
		flush (o.toString() + "\n");
	}

	private void flush(String output) {
		CapturedOutputMessage msg = new CapturedOutputMessage(output);
		try {
			messages.sendToClient(msg);
		} catch (InterruptedException e) {
		}
		output = "";
	}

	
}
