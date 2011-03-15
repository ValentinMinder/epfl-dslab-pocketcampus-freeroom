package org.pocketcampus.core.plugin;


/**
 * Represents the description of a plugin.
 * 
 * @status working
 * @author florian
 * @license
 *
 */

public class PluginDescriptor {
	private String name_;
	private Id id_;
	private VersionNumber version_;
	private Icon icon_;
	private Class<? extends PluginBase> baseClass_;
	private Class<? extends DisplayBase> displayClass_;
	private Class<? extends ConfigurationBase> configurationClass_;

	public PluginDescriptor(String name, Id id, VersionNumber version, Icon icon, Class<? extends PluginBase> baseClass, Class<? extends DisplayBase> displayClass, Class<? extends ConfigurationBase> configurationClass) {
		name_ = name;
		id_ = id;
		version_ = version;
		icon_ = icon;
		baseClass_ = baseClass;
		displayClass_ = displayClass;
		configurationClass_ = configurationClass;
	}

	@Override
	public String toString() {
		String text;
		
		text  = "Plugin \"" + name_ + "\" (";
		text += "id:" + id_ + " ";
		text += "version:" + version_ + " ";
		text += "icon:" + icon_ + " ";
		text += "baseClass:" + baseClass_ + " ";
		text += "displayClass:" + displayClass_ + " ";
		text += "configurationClass:" + configurationClass_ + " ";
		text += ")";
			
		return text;
	}
	
	
	// ACCESSORS
	
	public String getName() {
		return name_;
	}
	
	public Object getId() {
		return id_;
	}

	public Class<? extends PluginBase> getBaseClass() {
		return baseClass_;
	}
	
	public Class<? extends DisplayBase> getDisplayClass() {
		return displayClass_;
	}
	
	public Class<? extends ConfigurationBase> getConfigurationClass() {
		return configurationClass_;
	}

	public Icon getIcon() {
		return icon_;
	}
}
