package org.pocketcampus.plugin.news.android;

import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.labeler.IFeedViewLabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.android.platform.sdk.ui.list.FeedWithImageListViewElement;
import org.pocketcampus.plugin.news.android.iface.INewsModel;
import org.pocketcampus.plugin.news.android.iface.INewsView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

/**
 * The Main View of the News plugin, first displayed when accessing News.
 * 
 * Displays News from newest to oldest
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class NewsMainView extends PluginView implements INewsView {

	/** The controller that does the interface between Model and View. */
	private NewsController mController;
	/** The corresponding model. */
	private INewsModel mModel;

	/** A simple full screen layout. */
	private StandardTitledLayout mLayout;

	/** The main list with news. */
	private FeedWithImageListViewElement mListView;

	/** Listener for when you click on a line in the list */
	private OnItemClickListener mOnItemClickListener;

	/** Code used to make a request to the preferences activity */
	private static final int PREFERENCES_REQUEST_CODE = 1555;

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
		return NewsController.class;
	}

	/**
	 * Called once the view is connected to the controller. If you don't
	 * implement <code>getMainControllerClass()</code> then the controller given
	 * here will simply be <code>null</code>.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Tracker
		Tracker.getInstance().trackPageView("news");

		// Get and cast the controller and model
		mController = (NewsController) controller;
		mModel = (NewsModel) controller.getModel();

		// The StandardLayout is a RelativeLayout with a TextView in its center.
		mLayout = new StandardTitledLayout(this, null);

		mLayout.setTitle(getString(R.string.news_plugin_title));

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);

		// We need to force the display before asking the controller for the
		// data,
		// as the controller may take some time to get it.
		displayData();
	}

	/**
	 * Initiates request to the server for news items.
	 */
	private void displayData() {
		mLayout.setText(getResources().getString(R.string.news_loading));
		mLayout.hideTitle();
		mController.getNewsItems();
	}

	/**
	 * Called when the list of news has been updated. Displays the list
	 * according to the user's preferences.
	 */
	@Override
	public void newsUpdated() {
		List<NewsItemWithImage> newsList = mModel.getNews(this);
		mLayout.removeFillerView();
		mLayout.hideTitle();
		if (newsList != null) {
			if (!newsList.isEmpty()) {
				mLayout.setTitle(getString(R.string.news_plugin_title));
				// Add them to the listView
				mListView = new FeedWithImageListViewElement(this, newsList,
						mNewsItemLabeler);

				// Set onClickListener
				setOnListViewClickListener();

				// Set the layout
				mLayout.addFillerView(mListView);

				mLayout.setText("");
			} else {
				mLayout.setText(getString(R.string.news_no_feed_selected));
			}
		} else {
			mLayout.setText(getString(R.string.news_no_news));
		}
	}

	/**
	 * Called when the feed Urls have been updated. Calls the preference
	 * activity.
	 */
	@Override
	public void feedUrlsUpdated() {

		Intent settings = new Intent(getApplicationContext(),
				NewsPreferencesView.class);

		startActivityForResult(settings, PREFERENCES_REQUEST_CODE);
	}

	/**
	 * Called when coming back from the preferences.
	 * 
	 * @param requestCode
	 *            The integer request code originally supplied to
	 *            startActivityForResult(), allowing you to identify who this
	 *            result came from.
	 * @param resultCode
	 *            The integer result code returned by the child activity through
	 *            its setResult().
	 * @param data
	 *            An Intent, which can return result data to the caller (various
	 *            data can be attached to Intent "extras").
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case PREFERENCES_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				newsUpdated();
			}
		}
	}

	/**
	 * Main News Options menu contains access to the Feed preferences.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.news_menu, menu);
		return true;
	}

	/**
	 * Decides what happens when the options menu is opened and an option is
	 * chosen (what view to display).
	 */
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		if (item.getItemId() == R.id.news_menu_settings) {
			mController.getFeedUrls();
		}
		return true;
	}

	/**
	 * Called when an error occurs while trying to contact the server.
	 */
	@Override
	public void networkErrorHappened() {
		// Tracker
		Tracker.getInstance().trackPageView("news/network_error");

		mLayout.removeFillerView();
		mLayout.hideTitle();
		mLayout.setText(getString(R.string.news_no_news));
	}

	/**
	 * Sets the clickListener of the listView.
	 */
	private void setOnListViewClickListener() {

		mOnItemClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
					int position, long arg3) {
				toDisplay = mModel.getNews(NewsMainView.this).get(position);

				mController.getNewsContent(toDisplay.getNewsItem()
						.getNewsItemId());

				// Tracker
				Tracker.getInstance().trackPageView(
						"news/click/" + toDisplay.getNewsItem().getTitle());
			}
		};
		mListView.setOnItemClickListener(mOnItemClickListener);
	}

	/**
	 * The labeler for a feed, to tell how it has to be displayed in a generic
	 * view.
	 */
	IFeedViewLabeler<NewsItemWithImage> mNewsItemLabeler = new IFeedViewLabeler<NewsItemWithImage>() {

		/**
		 * @param newsItem
		 *            the NewsItem to be represented
		 * @return The title of the NewsItem.
		 */
		@Override
		public String getTitle(NewsItemWithImage newsItem) {
			return newsItem.getNewsItem().getTitle();
		}

		/**
		 * @param newsItem
		 *            the NewsItem to be represented
		 * @return The description of the NewsItem.
		 */
		@Override
		public String getDescription(NewsItemWithImage newsItem) {
			return "";
		}

		/**
		 * @param newsItem
		 *            the NewsItem to be represented
		 * @return The layout with the image associated to the NewsItem.
		 */
		@Override
		public LinearLayout getPictureLayout(NewsItemWithImage newsItem) {
			return new LoaderNewsImageView(NewsMainView.this, newsItem);
		}
	};

	private NewsItemWithImage toDisplay;

	@Override
	public void newsContentLoaded(String content) {
		if (toDisplay != null) {
			Intent news = new Intent(getApplicationContext(),
					NewsItemView.class);
			news.putExtra("org.pocketcampus.news.newsitem.title", toDisplay
					.getNewsItem().getTitle());
			news.putExtra("org.pocketcampus.news.newsitem.feed", toDisplay
					.getNewsItem().getFeed());
			news.putExtra("org.pocketcampus.news.newsitem.bitmap",
					toDisplay.getBitmapDrawable());

			news.putExtra("org.pocketcampus.news.newsitem.description", content);

			startActivity(news);
		}
	}
}
