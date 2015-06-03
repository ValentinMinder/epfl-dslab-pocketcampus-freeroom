package org.pocketcampus.platform.android.core;

import android.graphics.drawable.Drawable;

/**
 * Information about a plugin.
 * 
 * @author Amer C <amer.chamseddine@epfl.ch>
 */
public class PluginInfo {
	private Drawable mIcon;
	private String mLabel;
	
	private String mMainClassName;
	private String mMainPackageName;
	
	private String mId;
	
	public void setMainClassName(String name) {
		mMainClassName = name;
	}
	
	public String getMainClassName() {
		return mMainClassName;
	}
	
	public void setMainPackageName(String name) {
		mMainPackageName = name;
	}
	
	public String getMainPackageName() {
		return mMainPackageName;
	}

	public Drawable getIcon() {
		return mIcon;
	}
	
	public void setIcon(Drawable icon) {
		this.mIcon = icon;
	}
	
	public String getLabel() {
		return mLabel;
	}
	
	public void setLabel(String label) {
		this.mLabel = label;
	}

	public String getId() {
		return mId;
	}
	
	public void setId(String s) {
		this.mId = s;
	}

}
