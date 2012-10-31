package org.pocketcampus.plugin.dashboard.android;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;

import android.content.Context;

/**
 * DashboardModel
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class DashboardModel extends PluginModel {
	
	public DashboardModel(Context context) {
	}
	
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IView.class;
	}

}
