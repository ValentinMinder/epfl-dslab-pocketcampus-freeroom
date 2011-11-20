package org.pocketcampus.plugin.news.android;

import java.util.ArrayList;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.element.PreferencesView;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.labeler.IViewConstructor;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.android.platform.sdk.ui.list.PreferencesListViewElement;
import org.pocketcampus.plugin.news.android.iface.INewsModel;
import org.pocketcampus.plugin.news.shared.Feed;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * The Preferences view of the news plugin, displayed when a user wants to
 * filter what Feeds to display in the news list.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class NewsPreferencesView extends PluginView {
	/* MVC */
	/** The model to which the view is linked */
	private INewsModel mModel;

	/* Layout */
	/** A simple full screen layout */
	private StandardLayout mLayout;
	/** The list to be displayed in the layout */
	private PreferencesListViewElement mListView;

	/* Preferences */
	/** The pointer to access and modify preferences stored on the phone */
	private SharedPreferences mNewsPrefs;
	/** Interface to modify values in SharedPreferences object */
	private Editor mNewsPrefsEditor;
	/** The name under which the preferences are stored on the phone */
	private static final String NEWS_PREFS_NAME = "NewsPrefs";

	/* Feeds */
	/** The list of Feeds the preferences are made on */
	private ArrayList<Feed> mFeeds;

	/* Listener */
	/** Callback for when an item is clicked in the list */
	private OnItemClickListener mListener;

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
		// Get and cast the model
		mModel = (NewsModel) controller.getModel();

		// The StandardLayout is a RelativeLayout with a TextView in its center.
		mLayout = new StandardLayout(this);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);

		// We need to force the display before asking the controller for the
		// data, as the controller may take some time to get it.
		displayData();
	}

	/**
	 * Displays the list of Feeds which the user can choose from
	 */
	private void displayData() {
		// List of Restaurants
		mFeeds = (ArrayList<Feed>) mModel.getFeedsList();

		if (mFeeds != null && !mFeeds.isEmpty()) {
			mListView = new PreferencesListViewElement(this, mFeeds,
					feedsLabeler, NEWS_PREFS_NAME);

			// Set onClickListener
			setOnListViewClickListener();

			mLayout.addView(mListView);

		} else {
			mLayout.setText("No Feeds");
		}

		mNewsPrefs = getSharedPreferences(NEWS_PREFS_NAME, 0);
		mNewsPrefsEditor = mNewsPrefs.edit();

		if (mNewsPrefs.getAll().isEmpty()) {
			Log.d("PREFERENCES", "First time instanciatation (NewsPreference)");
			for (Feed f : mFeeds) {
				mNewsPrefsEditor.putBoolean(f.getTitle(), true);
			}
			mNewsPrefsEditor.commit();
		}
	}

	/**
	 * Sets what happens when the user clicks on an item in the list of
	 * Restaurants
	 */
	private void setOnListViewClickListener() {

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View prefBox,
					int position, long isChecked) {

				if (isChecked == 1) {
					mNewsPrefsEditor.putBoolean(
							mFeeds.get(position).title, true);
					mNewsPrefsEditor.commit();
				} else {
					mNewsPrefsEditor.putBoolean(
							mFeeds.get(position).title, false);
					mNewsPrefsEditor.commit();
				}
			}
		});
	}

	/**
	 * The labeler for a Feed, to tell how it has to be displayed in a
	 * generic view.
	 */
	ILabeler<Feed> feedsLabeler = new ILabeler<Feed>() {

		@Override
		public String getLabel(Feed feed) {
			return feed.getTitle();
		}
	};

	/**
	 * The constructor for a Feed View to be displayed in the list
	 */
	IViewConstructor feedConstructor = new IViewConstructor() {

		@Override
		public View getNewView(Object currentObject, Context context,
				ILabeler<? extends Object> labeler, int position) {
			return new PreferencesView(currentObject, context, labeler,
					NEWS_PREFS_NAME, mListener, position);
		}
	};
}