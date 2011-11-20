package org.pocketcampus.plugin.news.android;

import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.labeler.IFeedViewLabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.android.platform.sdk.ui.list.FeedListViewElement;
import org.pocketcampus.android.platform.sdk.utils.LoaderImageView;
import org.pocketcampus.plugin.news.android.iface.INewsModel;
import org.pocketcampus.plugin.news.android.iface.INewsView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class NewsMainView extends PluginView implements INewsView {
	private NewsController mController;
	private INewsModel mModel;

	private StandardLayout mLayout;
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

		// Get and cast the controller and model
		mController = (NewsController) controller;
		mModel = (NewsModel) controller.getModel();

		// The StandardLayout is a RelativeLayout with a TextView in its center.
		mLayout = new StandardLayout(this, null);

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
	 * Initiates request for the restaurant, meal and sandwich data
	 */
	private void displayData() {
		mLayout.setText(getResources().getString(R.string.news_no_news));
		mController.getNewsItems();
	}

	@Override
	public void newsUpdated() {
		List<NewsItemWithImage> newsList = mModel.getNews();
		if (newsList != null) {

			// Add them to the listView
			mListView = new FeedListViewElement(this, newsList,
					mNewsItemLabeler);

			// Set onClickListener
			setOnListViewClickListener();

			// Set the layout
			mLayout.addView(mListView);

			mLayout.setText("");
		} else {
			mLayout.setText(getString(R.string.news_no_news));
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
			// backFromPreferences = true;
			Intent settings = new Intent(getApplicationContext(),
					NewsPreferences.class);
			startActivity(settings);

		}
		return true;
	}

	@Override
	public void networkErrorHappened() {
		// TODO Auto-generated method stub

	}

	/* Sets the clickLIstener of the listView */
	private void setOnListViewClickListener() {

		mOnItemClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
					int position, long arg3) {
				Toast.makeText(NewsMainView.this, "Hello", Toast.LENGTH_SHORT)
						.show();
				 Intent news = new Intent(getApplicationContext(),
				 NewsItemView.class);
				// news.putExtra("org.pocketcampus.news.newsitem",
				// mModel.getNews().get(position));
				 startActivity(news);
			}
		};
		mListView.setOnLineClickListener(mOnItemClickListener);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// newsProvider_.refreshIfNeeded();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// newsProvider_.removeNewsListener(this);
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
			return obj.getNewsItem().getDescription();
		}

		@Override
		public LinearLayout getPictureLayout(NewsItemWithImage obj) {
			return new LoaderImageView(NewsMainView.this, obj.getNewsItem()
					.getImageUrl());
		}
	};
}
