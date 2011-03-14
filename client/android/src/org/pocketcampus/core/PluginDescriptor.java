package org.pocketcampus.core;


public class PluginDescriptor {
	private String name_;
	private Id id_;
	private VersionNumber version_;
	private Icon icon_;
	private Class<? extends DisplayBase> displayClass_;
	private Class<? extends ConfigurationBase> configurationClass_;

	public PluginDescriptor(String name, Id id, VersionNumber version, Icon icon, Class<? extends DisplayBase> displayClass, Class<? extends ConfigurationBase> configurationClass) {
		name_ = name;
		id_ = id;
		version_ = version;
		icon_ = icon;
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
		text += "displayIntent:" + displayClass_ + " ";
		text += "configurationIntent:" + configurationClass_ + " ";
		text += ")";
			
		return text;
	}

	public Object getId() {
		return id_;
	}

	public Class<? extends DisplayBase> getDisplayClass() {
		return displayClass_;
	}
	
	public Class<? extends ConfigurationBase> getConfigurationClass() {
		return configurationClass_;
	}

	public String getName() {
		return name_;
	}
}
