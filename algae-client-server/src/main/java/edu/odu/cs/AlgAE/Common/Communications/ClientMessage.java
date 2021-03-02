/**
 *
 */
package edu.odu.cs.AlgAE.Common.Communications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;


/**
 * Messages sent to the client.
 *
 * Client messages have a highly variable and sometimes complex structure.
 *
 * @author zeil
 *
 */
public abstract class ClientMessage extends MessageBase {
    
    /**
     * A string used to indicate the end of a serialized message.
     */
    public static final String MessageTerminationMarker = "--------";
    
    /**
     * Message logger
     */
    private static Logger logger = Logger.getLogger(ClientMessage.class.getName());

    /**
     * Construct a new client message
     *
     * @param messageKind type of message
     */
    public ClientMessage(String messageKind) {
        super(messageKind);
    }
    
    
    public abstract boolean equals (Object clientMessage);
    
    /**
     * Converts the message to a string that can be shipped over a network
     * (typically from a remote animation server).
     *
     * This imposes some requirements on the subclasses of ClientMessage.
     *   - They must provide a parameter-free constructor (though it need not
     *        be a particularly useful one
     *   - All data required to reconstruct must be accessible via get/set function pairs
     *
     * @return XML encoding of the message
     */
    public String serialize()
    {
        Gson gson = new Gson();
        String className = getClass().getName();
        if (className.contains(".")) {
            className = className.substring(className.lastIndexOf('.')+1);
        }
        String json0 = gson.toJson(className);
        String json = gson.toJson(this);
        return json0 + "\n" + json + "\n" + MessageTerminationMarker + "\n";
    }
    
    /**
     * Rebuilds a message from XML as generated by toXML() or an equivalent
     * (typically from a remote animation server).
     *
     * This imposes some requirements on the subclasses of ClientMessage.
     *   - They must provide a parameter-free constructor (though it need not
     *        be a particularly useful one
     *   - All data required to reconstruct must be accessible via get/set function pairs
     *
     * @param serializedInput string containing the XML encoding of a client message
     * @return a client message
     */
    public static ClientMessage load(InputStream serializedInput)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(serializedInput));
        Class<ClientMessage> actualMessageClass = null;
        // Read forward until we get the name of a recognized message class within quotation marks.
        while (actualMessageClass == null) {
            String messageClass;
            try {
                messageClass = reader.readLine();
            } catch (IOException e1) {
                logger.severe("Unable to read message class: " + e1);
                return new ForceShutDownMessage("protocol failure");
            }
            if (messageClass == null) {
                return new ForceShutDownMessage("protocol failure");
            }
            if (messageClass.startsWith("\"") && messageClass.endsWith("\"")) {
                messageClass = messageClass.substring(1, messageClass.length()-1);
                messageClass = ClientMessage.class.getPackage().getName()
                        + "." + messageClass;
                try {
                    @SuppressWarnings("unchecked")
                    Class<ClientMessage> amClass = (Class<ClientMessage>)Class.forName(messageClass);
                    actualMessageClass = amClass;
                } catch (ClassNotFoundException e) {
                    actualMessageClass = null;
                }
            }
        }
        // Now accumulate the lines making up the message, terminated by a 
        // line containing only MessageTerminationMarker. 
        StringBuffer messageBuffer = new StringBuffer();
        String line = "";
        while (true) {
            try {
                line = reader.readLine();
            } catch (IOException e) {
                line = null;
            }
            if (line != null && !line.equals(MessageTerminationMarker)) {
                messageBuffer.append(line);
                messageBuffer.append("\n");
            } else {
                break;
            }
        }
        // Then parse a value of the message type.
        Gson gson = new Gson();
        try {
            JsonReader jreader = new JsonReader(new StringReader(messageBuffer.toString()));
            jreader.setLenient(true);
            ClientMessage message = gson.fromJson(jreader, actualMessageClass);
            return message;
        } catch (JsonSyntaxException e) {
            logger.severe("Unable to parse message, for type "
                    + actualMessageClass.getName() + ": " + e);
            return new ForceShutDownMessage("protocol failure");
        } catch (JsonIOException e) {
            logger.severe("Unable to parse message, for type "
                    + actualMessageClass.getName() + ": " + e);
            return new ForceShutDownMessage("protocol failure");
        }
    }
    
    
    //public abstract ClientMessage deserializex(String serializedInput);
    
    
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(this);
        return json;
    }
    
}