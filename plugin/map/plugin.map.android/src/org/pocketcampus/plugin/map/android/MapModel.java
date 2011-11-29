package org.pocketcampus.plugin.map.android;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;

public class MapModel extends PluginModel {
//	private ITestView mListeners = (ITestView) getListeners();
	private int mFoo;
	private int mBar;
	
	@Override
	protected Class<? extends IView> getViewInterface() {
		return null;
	}
}
