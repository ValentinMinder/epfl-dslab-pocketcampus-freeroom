package org.pocketcampus.plugin.news.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.android.platform.sdk.ui.list.PreferencesListViewElement;
import org.pocketcampus.plugin.news.android.iface.INewsModel;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * The Preferences view of the NEWS plugin, displayed when a user wants to
 * filter what feeds to display in the different News lists.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class NewsPreferencesView extends PluginView {
	/* MVC */
	/** The model to which the view is linked. */
	private INewsModel mModel;

	/* Layout */
	/** A simple full screen layout. */
	private StandardTitledLayout mLayout;
	/** The list to be displayed in the layout. */
	private PreferencesListViewElement mListView;

	/** Used to put in preferences if specific RSS has to be loaded */
	protected final static String LOAD_RSS = "load_rss";

	/* Preferences */
	/** The pointer to access and modify preferences stored on the phone. */
	private SharedPreferences mNewsPrefs;
	/** Interface to modify values in SharedPreferences object. */
	private Editor mNewsPrefsEditor;
	/** The name under which the preferences are stored on the phone. */
	public static final String NEWS_PREFS_NAME = "NewsPrefs";

	/* Feed */
	/** The list of Feeds the preferences are made on. */
	private List<String> mFeedNames;

	/**
	 * Defines what the main controller is for this view.
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
		Tracker.getInstance().trackPageView("news/preferences");
		// Get and cast the model
		mModel = (NewsModel) controller.getModel();

		// The StandardLayout is a RelativeLayout with a TextView in its center.
		mLayout = new StandardTitledLayout(this);

		mLayout.setTitle(getString(R.string.news_preferences));

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);

		// We need to force the display before asking the controller for the
		// data, as the controller may take some time to get it.
		displayData();
	}

	/**
	 * Displays the list of feeds which the user can choose from.
	 */
	private void displayData() {
		// Hashmap of feeds
		HashMap<String, String> mFeedNamesAndUrls = (HashMap<String, String>) mModel
				.getFeedsUrls();

		mFeedNames = new ArrayList<String>();
		mFeedNames.addAll(mFeedNamesAndUrls.keySet());
		Collections.sort(mFeedNames);

		if (mFeedNames != null && !mFeedNames.isEmpty()) {
			mListView = new PreferencesListViewElement(this, mFeedNames,
					feedLabeler, NEWS_PREFS_NAME);

			// Set onClickListener
			setOnListViewClickListener();

			mLayout.addFillerView(mListView);

			mNewsPrefs = getSharedPreferences(NEWS_PREFS_NAME, 0);
			mNewsPrefsEditor = mNewsPrefs.edit();

			if (mNewsPrefs.getAll().isEmpty()) {
				Log.d("PREFERENCES",
						"First time instanciatation (NewsPreference)");
				for (String r : mFeedNames) {
					mNewsPrefsEditor.putBoolean(r, true);
				}
				mNewsPrefsEditor.commit();
			}
		} else {
			mLayout.setText("No Feeds");
		}

	}

	@Override
	public void onBackPressed() {
		setResult(Activity.RESULT_OK);
		finish();
	}

	/**
	 * Sets what happens when the user clicks on an item in the list of Feeds.
	 */
	private void setOnListViewClickListener() {

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View prefBox,
					int position, long isChecked) {

				if (isChecked == 1) {
					// Tracker
					Tracker.getInstance().trackPageView(
							"news/preferences/add/" + mFeedNames.get(position));
					mNewsPrefsEditor.putBoolean(mFeedNames.get(position), true);
					Log.d("Prefs", mFeedNames.get(position));
					mNewsPrefsEditor.commit();
				} else {
					Tracker.getInstance().trackPageView(
							"news/preferences/remove/"
									+ mFeedNames.get(position));
					Log.d("Prefs", mFeedNames.get(position));

					mNewsPrefsEditor.putBoolean(mFeedNames.get(position), false);
					mNewsPrefsEditor.commit();
				}
			}
		});

	}

	/**
	 * The labeler for a Feed, to tell how it has to be displayed in a generic
	 * view.
	 */
	ILabeler<String> feedLabeler = new ILabeler<String>() {

		/**
		 * Returns the name of a feed.
		 * 
		 * @param feed
		 *            The feed to be displayed.
		 * @return The feed name.
		 */
		@Override
		public String getLabel(String feed) {
			return feed;
		}
	};
}