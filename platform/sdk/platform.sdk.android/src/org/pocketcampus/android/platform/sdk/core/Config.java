package org.pocketcampus.android.platform.sdk.core;

import android.util.Log;

/**
 * Contains various configuration settings.
 *  
 * @author Florian
 */
public class Config {
	/** Server IP. */
	public final static String SERVER_IP = "128.178.77.233";
	
	/** Server port. */
	public final static int SERVER_PORT = 9090;
	
	/** Level of information reported by the logger, a lower number mean more. */
	// XXX not used for now
	public final static int LOG_LEVEL = Log.VERBOSE;

	/** Time before giving up HTTP connection (ms). */
	public static final int HTTP_CONNECT_TIMEOUT = 20000;

	/** Time before giving up HTTP read operation (ms). */
	public static final int HTTP_READ_TIMEOUT = 20000;
}
