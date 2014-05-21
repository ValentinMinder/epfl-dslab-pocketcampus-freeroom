package org.pocketcampus.plugin.events.android;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.events.android.iface.IEventsController;
import org.pocketcampus.plugin.events.android.iface.IEventsView;
import org.pocketcampus.plugin.events.android.req.SendRegEmailRequest;
import org.pocketcampus.plugin.events.android.req.ExchangeContactsRequest;
import org.pocketcampus.plugin.events.android.req.GetEventItemRequest;
import org.pocketcampus.plugin.events.android.req.GetEventPoolRequest;
import org.pocketcampus.plugin.events.android.req.SendFavoritesByEmailRequest;
import org.pocketcampus.plugin.events.shared.AdminSendRegEmailRequest;
import org.pocketcampus.plugin.events.shared.Constants;
import org.pocketcampus.plugin.events.shared.EventItem;
import org.pocketcampus.plugin.events.shared.EventItemRequest;
import org.pocketcampus.plugin.events.shared.EventPool;
import org.pocketcampus.plugin.events.shared.EventPoolRequest;
import org.pocketcampus.plugin.events.shared.EventsService.Client;
import org.pocketcampus.plugin.events.shared.EventsService.Iface;
import org.pocketcampus.plugin.events.shared.ExchangeRequest;
import org.pocketcampus.plugin.events.shared.SendEmailRequest;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.text.TextUtils;

/**
 * EventsController - Main logic for the Events Plugin.
 * 
 * This class issues requests to the Events PocketCampus
 * server to get the Events data from Memento.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class EventsController extends PluginController implements IEventsController {

	/** The plugin's model. */
	private EventsModel mModel;

	/** Interface to the plugin's server client */
	private Iface mClientEI;
	private Iface mClientEP;
	private Iface mClientEX;
	private GetEventPoolRequest currEventPoolRequest;
	private GetEventItemRequest currEventItemRequest;
	private ExchangeContactsRequest currExchangeContactsRequest;
	private SendFavoritesByEmailRequest currSendFavoritesByEmailRequest;

	/** The name of the plugin */
	private String mPluginName = "events";

	/**
	 * Initializes the plugin with a model and a client.
	 */
	@Override
	public void onCreate() {
		// Initializing the model is part of the controller's job...
		mModel = new EventsModel(getApplicationContext());

		// ...as well as initializing the client.
		// The "client" is the connection we use to access the service.
		mClientEI = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientEP = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientEX = (Iface) getClient(new Client.Factory(), mPluginName);

		// initialize ImageLoader
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
	}

	/**
	 * Returns the model for which this controller works.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}


	/**
	 * Initiates a request to the server to get the events items.
	 */
	public void refreshEventPool(IEventsView caller, long eventPoolId, boolean fetchPast, boolean useCache) {
		if(currEventPoolRequest != null)
			currEventPoolRequest.cancel(true);
		EventPoolRequest req = new EventPoolRequest(eventPoolId);
		req.setUserTickets(mModel.getTickets());
		req.setStarredEventItems(mModel.getFavorites());
		req.setFetchPast(fetchPast);
		req.setLang(Locale.getDefault().getLanguage());
		req.setPeriod(mModel.getPeriod());
		currEventPoolRequest = new GetEventPoolRequest(caller);
		currEventPoolRequest.setBypassCache(!useCache).start(this, mClientEP, req);
	}

	/**
	 * Initiates a request to the server to get the events pools.
	 */
	public void refreshEventItem(IEventsView caller, long eventItemId, boolean useCache) {
		if(currEventItemRequest != null)
			currEventItemRequest.cancel(true);
		EventItemRequest req = new EventItemRequest(eventItemId);
		req.setUserTickets(mModel.getTickets());
		req.setLang(Locale.getDefault().getLanguage());
		currEventItemRequest = new GetEventItemRequest(caller);
		currEventItemRequest.setBypassCache(!useCache).start(this, mClientEI, req);
	}

	/**
	 * Initiates a request to exchange contact information.
	 */
	public void exchangeContacts(IEventsView caller, String exchangeToken) {
		if(currExchangeContactsRequest != null)
			currExchangeContactsRequest.cancel(true);
		ExchangeRequest req = new ExchangeRequest(exchangeToken);
		req.setUserTickets(mModel.getTickets());
		currExchangeContactsRequest = new ExchangeContactsRequest(caller);
		currExchangeContactsRequest.start(this, mClientEX, req);
	}

	/**
	 * Initiates a request to send favorites by email.
	 */
	public void sendFavoritesByEmail(IEventsView caller, long eventPoolId, String emailAddress) {
		if(currSendFavoritesByEmailRequest != null)
			currSendFavoritesByEmailRequest.cancel(true);
		SendEmailRequest req = new SendEmailRequest(eventPoolId, mModel.getFavorites());
		req.setUserTickets(mModel.getTickets());
		req.setEmailAddress(emailAddress);
		req.setLang(Locale.getDefault().getLanguage());
		currSendFavoritesByEmailRequest = new SendFavoritesByEmailRequest(caller);
		currSendFavoritesByEmailRequest.start(this, mClientEX, req);
	}

	/**
	 * Initiates a request to send reg emails.
	 */
	public void adminSendRegEmails(IEventsView caller, String templateId) {
		AdminSendRegEmailRequest req = new AdminSendRegEmailRequest(templateId);
		new SendRegEmailRequest(caller).start(this, mClientEX, req);
	}


	/*****
	 * HELPER CLASSES AND FUNCTIONS
	 */
	
	public static <T> List<T> oneItemList(T obj) {
		List<T> list = new LinkedList<T>();
		list.add(obj);
		return list;
	}
	
	public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
	
	public static SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm", Locale.US);
	
	public static Comparator<EventItem> getEventItemComp4sort(final boolean decr) {
		return new Comparator<EventItem>() {
			public int compare(EventItem lhs, EventItem rhs) {
				if(lhs.isSetStartDate() && rhs.isSetStartDate()) {
					if(decr)	return Long.valueOf(rhs.getStartDate()).compareTo(lhs.getStartDate());
					else 		return Long.valueOf(lhs.getStartDate()).compareTo(rhs.getStartDate());
				}
				if(lhs.isSetEventTitle() && rhs.isSetEventTitle()) {
					return lhs.getEventTitle().compareTo(rhs.getEventTitle());
				}
				return 0;
			}
		};
	}
	
	public static Comparator<EventPool> getEventPoolComp4sort() {
		return new Comparator<EventPool>() {
			public int compare(EventPool lhs, EventPool rhs) {
				return Long.valueOf(lhs.getPoolId()).compareTo(rhs.getPoolId());
			}
		};
	}
	
	public static String expandTags(List<String> shortTags) {
		List<String> longTags = new LinkedList<String>();
		for(String tag : shortTags)
			longTags.add(Constants.EVENTS_TAGS.get(tag));
		return TextUtils.join(", ", longTags);
	}
		
	public static void updateEventCategs(Map<Integer, String> updated) {
		Constants.EVENTS_CATEGS.clear();
		Constants.EVENTS_CATEGS.putAll(updated);
	}

	public static void updateEventTags(Map<String, String> updated) {
		Constants.EVENTS_TAGS.clear();
		Constants.EVENTS_TAGS.putAll(updated);
	}

}
