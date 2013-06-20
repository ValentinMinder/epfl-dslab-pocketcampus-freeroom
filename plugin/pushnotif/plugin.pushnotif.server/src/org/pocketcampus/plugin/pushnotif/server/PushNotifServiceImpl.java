package org.pocketcampus.plugin.pushnotif.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.thrift.TException;
import org.pocketcampus.platform.launcher.server.PocketCampusServer;
import org.pocketcampus.platform.sdk.shared.pushnotif.PushNotifMapRequest;
import org.pocketcampus.platform.sdk.shared.pushnotif.PushNotifSendRequest;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifService;

/**
 * PushNotifServiceImpl
 * 
 * The implementation of the server side of the PushNotif Plugin.
 * 
 * It routes the push notifs of other plugins to the user.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class PushNotifServiceImpl implements PushNotifService.Iface {

	private PushNotifDataStore dataStore;

	public PushNotifServiceImpl() {
		System.out.println("Starting PushNotif plugin server ...");
		dataStore = new PushNotifDataStore();
	}

	public Boolean addMapping(PushNotifMapRequest req) {
		System.out.println("addMapping");
		return dataStore.insertMapping(req.getPluginName(), req.getUserId(), req.getDeviceOs(), req.getPushToken());
	}

	public Boolean sendMessage(PushNotifSendRequest req) {
		System.out.println("sendMessage");
		List<String> androidTokens = dataStore.selectTokens(req.getPluginName(), req.getUserIds(), "ANDROID");
		List<String> iosTokens = dataStore.selectTokens(req.getPluginName(), req.getUserIds(), "IOS");
		if(androidTokens == null || iosTokens == null)
			return false;
		Map<String, String> pluginMessage = new HashMap<String, String>(req.getMessageMap());
		// Override pluginName 
		pluginMessage.put("pluginName", req.getPluginName());
		PushNotifMsgSender.sendToAndroidDevices(dataStore, new HashSet<String>(androidTokens), pluginMessage);
		PushNotifMsgSender.sendToiOSDevices(dataStore, new HashSet<String>(iosTokens), pluginMessage);
		return true;
	}

	@Override
	public int deleteMapping(String dummy) throws TException {
		System.out.println("deleteMapping");
		HttpServletRequest req = PocketCampusServer.getHttpRequest(dummy);
		if(req == null) return 500;
		String os = req.getHeader("X-PC-PUSHNOTIF-OS");
		String token = req.getHeader("X-PC-PUSHNOTIF-TOKEN");
		if(os == null || token == null) return 500;
		return (dataStore.deletePushToken(os, token) ? 200 : 500);
	}

}
