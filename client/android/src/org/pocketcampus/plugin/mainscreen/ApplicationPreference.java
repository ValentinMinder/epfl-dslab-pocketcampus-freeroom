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

/**
 * The general application preferences. Now it is just about switching the language.
 * @author Guillaume
 *
 */
public class ApplicationPreference extends PluginPreference {

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

		CheckBoxPreference checkBoxPref;
		
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

	
//	@Override
//	public void onBackPressed() {
//		Intent intent = new Intent(this, new MainscreenPreference().getClass());
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		this.getApplicationContext().startActivity(intent);
//	}
	
}
