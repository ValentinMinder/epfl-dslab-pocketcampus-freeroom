package org.pocketcampus.plugin.events.android;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.events.android.iface.IEventsController;
import org.pocketcampus.plugin.events.android.iface.IEventsView;
import org.pocketcampus.plugin.events.android.req.ExchangeContactsRequest;
import org.pocketcampus.plugin.events.android.req.GetEventItemRequest;
import org.pocketcampus.plugin.events.android.req.GetEventPoolRequest;
import org.pocketcampus.plugin.events.shared.Constants;
import org.pocketcampus.plugin.events.shared.EventItem;
import org.pocketcampus.plugin.events.shared.EventItemRequest;
import org.pocketcampus.plugin.events.shared.EventPool;
import org.pocketcampus.plugin.events.shared.EventPoolRequest;
import org.pocketcampus.plugin.events.shared.EventsService.Client;
import org.pocketcampus.plugin.events.shared.EventsService.Iface;
import org.pocketcampus.plugin.events.shared.ExchangeRequest;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

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
	public void refreshEventPool(IEventsView caller, long eventPoolId, boolean useCache) {
		if(currEventPoolRequest != null)
			currEventPoolRequest.cancel(true);
		EventPoolRequest req = new EventPoolRequest(eventPoolId);
		req.setUserToken(mModel.getToken());
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
		req.setUserToken(mModel.getToken());
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
		ExchangeRequest req = new ExchangeRequest(mModel.getToken(), exchangeToken);
		currExchangeContactsRequest = new ExchangeContactsRequest(caller);
		currExchangeContactsRequest.start(this, mClientEX, req);
	}


	/*****
	 * HELPER CLASSES AND FUNCTIONS
	 */
	
	public static interface Preparator<T> {
		public Object content(int res, T item);
		public int[] resources();
		public void finalize(Map<String, Object> map, T item);
	}
	
	public static class Preparated<T> {
		List<T> events;
		Preparator<T> prep;
		List<Map<String, ?>> data = null;
		String[] keys = null;
		int[] res = null;
		public Preparated(List<T> events, Preparator<T> prep) {
			this.events = events;
			this.prep = prep;
			compute();
		}
		private void compute() {
			res = prep.resources();
			keys = new String[res.length];
			for(int j = 0; j < res.length; j++)
				keys[j] = "KEY_" + j;
			data = new LinkedList<Map<String,?>>();
			for(int i = 0; i < events.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				T e = events.get(i);
				for(int j = 0; j < res.length; j++)
					map.put(keys[j], prep.content(res[j], e));
				prep.finalize(map, e);
				data.add(map);
			}
		}
		public List<Map<String, ?>> getMap() {
			return data;
		}
		public String[] getKeys() {
			return keys;
		}
		public int[] getResources() {
			return res;
		}
	}
	
	public static interface SingleChoiceHandler<T> {
		void saveSelection(T t);
	}
	
	public static interface MultiChoiceHandler<T> {
		void saveSelection(T t, boolean isChecked);
	}
	
	public static <T extends Comparable<? super T>> OnMenuItemClickListener buildMenuListenerSingleChoiceDialog(
			final Context context, final Map<T, String> map, final String title, final T selected, final SingleChoiceHandler<T> handler) {
		final List<T> keysList = new LinkedList<T>(map.keySet());
		Collections.sort(keysList);
		final int selPos = keysList.indexOf(selected);
		final List<String> valuesList = extractValues(map, keysList);
		return new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				AlertDialog dialog = new AlertDialog.Builder(context)
						.setTitle(title)
						.setSingleChoiceItems(
								valuesList.toArray(new String[]{}), selPos, 
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										handler.saveSelection(keysList.get(which));
										dialog.dismiss();
									}
								})
						.create();
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
				return true;
			}
		};
	}

	public static <T extends Comparable<? super T>> OnMenuItemClickListener buildMenuListenerMultiChoiceDialog(
			final Context context, final Map<T, String> map, final String title, final Set<T> selected, final MultiChoiceHandler<T> handler) {
		final List<T> keysList = new LinkedList<T>(map.keySet());
		Collections.sort(keysList);
		final boolean[] selPos = new boolean[keysList.size()];
		for(int i = 0; i < keysList.size(); i++) {
			selPos[i] = selected.contains(keysList.get(i));
		}
		final List<String> valuesList = extractValues(map, keysList);
		return new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				AlertDialog dialog = new AlertDialog.Builder(context)
						.setTitle(title)
						.setMultiChoiceItems(
								valuesList.toArray(new String[]{}), selPos, 
								new OnMultiChoiceClickListener() {
									public void onClick(DialogInterface dialog, int which, boolean isChecked) {
										handler.saveSelection(keysList.get(which), isChecked);
									}
								})
						.create();
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
				return true;
			}
		};
	}

	public static <K, V> List<V> extractValues(Map<K, V> map, List<K> keysList) {
		List<V> vals = new LinkedList<V>();
		for(K key : keysList) {
			vals.add(map.get(key));
		}
		return vals;
	}
	
	public static <T> List<T> oneItemList(T obj) {
		List<T> list = new LinkedList<T>();
		list.add(obj);
		return list;
	}
	
	public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
	
	public static SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm", Locale.US);
	
	public static Comparator<EventItem> eventItemComp4sort = new Comparator<EventItem>() {
		public int compare(EventItem lhs, EventItem rhs) {
			if(lhs.isSetStartDate() && rhs.isSetStartDate())
				return Long.valueOf(lhs.getStartDate()).compareTo(rhs.getStartDate());
			if(lhs.isSetEventTitle() && rhs.isSetEventTitle())
				return lhs.getEventTitle().compareTo(rhs.getEventTitle());
			return 0;
		}
	};

	public static Comparator<EventPool> eventPoolComp4sort = new Comparator<EventPool>() {
		public int compare(EventPool lhs, EventPool rhs) {
			return Long.valueOf(lhs.getPoolId()).compareTo(rhs.getPoolId());
		}
	};

	public static <T> List<T> intersect(List<T> l1, List<T> l2) {
		List<T> nl = new LinkedList<T>(l1);
		nl.retainAll(l2);
		return nl;
	}
	
	public static String expandTags(List<String> shortTags) {
		List<String> longTags = new LinkedList<String>();
		for(String tag : shortTags)
			longTags.add(Constants.EVENTS_TAGS.get(tag));
		return TextUtils.join(", ", longTags);
	}
	
	public static <K, V> Map<K, V> subMap(Map<K, V> map, Collection<K> subKeys) {
		Map<K, V> newMap = new HashMap<K, V>();
		for(K k : subKeys) {
			if(map.containsKey(k))
				newMap.put(k, map.get(k));
		}
		return newMap;
	}

}
