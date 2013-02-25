package org.pocketcampus.plugin.events.android;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.pocketcampus.plugin.events.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.adapter.LazyAdapter;
import org.pocketcampus.android.platform.sdk.ui.adapter.LazyAdapter.Actuated;
import org.pocketcampus.android.platform.sdk.ui.adapter.LazyAdapter.Actuator;
import org.pocketcampus.android.platform.sdk.ui.adapter.SeparatedListAdapter;
import org.pocketcampus.plugin.events.android.iface.IEventsView;
import org.pocketcampus.plugin.events.shared.Constants;
import org.pocketcampus.plugin.events.shared.EventItem;
import org.pocketcampus.plugin.events.shared.EventPool;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import static org.pocketcampus.plugin.events.android.EventDetailView.*;
import static org.pocketcampus.plugin.events.android.EventsController.*;

/**
 * EventsMainView - Main view that shows list of Events.
 * 
 * This is the main view in the Events Plugin.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class EventsMainView extends PluginView implements IEventsView {

	private EventsController mController;
	private EventsModel mModel;
	
	public static final String EXTRAS_KEY_EVENTPOOLID = "eventPoolId";
	public static final String QUERYSTRING_KEY_EVENTPOOLID = "id";
	public static final String QUERYSTRING_KEY_TOKEN = "token";
	public static final String MAP_KEY_EVENTITEMID = "EVENT_ITEM_ID";
	
	private ListView mLayout;
	
	private long eventPoolId;
	private List<Long> displayedEvents = new LinkedList<Long>();
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return EventsController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		
		// Get and cast the controller and model
		mController = (EventsController) controller;
		mModel = (EventsModel) controller.getModel();


		// The ActionBar is added automatically when you call setContentView
		//disableActionBar();
		setContentView(R.layout.events_main);
		mLayout = (ListView) findViewById(R.id.events_main_list);
	}
	

	/**
	 * Handles the intent that was used to start this plugin.
	 * 
	 * We need to read the Extras.
	 */
	@Override
	protected void handleIntent(Intent aIntent) {
		eventPoolId = Constants.CONTAINER_EVENT_ID;
		if(aIntent != null) {
			Bundle aExtras = aIntent.getExtras();
			Uri aData = aIntent.getData();
			if(aExtras != null && aExtras.containsKey(EXTRAS_KEY_EVENTPOOLID)) {
				System.out.println("Started with intent to display pool " + eventPoolId);
				eventPoolId = Long.parseLong(aExtras.getString(EXTRAS_KEY_EVENTPOOLID));
			} else if(aData != null && aData.getQueryParameter(QUERYSTRING_KEY_EVENTPOOLID) != null) {
				System.out.println("External start with intent to display pool " + eventPoolId);
				eventPoolId = Long.parseLong(aData.getQueryParameter(QUERYSTRING_KEY_EVENTPOOLID));
				if(aData.getQueryParameter(QUERYSTRING_KEY_TOKEN) != null) {
					System.out.println("Got also a token :-)");
					mModel.setToken(aData.getQueryParameter(QUERYSTRING_KEY_TOKEN));
				}
			}
		}
		
		//Tracker
		if(eventPoolId == Constants.CONTAINER_EVENT_ID) Tracker.getInstance().trackPageView("events");
		else Tracker.getInstance().trackPageView("events/" + eventPoolId + "/subevents");
		
		mController.refreshEventPool(eventPoolId, false);
		eventPoolsUpdated(null);
	}

	@Override
	public void eventPoolsUpdated(List<Long> updated) {
		System.out.println("EventsMainView::eventPoolsUpdated");
		
		if(updated != null && !updated.contains(eventPoolId))
			return;
		
		//System.out.println("eventsListUpdated getting pool");
		final EventPool parentEvent = mModel.getEventPool(eventPoolId);
		if(parentEvent == null || parentEvent.getChildrenEvents() == null)
			return; // Ow!
		
		
		//System.out.println("eventsListUpdated building hash childrenEvent=" + parentEvent.getChildrenEvents().size());
		Map<Integer, List<EventItem>> eventsByCateg = new HashMap<Integer, List<EventItem>>();
		displayedEvents.clear();
		for(long eventId : parentEvent.getChildrenEvents()) {
			EventItem e = mModel.getEventItem(eventId);
			//e.setEventCateg(1);
			if(e == null || !e.isSetEventCateg())
				continue;
			displayedEvents.add(eventId);
			if(!eventsByCateg.containsKey(e.getEventCateg()))
				eventsByCateg.put(e.getEventCateg(), new LinkedList<EventItem>());
			eventsByCateg.get(e.getEventCateg()).add(e);
		}
		
		SeparatedListAdapter adapter = new SeparatedListAdapter(this, R.layout.event_list_header);
		List<Integer> categList = new LinkedList<Integer>(eventsByCateg.keySet());
		Collections.sort(categList);
		for(int i : categList) {
			List<EventItem> categEvents = eventsByCateg.get(i);
			Collections.sort(categEvents, eventItemComp4sort);
			Preparated<EventItem> p = new Preparated<EventItem>(categEvents, new Preparator<EventItem>() {
				public int[] resources() {
					return new int[] { R.id.event_title, R.id.event_speaker, R.id.event_thumbnail, R.id.event_time, R.id.event_fav_star };
				}
				public Object content(int res, final EventItem e) {
					switch (res) {
					case R.id.event_title:
						return e.getEventTitle();
					case R.id.event_speaker:
						return e.getEventPlace();
					case R.id.event_thumbnail:
						return getResizedPhotoUrl(e.getEventPicture(), 48);
					case R.id.event_time:
						if(!e.isSetStartDate())
							return null;
						String startTime = simpleTimeFormat.format(new Date(e.getStartDate()));
						String startDay = simpleDateFormat.format(new Date(e.getStartDate()));
						String endDay = simpleDateFormat.format(new Date(e.getEndDate()));
						String today = simpleDateFormat.format(new Date());
						if(today.compareTo(startDay) >= 0 && today.compareTo(endDay) <= 0) {
							if(e.isFullDay())
								return "Today";
							else
								return startTime;
						} else {
							return startDay;
						}
					case R.id.event_fav_star:
						if(parentEvent.isDisableStar())
							return android.R.drawable.divider_horizontal_bright;
						Integer fav = android.R.drawable.star_off;
						if(e.getEventCateg() == -2)
							fav = android.R.drawable.star_on;
						return new Actuated(fav, new Actuator() {
							public void triggered() {
								System.out.println("toggle fav event: " + e.getEventTitle());
								mModel.markFavorite(e.getEventId(), (e.getEventCateg() != -2));
							}
						});
					default:
						return null;
					}
				}
				public void finalize(Map<String, Object> map, EventItem item) {
					map.put(MAP_KEY_EVENTITEMID, item.getEventId() + "");
				}
			});
			adapter.addSection(Constants.EVENTS_CATEGS.get(i), new LazyAdapter(this, p.getMap(), 
					R.layout.events_list_row, p.getKeys(), p.getResources()));
		}
		
		mLayout.setAdapter(adapter);
		//mLayout.setCacheColorHint(Color.TRANSPARENT);
		//mLayout.setFastScrollEnabled(true);
		//mLayout.setScrollingCacheEnabled(false);
		//mLayout.setPersistentDrawingCache(ViewGroup.PERSISTENT_SCROLLING_CACHE);
		
		mLayout.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
		
		mLayout.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Object o = arg0.getItemAtPosition(arg2);
				if(o instanceof Map<?, ?>) {
					Intent i = new Intent(EventsMainView.this, EventDetailView.class);
					i.putExtra(EXTRAS_KEY_EVENTITEMID, ((Map<?, ?>) o).get(MAP_KEY_EVENTITEMID).toString());
					EventsMainView.this.startActivity(i);
				} else {
					Toast.makeText(getApplicationContext(), o.toString(), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		
	}
	
	@Override
	public void eventItemsUpdated(List<Long> updated) {
		if(intersect(displayedEvents, updated).size() > 0)
			eventPoolsUpdated(null);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem categMenu = menu.add("Choose Category");
		categMenu.setOnMenuItemClickListener(buildMenuListener(this, 
				Constants.EVENTS_CATEGS, "Choose a Category", 
				new SelectionHandler<Integer>() {
					public void saveSelection(Integer t) {
						mModel.setCateg(t);
					}
				}
		));
		MenuItem feedMenu = menu.add("Choose Feed");
		feedMenu.setOnMenuItemClickListener(buildMenuListener(this,
				Constants.EVENTS_TAGS, "Choose a Feed", 
				new SelectionHandler<String>() {
					public void saveSelection(String t) {
						mModel.setTag(t);
					}
				}
		));
		MenuItem periodMenu = menu.add("Choose Period");
		periodMenu.setOnMenuItemClickListener(buildMenuListener(this,
				Constants.EVENTS_PERIODS, "Choose a Period", 
				new SelectionHandler<Integer>() {
					public void saveSelection(Integer t) {
						mModel.setPeriod(t);
						mController.refreshEventPool(eventPoolId, false);
					}
				}
		));
		return true;
	}
	
	@Override
	public void networkErrorCacheExists() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_connection_no_cache_yes), Toast.LENGTH_SHORT).show();
		mController.refreshEventPool(eventPoolId, true);
	}
	
	@Override
	public void networkErrorHappened() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_connection_error_happened), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void mementoServersDown() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_upstream_server_down), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void identificationRequired() {
		Toast.makeText(getApplicationContext(), 
				"Please scan the barcode in the email to enable this feature", 
				Toast.LENGTH_SHORT).show();
	}

}
