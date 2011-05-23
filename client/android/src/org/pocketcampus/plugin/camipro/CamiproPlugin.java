package org.pocketcampus.plugin.camipro;

import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.plugin.authentication.AuthenticationPlugin;

import android.os.Bundle;

/**
 * PluginBase class for the Camipro plugin.
 * 
 * This activity only takes care of checking if the user is already logged in.
 * The user is then redirected to antoher activity.
 * 
 * @status Complete
 * 
 * @author Jonas
 *
 */
public class CamiproPlugin extends PluginBase {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AuthenticationPlugin.authenticate(this, SecuredCamipro.class);
		finish();
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new CamiproInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return null;
	}
}
