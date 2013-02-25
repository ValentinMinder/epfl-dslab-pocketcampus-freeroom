package org.pocketcampus.plugin.events.android;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.events.android.iface.IEventsModel;
import org.pocketcampus.plugin.events.android.iface.IEventsView;
import org.pocketcampus.plugin.events.shared.EventItem;
import org.pocketcampus.plugin.events.shared.EventPool;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import static org.pocketcampus.plugin.events.android.EventsController.*;

/**
 * EventsModel - The Model that stores the data of this plugin.
 * 
 * This is the Model associated with the Events plugin.
 * It stores the data required for the correct functioning of the plugin.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class EventsModel extends PluginModel implements IEventsModel {
	
	
	/**
	 * Some constants.
	 */
	private static final String EVENTS_STORAGE_NAME = "EVENTS_STORAGE_NAME";
	
	private static final String EVENTS_PERIOD_KEY = "EVENTS_PERIOD_KEY";
	private static final String EVENTS_FAVOTITES_LIST_KEY = "EVENTS_FAVOTITES_LIST_KEY";
	private static final String EVENTS_TOKEN_KEY = "EVENTS_TOKEN_KEY";
	
	//private static final String EVENTS_REQUEST_CACHE_PREFIX = "org.pocketcampus.plugin.events.eventscache";
	
	/**
	 * SharedPreferences object responsible for the persistent data storage.
	 */
	private SharedPreferences iStorage;
	
	//private Context cntxt;

	/**
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	IEventsView mListeners = (IEventsView) getListeners();
	
	/**
	 * Member variables containing required data for the plugin.
	 */
	private Map<Long, EventItem> iEvents;
	private Map<Long, EventPool> iPools;
	
	/**
	 * Member variables that need to be persistent
	 */
	private int iPeriod; // we send
	private List<Long> iFavorites; // for eventItems only
	private String iToken; // used to identify users (authentication)
	
	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public EventsModel(Context context) {
		iStorage = context.getSharedPreferences(EVENTS_STORAGE_NAME, 0);
		//cntxt = context;
		
		iPeriod = iStorage.getInt(EVENTS_PERIOD_KEY, 30);
		iFavorites = decodeFavList(iStorage.getString(EVENTS_FAVOTITES_LIST_KEY, ""));
		iToken = iStorage.getString(EVENTS_TOKEN_KEY, null);
		
		//EventPoolChildrenReply cached = (EventPoolChildrenReply) RequestCache.queryCache(cntxt, EVENTS_REQUEST_CACHE_PREFIX, null);
		//iEvents = (cached == null ? new HashMap<Long, EventItem>() : cached.getItems());
		iEvents = new HashMap<Long, EventItem>();
		iPools = new HashMap<Long, EventPool>();
	}
	
	public void markFavorite(long l, boolean fav) {
		if(iEvents.get(l) == null)
			return;
		if(fav) {
			if(!iFavorites.contains(l)) {
				iFavorites.add(l);
				savePrefs();
				mListeners.eventItemsUpdated(Arrays.asList(new Long[] {l}));
			}
		} else {
			if(iFavorites.remove((Long) l)) {
				savePrefs();
				mListeners.eventItemsUpdated(Arrays.asList(new Long[] {l}));
			}
		}
	}
	
	/**
	 * Setter and getter for iEvents
	 */
	public EventItem getEventItem(long id) {
		EventItem i = iEvents.get(id);
		if(i == null)
			return null;
		i = new EventItem(i);
		if(iFavorites.contains(id))
			i.setEventCateg(-2);
		return i;
	}
	public void addEventItem(EventItem obj) {
		if(!obj.isSetEventCateg()) obj.setEventCateg(1000000); // uncategorized
		if(!obj.isSetEventTags() || obj.getEventTags().size() == 0) obj.setEventTags(oneItemList("unlabeled")); // unlabeled
		iEvents.put(obj.getEventId(), obj);
		mListeners.eventItemsUpdated(oneItemList(obj.getEventId()));
	}
	public void addEventItems(Map<Long, EventItem> obj) {
		for(EventItem e : obj.values()) {
			if(!e.isSetEventCateg()) e.setEventCateg(1000000); // uncategorized
			if(!e.isSetEventTags() || e.getEventTags().size() == 0) e.setEventTags(oneItemList("unlabeled")); // unlabeled
		}
		iEvents.putAll(obj);
		mListeners.eventItemsUpdated(new LinkedList<Long>(obj.keySet()));
	}
	
	/**
	 * Setter and getter for iPools
	 */
	public EventPool getEventPool(long id) {
		return iPools.get(id);
	}
	public void addEventPool(EventPool obj) {
		iPools.put(obj.getPoolId(), obj);
		mListeners.eventPoolsUpdated(oneItemList(obj.getPoolId()));
	}
	public void addEventPools(Map<Long, EventPool> obj) {
		iPools.putAll(obj);
		mListeners.eventPoolsUpdated(new LinkedList<Long>(obj.keySet()));
	}
	
	/**
	 * Setter and getter for iPeriod iFeed and iCateg
	 */
	public int getPeriod() {
		return iPeriod;
	}
	public void setPeriod(int x) {
		iPeriod = x;
		savePrefs();
	}
	public String getToken() {
		return iToken;
	}
	public void setToken(String x) {
		iToken = x;
		savePrefs();
	}
	
	private void savePrefs() {
		iStorage.edit()
				.putInt(EVENTS_PERIOD_KEY, iPeriod)
				.putString(EVENTS_FAVOTITES_LIST_KEY, encodeFavList(iFavorites))
				.putString(EVENTS_TOKEN_KEY, iToken)
				.commit();
	}
	
	private String encodeFavList(List<Long> fav) {
		if(fav.size() == 0)
			return "";
		String[] s = new String[fav.size()];
		for(int i = 0; i < fav.size(); i++)
			s[i] = fav.get(i).toString();
		return TextUtils.join(",", s);
	}
	private List<Long> decodeFavList(String fav) {
		List<Long> s = new LinkedList<Long>();
		if("".equals(fav))
			return s;
		for(String z : fav.split("[,]"))
			s.add(Long.parseLong(z));
		return s;
	}
	
	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IEventsView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public IEventsView getListenersToNotify() {
		return mListeners;
	}
	
}
