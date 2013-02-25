package org.pocketcampus.plugin.events.android;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pocketcampus.plugin.events.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.adapter.LazyAdapter;
import org.pocketcampus.android.platform.sdk.ui.adapter.LazyAdapter.Actuated;
import org.pocketcampus.android.platform.sdk.ui.adapter.LazyAdapter.Actuator;
import org.pocketcampus.android.platform.sdk.ui.adapter.SeparatedListAdapter;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.plugin.events.android.iface.IEventsView;
import org.pocketcampus.plugin.events.shared.Constants;
import org.pocketcampus.plugin.events.shared.EventItem;
import org.pocketcampus.plugin.events.shared.EventPool;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
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
	public static final String QUERYSTRING_KEY_EVENTPOOLID = "eventPoolId";
	public static final String QUERYSTRING_KEY_TOKEN = "userToken";
	public static final String QUERYSTRING_KEY_EXCHANGETOKEN = "exchangeToken";
	public static final String MAP_KEY_EVENTITEMID = "EVENT_ITEM_ID";
	
	private boolean displayingList;
	
	private long eventPoolId;
	private List<Long> eventsInRS = new LinkedList<Long>();
	private Set<Integer> categsInRS = new HashSet<Integer>();
	private Set<String> tagsInRS = new HashSet<String>();
	
	EventPool thisEventPool;
	Map<Integer, List<EventItem>> eventsByCateg;
	Set<Integer> filteredCategs;
	Set<String> filteredTags;
	
	ListView mList;
	ScrollStateSaver scrollState;
	
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
		displayingList = true;
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
			//Uri aData = aIntent.getData();
			if(aExtras != null && aExtras.containsKey(EXTRAS_KEY_EVENTPOOLID)) {
				eventPoolId = Long.parseLong(aExtras.getString(EXTRAS_KEY_EVENTPOOLID));
				System.out.println("Started with intent to display pool " + eventPoolId);
			/*} else {
				externalCall(aData); // TODO too many refresh*/
			}
		}
		
		//Tracker
		if(eventPoolId == Constants.CONTAINER_EVENT_ID) Tracker.getInstance().trackPageView("events");
		else Tracker.getInstance().trackPageView("events/" + eventPoolId + "/subevents");
		
		mController.refreshEventPool(this, eventPoolId, false);
		//eventPoolsUpdated(null);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(displayingList && scrollState != null)
			scrollState.restore(mList);
		/*if(mController != null)
			mController.refreshEventPool(this, eventPoolId, false);*/
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(displayingList)
			scrollState = new ScrollStateSaver(mList);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		System.out.println("back from barcode scanner");
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null && "QR_CODE".equals(scanResult.getFormatName())) {
			externalCall(Uri.parse(scanResult.getContents()));
		}
	}
	
	private void externalCall(Uri aData) {
		if(aData != null && aData.getQueryParameter(QUERYSTRING_KEY_EVENTPOOLID) != null) {
			eventPoolId = Long.parseLong(aData.getQueryParameter(QUERYSTRING_KEY_EVENTPOOLID));
			System.out.println("External start with intent to display pool " + eventPoolId);
			if(aData.getQueryParameter(QUERYSTRING_KEY_TOKEN) != null) {
				System.out.println("Got also a token :-)");
				mModel.setToken(aData.getQueryParameter(QUERYSTRING_KEY_TOKEN));
				mController.refreshEventPool(this, eventPoolId, false);
			} else if(aData.getQueryParameter(QUERYSTRING_KEY_EXCHANGETOKEN) != null) {
				System.out.println("Got request to exchange contacts");
				if(mModel.getToken() != null)
					mController.exchangeContacts(this, aData.getQueryParameter(QUERYSTRING_KEY_EXCHANGETOKEN));
			}
		}
	}
	
	@Override
	public void eventPoolsUpdated(List<Long> updated) {
		
		if(updated != null && !updated.contains(eventPoolId))
			return;
		
		System.out.println("EventsMainView::eventPoolsUpdated eventPoolId=" + eventPoolId + " obj=" + this);
		
		//System.out.println("eventsListUpdated getting pool");
		thisEventPool = mModel.getEventPool(eventPoolId);
		if(thisEventPool == null || thisEventPool.getChildrenEvents() == null)
			return; // Ow!
		
		
		//System.out.println("eventsListUpdated building hash childrenEvent=" + parentEvent.getChildrenEvents().size());
		eventsByCateg = new HashMap<Integer, List<EventItem>>();
		eventsInRS.clear();
		categsInRS.clear();
		tagsInRS.clear();
		for(long eventId : thisEventPool.getChildrenEvents()) {
			EventItem e = mModel.getEventItem(eventId);
			//e.setEventCateg(1);
			if(e == null)
				continue;
			if(!e.isSetEventCateg())
				e.setEventCateg(1000000); // uncategorized
			if(!e.isSetEventTags() || e.getEventTags().size() == 0)
				e.setEventTags(oneItemList("unlabeled")); // unlabeled
			eventsInRS.add(eventId);
			categsInRS.add(e.getEventCateg());
			if(e.isSetEventTags()) 
				tagsInRS.addAll(e.getEventTags());
			if(!eventsByCateg.containsKey(e.getEventCateg()))
				eventsByCateg.put(e.getEventCateg(), new LinkedList<EventItem>());
			eventsByCateg.get(e.getEventCateg()).add(e);
		}
		
		filteredCategs = new HashSet<Integer>(categsInRS);
		filteredTags = new HashSet<String>(tagsInRS);
		
		updateDisplay(false);
	}
	
	/*private List<EventItem> filterByTags(List<EventItem> events) {
		for()
		
	}*/
	
	private void updateDisplay(boolean saveScroll) {

		if(saveScroll && displayingList)
			scrollState = new ScrollStateSaver(mList);
		
		SeparatedListAdapter adapter = new SeparatedListAdapter(this, R.layout.event_list_header);
		List<Integer> categList = new LinkedList<Integer>(eventsByCateg.keySet());
		Collections.sort(categList);
		for(int i : categList) {
			if(!filteredCategs.contains(i))
				continue;
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
						return (e.isSetEventPlace() ? e.getEventPlace() : e.getEventSpeaker());
					case R.id.event_thumbnail:
						return e.getEventThumbnail();
					case R.id.event_time:
						if(!e.isSetStartDate())
							return null;
						String startTime = simpleTimeFormat.format(new Date(e.getStartDate()));
						String startDay = simpleDateFormat.format(new Date(e.getStartDate()));
						if(e.isFullDay())
							return startDay;
						else
							return startDay + " - " + startTime;
					case R.id.event_fav_star:
						if(thisEventPool.isDisableStar())
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
		
		if(eventsInRS.size() == 0 && thisEventPool.isSetNoResultText()) {
			displayingList = false;
			StandardLayout sl = new StandardLayout(this);
			sl.setText(thisEventPool.getNoResultText());
			setContentView(sl);
		} else {
			if(!displayingList) {
				setContentView(R.layout.events_main);
				mList = (ListView) findViewById(R.id.events_main_list);
				displayingList = true;
			}
			mList.setAdapter(adapter);
			//mList.setCacheColorHint(Color.TRANSPARENT);
			//mList.setFastScrollEnabled(true);
			//mList.setScrollingCacheEnabled(false);
			//mList.setPersistentDrawingCache(ViewGroup.PERSISTENT_SCROLLING_CACHE);
			
			mList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
			
			mList.setOnItemClickListener(new OnItemClickListener() {
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
			
			if(scrollState != null)
				scrollState.restore(mList);
			
		}
	}
	
	@Override
	public void eventItemsUpdated(List<Long> updated) {
		if(intersect(eventsInRS, updated).size() > 0)
			eventPoolsUpdated(null);
	}
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		boolean showScanBarcode = false;
		boolean showFilterCateg = true;
		boolean showFilterTags = true;
		EventPool pool = mModel.getEventPool(eventPoolId);
		if(pool != null) {
			showScanBarcode = pool.isEnableScan();
			showFilterCateg = !pool.isDisableFilterByCateg();
			showFilterTags = !pool.isDisableFilterByTags();
		}
		if(showScanBarcode) {
			MenuItem scanMenu = menu.add("Scan barcode");
			scanMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					IntentIntegrator integrator = new IntentIntegrator(EventsMainView.this);
					integrator.initiateScan();
					return true;
				}
			});
		}
		if(showFilterCateg) {
			MenuItem categMenu = menu.add("Filter by category");
			categMenu.setOnMenuItemClickListener(buildMenuListenerMultiChoiceDialog(this, 
					subMap(Constants.EVENTS_CATEGS, categsInRS), "Choose Category", filteredCategs,
					new MultiChoiceHandler<Integer>() {
						public void saveSelection(Integer t, boolean isChecked) {
							if(isChecked)
								filteredCategs.add(t);
							else
								filteredCategs.remove(t);
							updateDisplay(true);
						}
					}
			));
		}
		if(showFilterTags) {
			MenuItem feedMenu = menu.add("Filter by tag(s)");
			feedMenu.setOnMenuItemClickListener(buildMenuListenerMultiChoiceDialog(this,
					subMap(Constants.EVENTS_TAGS, tagsInRS), "Choose Tag(s)", filteredTags,
					new MultiChoiceHandler<String>() {
						public void saveSelection(String t, boolean isChecked) {
							if(isChecked)
								filteredTags.add(t);
							else
								filteredTags.remove(t);
							updateDisplay(true);
						}
					}
			));
		}
		if(eventPoolId == Constants.CONTAINER_EVENT_ID) { // settings thingy
			MenuItem periodMenu = menu.add("Choose a period");
			periodMenu.setOnMenuItemClickListener(buildMenuListenerSingleChoiceDialog(this,
					Constants.EVENTS_PERIODS, "Choose Period", mModel.getPeriod(), 
					new SingleChoiceHandler<Integer>() {
						public void saveSelection(Integer t) {
							mModel.setPeriod(t);
							mController.refreshEventPool(EventsMainView.this, eventPoolId, false);
						}
					}
			));
		}
		return true;
	}
	
	@Override
	public void networkErrorCacheExists() {
		Toast.makeText(getApplicationContext(), getResources().getString(
				R.string.sdk_connection_no_cache_yes), Toast.LENGTH_SHORT).show();
		mController.refreshEventPool(this, eventPoolId, true);
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
	public void exchangeContactsFinished(boolean success) {
		if(success) {
			mController.refreshEventPool(this, eventPoolId, false);
			Toast.makeText(getApplicationContext(), 
					"Successfully exchanged contacts information", 
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), 
					"Failed to exchange contact information", 
					Toast.LENGTH_SHORT).show();
		}
	}

}
