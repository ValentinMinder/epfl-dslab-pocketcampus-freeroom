package org.pocketcampus.plugin.pushnotif.server;

import org.apache.thrift.TException;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.platform.shared.PCConstants;
import org.pocketcampus.platform.shared.utils.ListUtils;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifService;

import java.util.*;

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
		dataStore = new PushNotifDataStore();
	}

	public Boolean addMapping(PushNotifMapReq req) {
		System.out.println("Pushnotif: addMapping");
		return dataStore.insertMapping(req.pluginName, req.userId, req.deviceOs, req.pushToken);
	}

	public Boolean sendMessage(PushNotifSendReq req) {
		System.out.println("Pushnotif: sendMessage");
		// Chunk list into 50 users batches so that
		// (1) we limit the size of the query that gets generated in selectTokens
		// (2) we don't exceed the limit of GCM in sendToAndroidDevices
		List<List<String>> chunks = ListUtils.chunkList(req.userIds, 50);
		boolean result = true;
		for(List<String> c : chunks)
			result = result && sendMessageInChunks(req.pluginName, c, req.messageMap);
		return result;
	}

	private Boolean sendMessageInChunks(String pluginName, List<String> userIds, Map<String, String> msg) {
		System.out.println("Pushnotif: sendMessageInChunks");
		Map<String, String> androidTokens = dataStore.selectTokens(pluginName, userIds, "ANDROID");
		Map<String, String> iosTokens = dataStore.selectTokens(pluginName, userIds, "IOS");
		if(androidTokens == null || iosTokens == null)
			return false;
		Set<String> users = new HashSet<String>(userIds);
		users.removeAll(androidTokens.values());
		users.removeAll(iosTokens.values());
		if(users.size() > 0) // we don't have a token for these users, sorry
			pushNotifNotifyFailedUsers(pluginName, new ArrayList<String>(users));
		Map<String, String> pluginMessage = new HashMap<String, String>(msg);
		// Override pluginName 
		pluginMessage.put("pluginName", pluginName);
		// LOGIC: we say that a user failed if we were not able to deliver a push message to any of his devices
		// TODO there's a little bug here: if a user has mixed devices, then at least 1 android and 1 ios have to succeed, otherwise we claim that the user failed 
		PushNotifMsgSender.sendToAndroidDevices(dataStore, androidTokens, pluginMessage);
		PushNotifMsgSender.sendToiOSDevices(dataStore, iosTokens, pluginMessage);
		return true;
	}

	@Override
	public int deleteMapping(String dummy) throws TException {
		Map<String, String> headers = PocketCampusServer.getRequestHeaders();
		if(headers == null) return 500;
		String os = headers.get("X-PC-PUSHNOTIF-OS");
		String token = headers.get("X-PC-PUSHNOTIF-TOKEN");
		if(os == null || token == null) return 500;
		return (dataStore.deletePushToken(os, token) ? 200 : 500);
	}


	public static class PushNotifMapReq {
		public PushNotifMapReq(String pluginName, String userId, String deviceOs, String pushToken) {
			this.pluginName = pluginName;
			this.userId = userId;
			this.deviceOs = deviceOs;
			this.pushToken = pushToken;
		}

		public String pluginName;
		public String userId;
		public String deviceOs;
		public String pushToken;
	}

	public static class PushNotifSendReq {
		public PushNotifSendReq(String pluginName, List<String> userIds, Map<String, String> messageMap) {
			this.pluginName = pluginName;
			this.userIds = userIds;
			this.messageMap = messageMap;
		}

		public String pluginName;
		public List<String> userIds;
		public Map<String, String> messageMap;
	}

	public static boolean pushNotifMap(String plugin, String userId) {
		Map<String, String> headers = PocketCampusServer.getRequestHeaders();
		if (headers == null)
			return false;
		String os = headers.get(PCConstants.HTTP_HEADER_PUSHNOTIF_OS);
		String token = headers.get(PCConstants.HTTP_HEADER_PUSHNOTIF_TOKEN);
		if (os == null || token == null || plugin == null || userId == null)
			return false;
		try {
			return (Boolean) PocketCampusServer.invokeOnPlugin("pushnotif", "addMapping", new PushNotifMapReq(plugin, userId, os, token));
		} catch (Exception e) {
		} 
		return false;
	}

	public static boolean pushNotifSend(String plugin, List<String> userIds, Map<String, String> msg) {
		if (msg == null || plugin == null || userIds == null)
			return false;
		try {
			return (Boolean) PocketCampusServer.invokeOnPlugin("pushnotif", "sendMessage", new PushNotifSendReq(plugin, userIds, msg));
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean pushNotifNotifyFailedUsers(String plugin, List<String> failedUsers) {
		if (plugin == null || failedUsers == null)
			return false;
		try {
			PocketCampusServer.invokeOnPlugin(plugin, "appendToFailedDevicesList", failedUsers);
			return true;
		} catch (Exception e) {
		}
		return false;
	}
}
