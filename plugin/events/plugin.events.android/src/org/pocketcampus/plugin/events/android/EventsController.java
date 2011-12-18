package org.pocketcampus.plugin.events.android;

import java.util.Locale;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.events.android.iface.IEventsController;
import org.pocketcampus.plugin.events.android.req.EventsItemsRequest;
import org.pocketcampus.plugin.events.android.req.FeedUrlsRequest;
import org.pocketcampus.plugin.events.shared.EventsService.Client;
import org.pocketcampus.plugin.events.shared.EventsService.Iface;
import org.pocketcampus.plugin.events.shared.EventsService.getEventsItems_args;
import org.pocketcampus.plugin.events.shared.EventsService.getFeedUrls_args;

/**
 * Controller for the events plugin. Takes care of interactions between the model
 * and the view and gets information from the server.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 */
public class EventsController extends PluginController implements IEventsController {

	/** The plugin's model. */
	private EventsModel mModel;

	/** Interface to the plugin's server client */
	private Iface mClient;

	/** The name of the plugin */
	private String mPluginName = "events";

	/**
	 * Initializes the plugin with a model and a client.
	 */
	@Override
	public void onCreate() {
		// Initializing the model is part of the controller's job...
		mModel = new EventsModel();

		// ...as well as initializing the client.
		// The "client" is the connection we use to access the service.
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);

	}

	/**
	 * Returns the model for which this controller works.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	/**
	 * Initiates a request to the server to get the feed urls and names.
	 */
	@Override
	public void getFeedUrls() {
		String language = Locale.getDefault().getLanguage();

		getFeedUrls_args param = new getFeedUrls_args(language);
		new FeedUrlsRequest().start(this, mClient, param);
	}
	
	/**
	 * Initiates a request to the server to get the events items.
	 */
	@Override
	public void getEventItems() {
		String language = Locale.getDefault().getLanguage();

		getEventsItems_args param = new getEventsItems_args(language);
		new EventsItemsRequest().start(this, mClient, param);
	}

	// /**
	// * Initiates a request to the server to get the events Feeds.
	// */
	// @Override
	// public void getFeeds() {
	// Log.d("events", "Sending feeds request");
	// new FeedsRequest().start(this,
	// (Iface) getClient(new Client.Factory(), mPluginName),
	// (Object) null);
	// }
}
