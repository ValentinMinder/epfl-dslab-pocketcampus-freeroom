package org.pocketcampus.core.plugin;

import android.preference.PreferenceActivity;

/**
 * Base class for the Configuration class of the plugins.
 * Allows a plugin to display of configuration page to the user, that can be accessed through the menu
 * or directly though the plugin's page.
 * 
 * @status incomplete
 * @author florian
 * @license 
 * 
 */

public class ConfigurationBase extends PreferenceActivity {
	public static Class<? extends PluginBase> plugin_;
	
}
