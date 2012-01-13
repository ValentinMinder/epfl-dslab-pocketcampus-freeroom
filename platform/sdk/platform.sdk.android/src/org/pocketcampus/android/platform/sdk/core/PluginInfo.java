package org.pocketcampus.android.platform.sdk.core;

import org.pocketcampus.android.platform.sdk.ui.Icon;

/**
 * Contains the information for a plugin.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 */
public class PluginInfo {
	private Icon mIcon;
	private String mLabel;
	
	private String mMainClassName;//TODO remove?
	private Class<? extends PluginView> mMainClass;
	
	private String mPreferencesClassName;//TODO remove?
	private Class<? extends PluginPreferenceActivity> mPreferenceClass;
	
	@SuppressWarnings("unchecked") //yes we DO check, stupid compiler
	public void setMainClassName(String name) throws ClassNotFoundException {
		Class<?> mainClass = Class.forName(name);
		
		if(PluginView.class.isAssignableFrom(mainClass)) {
			mMainClass = (Class<? extends PluginView>) mainClass;
			mMainClassName = name;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setPreferenceClassName(String name) throws ClassNotFoundException {
		Class<?> preferenceClass = Class.forName(name);
		
		if(PluginPreferenceActivity.class.isAssignableFrom(preferenceClass)) {
			mPreferenceClass = (Class<? extends PluginPreferenceActivity>) preferenceClass;
			mPreferencesClassName = name;
		}
	}
	
	public Class<? extends PluginView> getMainClass() {
		return mMainClass;
	}

	public Class<? extends PluginPreferenceActivity> getPreferenceClass() {
		return mPreferenceClass;
	}

	public Icon getIcon() {
		return mIcon;
	}
	
	public void setIcon(Icon icon) {
		this.mIcon = icon;
	}
	
	public String getLabel() {
		return mLabel;
	}
	
	public void setLabel(String label) {
		this.mLabel = label;
	}
	
	@Override
	public String toString() {
		return "PluginInfo[mIcon=" + mIcon + ", mLabel=" + mLabel
		+ ", mMainClassName=" + mMainClassName + ", mMainClass="
		+ mMainClass + ", mPreferencesClassName="
		+ mPreferencesClassName + ", mPreferenceClass="
		+ mPreferenceClass + "]";
	}
}
