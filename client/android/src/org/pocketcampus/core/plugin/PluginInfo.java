package org.pocketcampus.core.plugin;


/**
 * Base class for the main plugin class, provides the info necessary for them to be handled by the core.
 * 
 * @status working
 * @author florian
 * @license
 *
 */

public abstract class PluginInfo {

	/**
	 * Plugin's name, using the resources
	 * @return
	 */
	public abstract int getNameResource();

	/**
	 * Plugin unique ID.
	 * @return
	 */
	public abstract Id getId();

	/**
	 * Plugin version number.
	 * @return
	 */
	public abstract VersionNumber getVersion();

	/**
	 * Plugin icon.
	 * @return
	 */
	public abstract Icon getIcon();
	
	/**
	 * Plugin smaller icon.
	 * @return
	 */
	public abstract Icon getMiniIcon();
	
	/**
	 * Had this plugin to the mainscreen menu?
	 * @return
	 */
	public abstract boolean hasMenuIcon();
	
//	@Override
//	public String toString() {
//		String text;
//		text  = "Plugin \"" + getName() + "\" (";
//		text += "menu icon:" + hasMenuIcon() + " ";
//		text += "id:" + getId() + " ";
//		text += "version:" + getVersion() + " ";
//		text += "icon:" + getIcon() + " ";
//		text += ")";
//			
//		return text;
//	}
	
}

































