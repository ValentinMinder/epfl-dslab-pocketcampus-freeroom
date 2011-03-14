package org.pocketcampus.core.plugin;

import java.util.Vector;

import android.content.Context;
import android.content.Intent;

/**
 * Main Core class, creates the list of all plugins available and handles the launch of the new activities.
 * This class is a singleton.
 * 
 * @status incomplete but working
 * @author florian
 * @license 
 *
 */

public class Core {
	private static Core instance_ = null;
	private ReleaseMode applicationMode_;
	private Vector<PluginDescriptor> availablePlugins_;

	public enum ReleaseMode {
		DEVELOPMENT, RELEASE
	}
	
	public static Core getInstance() {
		if(instance_ == null) {
			return new Core();
		} else {
			return instance_;
		}
	}
	
	private Core() {
		applicationMode_ = ReleaseMode.DEVELOPMENT;
		availablePlugins_ = PluginDiscoverer.discoverPlugins(applicationMode_);
	}
	
	public void displayPlugin(Context ctx, PluginDescriptor plugin) {
		Intent intent = new Intent(ctx, plugin.getDisplayClass());
		startActivity(ctx, intent);
	} 
	
	public void configurePlugin(Context ctx, PluginDescriptor plugin) {
		Intent intent = new Intent(ctx, plugin.getConfigurationClass());
		startActivity(ctx, intent);
	}
	
	private void startActivity(Context ctx, Intent intent) {
		// TODO handles displayMode s
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}
	
	public Vector<PluginDescriptor> getAvailablePlugins() {
		return availablePlugins_;
	}
}
