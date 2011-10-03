package org.pocketcampus.android.platform.sdk.core;

import android.util.Log;

/**
 * Contains various configuration settings.
 *  
 * @author Florian
 */
public class Config {
	/** Server IP. */
//	public final static String SERVER_IP = "10.0.0.157";
	public final static String SERVER_IP = "128.178.254.53";
	
	/** Server port. */
	public final static int SERVER_PORT = 8080;
	
	/** Level of information reported by the logger, a lower number mean more. */
	// XXX not used for now
	public final static int LOG_LEVEL = Log.VERBOSE;
}
