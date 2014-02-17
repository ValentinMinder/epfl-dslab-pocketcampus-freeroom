package org.pocketcampus.platform.launcher.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServlet;
import org.pocketcampus.platform.sdk.shared.utils.PcConstants;

import ch.epfl.tequila.client.model.ClientConfig;
import ch.epfl.tequila.client.model.TequilaPrincipal;
import ch.epfl.tequila.client.service.TequilaService;

public class PocketCampusServer extends ServerBase {

	private static Map<String, Object> pluginsImpl = new HashMap<String, Object>(); 
	
	@Override
	protected ArrayList<Processor> getServiceProcessors() {
		ArrayList<Processor> processors = new ArrayList<Processor>();
		for(String plugin : PC_SRV_CONFIG.getString("ENABLED_PLUGINS").split(",")) {
			boolean skipped = true;
			String srvr_pref = "org.pocketcampus.plugin." + plugin.toLowerCase() + ".server.";
			String shrd_pref = "org.pocketcampus.plugin." + plugin.toLowerCase() + ".shared.";
			try {
				Class cls_impl = Class.forName(srvr_pref + plugin + "ServiceImpl");
				Class cls_srvc = Class.forName(shrd_pref + plugin + "Service$Processor");
				Class cls_ifce = Class.forName(shrd_pref + plugin + "Service$Iface");
				Constructor con_impl = cls_impl.getConstructor();
				Object obj_impl = con_impl.newInstance();
				Constructor con_srvc = cls_srvc.getConstructor(cls_ifce);
				Object obj_srvc = con_srvc.newInstance(obj_impl);
				Processor proc = new Processor((TProcessor) obj_srvc, plugin.toLowerCase());
				if(obj_impl instanceof RawPlugin)
					proc.setRawProcessor(((RawPlugin) obj_impl).getServlet());
				processors.add(proc);
				pluginsImpl.put(plugin.toLowerCase(), obj_impl);
				skipped = false;
			} catch (ClassNotFoundException e) {
			} catch (SecurityException e) {
			} catch (NoSuchMethodException e) {
			} catch (IllegalArgumentException e) {
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
			if(skipped) {
				System.out.println("Not found: " + plugin + " plugin, skipping ...");
			} else {
				System.out.println("Started: " + plugin + " plugin.");
			}
		}
		return processors;
	}

	/***
	 * STATIC FUNCTIONS
	 */
	
	public static Object invokeOnPlugin(String pluginName, String methodName, Object arg) throws NoSuchObjectException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if(!pluginsImpl.containsKey(pluginName.toLowerCase())) {
			throw new NoSuchObjectException("Plugin not found: " + pluginName);
		}
		Object obj = pluginsImpl.get(pluginName.toLowerCase());
		Method m = obj.getClass().getMethod(methodName, arg.getClass());
		return m.invoke(obj, arg);
	}
	
	public static String getClientIp(Object firstArg) {
		HttpServletRequest req = (HttpServletRequest) TServlet.requestsMap.get(firstArg);
		if(req == null) return null;
		return req.getRemoteAddr();
	}

	public static String getServerIp(Object firstArg) {
		HttpServletRequest req = (HttpServletRequest) TServlet.requestsMap.get(firstArg);
		if(req == null) return null;
		return req.getLocalAddr();
	}

	public static HttpServletRequest getHttpRequest(Object firstArg) {
		System.out.println("requestsMap has " + TServlet.requestsMap.size() + " items");
		return (HttpServletRequest) TServlet.requestsMap.get(firstArg);
	}
	
	/*****
	 * PUSH NOTIF CRAP 
	 * @author amer
	 *
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

	public static boolean pushNotifMap(Object firstArg, String plugin, String userId) {
		HttpServletRequest req = (HttpServletRequest) TServlet.requestsMap.get(firstArg);
		if(req == null) return false;
		String os = req.getHeader(PcConstants.HTTP_HEADER_PUSHNOTIF_OS);
		String token = req.getHeader(PcConstants.HTTP_HEADER_PUSHNOTIF_TOKEN);
		if(os == null || token == null || plugin == null || userId == null) return false;
		try {
			return (Boolean) invokeOnPlugin("pushnotif", "addMapping", new PushNotifMapReq(plugin, userId, os, token));
		} catch (NoSuchObjectException e) {
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		return false;
	}
	
	public static boolean pushNotifSend(String plugin, List<String> userIds, Map<String, String> msg) {
		if(msg == null || plugin == null || userIds == null) return false;
		try {
			return (Boolean) invokeOnPlugin("pushnotif", "sendMessage", new PushNotifSendReq(plugin, userIds, msg));
		} catch (NoSuchObjectException e) {
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		return false;
	}
	
	public static boolean pushNotifNotifyFailedUsers(String plugin, List<String> failedUsers) {
		if(plugin == null || failedUsers == null)
			return false;
		try {
			PocketCampusServer.invokeOnPlugin(plugin, "appendToFailedDevicesList", failedUsers);
			return true;
		} catch (NoSuchObjectException e) {
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		return false;
	}
	
	/*****
	 * TEQUILA AUTHENTICATION CRAP 
	 * @author amer
	 *
	 */
	
	public static String authGetTequilaToken(String plugin) {

		ClientConfig config = new ClientConfig();
		config.setHost("tequila.epfl.ch");
		//config.setOrg("PocketCampusOrg");
		config.setService(plugin + "@pocketcampus");
		config.setRequest("name firstname email title unit office phone username uniqueid unixid groupid where categorie");
		config.setAllows("categorie=epfl-guests");
		//config.setAuthstrength("2");

		try {
			return TequilaService.instance().createRequest(config, "pocketcampus://" + plugin + ".plugin.pocketcampus.org/authenticated");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	public static TequilaPrincipal authGetTequilaPrincipal(String token) throws SecurityException {

		ClientConfig config = new ClientConfig();
		config.setHost("tequila.epfl.ch");

		try {
			return TequilaService.instance().validateKey(config, token);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public static String authGetUserGaspar(Object firstArg) {
		HttpServletRequest req = (HttpServletRequest) TServlet.requestsMap.get(firstArg);
		if(req == null) return null;
		String pcSessionId = req.getHeader(PcConstants.HTTP_HEADER_AUTH_PCSESSID);
		if(pcSessionId == null) 
			return null;
		try {
			return (String) invokeOnPlugin("authentication", "getGasparFromSession", pcSessionId);
		} catch (NoSuchObjectException e) {
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		return null;
	}
	
	public static String authGetUserSciper(Object firstArg) {
		HttpServletRequest req = (HttpServletRequest) TServlet.requestsMap.get(firstArg);
		if(req == null) return null;
		String pcSessionId = req.getHeader(PcConstants.HTTP_HEADER_AUTH_PCSESSID);
		if(pcSessionId == null) 
			return null;
		try {
			return (String) invokeOnPlugin("authentication", "getSciperFromSession", pcSessionId);
		} catch (NoSuchObjectException e) {
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		return null;
	}
	
}
