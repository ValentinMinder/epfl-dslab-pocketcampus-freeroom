package org.pocketcampus.platform.launcher.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

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
import org.pocketcampus.platform.sdk.shared.pushnotif.PushNotifMapRequest;
import org.pocketcampus.platform.sdk.shared.pushnotif.PushNotifSendRequest;

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
				processors.add(new Processor((TProcessor) obj_srvc, plugin.toLowerCase()));
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
	
	public static boolean pushNotifMap(Object firstArg, String plugin, String userId) {
		HttpServletRequest req = (HttpServletRequest) TServlet.requestsMap.get(firstArg);
		if(req == null) return false;
		String os = req.getHeader("X-PC-PUSHNOTIF-OS");
		String token = req.getHeader("X-PC-PUSHNOTIF-TOKEN");
		if(os == null || token == null || plugin == null || userId == null) return false;
		try {
			return (Boolean) invokeOnPlugin("pushnotif", "addMapping", new PushNotifMapRequest(plugin, userId, os, token));
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
			return (Boolean) invokeOnPlugin("pushnotif", "sendMessage", new PushNotifSendRequest(plugin, userIds, msg));
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

}
