package org.pocketcampus.plugin.news;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginPreference;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceCategory;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setPreferenceScreen(createPreferenceHierarchy());
		
	}
	
	/**
	 * Create the preferences programmaticaly
	 * @return 
	 */
	private PreferenceScreen createPreferenceHierarchy() {
        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

        PreferenceCategory inlinePrefCat = new PreferenceCategory(this);
        inlinePrefCat.setTitle(R.string.news_preferences_title);
        root.addPreference(inlinePrefCat);
        
        // Feeds to display
		String[] urls = getResources().getStringArray(R.array.news_feeds_url);
		String[] names = getResources().getStringArray(R.array.news_feeds_name);

        CheckBoxPreference checkBoxPref;
		int i = 0;
		for(String url : urls) {

	        checkBoxPref = new CheckBoxPreference(this);
	        
	        checkBoxPref.setKey("load_rss" + url);
	        
	        checkBoxPref.setTitle(names[i++]);
	        checkBoxPref.setSummary(url);
	        
	        checkBoxPref.setDefaultValue(true);
	        
	        inlinePrefCat.addPreference(checkBoxPref);
		}
		
		// List of available refresh rates for the feeds
		ListPreference lp = new ListPreference(this);
		lp.setEntries(R.array.news_refresh_entries);
		lp.setEntryValues(R.array.news_refresh_values);
		lp.setDefaultValue(getResources().getStringArray(R.array.news_refresh_values)[0]);
		lp.setKey("news_refresh_rate");
		lp.setTitle(R.string.news_refresh_title);
		lp.setSummary(R.string.news_refresh_summary);
		lp.setDialogTitle(R.string.news_refresh_title);
		root.addPreference(lp);
		

        return root;
    }
}
