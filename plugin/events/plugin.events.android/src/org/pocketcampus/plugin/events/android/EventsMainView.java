package org.pocketcampus.plugin.events.android;

import static org.pocketcampus.platform.android.utils.DialogUtils.showInputDialog;
import static org.pocketcampus.platform.android.utils.DialogUtils.showMultiChoiceDialog;
import static org.pocketcampus.platform.android.utils.DialogUtils.showSingleChoiceDialog;
import static org.pocketcampus.platform.android.utils.MapUtils.subMap;
import static org.pocketcampus.platform.android.utils.SetUtils.difference;
import static org.pocketcampus.platform.android.utils.SetUtils.intersect;
import static org.pocketcampus.plugin.events.android.EventsController.getEventItemComp4sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter;
import org.pocketcampus.platform.android.ui.adapter.SeparatedListAdapter2;
import org.pocketcampus.platform.android.ui.layout.StandardLayout;
import org.pocketcampus.platform.android.utils.DialogUtils.MultiChoiceHandler;
import org.pocketcampus.platform.android.utils.DialogUtils.SingleChoiceHandler;
import org.pocketcampus.platform.android.utils.DialogUtils.TextInputHandler;
import org.pocketcampus.platform.android.utils.Preparated;
import org.pocketcampus.platform.android.utils.Preparator;
import org.pocketcampus.platform.android.utils.ScrollStateSaver;
import org.pocketcampus.plugin.events.R;
import org.pocketcampus.plugin.events.android.iface.IEventsView;
import org.pocketcampus.plugin.events.shared.Constants;
import org.pocketcampus.plugin.events.shared.EventItem;
import org.pocketcampus.plugin.events.shared.EventPool;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.markupartist.android.widget.Action;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

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
	public static final String QUERYSTRING_KEY_TICKET = "userTicket";
	public static final String QUERYSTRING_KEY_EXCHANGETOKEN = "exchangeToken";
	public static final String QUERYSTRING_KEY_TEMPLATEID = "templateId";
	public static final String QUERYSTRING_KEY_MARKFAVORITE = "markFavorite";
	public static final String MAP_KEY_EVENTITEMID = "EVENT_ITEM_ID";
	public static final String MAP_KEY_EVENTITEMTITLE = "EVENT_ITEM_TITLE";

	private boolean displayingList;

	private long eventPoolId;
	private boolean happeningNow = false;
	private boolean fetchPast = false;
	private List<Long> eventsInRS = new LinkedList<Long>();
	private Set<Integer> categsInRS = new HashSet<Integer>();
	private Set<String> tagsInRS = new HashSet<String>();

	EventPool thisEventPool;
	Map<String, List<EventItem>> eventsByTags;
	Set<Integer> filteredCategs = new HashSet<Integer>();
	Set<String> filteredTags = new HashSet<String>();

	StickyListHeadersListView mList;
	ScrollStateSaver scrollState;
	ProgressDialog loading;

	protected Class<? extends PluginController> getMainControllerClass() {
		return EventsController.class;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {

		// Get and cast the controller and model
		mController = (EventsController) controller;
		mModel = (EventsModel) controller.getModel();

		setActionBarTitle(getString(R.string.events_plugin_title));
	}

	/**
	 * Handles the intent that was used to start this plugin.
	 * 
	 * We need to read the Extras.
	 */
	@Override
	protected void handleIntent(Intent aIntent) {
		eventPoolId = Constants.CONTAINER_EVENT_ID;
		boolean processedIntent = false;
		if (aIntent != null) {
			Bundle aExtras = aIntent.getExtras();
			Uri aData = aIntent.getData();
			if (aExtras != null && aExtras.containsKey(EXTRAS_KEY_EVENTPOOLID)) {
				eventPoolId = Long.parseLong(aExtras.getString(EXTRAS_KEY_EVENTPOOLID));
				System.out.println("Started with intent to display pool " + eventPoolId);
				mController.refreshEventPool(this, eventPoolId, happeningNow, fetchPast, false);
				processedIntent = true;
			} else if (aData != null && aData.getQueryParameter(QUERYSTRING_KEY_EVENTPOOLID) != null) {
				eventPoolId = Long.parseLong(aData.getQueryParameter(QUERYSTRING_KEY_EVENTPOOLID));
				System.out.println("External start with intent to display pool " + eventPoolId);
				externalCall(aData);
				processedIntent = true;
			}
		}
		if (!processedIntent)
			mController.refreshEventPool(this, eventPoolId, happeningNow, fetchPast, false);

	}

	@Override
	protected String screenName() {
		return "/events/pool";
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (displayingList && scrollState != null)
			scrollState.restore(mList);
		if (thisEventPool != null && thisEventPool.isRefreshOnBack())
			mController.refreshEventPool(this, eventPoolId, happeningNow, fetchPast, false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (displayingList && mList != null)
			scrollState = new ScrollStateSaver(mList);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		System.out.println("back from barcode scanner");
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null && "QR_CODE".equals(scanResult.getFormatName())) {
			// filteredCategs = null; // no need to reset them
			// filteredTags = null; // because they will get updated
			externalCall(Uri.parse(scanResult.getContents()));
		}
	}

	private void externalCall(Uri aData) {
		if (aData == null)
			return;
		if (aData.getQueryParameter(QUERYSTRING_KEY_TICKET) != null) {
			System.out.println("Got also a token :-)");
			String ticket = aData.getQueryParameter(QUERYSTRING_KEY_TICKET);
			trackEvent("UserTicketInURL", ticket);
			mModel.addTicket(ticket);
			mController.refreshEventPool(this, eventPoolId, happeningNow, fetchPast, false);
			return;
		}
		if (aData.getQueryParameter(QUERYSTRING_KEY_MARKFAVORITE) != null) {
			System.out.println("Should mark as favorite");
			String fav = aData.getQueryParameter(QUERYSTRING_KEY_MARKFAVORITE);
			trackEvent("MarkFavoriteInURL", fav);
			mModel.markFavorite(Long.parseLong(fav), true);
			Intent i = new Intent(EventsMainView.this, EventDetailView.class);
			i.putExtra(EventDetailView.EXTRAS_KEY_EVENTITEMID, fav);
			EventsMainView.this.startActivity(i);
		}
		if (aData.getQueryParameter(QUERYSTRING_KEY_EXCHANGETOKEN) != null) {
			System.out.println("Got request to exchange contacts");
			mController.exchangeContacts(this, aData.getQueryParameter(QUERYSTRING_KEY_EXCHANGETOKEN));
			return;
		}
		if (aData.getQueryParameter(QUERYSTRING_KEY_TEMPLATEID) != null) {
			System.out.println("Got request to send reg emails");
			mController.adminSendRegEmails(this, aData.getQueryParameter(QUERYSTRING_KEY_TEMPLATEID));
		}
		mController.refreshEventPool(this, eventPoolId, happeningNow, fetchPast, false);
	}

	@Override
	public void eventPoolsUpdated(List<Long> updated) {

		// The ActionBar is added automatically when you call setContentView
		// disableActionBar();
		setContentView(R.layout.events_main);
		mList = (StickyListHeadersListView) findViewById(R.id.events_main_list);
		displayingList = true;

		if (updated != null && !updated.contains(eventPoolId))
			return;

		System.out.println("EventsMainView::eventPoolsUpdated eventPoolId=" + eventPoolId + " obj=" + this);

		// System.out.println("eventsListUpdated getting pool");
		thisEventPool = mModel.getEventPool(eventPoolId);
		if (thisEventPool == null)
			return; // Ow!

		// System.out.println("eventsListUpdated building hash childrenEvent=" +
		// parentEvent.getChildrenEvents().size());
		eventsByTags = new HashMap<String, List<EventItem>>();
		eventsInRS.clear();
		Set<Integer> newCategsInRS = new HashSet<Integer>();
		Set<String> newTagsInRS = new HashSet<String>();
		if (thisEventPool.isSetChildrenEvents()) {
			for (long eventId : thisEventPool.getChildrenEvents()) {
				EventItem e = mModel.getEventItem(eventId);
				// e.setEventCateg(1);
				if (e == null)
					continue;
				eventsInRS.add(eventId);
				if (e.getEventCateg() > 0)
					newCategsInRS.add(e.getEventCateg());
				if (e.isSetEventTags())
					newTagsInRS.addAll(e.getEventTags());
				for (String t : e.getEventTags()) {
					if (!eventsByTags.containsKey(t))
						eventsByTags.put(t, new LinkedList<EventItem>());
					eventsByTags.get(t).add(e);
				}
			}
		}

		filteredCategs.addAll(difference(newCategsInRS, categsInRS)); // if new
																		// categories
																		// appeared,
																		// then
																		// add
																		// them
																		// to
																		// filtered
																		// because
																		// otherwise
																		// they
																		// might
																		// go
																		// unnoticed
		filteredTags.addAll(difference(newTagsInRS, tagsInRS)); // if new tags
																// appeared,
																// then add them
																// to filtered
																// because
																// otherwise
																// they might go
																// unnoticed
		categsInRS = newCategsInRS;
		tagsInRS = newTagsInRS;

		updateActionBar();

		updateDisplay(false);
	}

	private void updateActionBar() {
		removeAllActionsFromActionBar();
		// if(eventPoolId == Constants.CONTAINER_EVENT_ID) {
		// addActionToActionBar(new Action() {
		// public void performAction(View view) {
		// happeningNow = !happeningNow;
		// trackEvent((happeningNow ? "SwitchToHapenningNowEvents" :
		// "SwitchBackToAllEvents"), null);
		// mController.refreshEventPool(EventsMainView.this, eventPoolId,
		// happeningNow, fetchPast, false);
		// }
		// public int getDrawable() {
		// return (happeningNow ? R.drawable.events_happening_now_sel1 :
		// R.drawable.events_happening_now1);
		// }
		// });
		// }
		if (!thisEventPool.isDisableFilterByCateg()) {
			final Map<Integer, String> subMap = subMap(Constants.EVENTS_CATEGS, categsInRS);
			if (subMap.size() > 0) {
				addActionToActionBar(new Action() {
					public void performAction(View view) {
						trackEvent("ShowCategories", null);
						showMultiChoiceDialog(EventsMainView.this, subMap, getString(R.string.events_filter_by_categ),
								filteredCategs, new MultiChoiceHandler<Integer>() {
									public void saveSelection(Integer t, boolean isChecked) {
										if (isChecked)
											filteredCategs.add(t);
										else
											filteredCategs.remove(t);
										updateDisplay(true);
									}
								});
					}

					public int getDrawable() {
						return R.drawable.pocketcampus_filter;
					}

					@Override
					public String getDescription() {
						return getString(R.string.events_filter_by_categ);
					}
				});
			}
		}
		if (!thisEventPool.isDisableFilterByTags()) {
			final Map<String, String> subMap = subMap(Constants.EVENTS_TAGS, tagsInRS);
			if (subMap.size() > 0) {
				addActionToActionBar(new Action() {
					public void performAction(View view) {
						trackEvent("ShowTags", null);
						showMultiChoiceDialog(EventsMainView.this, subMap, getString(R.string.events_filter_by_tags),
								filteredTags, new MultiChoiceHandler<String>() {
									public void saveSelection(String t, boolean isChecked) {
										if (isChecked)
											filteredTags.add(t);
										else
											filteredTags.remove(t);
										updateDisplay(true);
									}
								});
					}

					public int getDrawable() {
						return R.drawable.pocketcampus_tags;
					}

					@Override
					public String getDescription() {
						return getString(R.string.events_filter_by_tags);
					}
				});
			}
		}
		if (thisEventPool.isEnableScan()) {
			addActionToActionBar(new Action() {
				public void performAction(View view) {
					trackEvent("ShowCodeScanner", null);
					IntentIntegrator integrator = new IntentIntegrator(EventsMainView.this);
					integrator.initiateScan();
				}

				public int getDrawable() {
					return R.drawable.events_camera;
				}

				@Override
				public String getDescription() {
					return getString(R.string.events_camera);
				}
			});
		}
		if (thisEventPool.isSendStarredItems()) {
			addActionToActionBar(new Action() {
				public void performAction(View view) {
					trackEvent("RequestEmail", null);
					showInputDialog(EventsMainView.this, getString(R.string.events_email_popup_title),
							getString(R.string.events_email_popup_body), getString(R.string.events_ok),
							new TextInputHandler() {
								public void gotText(String s) {
									mController.sendFavoritesByEmail(EventsMainView.this, eventPoolId, s);
								}
							});
				}

				public int getDrawable() {
					return R.drawable.sdk_email;
				}

				@Override
				public String getDescription() {
					return getString(R.string.events_email);
				}
			});
		}

		if (happeningNow)
			return;
		if (eventPoolId == Constants.CONTAINER_EVENT_ID) { // settings thingy
			addActionToActionBar(new Action() {
				@Override
				public void performAction(View view) {
					showSingleChoiceDialog(EventsMainView.this, Constants.EVENTS_PERIODS, "Choose period",
							mModel.getPeriodInHours(), new SingleChoiceHandler<Integer>() {
								public void saveSelection(Integer t) {
									mModel.setPeriodInHours(t);
									trackEvent("ChangePeriod", "" + t);
									mController.refreshEventPool(EventsMainView.this, eventPoolId, happeningNow,
											fetchPast, false);
								}
							});
				}

				@Override
				public int getDrawable() {
					return R.drawable.events_menu_choose_period;
				}

				@Override
				public String getDescription() {
					return getString(R.string.events_choose_period);
				}
			});

			addActionToActionBar(new Action() {

				@Override
				public void performAction(View view) {
					fetchPast = !fetchPast;
					trackEvent((fetchPast ? "SwitchToPastEvents" : "SwitchBackToUpcomingEvents"), null);
					if (fetchPast) {
						setActionBarTitle(getString(R.string.events_past_events_title));
					} else {
						setActionBarTitle(getString(R.string.events_plugin_title));
					}
					mController.refreshEventPool(EventsMainView.this, eventPoolId, happeningNow, fetchPast, false);
				}

				@Override
				public int getDrawable() {
					return fetchPast ? R.drawable.events_future_events : R.drawable.events_past_events;
				}

				@Override
				public String getDescription() {
					return getString(R.string.events_past_events);
				}
			});
		}

	}

	private void updateDisplay(boolean saveScroll) {

		if (saveScroll && displayingList)
			scrollState = new ScrollStateSaver(mList);

		Set<EventItem> filteredEvents = new HashSet<EventItem>();
		for (String tag : filteredTags) {
			List<EventItem> tagEvents = eventsByTags.get(tag);
			if (tagEvents == null) // if tag becomes empty (shorter period
									// selected)
				continue; // then skip it
			filteredEvents.addAll(tagEvents);
		}

		// Map<Integer, List<EventItem>> eventsByCateg = new HashMap<Integer,
		// List<EventItem>>();
		SparseArray<List<EventItem>> eventsByCateg = new SparseArray<List<EventItem>>();

		for (EventItem e : filteredEvents) {
			if (e.getEventCateg() < 0)
				filteredCategs.add(e.getEventCateg()); // make sure special
														// categs are always
														// displayed
			if (eventsByCateg.indexOfKey(e.getEventCateg()) < 0)
				eventsByCateg.put(e.getEventCateg(), new LinkedList<EventItem>());
			eventsByCateg.get(e.getEventCateg()).add(e);
		}

		SeparatedListAdapter2 adapter = new SeparatedListAdapter2(this, R.layout.sdk_separated_list_header2);
		List<Integer> categList = new ArrayList<Integer>(filteredCategs);
		Collections.sort(categList);
		for (int i : categList) {
			List<EventItem> categEvents = eventsByCateg.get(i);
			if (categEvents == null) // if category becomes empty (filtering by
										// tags)
				continue; // then skip it
			Collections.sort(categEvents, getEventItemComp4sort(fetchPast));
			Preparated<EventItem> p = new Preparated<EventItem>(categEvents, new Preparator<EventItem>() {
				public int[] resources() {
					return new int[] { R.id.event_title, R.id.event_speaker, R.id.event_thumbnail, R.id.event_time /*
																													 * ,
																													 * R
																													 * .
																													 * id
																													 * .
																													 * event_fav_star
																													 */};
				}

				public Object content(int res, final EventItem e) {
					switch (res) {
					case R.id.event_title:
						return e.getEventTitle();
					case R.id.event_speaker:
						return (e.isSetSecondLine() ? e.getSecondLine() : (e.isSetEventPlace() ? e.getEventPlace() : e
								.getEventSpeaker()));
					case R.id.event_thumbnail:
						return e.getEventThumbnail();
					case R.id.event_time:
						if (e.isSetTimeSnippet())
							return e.getTimeSnippet();
						if (!e.isSetStartDate())
							return null;
						String startTime = EventsController.getTimeFormat(EventsMainView.this).format(
								new Date(e.getStartDate()));
						String startDay = EventsController.getDateFormat(EventsMainView.this).format(
								new Date(e.getStartDate()));
						if (e.isFullDay())
							return startDay;
						else
							return startDay + " - " + startTime;
						// case R.id.event_fav_star:
						// if (thisEventPool.isDisableStar())
						// return R.drawable.sdk_transparent;
						// Integer fav = R.drawable.sdk_star_off;
						// if (e.getEventCateg() == -2)
						// fav = R.drawable.sdk_star_on;
						// return new Actuated(fav, new Actuator() {
						// public void triggered() {
						// System.out.println("toggle fav event: "
						// + e.getEventTitle());
						// scrollState = null;
						// mModel.markFavorite(e.getEventId(),
						// (e.getEventCateg() != -2));
						// }
						// });
					default:
						return null;
					}
				}

				public void finalize(Map<String, Object> map, EventItem item) {
					map.put(MAP_KEY_EVENTITEMID, item.getEventId() + "");
					map.put(MAP_KEY_EVENTITEMTITLE, item.getEventTitle());
				}
			});
			LazyAdapter lazyAdapter = new LazyAdapter(this, p.getMap(), R.layout.events_list_row, p.getKeys(),
					p.getResources());
			lazyAdapter.setStubImage(R.drawable.events_icon);
			lazyAdapter.setNoImage(R.drawable.events_icon);
			lazyAdapter.setImageOnFail(R.drawable.events_icon);
			adapter.addSection(Constants.EVENTS_CATEGS.get(i), lazyAdapter);
		}

		if (eventsInRS.size() == 0 && thisEventPool.isSetNoResultText()) {
			displayingList = false;
			StandardLayout sl = new StandardLayout(this);
			sl.setText(thisEventPool.getNoResultText());
			setContentView(sl);
		} else {
			if (!displayingList) {
				setContentView(R.layout.events_main);
				mList = (StickyListHeadersListView) findViewById(R.id.events_main_list);
				displayingList = true;
			}
			mList.setAdapter(adapter);
			// mList.setCacheColorHint(Color.TRANSPARENT);
			// mList.setFastScrollEnabled(true);
			// mList.setScrollingCacheEnabled(false);
			// mList.setPersistentDrawingCache(ViewGroup.PERSISTENT_SCROLLING_CACHE);
			// mList.setDivider(null);
			// mList.setDividerHeight(0);

			mList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

			mList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Object o = arg0.getItemAtPosition(arg2);
					if (o instanceof Map<?, ?>) {
						String eId = ((Map<?, ?>) o).get(MAP_KEY_EVENTITEMID).toString();
						String eTitle = ((Map<?, ?>) o).get(MAP_KEY_EVENTITEMTITLE).toString();
						Intent i = new Intent(EventsMainView.this, EventDetailView.class);
						i.putExtra(EventDetailView.EXTRAS_KEY_EVENTITEMID, eId);
						i.putExtra(EventDetailView.EXTRAS_KEY_DISABLESTAR, thisEventPool.isDisableStar());
						EventsMainView.this.startActivity(i);
						trackEvent("ShowEventItem", eId + "-" + eTitle);
					} else {
						Toast.makeText(getApplicationContext(), o.toString(), Toast.LENGTH_SHORT).show();
					}
				}
			});

			if (scrollState != null)
				scrollState.restore(mList);

		}
	}

	@Override
	public void eventItemsUpdated(List<Long> updated) {
		if (intersect(eventsInRS, updated).size() > 0)
			eventPoolsUpdated(null);
	}

	// @Override
	// public boolean onPrepareOptionsMenu(Menu menu) {
	// menu.clear();
	// if(happeningNow)
	// return true;
	// if(eventPoolId == Constants.CONTAINER_EVENT_ID) { // settings thingy
	// MenuItem periodMenu = menu.add("Choose period");
	// periodMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
	// public boolean onMenuItemClick(MenuItem item) {
	// showSingleChoiceDialog(EventsMainView.this, Constants.EVENTS_PERIODS,
	// "Choose period", mModel.getPeriodInHours(), new
	// SingleChoiceHandler<Integer>() {
	// public void saveSelection(Integer t) {
	// mModel.setPeriodInHours(t);
	// trackEvent("ChangePeriod", "" + t);
	// mController.refreshEventPool(EventsMainView.this, eventPoolId,
	// happeningNow, fetchPast, false);
	// }
	// });
	// return true;
	// }
	// });
	// MenuItem pastMenu = menu.add(fetchPast ? "View upcoming events" :
	// "View past events");
	// pastMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
	// public boolean onMenuItemClick(MenuItem item) {
	// fetchPast = !fetchPast;
	// trackEvent((fetchPast ? "SwitchToPastEvents" :
	// "SwitchBackToUpcomingEvents"), null);
	// mController.refreshEventPool(EventsMainView.this, eventPoolId,
	// happeningNow, fetchPast, false);
	// return true;
	// }
	// });
	// }
	// return true;
	// }

	
	@Override
	public synchronized void showLoading() {
		hideLoading();
		loading = ProgressDialog.show(this, null, getString(R.string.sdk_loading), true, false);
	}

	@Override
	public synchronized void hideLoading() {
		if(loading != null) {
			loading.dismiss();
			loading = null;
		}
	}

	@Override
	public void networkErrorCacheExists() {
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.sdk_connection_no_cache_yes),
				Toast.LENGTH_SHORT).show();
		mController.refreshEventPool(this, eventPoolId, happeningNow, fetchPast, true);
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
		if (success) {
			mController.refreshEventPool(this, eventPoolId, happeningNow, fetchPast, false);
			Toast.makeText(getApplicationContext(), "Successfully exchanged contacts information", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(getApplicationContext(), "Failed to exchange contact information", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void sendEmailRequestFinished(boolean success) {
		if (success) {
			Toast.makeText(getApplicationContext(), getString(R.string.events_email_success), Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.events_email_failure), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void sendAdminRegEmailFinished(boolean success) {
		Toast.makeText(getApplicationContext(), (success ? getString(R.string.events_email_success) : getString(R.string.events_email_failure)),
				Toast.LENGTH_SHORT).show();
	}

}
