package org.pocketcampus.plugin.news.android;

import java.util.HashMap;
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
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 */
public class NewsMainView extends PluginView implements INewsView {
	private NewsController mController;
	private INewsModel mModel;

	private StandardTitledLayout mLayout;
	private FeedWithImageListViewElement mListView;

	private OnItemClickListener mOnItemClickListener;

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
	 * Initiates request for news items
	 */
	private void displayData() {
		mLayout.setText(getResources().getString(R.string.news_loading));
		mLayout.hideTitle();
		mController.getNewsItems();
	}

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

	@Override
	public void feedUrlsUpdated() {

		Intent settings = new Intent(getApplicationContext(),
				NewsPreferences.class);
		System.out.println(mModel.getFeedsUrls().size());
		settings.putExtra("org.pocketcampus.news.feedUrls",
				(HashMap<String, String>) mModel.getFeedsUrls());
		
		startActivityForResult(settings, PREFERENCES_REQUEST_CODE);
	}

	/**
	 * Called when coming back from the preferences
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
	 * Main Food Options menu contains access to Meals by restaurants, ratings,
	 * Sandwiches, Suggestions and Settings
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.news_menu, menu);
		return true;
	}

	/**
	 * Decides what happens when the options menu is opened and an option is
	 * chosen (what view to display)
	 */
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		if (item.getItemId() == R.id.news_menu_settings) {
			mController.getFeedUrls();
		}
		return true;
	}

	@Override
	public void networkErrorHappened() {
		// Tracker
		 Tracker.getInstance().trackPageView("news/network_error");

		mLayout.removeFillerView();
		mLayout.hideTitle();
		mLayout.setText(getString(R.string.news_no_news));
	}

	/* Sets the clickLIstener of the listView */
	private void setOnListViewClickListener() {

		mOnItemClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
					int position, long arg3) {
				Intent news = new Intent(getApplicationContext(),
						NewsItemView.class);
				NewsItemWithImage toPass = mModel.getNews(NewsMainView.this)
						.get(position);
				news.putExtra("org.pocketcampus.news.newsitem.title", toPass
						.getNewsItem().getTitle());
				news.putExtra("org.pocketcampus.news.newsitem.description",
						toPass.getNewsItem().getContent());
				news.putExtra("org.pocketcampus.news.newsitem.feed", toPass
						.getNewsItem().getFeed());
				news.putExtra("org.pocketcampus.news.newsitem.bitmap",
						toPass.getBitmapDrawable());

				// Tracker
				 Tracker.getInstance().trackPageView(
				 "news/click/" + toPass.getNewsItem().getTitle());

				startActivity(news);
			}
		};
		mListView.setOnItemClickListener(mOnItemClickListener);
	}

	/**
	 * The labeler for a feed, to tell how it has to be displayed in a generic
	 * view.
	 */
	IFeedViewLabeler<NewsItemWithImage> mNewsItemLabeler = new IFeedViewLabeler<NewsItemWithImage>() {

		@Override
		public String getTitle(NewsItemWithImage obj) {
			return obj.getNewsItem().getTitle();
		}

		@Override
		public String getDescription(NewsItemWithImage obj) {
			return obj.getNewsItem().getContent();
		}

		@Override
		public LinearLayout getPictureLayout(NewsItemWithImage obj) {
			return new LoaderNewsImageView(NewsMainView.this, obj);
		}
	};
}
