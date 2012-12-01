package org.pocketcampus.plugin.events.android;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.pocketcampus.plugin.events.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.android.platform.sdk.ui.list.FeedListViewElement;
import org.pocketcampus.plugin.events.android.iface.IEventsModel;
import org.pocketcampus.plugin.events.android.iface.IEventsView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 */
public class EventsMainView extends PluginView implements IEventsView {
	private EventsController mController;
	private IEventsModel mModel;

	private StandardTitledLayout mLayout;
	private FeedListViewElement mListView;

	private OnItemClickListener mOnItemClickListener;

	/**
	 * Defines what the main controller is for this view. This is optional, some
	 * view may not need a controller (see for example the dashboard).
	 * 
	 * This is only a shortcut for what is done in
	 * <code>getOtherController()</code> below: if you know you'll need a
	 * controller before doing anything else in this view, you can define it as
	 * you're main controller so you know it'll be ready as soon as
	 * <code>onDisplay()</code> is called.
	 */
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return EventsController.class;
	}

	/**
	 * Called once the view is connected to the controller. If you don't
	 * implement <code>getMainControllerClass()</code> then the controller given
	 * here will simply be <code>null</code>.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		//Tracker
//		Tracker.getInstance().trackPageView("events");
		
		// Get and cast the controller and model
		mController = (EventsController) controller;
		mModel = (EventsModel) controller.getModel();

		// The StandardLayout is a RelativeLayout with a TextView in its center.
		mLayout = new StandardTitledLayout(this, null);

		mLayout.setTitle(getString(R.string.events_plugin_title));

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);

		// We need to force the display before asking the controller for the
		// data,
		// as the controller may take some time to get it.
		displayData();
	}

	/**
	 * Initiates request for events items
	 */
	private void displayData() {
		mLayout.setText(getResources().getString(R.string.events_loading));
		mLayout.hideTitle();
		mController.getEventItems();
	}

	@Override
	public void eventsUpdated() {
		List<EventsItemWithSpanned> eventsList = mModel.getEvents(this);
		mLayout.removeFillerView();
		mLayout.hideTitle();
		if (eventsList != null) {
			if (!eventsList.isEmpty()) {
				// Add them to the listView
				mListView = new FeedListViewElement(this, eventsList,
						mEventsItemLabeler);

				// Set onClickListener
				setOnListViewClickListener();

				// Set the layout
				mLayout.addFillerView(mListView);

				mLayout.setText("");
			} else {
				mLayout.setText(getString(R.string.events_no_feed_selected));
			}
		} else {
			mLayout.setText(getString(R.string.events_no_events));
		}
	}

	@Override
	public void feedUrlsUpdated() {

		Intent settings = new Intent(getApplicationContext(),
				EventsPreferences.class);
		System.out.println(mModel.getFeedsUrls().size());
		settings.putExtra("org.pocketcampus.events.feedUrls",
				(HashMap<String, String>) mModel.getFeedsUrls());
		startActivity(settings);
	}

	/**
	 * Main Food Options menu contains access to Meals by restaurants, ratings,
	 * Sandwiches, Suggestions and Settings
	 */
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.events_menu, menu);
//		return true;
//	}

	/**
	 * Decides what happens when the options menu is opened and an option is
	 * chosen (what view to display)
	 */
//	@Override
//	public boolean onOptionsItemSelected(android.view.MenuItem item) {
//		if (item.getItemId() == R.id.events_menu_settings) {
//			mController.getFeedUrls();
//		}
//		return true;
//	}

	@Override
	public void networkErrorHappened() {
		//Tracker
//		Tracker.getInstance().trackPageView("events/network_error");
		
		mLayout.removeFillerView();
		mLayout.hideTitle();
		mLayout.setText(getString(R.string.events_no_events));
	}

	/* Sets the clickLIstener of the listView */
	private void setOnListViewClickListener() {

		mOnItemClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
					int position, long arg3) {
				Intent events = new Intent(getApplicationContext(),
						EventsItemView.class);
				EventsItemWithSpanned toPass = mModel.getEvents(
						EventsMainView.this).get(position);
				events.putExtra("org.pocketcampus.events.eventsitem.title",
						toPass.getEventsItem().getTitle());
				events.putExtra(
						"org.pocketcampus.events.eventsitem.description",
						toPass.getFormattedDescription());
				events.putExtra("org.pocketcampus.events.eventsitem.feed",
						toPass.getEventsItem().getFeed());

				DateFormat df = new SimpleDateFormat("EEEE dd MMMM yyyy");

				String info = "";

				// Format date
				long startDateLong = toPass.getEventsItem().getStartDate();
				long endDateLong = toPass.getEventsItem().getEndDate();
				if (startDateLong != 0) {
					Date startDate = new Date(startDateLong);
					if (endDateLong != 0 && startDateLong != endDateLong) {
						Date endDate = new Date(endDateLong);

						info = getString(R.string.events_from) + " "
								+ bold(df.format(startDate)) + " "
								+ getString(R.string.events_to) + " "
								+ bold(df.format(endDate));
					} else {
						info = getString(R.string.events_on) + " "
								+ bold(df.format(startDate));
					}

					if (!(toPass.getEventsItem().getStartTime()).equals("")) {
						DateFormat time = new SimpleDateFormat("hh:mm");

						try {
							Date startTimeDate = time.parse(toPass
									.getEventsItem().getStartTime());
							String startTimeString = time.format(startTimeDate);

							info = info + " " + getString(R.string.events_at)
									+ " " + startTimeString;
						} catch (ParseException e) {
						}

					}

				}
				if (!(toPass.getEventsItem().getSpeaker()).equals("")) {
					info = info + "<br>" + getString(R.string.events_speaker)
							+ " " + bold(toPass.getEventsItem().getSpeaker());
				}

				if (!(toPass.getEventsItem().getRoom()).equals("")) {
					info = info + "<br>" + getString(R.string.events_room)
							+ " " + toPass.getEventsItem().getRoom();
				} else if (!(toPass.getEventsItem().getLocation()).equals("")) {
					info = info + "<br>" + getString(R.string.events_location)
							+ " " + bold(toPass.getEventsItem().getLocation());
				}

				events.putExtra("org.pocketcampus.events.eventsitem.info", info);

				//Tracker
//				Tracker.getInstance().trackPageView("events/click/" + toPass.getEventsItem().getTitle());
				
				startActivity(events);
			}
		};
		mListView.setOnItemClickListener(mOnItemClickListener);
	}

	private String bold(String toBoldify) {
		return "<b>" + toBoldify + "</b>";
	}

	/**
	 * The labeler for a feed, to tell how it has to be displayed in a generic
	 * view.
	 */
	ILabeler<EventsItemWithSpanned> mEventsItemLabeler = new ILabeler<EventsItemWithSpanned>() {

		@Override
		public String getLabel(EventsItemWithSpanned obj) {
			return obj.getEventsItem().getTitle();
		}
	};

}
