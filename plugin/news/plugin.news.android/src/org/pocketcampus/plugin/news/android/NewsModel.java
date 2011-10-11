package org.pocketcampus.plugin.news.android;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.news.android.iface.INewsModel;
import org.pocketcampus.plugin.news.android.iface.INewsView;

public class NewsModel extends PluginModel implements INewsModel {

	@Override
	protected Class<? extends IView> getViewInterface() {
		return INewsView.class;
	}

}
