package org.pocketcampus.plugin.events.android;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.events.android.iface.IEventsController;
import org.pocketcampus.plugin.events.android.req.GetEventItemRequest;
import org.pocketcampus.plugin.events.android.req.GetEventPoolRequest;
import org.pocketcampus.plugin.events.shared.Constants;
import org.pocketcampus.plugin.events.shared.EventItem;
import org.pocketcampus.plugin.events.shared.EventItemRequest;
import org.pocketcampus.plugin.events.shared.EventPool;
import org.pocketcampus.plugin.events.shared.EventPoolRequest;
import org.pocketcampus.plugin.events.shared.EventsService.Client;
import org.pocketcampus.plugin.events.shared.EventsService.Iface;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
	private Iface mClient;

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
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);

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
	public void refreshEventPool(long eventPoolId, boolean useCache) {
		EventPoolRequest req = new EventPoolRequest(eventPoolId);
		req.setUserToken(mModel.getToken());
		req.setLang(Locale.getDefault().getLanguage());
		req.setPeriod(mModel.getPeriod());
		new GetEventPoolRequest().setBypassCache(!useCache).start(this, mClient, req);
	}

	/**
	 * Initiates a request to the server to get the events pools.
	 */
	public void refreshEventItem(long eventItemId, boolean useCache) {
		EventItemRequest req = new EventItemRequest(eventItemId);
		req.setUserToken(mModel.getToken());
		req.setLang(Locale.getDefault().getLanguage());
		new GetEventItemRequest().setBypassCache(!useCache).start(this, mClient, req);
	}


	/*****
	 * HELPER CLASSES AND FUNCTIONS
	 */
	
	public static String getResizedPhotoUrl (String image, int newSize) {
		if(image == null)
			return null;
		if(image.contains("memento.epfl.ch/image")) {
			image = getSubstringBetween(image, "image/", "/"); // get the image id
			image = "http://memento.epfl.ch/image/" + image + "/" + newSize + "x" + newSize+ ".jpg";
		} else if(image.contains("secure.gravatar.com")) {
			image = getSubstringBetween(image, "avatar/", "?"); // get the image id
			image = "http://secure.gravatar.com/avatar/" + image + "?s=" + newSize;
		}
		return image;
	}
	
	public static  String getSubstringBetween(String orig, String before, String after) {
		int b = orig.indexOf(before);
		if(b != -1) {
			orig = orig.substring(b + before.length());
		}
		int a = orig.indexOf(after);
		if(a != -1) {
			orig = orig.substring(0, a);
		}
		return orig;
	}

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
	
	public static interface SelectionHandler<T> {
		void saveSelection(T t);
	}
	
	public static <T extends Comparable<? super T>> OnMenuItemClickListener buildMenuListener(
			final Context context, final Map<T, String> map, final String title, final SelectionHandler<T> handler) {
		final List<T> keysList = new LinkedList<T>(map.keySet());
		Collections.sort(keysList);
		final List<String> valuesList = extractValues(map, keysList);
		return new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				AlertDialog dialog = new AlertDialog.Builder(context)
						.setTitle(title)
						.setItems(
								valuesList.toArray(new String[]{}), 
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										handler.saveSelection(keysList.get(which));
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

}
