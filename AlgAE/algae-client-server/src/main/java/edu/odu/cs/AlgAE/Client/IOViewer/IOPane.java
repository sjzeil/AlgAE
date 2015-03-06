package edu.odu.cs.AlgAE.Client.IOViewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.odu.cs.AlgAE.Common.Communications.ServerCommunications;

public class IOPane extends JPanel
{
  private JTextArea stdOutputArea;

  public IOPane(ServerCommunications anim)
  {
    super();
	setLayout (new BorderLayout());
	stdOutputArea = new JTextArea();
	stdOutputArea.setEditable(false);
	stdOutputArea.setRows(12);
	add(new JScrollPane(stdOutputArea), BorderLayout.CENTER);
	

  }




  public void setFontSize (int size)
  {
    Font textFont = new Font("Courier", Font.PLAIN, size);
    stdOutputArea.setFont (textFont);
    repaint();
  }


  public Dimension getPreferredSize() {
    return new Dimension (250,50);
  }


  public void print (String str)
  {
	  stdOutputArea.append(str);

	  // Make sure the last line is always visible
	  stdOutputArea.setCaretPosition(stdOutputArea.getDocument().getLength());

	  // Keep the text area down to a certain character size
	  int maxSize = 50000;
	  int consoleSize = stdOutputArea.getDocument().getLength();
	  if (consoleSize >= maxSize) {
		  stdOutputArea.replaceRange("", 0, maxSize/2);
	  }
  }




}
