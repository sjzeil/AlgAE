package edu.odu.cs.AlgAE.Client.SourceViewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.odu.cs.AlgAE.Common.Communications.ServerMessage;
import edu.odu.cs.AlgAE.Common.Communications.ServerMessage.ServerMessageTypes;
import edu.odu.cs.AlgAE.Common.Communications.ServerCommunications;
import edu.odu.cs.AlgAE.Common.Snapshot.SourceLocation;

public class SourcePane extends JPanel implements SourceViewer
{

  //private static Logger logger = Logger.getLogger(SourcePane.class.getName());



  private JTextArea mainPanel;
  private ArrayList<SourceFile> files;
  private JComboBox<String> fileSelector;
  private int selected;
  private SourceLocation displaying;
  private JComboBox<Integer> fontSizeSelector;
  private ServerCommunications server;




  public SourcePane(ServerCommunications server)
  {
	  this.server = server;
	
	  setLayout(new BorderLayout());

	  mainPanel = new JTextArea();
	  mainPanel.setEditable(false);
	  mainPanel.setSelectedTextColor(Color.red);
	
	  JScrollPane scrolled = new JScrollPane(mainPanel);

	  add (scrolled, BorderLayout.CENTER);

	  JPanel controls = new JPanel();
	  add (controls, BorderLayout.SOUTH);
	
	  fileSelector = new JComboBox<>();
	  controls.add (fileSelector);

	  files = new ArrayList<SourceFile>();

	  Integer[] fontSizes = {8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 28, 32, 36};
	  fontSizeSelector = new JComboBox<Integer>(fontSizes);
	  fontSizeSelector.setSelectedIndex(4);
	  setFontSize(12);
	  fontSizeSelector.addActionListener(
			  new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Integer fontSize = (Integer)fontSizeSelector.getSelectedItem();
			        setFontSize(fontSize);
				}
			}
			  );
	
	  controls.add(new JLabel("  Size:"));
	  controls.add(fontSizeSelector);
	
	  selected = -1;
	  displaying = null;
	  setFocusable(false);
  }

  /**
   * Display a specified block of source code at a specific line number
   */
  public void display (SourceLocation sloc)
  {
	  ServerMessage msg = null;
	  synchronized (this) {
		  if (displaying != null && sloc.equals(displaying)) {
			  return;
		  }
		  if (sloc.getFileName().length() == 0)
			  return;

		  int found = -1;
		  for (int i = 0; i < files.size() && found < 0; ++i) {
			  if (sloc.getFileName().equals(files.get(i).getFileName())) {
				  found = i;
			  }
		  }
		  if (found < 0) {
			  SourceFile newEntry = new SourceFile (sloc.getFileName());
			  found = files.size();
			  files.add(newEntry);
			  fileSelector.addItem (sloc.getFileName());
			  found = files.size()-1;
			  msg = new ServerMessage (ServerMessageTypes.GetSourceCode, sloc.getFileName());
		  }
		
		  SourceFile selectedEntry = files.get(found);
		  if (found != selected) {
			  selected = found;
			  fileSelector.setSelectedIndex(selected);
			  mainPanel.setText(selectedEntry.getContents());
		  }
		  if (sloc.getLineNumber() > 0 && sloc.getLineNumber() <= selectedEntry.getLineOffsets().size()-1) {
			  displaying = sloc;
			  int start = selectedEntry.getLineOffsets().get(sloc.getLineNumber()-1);
			  int lineNum = sloc.getLineNumber();
			  int stop = start;
			  while (lineNum < selectedEntry.getLineOffsets().size() && stop == start) {
				  stop = selectedEntry.getLineOffsets().get(lineNum);
				  ++lineNum;
			  }
			  mainPanel.setCaretPosition(start);
			  mainPanel.moveCaretPosition(stop-1);
			  mainPanel.requestFocus();
			  mainPanel.repaint();
		  }
	  }
	  if (msg != null) {
		  try {
			  server.sendToServer(msg);
		  } catch (InterruptedException e) {
			  //e.printStackTrace();
		  }

	  }
  }


  /**
   * Register a block of text as the source code corresponding to a file name (path).
   *
   * @param fileName   unique identifier for this source code, usually a file name or path
   * @param sourceCodeText  source code text
   */
  public synchronized void addSourceCode (String fileName, String sourceCodeText)
  {
	  int found = -1;
	  for (int i = 0; i < files.size() && found < 0; ++i) {
		  if (fileName.equals(files.get(i).getFileName())) {
			  found = i;
		  }
	  }
	  if (found < 0) {
		  SourceFile newEntry = new SourceFile (fileName);
		  found = files.size();
		  files.add(newEntry);
		  fileSelector.addItem (fileName);
		  found = files.size()-1;
	  }
	  SourceFile selectedEntry = files.get(found);
	  selectedEntry.setContents(sourceCodeText);
  }




  public void setFontSize (int size)
  {
    Font textFont = new Font("Courier", Font.PLAIN, size);
    mainPanel.setFont (textFont);
  }


  public Dimension getPreferredSize() {
    return new Dimension (400,50);
  }




}
