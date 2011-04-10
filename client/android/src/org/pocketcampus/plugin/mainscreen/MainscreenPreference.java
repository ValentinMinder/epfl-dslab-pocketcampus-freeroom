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

		// mainscreen preferences
//		PreferenceCategory mainscreenPref = new PreferenceCategory(this);
//		mainscreenPref.setTitle("Mainscreen Preferences");
//		root.addPreference(mainscreenPref);
		// TODO

		// plugins preferences
		PreferenceCategory pluginPrefCat = new PreferenceCategory(this);
		pluginPrefCat.setTitle("Plugins Preferences");
		root.addPreference(pluginPrefCat);
		
		Vector<PluginBase> plugins = Core.getInstance().getAvailablePlugins();
		
		Preference pluginPref;
		for (final PluginBase plugin : plugins) {
			if(plugin.getPluginPreference() != null) {
				pluginPref = new Preference(this);
				pluginPref.setTitle(plugin.getPluginInfo().getName());
				
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
