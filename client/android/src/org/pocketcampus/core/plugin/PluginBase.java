package org.pocketcampus.core.plugin;

/**
 * Base class for the main plugin class, provides the info necessary for them to be handled by the core.
 * 
 * @status working
 * @author florian
 * @license
 *
 */

public abstract class PluginBase {
	// "human readable" name
	public abstract String getName();

	// unique ID
	public abstract Id getId();

	// version number
	public abstract VersionNumber getVersion();

	// intent used to display the plugin
	public abstract Class<? extends DisplayBase> getDisplayClass();

	// intent used to configure the plugin
	public abstract Class<? extends ConfigurationBase> getConfigurationClass();

	// icon
	public abstract Icon getIcon();
	
	// plugin descriptor
	public PluginDescriptor getPluginDescriptor() {
		return new PluginDescriptor(getName(), getId(), getVersion(), getIcon(), getClass(), getDisplayClass(), getConfigurationClass());
	}
}

































