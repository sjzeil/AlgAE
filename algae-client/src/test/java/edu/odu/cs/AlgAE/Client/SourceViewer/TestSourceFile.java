/**
 * 
 */
package edu.odu.cs.AlgAE.Client.SourceViewer;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import edu.odu.cs.AlgAE.Client.SourceViewer.SourceFile;


/**
 * @author zeil
 *
 */
public class TestSourceFile {



	/**
	 * Test method for {@link edu.odu.cs.AlgAE.Client.SourceViewer.SourceFile#load()}.
	 */
	@Ignore
	public void testLoadSelf() {
		SourceFile file = new SourceFile(getClass(), "TestSourceFile.java");
		String sourceCode = file.getContents();
		System.out.print (sourceCode);
		
		assertTrue (sourceCode.contains("testLoadSelf()"));
		assertTrue (sourceCode.startsWith("001: /**"));
		assertTrue (sourceCode.endsWith("// last line\n"));
		// Don't show this //! Show this instead
		assertFalse (sourceCode.contains("Don't show " + "this"));
		assertFalse (sourceCode.contains("!" + " "));
	}

	/**
	 * Test method for {@link edu.odu.cs.AlgAE.Client.SourceViewer.SourceFile#load()}.
	 */
	@Test
	public void testSetContents() {
		SourceFile file = new SourceFile("someOtherFile.java");
		String sourceCode = file.getContents();
		System.out.print (sourceCode);
		assertTrue(sourceCode.contains("**"));
		
		file.setContents("int main (String[] args) {\nreturn;\n   // Don't show this //! Show this instead\n}\n");
		
		sourceCode = file.getContents();
		assertTrue (sourceCode.contains("int main"));
		assertTrue (sourceCode.startsWith("001: "));
		assertTrue (sourceCode.endsWith("}\n"));
		assertFalse (sourceCode.contains("Don't show " + "this"));
		assertTrue (sourceCode.contains("Show this instead"));
	}

	
}
// last line
