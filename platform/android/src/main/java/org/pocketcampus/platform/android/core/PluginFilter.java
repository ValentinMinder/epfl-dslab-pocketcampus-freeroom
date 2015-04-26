package org.pocketcampus.platform.android.core;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

/**
 * Utility to filter <code>Activity</code>s based on their Action and Category.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 */
public class PluginFilter {

	private Context mContext;
	private Intent mIntent;

	public PluginFilter(Context context) {
		mContext = context;
		mIntent = new Intent();
	}
	
	public void setActionConstraint(String action) {
		mIntent.setAction(action);
	}
	
	public void addCategoryConstraint(String category) {
		mIntent.addCategory(category);
	}

	public List<ResolveInfo> getMatchingPlugins() {
		return mContext.getPackageManager().queryIntentActivities(mIntent, 0);
	}
	
}
