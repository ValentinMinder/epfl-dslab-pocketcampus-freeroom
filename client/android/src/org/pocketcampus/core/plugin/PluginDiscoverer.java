package org.pocketcampus.core.plugin;

import java.util.Vector;

import org.pocketcampus.plugin.authentication.AuthenticationPlugin;
import org.pocketcampus.plugin.food.FoodPlugin;
import org.pocketcampus.plugin.logging.LoggingPlugin;
import org.pocketcampus.plugin.news.NewsPlugin;
import org.pocketcampus.plugin.positioning.PositioningPlugin;

/**
 * Discovers which plugin are available.
 * 
 * @status incomplete but basic stuff working
 * @author florian
 * @license
 *
 */

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
		// TODO scan for available plugins automatically
		plugins.add((new NewsPlugin()).getPluginDescriptor());
		plugins.add((new AuthenticationPlugin()).getPluginDescriptor());
		plugins.add((new PositioningPlugin()).getPluginDescriptor());
		plugins.add((new LoggingPlugin()).getPluginDescriptor());
		plugins.add((new FoodPlugin()).getPluginDescriptor());
		////////////////////////////////
		
		return plugins;
	}

	private static Vector<PluginDescriptor> readPluginList() {
		Vector<PluginDescriptor> plugins = new Vector<PluginDescriptor>();
		// TODO read from XML config file
		return plugins;
	}
}
