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
    private static final String SpecialOpenCommentMarker = "/*!";
    private static final String SpecialCloseCommentMarker = "!*/";
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
        try (BufferedReader in = new BufferedReader (new StringReader(text))) {
            extractSourceCode(in);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void load(Class<?> container)
    {
        String resourceName = "/" + container.getPackage().getName().replace(".", "/") + "/" + fileName;
        
        InputStream resourceIn = container.getResourceAsStream(resourceName);
        //System.err.println ("resourceIn: " + resourceIn);
        if (resourceIn != null) {
            try (BufferedReader in = new BufferedReader (new InputStreamReader(resourceIn));) {
                
            extractSourceCode(in);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            contents = "Could not load " + fileName;
        }
    }


    private void extractSourceCode(BufferedReader in) throws IOException {
        StringBuffer contentBuf = new StringBuffer();
        int offSet = 0;
        int lineCount = 0;
        lineOffsets.clear();
        boolean inBlockComment = false;
        while (in.ready()) {
            lineOffsets.add(offSet);
            String line = in.readLine();
            if (line == null)
                break;
            if (inBlockComment) {
                if (line.contains(SpecialCloseCommentMarker)) {
                    line = line.substring(line.indexOf(SpecialCloseCommentMarker)+SpecialCloseCommentMarker.length());
                    inBlockComment = false;
                } else {
                    continue;
                }
            }
            while ((!inBlockComment) && line.contains(SpecialOpenCommentMarker)) {
                if (line.contains(SpecialCloseCommentMarker)) {
                    line = line.substring(0, line.indexOf(SpecialOpenCommentMarker))
                            + line.substring(line.indexOf(SpecialCloseCommentMarker)+SpecialCloseCommentMarker.length());
                } else {
                    line = line.substring(0, line.indexOf(SpecialOpenCommentMarker));
                    inBlockComment = true;
                }
            }
            if (line.endsWith(SpecialSourceMarker)) {
                continue;
            }
            if (line.contains(SpecialSourceMarker)) {
                line = line.substring(line.indexOf(SpecialSourceMarker) + SpecialSourceMarker.length());
                if (line.trim().length() == 0) {
                    continue;
                }
            }
            line = line.replace("\t", "    ");
            ++lineCount;
            String lineNum = "" + lineCount;
            while (lineNum.length() < 3) {
                lineNum = "0" + lineNum;
            }
            line = lineNum + ": " + line + "\n";
            contentBuf.append(line);
            offSet += line.length();
        }
        contents = contentBuf.toString();
    }





} // SourceFile
