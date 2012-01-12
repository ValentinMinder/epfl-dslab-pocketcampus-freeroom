//package org.pocketcampus.plugin.news.android;
//
//import java.util.HashMap;
//import java.util.Set;
//
//import org.pocketcampus.R;
//import org.pocketcampus.android.platform.sdk.tracker.Tracker;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.preference.CheckBoxPreference;
//import android.preference.Preference;
//import android.preference.Preference.OnPreferenceChangeListener;
//import android.preference.PreferenceActivity;
//import android.preference.PreferenceCategory;
//import android.preference.PreferenceManager;
//import android.preference.PreferenceScreen;
//import android.util.Log;
//
///**
// * PluginPreference class for the News plugin.
// * 
// * @status complete
// * 
// * @author Jonas
// * 
// */
//public class NewsPreferences extends PreferenceActivity {
//
//	// Preferences Strings
//	protected final static String CACHE_TIME = "news_cache_time";
//	protected final static String LOAD_RSS = "load_rss";
//	protected final static String REFRESH_RATE = "news_refresh_rate";
//	protected final static String SHOW_IMG = "news_show_thumbnail";
//
//	private HashMap<String, String> mFeedNamesAndUrls;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// Tracker
//		 Tracker.getInstance().trackPageView("news/preferences");
//
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.news_preference);
//
//		handleExtras();
//
//		setPreferenceScreen(createPreferenceHierarchy());
//	}
//
//	/**
//	 * Create the preferences programmatically.
//	 * 
//	 * @return The corresponding Preference Screen.
//	 */
//	private PreferenceScreen createPreferenceHierarchy() {
//		// Root
//		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(
//				this);
//
//		PreferenceCategory rssPrefCat = new PreferenceCategory(this);
//		rssPrefCat.setTitle(R.string.news_preferences_rss_title);
//		root.addPreference(rssPrefCat);
//
//		// We want to force a refresh when the preferences have been changed
//		final NewsPreferences that = this;
//		OnPreferenceChangeListener listener = new OnPreferenceChangeListener() {
//			@Override
//			public boolean onPreferenceChange(Preference arg0, Object arg1) {
//				// Tracker
//				 Tracker.getInstance().trackPageView("news/preferences/change");
//
//				PreferenceManager.getDefaultSharedPreferences(that).edit()
//						.putLong(CACHE_TIME, 0).commit();
//				return true;
//			}
//		};
//
//		if (mFeedNamesAndUrls != null) {
//			Set<String> mFeedNames = mFeedNamesAndUrls.keySet();
//
//			String[] urls = new String[mFeedNames.size()];
//			String[] names = new String[urls.length];
//			int i = 0;
//			for (String name : mFeedNames) {
//				// Feeds to display
//				names[i] = name;
//				urls[i] = mFeedNamesAndUrls.get(name);
//				i++;
//			}
//			CheckBoxPreference checkBoxPref;
//			int j = 0;
//			for (String name : names) {
//				checkBoxPref = new CheckBoxPreference(this);
//				checkBoxPref.setKey(LOAD_RSS + name);
//				checkBoxPref.setTitle(name);
//				checkBoxPref.setSummary(urls[j++]);
//				checkBoxPref.setDefaultValue(true);
//				checkBoxPref.setOnPreferenceChangeListener(listener);
//
//				rssPrefCat.addPreference(checkBoxPref);
//			}
//		}
//
//		return root;
//	}
//
//	@Override
//	public void onBackPressed() {
//		setResult(Activity.RESULT_OK);
//		finish();
//	}
//
//	/**
//	 * Handle extras from the MainView.
//	 */
//	@SuppressWarnings("unchecked")
//	private void handleExtras() {
//		Bundle extras = getIntent().getExtras();
//		System.out.println("Handling extra: " + extras);
//		if (extras != null) {
//			mFeedNamesAndUrls = (HashMap<String, String>) extras
//					.getSerializable("org.pocketcampus.news.feedUrls");
//			System.out.println(mFeedNamesAndUrls.size());
//		} else {
//			Log.d("NEWSPREFERENCESVIEW", "No extras received!");
//		}
//	}
//}
