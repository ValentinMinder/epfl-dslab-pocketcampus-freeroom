package org.pocketcampus.core.plugin;

import java.util.Vector;

import org.pocketcampus.plugin.bikes.BikesPlugin;
import org.pocketcampus.plugin.camipro.CamiproPlugin;
import org.pocketcampus.plugin.food.FoodPlugin;
import org.pocketcampus.plugin.map.MapPlugin;
import org.pocketcampus.plugin.news.NewsPlugin;
import org.pocketcampus.plugin.transport.TransportPlugin;

/**
 * Discovers which plugin are available.
 * 
 * @status incomplete but basic stuff working
 * @author florian
 * @license
 *
 */

public class PluginDiscoverer {
	static Vector<PluginBase> discoverPlugins(Core.ReleaseMode appMode) {
		if(appMode == Core.ReleaseMode.RELEASE) {
			return readPluginList();
			
		} else {
			return scanForPlugins();
		}
	}

	private static Vector<PluginBase> scanForPlugins() {
		Vector<PluginBase> plugins = new Vector<PluginBase>();
		
		// LIST OF REGISTERED PLUGINS //
		// TODO scan for available plugins automatically
//		plugins.add(new TestPlugin());
//		plugins.add(new DirectoryPlugin());
		plugins.add(new FoodPlugin());
		plugins.add(new TransportPlugin());
//		plugins.add(new SocialPlugin());
		plugins.add(new MapPlugin());
		plugins.add(new CamiproPlugin());
		plugins.add(new NewsPlugin());
//		plugins.add(new BikesPlugin());
		
		////////////////////////////////
		
		return plugins;
	}

	private static Vector<PluginBase> readPluginList() {
		Vector<PluginBase> plugins = new Vector<PluginBase>();
		// TODO read from XML config file
		return plugins;
	}
}
