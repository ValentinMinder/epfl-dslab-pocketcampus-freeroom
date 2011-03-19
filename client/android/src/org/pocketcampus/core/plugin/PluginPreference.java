package org.pocketcampus.core.plugin;

import org.pocketcampus.R;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;

import android.preference.PreferenceActivity;

public class PluginPreference extends PreferenceActivity {
	protected void setupActionBar() {
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(getResources().getText(R.string.app_name));
		actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin.createIntent(this), R.drawable.mini_home));
	}
}
