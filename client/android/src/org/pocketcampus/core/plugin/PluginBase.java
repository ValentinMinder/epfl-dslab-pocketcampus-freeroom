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

	/**
	 * Setups plugin
	 */
	public PluginBase() {
		try {
			if(getDisplayClass() != null) {
				getDisplayClass().getField("plugin_").set(null, this);
			}

			if(getConfigurationClass() != null) {
				getConfigurationClass().getField("plugin_").set(null, this);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * "human readable" name
	 */
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
	protected PluginDescriptor getPluginDescriptor() {
		return new PluginDescriptor(getName(), getId(), getVersion(), getIcon(), getClass(), getDisplayClass(), getConfigurationClass());
	}
}

































