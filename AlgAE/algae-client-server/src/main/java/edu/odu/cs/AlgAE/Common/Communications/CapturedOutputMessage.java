/**
 *
 */
package edu.odu.cs.AlgAE.Common.Communications;


/**
 * Indicates that animated code has written some text to standard output
 *
 * @author zeil
 *
 */
public class CapturedOutputMessage extends ClientMessage {
    
    private String output;

    /**
     * @param outputString  text that was sent to standard output
     */
    public CapturedOutputMessage(String outputString) {
        super("CapturedOutput");
        this.output = outputString;
    }

    public CapturedOutputMessage() {
        super("CapturedOutput");
        this.output = "";
    }

    
    /**
     * @return the output string
     */
    public String getOutput() {
        return output;
    }

    /**
     * Set the output string
     */
    public void setOutput(String output) {
        this.output= output;
    }

    
    @Override
    public boolean equals(Object clientMessage) {
        if (clientMessage == null)
            return false;
        try {
            CapturedOutputMessage msg = (CapturedOutputMessage)clientMessage;
            return msg.output.equals(output);
        } catch (Exception e) {
            return false;
        }
    }

}
