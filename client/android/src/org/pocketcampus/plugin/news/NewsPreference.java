package org.pocketcampus.plugin.news;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

/**
 * PluginPreference class for the News plugin. 
 * 
 * @status complete
 * 
 * @author Jonas
 *
 */
public class NewsPreference extends PluginPreference {
	
	// Preferences Strings
	protected final static String CACHE_TIME = "news_cache_time";
	protected final static String LOAD_RSS = "load_rss";
	protected final static String REFRESH_RATE = "news_refresh_rate";
	protected final static String SHOW_IMG = "news_show_thumbnail";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainscreen_preference);
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(getResources().getText(R.string.app_name));
		actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin.createIntent(this), R.drawable.mini_home));
		
		setPreferenceScreen(createPreferenceHierarchy());
	}
	
	/**
	 * Create the preferences programmatically
	 * @return 
	 */
	private PreferenceScreen createPreferenceHierarchy() {
        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

		// Options
		PreferenceCategory otherPrefsCat = new PreferenceCategory(this);
		otherPrefsCat.setTitle(R.string.news_preferences_other_title);
        root.addPreference(otherPrefsCat);
		
		// List of available refresh rates for the feeds
		ListPreference lp = new ListPreference(this);
		lp.setEntries(R.array.news_refresh_entries);
		lp.setEntryValues(R.array.news_refresh_values);
		lp.setDefaultValue(getResources().getStringArray(R.array.news_refresh_values)[getResources().getInteger(R.integer.news_default_refresh)]);
		lp.setKey(REFRESH_RATE);
		lp.setTitle(R.string.news_refresh_title);
		lp.setSummary(R.string.news_refresh_summary);
		lp.setDialogTitle(R.string.news_refresh_title);
		otherPrefsCat.addPreference(lp);
		
		CheckBoxPreference showImgPref = new CheckBoxPreference(this);
		showImgPref.setTitle(R.string.news_show_image_title);
		showImgPref.setSummary(R.string.news_show_image_summary);
		showImgPref.setDefaultValue(true);
		showImgPref.setKey(SHOW_IMG);
		otherPrefsCat.addPreference(showImgPref);

		// Feeds chooser
        PreferenceCategory rssPrefCat = new PreferenceCategory(this);
        rssPrefCat.setTitle(R.string.news_preferences_rss_title);
        root.addPreference(rssPrefCat);
        
        // We want to force a refresh when the preferences have been changed
        final NewsPreference that = this;
        OnPreferenceChangeListener listener = new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference arg0, Object arg1) {
				PreferenceManager.getDefaultSharedPreferences(that).edit().putLong(CACHE_TIME, 0).commit();
				return true;
			}
		};
        
        // Feeds to display
		String[] urls  = getResources().getStringArray(R.array.news_feeds_url);
		String[] names = getResources().getStringArray(R.array.news_feeds_name);

        CheckBoxPreference checkBoxPref;
		int i = 0;
		for(String url : urls) {

	        checkBoxPref = new CheckBoxPreference(this);
	        checkBoxPref.setKey(LOAD_RSS + url);
	        checkBoxPref.setTitle(names[i++]);
	        checkBoxPref.setSummary(url);
	        if(i == 1)
	        	checkBoxPref.setDefaultValue(true);
	        else
	        	checkBoxPref.setDefaultValue(false);
	        checkBoxPref.setOnPreferenceChangeListener(listener);
	        
	        rssPrefCat.addPreference(checkBoxPref);
		}
		
        return root;
    }
}
