package org.pocketcampus.plugin.mainscreen;

import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.Core;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

public class MainscreenPreference extends PluginPreference {
	private Context ctx_;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainscreen_preference);
		ctx_ = this;
		
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(getResources().getText(R.string.app_name));
		actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin.createIntent(this), R.drawable.mini_home));
		
		setPreferenceScreen(createPreferenceHierarchy());
	}

	private PreferenceScreen createPreferenceHierarchy() {
		// root
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

//		 language preferences
		PreferenceCategory mainscreenPref = new PreferenceCategory(this);
		mainscreenPref.setTitle(R.string.application_preference);
		root.addPreference(mainscreenPref);
		
		Preference appPref;
		appPref = new Preference(this);
		appPref.setTitle(R.string.mainscreen_language);
		
		OnPreferenceClickListener onAppPreferenceClickListener = new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(ctx_, new ApplicationPreference().getClass());
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				return false;
			}
		};
		
		appPref.setOnPreferenceClickListener(onAppPreferenceClickListener);
		
		mainscreenPref.addPreference(appPref);
		
		//add the mainscreen preferences
		Preference mainPref;
		mainPref = new Preference(this);
		mainPref.setTitle(R.string.mainscreen_plugin_preference_title);
		
		OnPreferenceClickListener onMainPreferenceClickListener = new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(ctx_, new MainscreenPluginPreference().getClass());
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				return false;
			}
		};
		
		mainPref.setOnPreferenceClickListener(onMainPreferenceClickListener);
		mainscreenPref.addPreference(mainPref);
		
		// TODO

		// plugins preferences
		PreferenceCategory pluginPrefCat = new PreferenceCategory(this);
		pluginPrefCat.setTitle(R.string.mainscreen_plugins_options);
		root.addPreference(pluginPrefCat);
		
		
		Vector<PluginBase> plugins = Core.getInstance().getAvailablePlugins();
		
		Preference pluginPref;
		for (final PluginBase plugin : plugins) {
			if(plugin.getPluginPreference() != null) {
				pluginPref = new Preference(this);
				pluginPref.setTitle(plugin.getPluginInfo().getNameResource());
				
				OnPreferenceClickListener onPreferenceClickListener = new OnPreferenceClickListener() {
					
					@Override
					public boolean onPreferenceClick(Preference preference) {
						Intent intent = new Intent(ctx_, plugin.getPluginPreference().getClass());
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						return false;
					}
				};
				
				pluginPref.setOnPreferenceClickListener(onPreferenceClickListener);
				
				pluginPrefCat.addPreference(pluginPref);
			}
		}

		return root;
	}
}
