package org.pocketcampus.plugin.preferenceslauncher;

import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.plugin.mainscreen.MainscreenPreference;

import android.content.Intent;
import android.os.Bundle;

/**
 * PluginBase class for the PreferencesLauncher plugin.
 * This is just used to launch the preferences.
 * 
 * @status complete
 * 
 * @author Jonas
 *
 */
public class PreferencesLauncherPlugin extends PluginBase {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = new Intent(this, MainscreenPreference.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new PreferencesLauncherInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return null;
	}

}
