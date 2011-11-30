package org.pocketcampus.server.plugin.takeoutreceiver;

import java.util.HashMap;
import java.util.Map;

public class DeviceTokenManager {
	private static final Map<Long, String> mapFromOrderIdToDeviceToken = new HashMap<Long, String>();

	public static void saveDeviceTokenForOrderId(String deviceToken,
			long orderID) {
		mapFromOrderIdToDeviceToken.put(orderID, deviceToken);
	}

	public static String getDeviceTokenForOrderID(long orderID) {
		return mapFromOrderIdToDeviceToken.get(orderID);
	}
	
	public static String formatDeviceToken(String rawToken) {
		String phoneToken = rawToken.replaceAll(" ", "");
		phoneToken = phoneToken.substring(1, phoneToken.length() - 1);
		return phoneToken;
	}
}
