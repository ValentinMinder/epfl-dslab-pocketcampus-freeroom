package org.pocketcampus.plugin.news;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginPreference;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

public class NewsPreference extends PluginPreference {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setPreferenceScreen(createPreferenceHierarchy());
		
	}
	
	private PreferenceScreen createPreferenceHierarchy() {
        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

        // Inline preferences 
        PreferenceCategory inlinePrefCat = new PreferenceCategory(this);
        inlinePrefCat.setTitle(R.string.news_preferences_title);
        root.addPreference(inlinePrefCat);
        
        
        // Example of next screen toggle preference
        CheckBoxPreference nextScreenCheckBoxPref;

		String[] urls = getResources().getStringArray(R.array.news_feeds_url);
		String[] names = getResources().getStringArray(R.array.news_feeds_name);
		
		int i = 0;
		for(String url : urls) {

	        nextScreenCheckBoxPref = new CheckBoxPreference(this);
	        
	        nextScreenCheckBoxPref.setKey("load_rss" + url);
	        
	        nextScreenCheckBoxPref.setTitle(names[i++]);
	        nextScreenCheckBoxPref.setSummary(url);
	        
	        nextScreenCheckBoxPref.setDefaultValue(true);
	        
	        inlinePrefCat.addPreference(nextScreenCheckBoxPref);
		}

        return root;
    }
}
