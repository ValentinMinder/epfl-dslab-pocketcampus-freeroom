package org.pocketcampus.plugin.camipro;

import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.plugin.authentication.AuthenticationPlugin;

import android.os.Bundle;

/**
 * PluginBase class for the Camipro plugin.
 * This uses the WebService provided by the Camipro team. 
 * 
 * Data is redownloaded every time the plugin launches.
 * Data is really small and changes often.
 * 
 * @status WIP
 * 
 * @author Jonas
 *
 */
public class CamiproPlugin extends PluginBase {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AuthenticationPlugin.authenticate(this, TransactionsList.class);
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
