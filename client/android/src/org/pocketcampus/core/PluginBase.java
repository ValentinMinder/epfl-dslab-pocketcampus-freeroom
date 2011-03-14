package org.pocketcampus.core;


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
		return new PluginDescriptor(getName(), getId(), getVersion(), getIcon(), getDisplayClass(), getConfigurationClass());
	}
}

































