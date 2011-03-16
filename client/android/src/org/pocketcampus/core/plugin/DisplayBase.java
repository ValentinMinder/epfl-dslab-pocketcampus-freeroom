package org.pocketcampus.core.plugin;

/**
 * Base class for the display of plugins. The Display class is the main interface of the plugins and will be started 
 * when the app is launched from the mainscreen.
 * 
 * @status incomplete
 * @author florian
 * @license
 *
 */

public interface DisplayBase {
	/**
	 * Reference to the base plugin, to access the current plugin's name id etc.
	 */
	public static PluginBase plugin_ = null;
	
	/**
	 * Provides a custom Request which will talk to the plugin servlet.
	 * @return
	 */
//	public Request newRequest() {
//		return new Request(plugin_.getId());
//	}
}
