package org.pocketcampus.plugin.events.android;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.pocketcampus.plugin.events.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.adapter.LazyAdapter;
import org.pocketcampus.android.platform.sdk.ui.adapter.SeparatedListAdapter;
import org.pocketcampus.plugin.events.android.EventsController.Preparated;
import org.pocketcampus.plugin.events.android.EventsController.Preparator;
import org.pocketcampus.plugin.events.android.iface.IEventsView;
import org.pocketcampus.plugin.events.shared.EventItem;
import org.pocketcampus.plugin.events.shared.EventPool;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import static org.pocketcampus.plugin.events.android.EventsController.*;
import static org.pocketcampus.plugin.events.android.EventsMainView.*;

/**
 * EventDetailView - View that shows an Event details.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class EventDetailView extends PluginView implements IEventsView {

	private EventsController mController;
	private EventsModel mModel;
	
	public static final String EXTRAS_KEY_EVENTITEMID = "eventItemId";
	public static final String QUERYSTRING_KEY_EVENTITEMID = "id";
    public final static String MAP_KEY_EVENTPOOLID = "EVENT_POOL_ID";  
	
	private ListView mLayout;
	
	private long eventItemId;
	private List<Long> displayedPools = new LinkedList<Long>();
	
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
		eventItemId = 0l;
		if(aIntent != null) {
			Bundle aExtras = aIntent.getExtras();
			Uri aData = aIntent.getData();
			if(aExtras != null && aExtras.containsKey(EXTRAS_KEY_EVENTITEMID)) {
				System.out.println("Started with intent to display event " + eventItemId);
				eventItemId = Long.parseLong(aExtras.getString(EXTRAS_KEY_EVENTITEMID));
			} else if(aData != null && aData.getQueryParameter(QUERYSTRING_KEY_EVENTITEMID) != null) {
				System.out.println("External start with intent to display event " + eventItemId);
				eventItemId = Long.parseLong(aData.getQueryParameter(QUERYSTRING_KEY_EVENTITEMID));
			}
		}
		if(eventItemId == 0l) {
			finish();
			return;
		}
		
		//Tracker
		Tracker.getInstance().trackPageView("events/" + eventItemId);
		
		mController.refreshEventItem(eventItemId, false);
		eventItemsUpdated(null);
	}


	@Override
	public void eventItemsUpdated(List<Long> updated) {
		System.out.println("EventDetailView::eventItemsUpdated");
		
		if(updated != null && !updated.contains(eventItemId))
			return;
		
		EventItem parentEvent = mModel.getEventItem(eventItemId);
		if(parentEvent == null)
			return; // Ow!
		
		// create our list and custom adapter
		SeparatedListAdapter adapter = new SeparatedListAdapter(this, R.layout.event_list_header);
		
	
		Preparated<EventItem> p1 = new Preparated<EventItem>(oneItemList(parentEvent), new Preparator<EventItem>() {
			public int[] resources() {
				return new int[] { R.id.event_list_complex_title, R.id.event_list_item_details, R.id.event_list_complex_image, R.id.event_list_complex_caption };
			}
			private String getFormattedTimeInterval(EventItem e) {
				String eventTime = simpleTimeFormat.format(new Date(e.getStartDate()));
				String eventTime1 = simpleTimeFormat.format(new Date(e.getEndDate()));
				if(e.getEndDate() > e.getStartDate())
					return eventTime + " - " + eventTime1;
				return eventTime;
			}
			private String getFormattedDateInterval(EventItem e) {
				String eventDate = simpleDateFormat.format(new Date(e.getStartDate()));
				String eventDate1 = simpleDateFormat.format(new Date(e.getEndDate()));
				if(e.getEndDate() > e.getStartDate() && eventDate1.compareTo(eventDate) > 0)
					return eventDate + " - " + eventDate1;
				return eventDate;
			}
			public Object content(int res, EventItem e) {
				switch (res) {
				case R.id.event_list_complex_title:
					return e.getEventTitle();
				case R.id.event_list_item_details:
					StringBuilder details = new StringBuilder();
					if(e.isSetStartDate()) {
						details.append("<br><b>On</b> " + getFormattedDateInterval(e));
						details.append(!e.isFullDay() ? ("<br><b>At</b> " + getFormattedTimeInterval(e)) : "");
					}
					details.append(e.isSetEventPlace() ? ("<br><b>In</b> <a" + (e.isSetLocationHref() ? (" href=\"" + e.getLocationHref() + "\"") : "") + ">" + e.getEventPlace() + "</a>") : "");
					details.append(e.isSetEventSpeaker() ? ("<br><b>By</b> " + e.getEventSpeaker()) : "");
					details.append(e.isSetDetailsLink() ? ("<br><b>More</b> <a href=\"" + e.getDetailsLink() + "\">details</a>") : "");
					details.append(e.isSetEventTags() && e.getEventTags().size() > 0 ? ("<br><b>Tag(s)</b> " + expandTags(e.getEventTags()) + "") : "");
					return "<p>" + details.toString() + "</p>";
				case R.id.event_list_complex_image:
					return getResizedPhotoUrl(e.getEventPicture(), 480);
				case R.id.event_list_complex_caption:
					return e.getEventDetails();
				default:
					return null;
				}
			}
			public void finalize(Map<String, Object> map, EventItem item) {
				map.put(LazyAdapter.NOT_SELECTABLE, "1");
				map.put(LazyAdapter.LINK_CLICKABLE, "1");
			}
		});
		adapter.addSection("Details" /* Constants.EVENTS_CATEGS.get(parentEvent.getEventCateg()) */ , new LazyAdapter(this, p1.getMap(),
				R.layout.event_big_image, p1.getKeys(), p1.getResources()) /* .setImageSizeCap(840) */ );
		
		
		/*if(thisEvent.isSetEventPicture()) {
		} else {
			security = new LinkedList<Map<String, ?>>();
			security.add(createItem(null, eventDateStr, null, null));

			adapter.addSection(Constants.EVENTS_CATEGS.get(thisEvent.getEventCateg()), new LazyAdapter(this, security,
					R.layout.event_list_complex,
					new String[] { ITEM_CAPTION }, new int[] {
							R.id.event_list_complex_caption }));
			
		}*/
		
		
		
		/*Preparated<EventItem> p2 = new Preparated<EventItem>(oneItemList(parentEvent), new Preparator<EventItem>() {
			public int[] resources() {
				return new int[] { R.id.event_list_complex_title, R.id.event_list_complex_caption };
			}
			public Object content(int res, EventItem e) {
				switch (res) {
				case R.id.event_list_complex_title:
					return e.getEventTitle();
				case R.id.event_list_complex_caption:
					return e.getEventDetails();
				default:
					return null;
				}
			}
			public void finalize(Map<String, Object> map, EventItem item) {
			}
		});
		adapter.addSection("Event details", new LazyAdapter(this, p2.getMap(),
				R.layout.event_list_complex, p2.getKeys(), p2.getResources()));*/

		/*if(parentEvent.isSetEventPlace()) {
			Preparated<EventItem> p3 = new Preparated<EventItem>(oneItemList(parentEvent), new Preparator<EventItem>() {
				public int[] resources() {
					return new int[] { R.id.event_title, R.id.event_speaker, R.id.event_thumbnail, R.id.event_time };
				}
				public Object content(int res, EventItem e) {
					switch (res) {
					case R.id.event_title:
					case R.id.event_speaker:
						return e.getEventPlace();
					case R.id.event_thumbnail:
						return android.R.drawable.ic_dialog_map;
					case R.id.event_time:
					default:
						return null;
					}
				}
				public void finalize(Map<String, Object> map, EventItem item) {
				}
			});
			adapter.addSection("Location", new SimpleAdapter(this, p3.getMap(),
					R.layout.events_list_row, p3.getKeys(), p3.getResources()));
		}*/

		if(parentEvent.isSetChildrenPools() && parentEvent.getChildrenPools().size() > 0) {
			LinkedList<EventPool> eventPools = new LinkedList<EventPool>();
			displayedPools.clear();
			for(long poolId : parentEvent.getChildrenPools()){
				EventPool pool = mModel.getEventPool(poolId);
				if(pool == null)
					continue;
				displayedPools.add(poolId);
				eventPools.add(pool);
			}
			Collections.sort(eventPools, eventPoolComp4sort);
			Preparated<EventPool> p4 = new Preparated<EventPool>(eventPools, new Preparator<EventPool>() {
				public int[] resources() {
					return new int[] { R.id.event_title, R.id.event_speaker, R.id.event_thumbnail, R.id.event_time };
				}
				public String content(int res, EventPool e) {
					switch (res) {
					case R.id.event_title:
						return e.getPoolTitle();
					case R.id.event_speaker:
						return e.getPoolPlace();
					case R.id.event_thumbnail:
						return e.getPoolPicture();
					case R.id.event_time:
					default:
						return null;
					}
				}
				public void finalize(Map<String, Object> map, EventPool item) {
					map.put(MAP_KEY_EVENTPOOLID, item.getPoolId() + "");
				}
			});
			adapter.addSection("More", new LazyAdapter(this, p4.getMap(),
					R.layout.events_list_row, p4.getKeys(), p4.getResources()));
		}

	
		/*
		
		System.out.println("eventsListUpdated building hash");
		Map<Integer, List<EventItem>> eventsByCateg = new HashMap<Integer, List<EventItem>>();
		for(long eventId : allEvents.get(Constants.CONTAINER_EVENT_ID).getChildrenEvents()) {
			EventItem e = allEvents.get(eventId);
			//e.setEventCateg(1);
			if(!e.isSetEventCateg())
				continue;
			if(!eventsByCateg.containsKey(e.getEventCateg()))
				eventsByCateg.put(e.getEventCateg(), new LinkedList<EventItem>());
			eventsByCateg.get(e.getEventCateg()).add(e);
		}
		
		SeparatedListAdapter adapter = new SeparatedListAdapter(this, R.layout.event_list_header);
		for(int i : eventsByCateg.keySet()) {
			List<Map<String, String>> eventsInCateg = new LinkedList<Map<String, String>>();
			for(EventItem e : eventsByCateg.get(i)) {
				eventsInCateg.add(createItem(e));
			}
			adapter.addSection(Constants.EVENTS_CATEGS.get(i), new LazyAdapter(this, eventsInCateg,
					0, R.layout.events_list_row,
					new String[] { ITEM_TITLE, ITEM_CAPTION, ITEM_IMAGE, ITEM_TIME }, new int[] {
							R.id.event_title, R.id.event_speaker, R.id.event_thumbnail, R.id.event_time }));
			
			//adapter.addSection(Constants.EVENTS_CATEGS.get(i), new LazyAdapter(this, eventsInCateg,
			//		0, R.layout.event_list_complex,
			//		new String[] { ITEM_TITLE, ITEM_CAPTION, ITEM_IMAGE }, new int[] {
			//		R.id.event_list_complex_title, R.id.event_list_complex_caption, R.id.event_list_complex_image }));
		}
		*/
		
		
		mLayout.setAdapter(adapter);
		//mLayout.setCacheColorHint(Color.TRANSPARENT);
		//mLayout.setFastScrollEnabled(true);
		mLayout.setScrollingCacheEnabled(false);
		//mLayout.setPersistentDrawingCache(ViewGroup.PERSISTENT_SCROLLING_CACHE);
		
		mLayout.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
		
		mLayout.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Object o = arg0.getItemAtPosition(arg2);
				if(o instanceof Map<?, ?> && ((Map<?, ?>) o).containsKey(MAP_KEY_EVENTPOOLID)) {
					Intent i = new Intent(EventDetailView.this, EventsMainView.class);
					i.putExtra(EXTRAS_KEY_EVENTPOOLID, ((Map<?, ?>) o).get(MAP_KEY_EVENTPOOLID).toString());
					EventDetailView.this.startActivity(i);
				} else {
					Toast.makeText(getApplicationContext(), o.toString(), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		
		
	}
	
	@Override
	public void eventPoolsUpdated(List<Long> updated) {
		if(intersect(displayedPools, updated).size() > 0)
			eventItemsUpdated(null);
	}
	
	
	@Override
	public void networkErrorCacheExists() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_connection_no_cache_yes), Toast.LENGTH_SHORT).show();
		mController.refreshEventItem(eventItemId, true);
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
