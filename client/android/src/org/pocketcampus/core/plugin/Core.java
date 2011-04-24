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
	private static ReleaseMode applicationMode_;
	private Vector<PluginBase> availablePlugins_;

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
	
	public void displayPlugin(Context ctx, PluginBase plugin) {
		Intent intent = new Intent(ctx, plugin.getClass());
		startActivity(ctx, intent);
	}
	
	public static void startPluginWithID(Context ctx, PluginBase plugin, int id) {
		Intent intent = new Intent(ctx, plugin.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("id", id);
		ctx.startActivity(intent);
	}
	
	public void configurePlugin(Context ctx, PluginBase plugin) {
		Intent intent = new Intent(ctx, plugin.getPluginPreference().getClass());
		startActivity(ctx, intent);
	}
	
	private void startActivity(Context ctx, Intent intent) {
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}
	
	public Vector<PluginBase> getAvailablePlugins() {
		return availablePlugins_;
	}

	public String getServerUrl() {
		// Change this to your local IP.
		return "http://128.178.252.49:8080/";
	}
	
	public static ReleaseMode getApplicationMode() {
		return applicationMode_;
	}
}
