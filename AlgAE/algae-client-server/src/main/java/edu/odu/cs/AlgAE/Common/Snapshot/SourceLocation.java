package edu.odu.cs.AlgAE.Common.Snapshot;

public class SourceLocation {

    private String fileName;
    private int lineNumber;
    
    public SourceLocation (String filename, int line) {
        fileName = filename;
        setLineNumber(line);
    }
    
    public SourceLocation () {
        fileName = "";
        setLineNumber(1);
    }
    
    
    public String toString() {
        return fileName + ":" + getLineNumber();
    }
    
    public boolean equals (Object obj) {
        SourceLocation other = (SourceLocation)obj;
        if (other == null)
            return false;
        else if (getLineNumber() != other.getLineNumber())
            return false;
        else if (fileName == null || other.fileName == null)
            return false;
        else return fileName.equals(other.fileName);
    }


    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }


    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    /**
     * @return the lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }


    /**
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }


}
