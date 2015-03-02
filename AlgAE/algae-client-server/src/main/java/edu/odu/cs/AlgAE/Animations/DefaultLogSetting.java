/**
 * 
 */
package edu.odu.cs.AlgAE.Animations;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Default logging level (mainly for logging messages exchanged 
 * between the client and server.)
 * 
 * @author zeil
 *
 */
public class DefaultLogSetting {

	public static Level defaultLevel = Level.FINE; // INFO; 

	private static Logger globalLogger;
	
    /**
     * Set up a java logger: log to a file if this is being run as an application, to the console
     *    if run as an applet.
     *    
     * @param isAnApplet
     */
	public static void setupLogging(boolean isAnApplet, String logFileName) {
		if (globalLogger == null) {
			globalLogger = Logger.getLogger("");
			if (!isAnApplet) {
				try {
					FileHandler handler = new FileHandler(logFileName);
					handler.setFormatter(new SimpleFormatter());
					// Suppress logging to console
					Handler[] handlers = globalLogger.getHandlers();
				    if (handlers.length > 0 && handlers[0] instanceof ConsoleHandler) {
				      globalLogger.removeHandler(handlers[0]);
				    }
				    globalLogger.addHandler(handler);
				} catch (SecurityException e) {
					System.err.println("Unable to set up logger: " + e);
				} catch (IOException e) {
					System.err.println("Unable to set up logger: " + e);
				}
			} else {
				Handler[] handlers = globalLogger.getHandlers();
			    if (handlers.length == 0) {
			    	ConsoleHandler handler = new ConsoleHandler();
			    	//handler.setFormatter(new SimpleFormatter());
				    globalLogger.addHandler(handler);
			    }					
			}
		    globalLogger.setLevel(Level.INFO);
		    Logger myLoggers = Logger.getLogger("edu.odu.cs.AlgAE");
		    myLoggers.setLevel(defaultLevel);
		}
	}

}
