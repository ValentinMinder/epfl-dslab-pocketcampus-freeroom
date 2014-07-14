package org.pocketcampus.platform.server.launcher;

import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.apache.thrift.TProcessor;
import org.pocketcampus.platform.server.RawPlugin;
import org.pocketcampus.platform.shared.PCConfig;

public class PocketCampusServer extends ServerBase {
	public static final PCConfig CONFIG = new PCConfig();
	
	private static final Map<String, Object> plugins = new HashMap<String, Object>();

	
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

	/** Gets the request headers of the current request. */
	public static Map<String, String> getRequestHeaders() {
		return TrackingThriftServlet.receivedRequestHeaders.get();
	}
	
	
	/** Gets the available services. */
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

	
	/** Gets an implementation of the service for the plugin with the specified name. */
	private Object getPluginService(final String pluginName) {
		String serviceName = "org.pocketcampus.plugin." + pluginName.toLowerCase() + ".server." + pluginName + "ServiceImpl";

		try {
			return Class.forName(serviceName).getConstructor().newInstance();
		} catch (Exception _) {
			return null;
		}
	}

	/** Gets a Thrift processor for the plugin with the specified service and name. */
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

	/** Gets a raw processor, if any, for the plugin with the specified service. */
	private HttpServlet getRawProcessor(final Object pluginService) {
		return pluginService instanceof RawPlugin ? ((RawPlugin) pluginService).getServlet() : null;
	}
}