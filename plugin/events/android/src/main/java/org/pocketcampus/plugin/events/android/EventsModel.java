package org.pocketcampus.plugin.events.android;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.platform.android.core.PluginModel;
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
	
	private static final String EVENTS_PERIOD_IN_HOURS_KEY = "EVENTS_PERIOD_IN_HOURS_KEY";
	private static final String EVENTS_FAVOTITES_LIST_KEY = "EVENTS_FAVOTITES_LIST_KEY";
	private static final String EVENTS_TICKETS_LIST_KEY = "EVENTS_TOKEN_KEY"; // backward compatibility 
	
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
	private int iPeriodInHours; // we send
	private List<Long> iFavorites; // for eventItems only
	private List<String> iTickets; // used to give access to a private event
	
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
		
		iPeriodInHours = iStorage.getInt(EVENTS_PERIOD_IN_HOURS_KEY, 7 * 24);
		iFavorites = decodeFavoritesList(iStorage.getString(EVENTS_FAVOTITES_LIST_KEY, ""));
		iTickets = decodeTicketsList(iStorage.getString(EVENTS_TICKETS_LIST_KEY, ""));
		
		//EventPoolChildrenReply cached = (EventPoolChildrenReply) RequestCache.queryCache(cntxt, EVENTS_REQUEST_CACHE_PREFIX, null);
		//iEvents = (cached == null ? new HashMap<Long, EventItem>() : cached.getItems());
		iEvents = new HashMap<Long, EventItem>();
		iPools = new HashMap<Long, EventPool>();
	}
	
	public void markFavorite(long l, boolean fav) {
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
	public List<Long> getFavorites() {
		return iFavorites;
	}
	
	public void addTicket(String ticket) {
		if(!iTickets.contains(ticket)) {
			iTickets.add(ticket);
			savePrefs();
		}
	}
	public List<String> getTickets() {
		return iTickets;
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
		iEvents.put(obj.getEventId(), obj);
		mListeners.eventItemsUpdated(oneItemList(obj.getEventId()));
	}
	public void addEventItems(Map<Long, EventItem> obj) {
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
	public int getPeriodInHours() {
		return iPeriodInHours;
	}
	public void setPeriodInHours(int x) {
		iPeriodInHours = x;
		savePrefs();
	}
	
	private void savePrefs() {
		iStorage.edit()
				.putInt(EVENTS_PERIOD_IN_HOURS_KEY, iPeriodInHours)
				.putString(EVENTS_FAVOTITES_LIST_KEY, encodeFavoritesList(iFavorites))
				.putString(EVENTS_TICKETS_LIST_KEY, encodeTicketsList(iTickets))
				.commit();
	}
	
	private static String encodeFavoritesList(List<Long> fav) {
		// itemId does not contain commas
		if(fav.size() == 0)
			return "";
		String[] s = new String[fav.size()];
		for(int i = 0; i < fav.size(); i++)
			s[i] = fav.get(i).toString();
		return TextUtils.join(",", s);
	}
	private static List<Long> decodeFavoritesList(String fav) {
		// itemId does not contain commas
		List<Long> s = new LinkedList<Long>();
		if("".equals(fav))
			return s;
		for(String z : fav.split("[,]"))
			s.add(Long.parseLong(z));
		return s;
	}
	
	private static String encodeTicketsList(List<?> fav) {
		// userTicket does not contain commas
		if(fav.size() == 0)
			return "";
		return TextUtils.join(",", fav.toArray());
	}
	private static List<String> decodeTicketsList(String fav) {
		// userTicket does not contain commas
		List<String> s = new LinkedList<String>();
		if("".equals(fav))
			return s;
		// Can't return Arrays.asList directly!
		s.addAll(Arrays.asList(fav.split("[,]")));
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
