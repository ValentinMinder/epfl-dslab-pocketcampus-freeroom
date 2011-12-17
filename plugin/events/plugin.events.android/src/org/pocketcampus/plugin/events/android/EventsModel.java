package org.pocketcampus.plugin.events.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.events.android.iface.IEventsModel;
import org.pocketcampus.plugin.events.android.iface.IEventsView;
import org.pocketcampus.plugin.events.shared.Feed;
import org.pocketcampus.plugin.events.shared.EventsItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class EventsModel extends PluginModel implements IEventsModel {
	/** Listeners for the state of the view */
	IEventsView mListeners = (IEventsView) getListeners();

	/** List of events items to display. */
	private List<EventsItemWithImage> mEventsItems;

	/** Access to the preferences */
	private SharedPreferences mPrefs;

	/** The map of feed names with their Urls */
	private HashMap<String, String> mFeedUrls;

	/** List of Feeds to display */
	private List<Feed> mEventsFeeds;

	@Override
	protected Class<? extends IView> getViewInterface() {
		return IEventsView.class;
	}

	public void setEvents(List<EventsItem> eventsItems) {
		if (eventsItems != null) {
			if (mEventsItems == null) {
				mEventsItems = new ArrayList<EventsItemWithImage>();
			}
			for (EventsItem ni : eventsItems) {
				EventsItemWithImage eventsItem = new EventsItemWithImage(ni);
				mEventsItems.add(eventsItem);
			}
			mListeners.eventsUpdated();
		}
	}

	@Override
	public List<EventsItemWithImage> getEvents(Context ctx) {
		if (mPrefs == null) {
			mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		}

		if (mEventsItems == null) {
			return null;
		}

		ArrayList<EventsItemWithImage> filteredList = new ArrayList<EventsItemWithImage>();
		for (EventsItemWithImage eventsItem : mEventsItems) {
			if (mPrefs.getBoolean(EventsPreferences.LOAD_RSS
					+ eventsItem.getEventsItem().getFeed(), true)) {
				if (!alreadyContains(filteredList, eventsItem)) {
					filteredList.add(eventsItem);
				}
			}
		}

		return filteredList;
	}

	private boolean alreadyContains(List<EventsItemWithImage> filteredList,
			EventsItemWithImage eventsItemWithImage) {
		for (EventsItemWithImage ni : filteredList) {
			if (ni.getEventsItem().getTitle()
					.equals(eventsItemWithImage.getEventsItem().getTitle())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<Feed> getFeedsList() {
		return mEventsFeeds;
	}

	@Override
	public void setFeedsList(List<Feed> list) {
		if (list != null) {
			mEventsFeeds = list;

			if (mEventsItems == null) {
				mEventsItems = new ArrayList<EventsItemWithImage>();
			}

			for (Feed f : mEventsFeeds) {
				List<EventsItem> feedItems = f.getItems();
				for (EventsItem ni : feedItems) {
					mEventsItems.add(new EventsItemWithImage(ni));
				}
			}
			mListeners.eventsUpdated();
		}
	}

	@Override
	public Map<String, String> getFeedsUrls() {
		return mFeedUrls;
	}

	@Override
	public void setFeedsUrls(Map<String, String> map) {
		if (map != null) {
			mFeedUrls = new HashMap<String, String>();
			Iterator<Entry<String, String>> entries = map.entrySet().iterator();
			while (entries.hasNext()) {
			  Entry<String, String> thisEntry = (Entry<String, String>) entries.next();
			  String key = (String) thisEntry.getKey();
			  String value = (String) thisEntry.getValue();
			  mFeedUrls.put(key, value);
			}
		} else {
			Log.d("EVENTSMODEL", "Null map");
		}
		mListeners.feedUrlsUpdated();
	}

	@Override
	public void notifyNetworkErrorFeedUrls() {
		System.out.println("NETWORK ERROR");
	}
}
