package org.pocketcampus.platform.server.launcher;

import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.thrift.TProcessor;
import org.pocketcampus.platform.server.RawPlugin;
import org.pocketcampus.platform.shared.PCConfig;
import org.pocketcampus.platform.shared.PCConstants;

import ch.epfl.tequila.client.model.ClientConfig;
import ch.epfl.tequila.client.model.TequilaPrincipal;
import ch.epfl.tequila.client.service.TequilaService;

public class PocketCampusServer extends ServerBase {
	public static final PCConfig CONFIG = new PCConfig();

	private static final Map<String, Object> plugins = new HashMap<String, Object>();

	@Override
	protected List<ServiceInfo> getServices() {
		ArrayList<ServiceInfo> processors = new ArrayList<ServiceInfo>();

		for (String pluginName : CONFIG.getString("ENABLED_PLUGINS").split(",")) {
			Object pluginService = getPluginService(pluginName);
			TProcessor thriftProcessor = getThriftProcessor(pluginService, pluginName);
			HttpServlet rawProcessor = getRawProcessor(pluginService);

			if (thriftProcessor == null) {
				System.out.println(pluginName + " plugin not found!");
			} else {
				processors.add(new ServiceInfo(pluginName.toLowerCase(), thriftProcessor, rawProcessor));
				plugins.put(pluginName.toLowerCase(), pluginService);
				System.out.println(pluginName + " plugin started.");
			}
		}
		return processors;
	}

	private Object getPluginService(final String pluginName) {
		String serviceName = "org.pocketcampus.plugin." + pluginName.toLowerCase() + ".server." + pluginName + "ServiceImpl";

		try {
			return Class.forName(serviceName).getConstructor().newInstance();
		} catch (Exception _) {
			return null;
		}
	}

	private TProcessor getThriftProcessor(final Object pluginService, final String pluginName) {
		if (pluginService == null) {
			return null;
		}

		final String sharedPrefix = "org.pocketcampus.plugin." + pluginName.toLowerCase() + ".shared." + pluginName;
		final String interfaceName = sharedPrefix + "Service$Iface";
		final String serviceProcessorName = sharedPrefix + "Service$Processor";

		try {
			final Class<?> interfaceClass = Class.forName(interfaceName);

			return (TProcessor) Class.forName(serviceProcessorName)
					.getConstructor(interfaceClass)
					.newInstance(pluginService);
		} catch (Exception _) {
			return null;
		}
	}

	private HttpServlet getRawProcessor(final Object pluginService) {
		return pluginService instanceof RawPlugin ? ((RawPlugin) pluginService).getServlet() : null;
	}

	
	/** Basic Messenger-like functionality. */
	public static Object invokeOnPlugin(String pluginName, String methodName, Object arg) 
			throws NoSuchObjectException, NoSuchMethodException {
		if (!plugins.containsKey(pluginName.toLowerCase())) {
			throw new NoSuchObjectException("Plugin not found: " + pluginName);
		}
		
		try {
			Object plugin = plugins.get(pluginName.toLowerCase());
			return plugin.getClass().getMethod(methodName, arg.getClass()).invoke(plugin, arg);
		} catch (Exception _) {
			throw new NoSuchMethodException("Method not found: " + methodName);
		}
	}

	
	/**
	 * PUSH NOTIF CRAP
	 * 
	 * @author Amer
	 */

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
		Map<String, String> headers = getRequestHeaders();
		if (headers == null)
			return false;
		String os = headers.get(PCConstants.HTTP_HEADER_PUSHNOTIF_OS);
		String token = headers.get(PCConstants.HTTP_HEADER_PUSHNOTIF_TOKEN);
		if (os == null || token == null || plugin == null || userId == null)
			return false;
		try {
			return (Boolean) invokeOnPlugin("pushnotif", "addMapping", new PushNotifMapReq(plugin, userId, os, token));
		} catch (Exception e) {
		} 
		return false;
	}

	public static boolean pushNotifSend(String plugin, List<String> userIds, Map<String, String> msg) {
		if (msg == null || plugin == null || userIds == null)
			return false;
		try {
			return (Boolean) invokeOnPlugin("pushnotif", "sendMessage", new PushNotifSendReq(plugin, userIds, msg));
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

	/**
	 * TEQUILA AUTHENTICATION CRAP
	 * 
	 * @author Amer
	 */

	public static String authGetTequilaToken(String plugin) {

		ClientConfig config = new ClientConfig();
		config.setHost("tequila.epfl.ch");
		// config.setOrg("PocketCampusOrg");
		config.setService(plugin + "@pocketcampus");
		config.setRequest("name firstname email title unit office phone username uniqueid unixid groupid where categorie");
		config.setAllows("categorie=epfl-guests");
		config.setAllows("categorie=Shibboleth");
		// config.setAuthstrength("2");

		try {
			return TequilaService.instance().createRequest(config, "pocketcampus://" + plugin + ".plugin.pocketcampus.org/authenticated");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static TequilaPrincipal authGetTequilaPrincipal(String token) {
		ClientConfig config = new ClientConfig();
		config.setHost("tequila.epfl.ch");

		try {
			return TequilaService.instance().validateKey(config, token);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String authGetUserGaspar() {
		return callOnAuthPlugin("getGasparFromSession");
	}

	public static String authGetUserGasparFromReq(HttpServletRequest request) {
		String pcSessionId = request.getHeader(PCConstants.HTTP_HEADER_AUTH_PCSESSID);
		try {
			return (String) invokeOnPlugin("authentication", "getGasparFromSession", pcSessionId);
		} catch (Exception e) {
		}
		return null;
	}

	public static String authGetUserSciper() {
		return callOnAuthPlugin("getSciperFromSession");
	}

	public static Map<String, String> getRequestHeaders() {
		return TrackingThriftServlet.receivedRequestHeaders.get();
	}

	public static List<String> authGetUserAttributes(List<String> attr) {
		String pcSessionId = getRequestHeaders().get(PCConstants.HTTP_HEADER_AUTH_PCSESSID);
		if (pcSessionId == null)
			return null;
		try {
			AuthUserDetailsReq dReq = new AuthUserDetailsReq(pcSessionId, attr);
			AuthUserDetailsResp dResp = (AuthUserDetailsResp) invokeOnPlugin("authentication", "getUserFieldsFromSession", dReq);
			return dResp.fieldValues;
		} catch (Exception e) {
		}
		return null;
	}

	private static String callOnAuthPlugin(String func) {
		String pcSessionId = getRequestHeaders().get(PCConstants.HTTP_HEADER_AUTH_PCSESSID);
		if (pcSessionId == null)
			return null;
		try {
			return (String) invokeOnPlugin("authentication", func, pcSessionId);
		} catch (Exception e) {
		}
		return null;
	}

	public static class AuthUserDetailsReq {
		public String sessionId;
		public List<String> requestedFields;

		public AuthUserDetailsReq(String sessionId, List<String> requestedFields) {
			this.sessionId = sessionId;
			this.requestedFields = requestedFields;
		}
	}

	public static class AuthUserDetailsResp {
		public List<String> fieldValues;

		public AuthUserDetailsResp(List<String> fieldValues) {
			this.fieldValues = fieldValues;
		}
	}
}