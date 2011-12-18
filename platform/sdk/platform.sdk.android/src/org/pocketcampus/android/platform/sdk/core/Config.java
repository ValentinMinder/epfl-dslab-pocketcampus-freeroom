package org.pocketcampus.android.platform.sdk.core;

import android.util.Log;

/**
 * Contains various configuration settings.
 *  
 * @author Florian
 */
public class Config {
	/** Server IP. */
	// Mac Mini's IP, requires VPN
	public static String SERVER_IP = "128.178.77.236";
	
	// pocketcampus.epfl.ch's IP, doesn't require VPN
//	public final static String SERVER_IP = "128.178.132.3";

	// Dev server (Florian)
//	public static String SERVER_IP = "192.168.0.5";
	
	/** Server port. */
	public static int SERVER_PORT = 9090;
	
	/** Level of information reported by the logger, a lower number mean more. */
	// XXX not used for now
	public final static int LOG_LEVEL = Log.DEBUG;

	/** Time before giving up HTTP connection (ms). */
	public static final int HTTP_CONNECT_TIMEOUT = 20000;

	/** Time before giving up HTTP read operation (ms). */
	public static final int HTTP_READ_TIMEOUT = 20000;
	
	/** DEBUG or not */
	public static boolean DEBUG = true;
	
	/** Version of the Application */
	public static String VERSION = "v3r1";
	
//	static {
//		try {
//			String configFile = Environment.getExternalStorageDirectory() + "/pocketcampus.config";
//			if(new File(configFile).exists()) {
//				FileReader fr = new FileReader(configFile);
//				BufferedReader br = new BufferedReader(fr);
//				String line;
//				while((line = br.readLine()) != null) {
//					String[] param = line.trim().split("=");
//					if(param.length == 2) {
//						if("SERVER_IP".equals(param[0]))
//							SERVER_IP = param[1];
//						if("SERVER_PORT".equals(param[0]))
//							SERVER_PORT = Integer.parseInt(param[1]);
//					}
//				}
//			} else {
//				FileWriter fw = new FileWriter(configFile, false);
//				fw.write("SERVER_IP=" + SERVER_IP + "\n");
//				fw.write("SERVER_PORT=" + SERVER_PORT + "\n");
//				fw.close();
//			}
//		} catch (Exception e) {
//			Log.e("DEBUG", "grrrrrrrrr Exception while running static code!?!?");
//			e.printStackTrace();
//		}
//	}
	
}
