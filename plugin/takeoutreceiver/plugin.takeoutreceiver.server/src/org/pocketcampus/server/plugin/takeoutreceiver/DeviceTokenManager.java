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
}
