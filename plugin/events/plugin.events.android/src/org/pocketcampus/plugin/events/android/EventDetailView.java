package org.pocketcampus.plugin.events.android;

import static org.pocketcampus.platform.android.utils.SetUtils.intersect;
import static org.pocketcampus.plugin.events.android.EventsController.getEventPoolComp4sort;
import static org.pocketcampus.plugin.events.android.EventsController.oneItemList;
import static org.pocketcampus.plugin.events.android.EventsMainView.EXTRAS_KEY_EVENTPOOLID;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter;
import org.pocketcampus.platform.android.ui.adapter.MultiListAdapter;
import org.pocketcampus.platform.android.utils.Preparated;
import org.pocketcampus.platform.android.utils.Preparator;
import org.pocketcampus.platform.android.utils.ScrollStateSaver;
import org.pocketcampus.plugin.events.R;
import org.pocketcampus.plugin.events.android.iface.IEventsView;
import org.pocketcampus.plugin.events.shared.EventItem;
import org.pocketcampus.plugin.events.shared.EventPool;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

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
	public static final String QUERYSTRING_KEY_EVENTITEMID = "eventItemId";
    public final static String MAP_KEY_EVENTPOOLID = "EVENT_POOL_ID";
    public final static String MAP_KEY_EVENTPOOLTITLE = "EVENT_POOL_TITLE";
    public final static String MAP_KEY_EVENTPOOLCLICKLINK = "EVENT_POOL_CLICKLINK";  
	
	private ListView mList;
	
	private long eventItemId;
	private List<Long> displayedPools = new LinkedList<Long>();
	
	ScrollStateSaver scrollState;
	
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
		mList = (ListView) findViewById(R.id.events_main_list);
		

		setActionBarTitle(getString(R.string.events_plugin_title));
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
				eventItemId = Long.parseLong(aExtras.getString(EXTRAS_KEY_EVENTITEMID));
				System.out.println("Started with intent to display event " + eventItemId);
			} else if(aData != null && aData.getQueryParameter(QUERYSTRING_KEY_EVENTITEMID) != null) {
				eventItemId = Long.parseLong(aData.getQueryParameter(QUERYSTRING_KEY_EVENTITEMID));
				System.out.println("External start with intent to display event " + eventItemId);
			}
		}
		if(eventItemId == 0l) {
			finish();
			return;
		}
		
		mController.refreshEventItem(this, eventItemId, false);
		eventItemsUpdated(null);
	}

	@Override
	protected String screenName() {
		return "/events/item";
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(scrollState != null)
			scrollState.restore(mList);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(mList != null)
			scrollState = new ScrollStateSaver(mList);
	}
	
	@Override
	public void eventItemsUpdated(List<Long> updated) {
		
		if(updated != null && !updated.contains(eventItemId))
			return;
		
		System.out.println("EventDetailView::eventItemsUpdated eventItemId=" + eventItemId + " obj=" + this);
		
		EventItem parentEvent = mModel.getEventItem(eventItemId);
		if(parentEvent == null)
			return; // Ow!
		
		// create our list and custom adapter
		MultiListAdapter adapter = new MultiListAdapter();
		
	
		Preparated<EventItem> p1 = new Preparated<EventItem>(oneItemList(parentEvent), new Preparator<EventItem>() {
			public int[] resources() {
				return new int[] { R.id.event_list_complex_title, R.id.event_list_complex_subtitle, R.id.event_list_item_details, R.id.event_list_complex_image, R.id.event_list_complex_caption, R.id.event_big_image };
			}
			DateFormat timeFormat = EventsController.getTimeFormat(EventDetailView.this);
			DateFormat dateFormat = EventsController.getDateFormat(EventDetailView.this);
			private String getFormattedTimeInterval(EventItem e) {
				String eventTime = timeFormat.format(new Date(e.getStartDate()));
				String eventTime1 = timeFormat.format(new Date(e.getEndDate()));
				if(e.getEndDate() > e.getStartDate())
					return eventTime + " - " + eventTime1;
				return eventTime;
			}
			private String getFormattedDateInterval(EventItem e) {
				long startDate = e.getStartDate();
				long endDate = e.getEndDate();
				if(e.isFullDay())
					endDate = endDate - 24 * 3600 * 1000;
				String startStr = dateFormat.format(new Date(startDate));
				String endStr = dateFormat.format(new Date(endDate));
				if(endDate > startDate)
					return startStr + " - " + endStr;
				return startStr;
			}
			public Object content(int res, EventItem e) {
				switch (res) {
				case R.id.event_list_complex_title:
					return (e.isHideTitle() ? null : e.getEventTitle());
				case R.id.event_list_complex_subtitle:
					return e.getSecondLine();
				case R.id.event_list_item_details:
					if(e.isHideEventInfo())
						return null;
					StringBuilder details = new StringBuilder();
					if(e.isSetStartDate()) {
						details.append("<br><b>On</b> " + getFormattedDateInterval(e));
						details.append(!e.isFullDay() ? ("<br><b>At</b> " + getFormattedTimeInterval(e)) : "");
					}
					details.append(e.isSetEventPlace() ? ("<br><b>In</b> <a" + (e.isSetLocationHref() ? (" href=\"" + e.getLocationHref() + "\"") : "") + ">" + e.getEventPlace() + "</a>") : "");
					details.append(e.isSetEventSpeaker() ? ("<br><b>By</b> " + e.getEventSpeaker()) : "");
					details.append(e.isSetDetailsLink() ? ("<br><b>More</b> <a href=\"" + e.getDetailsLink() + "\">details</a>") : "");
					//details.append(e.isSetEventTags() && e.getEventTags().size() > 0 ? ("<br><b>Tag(s)</b> " + expandTags(e.getEventTags()) + "") : "");
					return "<p>" + details.toString() + "</p>";
				case R.id.event_list_complex_image:
					if(!e.isSetEventThumbnail() || e.isHideThumbnail())
						return -1; // R.drawable.events_transparent;
					return e.getEventThumbnail();
				case R.id.event_list_complex_caption:
					return e.getEventDetails();
				case R.id.event_big_image:
					if(!e.isSetEventPicture())
						return -1; // R.drawable.events_transparent;
					return e.getEventPicture();
				default:
					return null;
				}
			}
			public void finalize(Map<String, Object> map, EventItem item) {
				map.put(LazyAdapter.NOT_SELECTABLE, "1");
				map.put(LazyAdapter.LINK_CLICKABLE, "1");
			}
		});
		adapter.addSection( new LazyAdapter(this, p1.getMap(),
				R.layout.event_details, p1.getKeys(), p1.getResources())  );
		
		
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
			Collections.sort(eventPools, getEventPoolComp4sort());
			Preparated<EventPool> p4 = new Preparated<EventPool>(eventPools, new Preparator<EventPool>() {
				public int[] resources() {
					return new int[] { R.id.event_title, R.id.event_place, R.id.event_thumbnail };
				}
				public String content(int res, EventPool e) {
					switch (res) {
					case R.id.event_title:
						return e.getPoolTitle();
					case R.id.event_place:
						return e.getPoolPlace();
					case R.id.event_thumbnail:
						return e.getPoolPicture();
					default:
						return null;
					}
				}
				public void finalize(Map<String, Object> map, EventPool item) {
					if(item.isSetOverrideLink()) {
						map.put(MAP_KEY_EVENTPOOLCLICKLINK, item.getOverrideLink());
					} else {
						map.put(MAP_KEY_EVENTPOOLID, item.getPoolId() + "");
						map.put(MAP_KEY_EVENTPOOLTITLE, item.getPoolTitle());
					}
				}
			});
			adapter.addSection( new LazyAdapter(this, p4.getMap(),
					R.layout.event_pool_row, p4.getKeys(), p4.getResources()));
		}

	
		
		mList.setAdapter(adapter);
		//mLayout.setCacheColorHint(Color.TRANSPARENT);
		//mLayout.setFastScrollEnabled(true);
		mList.setScrollingCacheEnabled(false);
		//mLayout.setPersistentDrawingCache(ViewGroup.PERSISTENT_SCROLLING_CACHE);
		//mList.setDivider(null);
		//mList.setDividerHeight(0);
		
		mList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
		
		mList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Object o = arg0.getItemAtPosition(arg2);
				if(o instanceof Map<?, ?> && ((Map<?, ?>) o).containsKey(MAP_KEY_EVENTPOOLCLICKLINK)) {
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(((Map<?, ?>) o).get(MAP_KEY_EVENTPOOLCLICKLINK).toString()));
					EventDetailView.this.startActivity(i);
				} else if(o instanceof Map<?, ?> && ((Map<?, ?>) o).containsKey(MAP_KEY_EVENTPOOLID)) {
					String eId = ((Map<?, ?>) o).get(MAP_KEY_EVENTPOOLID).toString();
					String eTitle = ((Map<?, ?>) o).get(MAP_KEY_EVENTPOOLTITLE).toString();
					Intent i = new Intent(EventDetailView.this, EventsMainView.class);
					i.putExtra(EXTRAS_KEY_EVENTPOOLID, eId);
					EventDetailView.this.startActivity(i);
					trackEvent("ShowEventPool", eId + "-" + eTitle);
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
		mController.refreshEventItem(this, eventItemId, true);
	}
	
	@Override
	public void networkErrorHappened() {
		setUnrecoverableErrorOccurred(getString(R.string.sdk_connection_error_happened));
	}
	
	@Override
	public void mementoServersDown() {
		setUnrecoverableErrorOccurred(getString(R.string.sdk_upstream_server_down));
	}

	@Override
	public void exchangeContactsFinished(boolean success) {
	}

	@Override
	public void sendEmailRequestFinished(boolean success) {
	}

	@Override
	public void sendAdminRegEmailFinished(boolean success) {
	}

}
