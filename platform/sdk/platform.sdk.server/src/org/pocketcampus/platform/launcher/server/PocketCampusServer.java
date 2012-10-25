package org.pocketcampus.platform.launcher.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.thrift.TProcessor;

public class PocketCampusServer extends ServerBase {

	private static HashMap<String, Object> pluginsImpl = new HashMap<String, Object>(); 
	
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

	public static Object invokeOnPlugin(String pluginName, String methodName, Object arg) throws NoSuchObjectException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if(!pluginsImpl.containsKey(pluginName.toLowerCase())) {
			throw new NoSuchObjectException("Plugin not found: " + pluginName);
		}
		Object obj = pluginsImpl.get(pluginName.toLowerCase());
		Method m = obj.getClass().getMethod(methodName, arg.getClass());
		return m.invoke(obj, arg);
	}
	
}
