package org.pocketcampus.plugin.pushnotif.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.thrift.TException;
import org.pocketcampus.platform.launcher.server.PocketCampusServer;
import org.pocketcampus.platform.sdk.shared.pushnotif.PushNotifMapRequest;
import org.pocketcampus.platform.sdk.shared.pushnotif.PushNotifSendRequest;
import org.pocketcampus.platform.sdk.shared.utils.ListUtils;
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
		// Chunk list into 50 users batches so that
		// (1) we limit the size of the query that gets generated in selectTokens
		// (2) we don't exceed the limit of GCM in sendToAndroidDevices
		List<List<String>> chunks = ListUtils.chunkList(req.getUserIds(), 50);
		boolean result = true;
		for(List<String> c : chunks)
			result = result && sendMessageInChunks(req.getPluginName(), c, req.getMessageMap());
		return result;
	}

	private Boolean sendMessageInChunks(String pluginName, List<String> userIds, Map<String, String> msg) {
		System.out.println("sendMessageInChunks");
		Map<String, String> androidTokens = dataStore.selectTokens(pluginName, userIds, "ANDROID");
		Map<String, String> iosTokens = dataStore.selectTokens(pluginName, userIds, "IOS");
		if(androidTokens == null || iosTokens == null)
			return false;
		Set<String> users = new HashSet<String>(userIds);
		users.removeAll(androidTokens.values());
		users.removeAll(iosTokens.values());
		if(users.size() > 0) // we don't have a token for these users, sorry
			PocketCampusServer.pushNotifNotifyFailedUsers(pluginName, new ArrayList<String>(users));
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
		System.out.println("deleteMapping");
		HttpServletRequest req = PocketCampusServer.getHttpRequest(dummy);
		if(req == null) return 500;
		String os = req.getHeader("X-PC-PUSHNOTIF-OS");
		String token = req.getHeader("X-PC-PUSHNOTIF-TOKEN");
		if(os == null || token == null) return 500;
		return (dataStore.deletePushToken(os, token) ? 200 : 500);
	}

}
