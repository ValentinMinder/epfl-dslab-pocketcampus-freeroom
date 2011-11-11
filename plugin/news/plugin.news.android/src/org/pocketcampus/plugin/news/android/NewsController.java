package org.pocketcampus.plugin.news.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.news.android.iface.INewsController;
import org.pocketcampus.plugin.news.android.req.NewsItemRequest;
import org.pocketcampus.plugin.news.shared.NewsService.Client;
import org.pocketcampus.plugin.news.shared.NewsService.Iface;

//import org.pocketcampus.plugin.news.shared.NewsService.Client;

public class NewsController extends PluginController implements INewsController {

	private NewsModel mModel;
	private Iface mClient;
	private String mPluginName = "news";

	@Override
	public void onCreate() {
		// Initializing the model is part of the controller's job...
		mModel = new NewsModel();

		// ...as well as initializing the client.
		// The "client" is the connection we use to access the service.
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
	}

	/**
	 * The view will call this in order to register in the model's listener
	 * list.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	@Override
	public void loadNews() {
		new NewsItemRequest().start(this, mClient, (Object) null);
	}

}
