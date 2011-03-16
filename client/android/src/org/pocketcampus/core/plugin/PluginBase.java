package org.pocketcampus.core.plugin;

import org.pocketcampus.core.communication.RequestFactory;

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
	 * Default constructor which setups plugin.
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
	
	
	//-------------------------------------------------------
	// Gives access to the Request classes.
	//-------------------------------------------------------
	
	public RequestFactory getRequestFactory() {
		return new RequestFactory(getId());
	}
	
	
	
	//-------------------------------------------------------
	// Abstract methods to get informations about the plugin.
	//-------------------------------------------------------
	
	/**
	 * Creates a plugin descriptor for this plugin from all the abstract classes.
	 */
	protected PluginDescriptor getPluginDescriptor() {
		return new PluginDescriptor(getName(), getId(), getVersion(), getIcon(), getClass(), getDisplayClass(), getConfigurationClass());
	}
	
	/**
	 * "human readable" name
	 */
	public abstract String getName();

	/**
	 * unique ID
	 * @return
	 */
	public abstract Id getId();

	/**
	 * version number
	 * @return
	 */
	public abstract VersionNumber getVersion();

	/**
	 * intent used to display the plugin
	 * @return
	 */
	public abstract Class<? extends DisplayBase> getDisplayClass();

	/**
	 * intent used to configure the plugin
	 * @return
	 */
	public abstract Class<? extends ConfigurationBase> getConfigurationClass();

	/**
	 * icon
	 * @return
	 */
	public abstract Icon getIcon();

	/**
	 * plugin descriptor 
	 * @return
	 */
}

































