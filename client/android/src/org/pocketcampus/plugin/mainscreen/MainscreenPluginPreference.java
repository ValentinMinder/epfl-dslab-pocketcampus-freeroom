package org.pocketcampus.plugin.mainscreen;

import java.util.HashMap;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;

import android.content.Intent;
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
					PreferenceManager.getDefaultSharedPreferences(that).edit().putBoolean(arg0.toString(), checked).commit();
				} else {
					if(MainscreenPluginPreference.counter_ == 1) {
						Toast.makeText(that, getResources().getString(R.string.mainscreen_news_plugins_message), Toast.LENGTH_SHORT).show();
					} else {
						PreferenceManager.getDefaultSharedPreferences(that).edit().putBoolean(arg0.toString(), checked).commit();
					}
				}

				forceRefresh();

				return false;
			}
		};

		// Feeds to display
		String[] plugins  = getResources().getStringArray(R.array.mainscreen_provider_plugins);

		CheckBoxPreference checkBoxPref;

		counter_ = 0;
		
		for(String key : plugins) {

			PluginBase plug = null;
			try {
				Class<?> cl = Class.forName(MainscreenPlugin.PACKAGE+key);
				plug = (PluginBase) cl.newInstance();
			} catch (ClassNotFoundException e) {
			} catch (IllegalAccessException e) {
			} catch (InstantiationException e) {
			}

			if(plug != null) {
				checkBoxPref = new CheckBoxPreference(this);
				checkBoxPref.setKey(key);
				checkBoxPref.setTitle(plug.getPluginInfo().getName());

				boolean checked = PreferenceManager.getDefaultSharedPreferences(that).getBoolean(plug.getPluginInfo().getName(), false);

				if(checked) counter_++;

				checkBoxPref.setChecked(checked);
				checkBoxPref.setOnPreferenceChangeListener(listener);

				news.addPreference(checkBoxPref);
			}
		}

		return root;
	}

	private void forceRefresh() {
		Intent selfIntent = getIntent();
		selfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(selfIntent);
		finish();
		overridePendingTransition(0, 0);
	}

}
