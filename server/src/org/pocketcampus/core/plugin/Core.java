package org.pocketcampus.core.plugin;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

import org.pocketcampus.core.provider.IProvider;

public class Core {
	/** Available plugins */
	private HashMap<String, IPlugin> pluginList_;
	
	/** Methods for the plugins  */
	private HashMap<String, HashMap<String, Method>> methodList_;

	/** Singleton instance */
	private static Core instance_;
	
	/**
	 * Private constructor.
	 */
	private Core() {
		methodList_ = new HashMap<String, HashMap<String, Method>>();
		pluginList_ = new HashMap<String, IPlugin>();
	}
	
	/** Access to the instance */
	public static Core getInstance() {
		if(instance_ == null) {
			instance_ = new Core();
		}
		
		return instance_;
	}

	/**
	 * Returns a list of instances of all plugins implementing a given interface.
	 * @param providerInterface
	 * @return
	 */
	public HashSet<IPlugin> getProvidersOf(Class<? extends IProvider> providerInterface) {
		
		// we want an interface
		if(!providerInterface.isInterface()) {
			return null;
		}
		
		HashSet<IPlugin> providers = new HashSet<IPlugin>();
		
		// find all appropriate plugins
		for(IPlugin plugin : pluginList_.values()) {
			if(providerInterface.isInstance(plugin)) {
				providers.add(plugin);
			}
		}
		
		return providers;
	}
	
	/**
	 * Plugin list getter.
	 * @return
	 */
	public HashMap<String, IPlugin> getPluginList() {
		return pluginList_;
	}
	
	/**
	 * Method list getter.
	 * @return
	 */
	public HashMap<String, HashMap<String, Method>> getMethodList() {
		return methodList_;
	}
}
