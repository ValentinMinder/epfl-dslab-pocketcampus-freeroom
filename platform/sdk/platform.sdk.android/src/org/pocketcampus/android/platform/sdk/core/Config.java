package org.pocketcampus.android.platform.sdk.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import android.os.Environment;
import android.util.Log;

/**
 * Contains various configuration settings.
 *  
 * @author Florian <florian.laurent@epfl.ch>
 */
public class Config {
	/** Server IP. */
	public static String SERVER_IP = "pocketcampus.epfl.ch";
	
	/** Server port. */
	public static int SERVER_PORT = 9090;
	
	/** Server proto. */
	public static String SERVER_PROTO = "http";
	
	/** Version of the Application */
	public static String SERVER_URI = "v3r1";
	
	/** Level of information reported by the logger, a lower number mean more. */
	// XXX not used for now
	public final static int LOG_LEVEL = Log.DEBUG;

	/** Time before giving up HTTP connection (ms). */
	public static final int HTTP_CONNECT_TIMEOUT = 5000;

	/** Time before giving up HTTP read operation (ms). */
	public static final int HTTP_READ_TIMEOUT = 60000;
	
	/** DEBUG or not */
	// XXX not used for now
	public static boolean DEBUG = false;
	
	/** Google Analytics Tracking Code */
	public static String GA_TRACKING_CODE = "UA-22135241-3"; // put dummy one when debugging
	
	static {
		try {
			String configFile = Environment.getExternalStorageDirectory() + "/pocketcampus.config";
			if(new File(configFile).exists()) {
				FileReader fr = new FileReader(configFile);
				BufferedReader br = new BufferedReader(fr);
				String line;
				while((line = br.readLine()) != null) {
					String[] param = line.trim().split("=");
					if(param.length == 2) {
						if("SERVER_IP".equals(param[0]))
							SERVER_IP = param[1];
						if("SERVER_PORT".equals(param[0]))
							SERVER_PORT = Integer.parseInt(param[1]);
						if("SERVER_URI".equals(param[0]))
							SERVER_URI = param[1];
						if("SERVER_PROTO".equals(param[0]))
							SERVER_PROTO = param[1];
					}
				}
			} else {
				/*FileWriter fw = new FileWriter(configFile, false);
				fw.write("SERVER_IP=" + SERVER_IP + "\n");
				fw.write("SERVER_PORT=" + SERVER_PORT + "\n");
				fw.write("USE_SSL=" + USE_SSL + "\n");
				fw.close();*/
			}
		} catch (Exception e) {
			Log.e("DEBUG", "grrrrrrrrr Exception while running static code!?!?");
			e.printStackTrace();
		}
	}
	
}
