package edu.odu.cs.AlgAE.Client.SourceViewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;


/**
 * SourceFile.java
 *
 * A description of a file of animated source code
 *
 *
 * @author Steven Zeil
 * @version
 */

public class SourceFile
{
    private static final String SpecialSourceMarker = "//!";
    private String fileName;
    private String contents;
    private ArrayList<Integer> lineOffsets;
    private int selectedLine;



    public SourceFile(Class<?> container, String fileName) {
        this.fileName = fileName;
        contents = "";
        lineOffsets = new ArrayList<Integer>();
        lineOffsets.add(0);
        selectedLine = -1;
        load(container);
    }


    public SourceFile(String fileName) {
        this.fileName = fileName;
        contents = "** not yet loaded **\n";
        lineOffsets = new ArrayList<Integer>();
        lineOffsets.add(0);
        selectedLine = -1;
        setContents(contents);
    }



    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }



    public String getContents() {
        return contents;
    }


    public ArrayList<Integer> getLineOffsets() {
        return lineOffsets;
    }



    public void setSelectedLine(int selectedLine) {
        this.selectedLine = selectedLine;
    }



    public int getSelectedLine() {
        return selectedLine;
    }


    public void setContents(String text)
    {
        contents = "";
        StringBuffer contentBuf = new StringBuffer();
        BufferedReader in = new BufferedReader (new StringReader(text));
        int offSet = 0;
        int lineCount = 0;
        lineOffsets.clear();
        try {
            while (in.ready()) {
                lineOffsets.add(offSet);
                String line = in.readLine();
                if (line == null)
                    break;
                ++lineCount;
                if (line.contains(SpecialSourceMarker)) {
                    line = line.substring(line.indexOf(SpecialSourceMarker) + SpecialSourceMarker.length());
                    if (line.length() == 0) {
                        continue;
                    }
                }
                line = line.replace("\t", "    ");
                String lineNum = "" + lineCount;
                while (lineNum.length() < 3) {
                    lineNum = "0" + lineNum;
                }
                line = lineNum + ": " + line + "\n";
                contentBuf.append(line);
                offSet += line.length();
            }
            contents = contentBuf.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void load(Class<?> container)
    {
        String resourceName = "/" + container.getPackage().getName().replace(".", "/") + "/" + fileName;
        
        InputStream resourceIn = container.getResourceAsStream(resourceName);
        //System.err.println ("resourceIn: " + resourceIn);
        if (resourceIn != null) {
            StringBuffer contentBuf = new StringBuffer();
            BufferedReader in = new BufferedReader (new InputStreamReader(resourceIn));
            int offSet = 0;
            int lineCount = 0;
            lineOffsets.clear();
            try {
                while (in.ready()) {
                    lineOffsets.add(offSet);
                    String line = in.readLine();
                    ++lineCount;
                    if (line.contains(SpecialSourceMarker)) {
                        line = line.substring(line.indexOf(SpecialSourceMarker) + SpecialSourceMarker.length());
                        if (line.length() == 0) {
                            continue;
                        }
                    }
                    line = line.replace("\t", "    ");
                    String lineNum = "" + lineCount;
                    while (lineNum.length() < 3) {
                        lineNum = "0" + lineNum;
                    }
                    line = lineNum + ": " + line + "\n";
                    contentBuf.append(line);
                    offSet += line.length();
                }
                contents = contentBuf.toString();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            contents = "Could not load " + fileName;
        }
    }



} // SourceFile
