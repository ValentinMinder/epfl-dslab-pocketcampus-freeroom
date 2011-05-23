package org.pocketcampus.plugin.mainscreen;

import java.util.ArrayList;
import java.util.Locale;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Core;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;


public class MainscreenPluginPreference extends PluginPreference {

	protected static int counter_ = 0;
    private Locale locale_ = null;
    protected static String currLang_;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		currLang_ = getBaseContext().getResources().getConfiguration().locale.getLanguage();
		
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


		PreferenceCategory news = new PreferenceCategory(this);
		news.setTitle(R.string.mainscreen_news_plugins);
		root.addPreference(news);

		final MainscreenPluginPreference that = this;

		// We want to force a refresh when the preferences have been changed
		OnPreferenceChangeListener listener = new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference arg0, Object arg1) {

				boolean checked = (Boolean)arg1;
				
				if(checked) {
					PreferenceManager.getDefaultSharedPreferences(that).edit().putBoolean(arg0.getKey(), checked).commit();
				} else {
//					The next lines are used if we want to force the user to display at least one plugin on the mainscreen.
//					As decided during the meetings, we allow the user to display nothing on the mainscreen
//					if(MainscreenPluginPreference.counter_ == 1) {
//						Toast.makeText(that, getResources().getString(R.string.mainscreen_news_plugins_message), Toast.LENGTH_SHORT).show();
//					} else {
//						PreferenceManager.getDefaultSharedPreferences(that).edit().putBoolean(arg0.getKey(), checked).commit();
//					}
					PreferenceManager.getDefaultSharedPreferences(that).edit().putBoolean(arg0.getKey(), checked).commit();
				}

				forceRefresh();

				return false;
			}
		};

		// Feeds to display
		ArrayList<PluginBase> plugins = Core.getInstance().getProvidersOf(IMainscreenNewsProvider.class);
		
		CheckBoxPreference checkBoxPref;

		counter_ = 0;
		
		int i = 0;
		
		for(PluginBase plug : plugins) {

			if(plug != null) {
				checkBoxPref = new CheckBoxPreference(this);
				checkBoxPref.setKey(plug.getPluginInfo().getId().toString());

				checkBoxPref.setTitle(plug.getPluginInfo().getNameResource());

				boolean checked = PreferenceManager.getDefaultSharedPreferences(that).getBoolean(plug.getPluginInfo().getId().toString(), true);

				if(checked) counter_++;

				checkBoxPref.setChecked(checked);
				checkBoxPref.setOnPreferenceChangeListener(listener);

				news.addPreference(checkBoxPref);
			}
			
			i++;
		}
		
		
		//Language selection
		PreferenceCategory language = new PreferenceCategory(this);
		language.setTitle(R.string.mainscreen_language);
		root.addPreference(language);

        final Configuration config = getBaseContext().getResources().getConfiguration();

        final String[] lang  = getResources().getStringArray(R.array.mainscreen_languages);
        
		OnPreferenceChangeListener langListener = new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference arg0, Object arg1) {

				currLang_ = arg0.getKey();
				
	            locale_ = new Locale(currLang_);
	            Locale.setDefault(locale_);
	            config.locale = locale_;
	            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
					            
				forceRefresh();

				return false;
			}
		};
		
		// Feeds to display
		String[] langAbr  = getResources().getStringArray(R.array.mainscreen_languages_abr);


		int j = 0;
				
		for(String str : lang) {

			checkBoxPref = new CheckBoxPreference(this);
			checkBoxPref.setKey(langAbr[j]);
			checkBoxPref.setTitle(str);

			
			boolean checked = currLang_.equals(langAbr[j]) ;

			checkBoxPref.setChecked(checked);
			checkBoxPref.setOnPreferenceChangeListener(langListener);

			language.addPreference(checkBoxPref);
			
			j++;
		}
		
		
		return root;
	}

	private void forceRefresh() {

		setPreferenceScreen(createPreferenceHierarchy());

	}
	


//	    @Override
//	    public void onConfigurationChanged(Configuration newConfig)
//	    {
//	        super.onConfigurationChanged(newConfig);
//	        if (locale != null)
//	        {
//	            newConfig.locale = locale;
//	            Locale.setDefault(locale);
//	            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
//	        }
//	    }

}
