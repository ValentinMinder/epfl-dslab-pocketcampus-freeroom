package org.pocketcampus.plugin.events.android;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.android.platform.sdk.ui.list.FeedListViewElement;
import org.pocketcampus.plugin.events.android.iface.IEventsModel;
import org.pocketcampus.plugin.events.android.iface.IEventsView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AbsListView.LayoutParams;
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

		// Get and cast the controller and model
		mController = (EventsController) controller;
		mModel = (EventsModel) controller.getModel();

		// The StandardLayout is a RelativeLayout with a TextView in its center.
		mLayout = new StandardTitledLayout(this, null);

		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		mLayout.setLayoutParams(layoutParams);
		mLayout.setGravity(Gravity.CENTER_VERTICAL);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);

		// We need to force the display before asking the controller for the
		// data,
		// as the controller may take some time to get it.
		displayData();
	}

	/**
	 * Called when this view is accessed after already having been initialized
	 * before
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d("ACTIVITY", "onRestart");
		eventsUpdated();
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
		List<EventsItemWithImage> eventsList = mModel.getEvents(this);
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.events_menu, menu);
		return true;
	}

	/**
	 * Decides what happens when the options menu is opened and an option is
	 * chosen (what view to display)
	 */
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		if (item.getItemId() == R.id.events_menu_settings) {
			mController.getFeedUrls();
		}
		return true;
	}

	@Override
	public void networkErrorHappened() {
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
				EventsItemWithImage toPass = mModel.getEvents(
						EventsMainView.this).get(position);
				events.putExtra("org.pocketcampus.events.eventsitem.title",
						toPass.getEventsItem().getTitle());
				events.putExtra(
						"org.pocketcampus.events.eventsitem.description",
						toPass.getFormattedDescription());
				events.putExtra("org.pocketcampus.events.eventsitem.feed",
						toPass.getEventsItem().getFeed());

				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

				if (toPass.getEventsItem().getStartDate() != 0) {
					Date startDate = new Date(toPass.getEventsItem()
							.getStartDate());
					String date;
					if(toPass.getEventsItem().getEndDate() != 0){
						Date endDate = new Date(toPass.getEventsItem()
								.getEndDate());
						date = getString(R.string.events_from)+" "+df.format(startDate)+" "+getString(R.string.events_to)+" "+df.format(endDate);
					} else {

						date = getString(R.string.events_on)+" "+df.format(startDate);
					}
					events.putExtra("org.pocketcampus.events.eventsitem.date", date
							);
				}
				startActivity(events);
			}
		};
		mListView.setOnItemClickListener(mOnItemClickListener);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// eventsProvider_.refreshIfNeeded();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// eventsProvider_.removeeventsListener(this);
	}

	/**
	 * The labeler for a feed, to tell how it has to be displayed in a generic
	 * view.
	 */
	ILabeler<EventsItemWithImage> mEventsItemLabeler = new ILabeler<EventsItemWithImage>() {

		@Override
		public String getLabel(EventsItemWithImage obj) {
			return obj.getEventsItem().getTitle();
		}
	};
}
