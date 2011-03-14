package org.pocketcampus.core;

import java.util.Vector;

import org.pocketcampus.plugin.authentication.AuthenticationPlugin;
import org.pocketcampus.plugin.logging.LoggingPlugin;
import org.pocketcampus.plugin.menu.MenuPlugin;
import org.pocketcampus.plugin.news.NewsPlugin;
import org.pocketcampus.plugin.positioning.PositioningPlugin;

public class PluginDiscoverer {
	static Vector<PluginDescriptor> discoverPlugins(Core.ReleaseMode appMode) {
		if(appMode == Core.ReleaseMode.RELEASE) {
			return readPluginList();
			
		} else {
			return scanForPlugins();
		}
	}

	private static Vector<PluginDescriptor> scanForPlugins() {
		Vector<PluginDescriptor> plugins = new Vector<PluginDescriptor>();
		
		// LIST OF REGISTERED PLUGINS //
		// TODO scan for available plugins
		plugins.add((new NewsPlugin()).getPluginDescriptor());
		plugins.add((new AuthenticationPlugin()).getPluginDescriptor());
		plugins.add((new PositioningPlugin()).getPluginDescriptor());
		plugins.add((new LoggingPlugin()).getPluginDescriptor());
		plugins.add((new MenuPlugin()).getPluginDescriptor());
		////////////////////////////////
		
		return plugins;
	}

	private static Vector<PluginDescriptor> readPluginList() {
		Vector<PluginDescriptor> plugins = new Vector<PluginDescriptor>();
		// TODO read from XML config file
		return plugins;
	}
}
