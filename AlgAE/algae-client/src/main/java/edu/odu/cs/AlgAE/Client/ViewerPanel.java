package edu.odu.cs.AlgAE.Client;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import edu.odu.cs.AlgAE.Client.DataViewer.Animator;
import edu.odu.cs.AlgAE.Client.IOViewer.IOPane;
import edu.odu.cs.AlgAE.Client.SourceViewer.SourcePane;






public class ViewerPanel extends JPanel
{

  private Animator dataPane;
  private SourcePane sourcePane;
  private IOPane ioPane;
    

  private JLabel statusLine;
  

    

  public ViewerPanel(String theTitle, 
		     Animator datapane, SourcePane sourcepane,
		     IOPane iopane)
  {
	  this.dataPane = datapane;
	  this.sourcePane = sourcepane;
	  this.ioPane = iopane;
	  
	  ioPane.setMinimumSize(new Dimension(0,0));
	  sourcePane.setMinimumSize(new Dimension(0,0));
	  dataPane.setMinimumSize(new Dimension(0,0));

	  dataPane.setPreferredSize(new Dimension(800,400));
	  ioPane.setPreferredSize(new Dimension(200,300));
	  sourcePane.setPreferredSize(new Dimension(600,300));

	  
	  JSplitPane splitPanel = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT,
			  sourcePane, ioPane);
	  splitPanel.setResizeWeight(0.33);
	  splitPanel.setOneTouchExpandable(true);

	  JSplitPane mainPanel = new JSplitPane (JSplitPane.VERTICAL_SPLIT,
			  dataPane, splitPanel);
	  mainPanel.setResizeWeight(0.33);
	  mainPanel.setOneTouchExpandable(true);
	  splitPanel.setMinimumSize(new Dimension(0,0));
	  splitPanel.setPreferredSize(new Dimension(800,200));
	  dataPane.setMinimumSize(new Dimension(200,100));


	  setLayout(new BorderLayout());

	  add (mainPanel, BorderLayout.CENTER);

	  statusLine = new JLabel("");

	  add (statusLine, BorderLayout.SOUTH);


  }

  

  
  


}
