package org.pocketcampus.core.plugin;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;

import android.app.Activity;

/**
 * Base class for the display of plugins. The Display class is the main interface of the plugins and will be started 
 * when the app is launched from the mainscreen.
 * 
 * @status incomplete
 * @author florian
 * @license
 *
 */

public abstract class PluginBase extends Activity {
	/**
	 * Access to the plugin infos.
	 * @return
	 */
	public abstract PluginInfo getPluginInfo();
	
	/**
	 * Access to the plugin preference activity.
	 * @return
	 */
	public abstract PluginPreference getPluginPreference();
	
	protected RequestHandler getRequestHandler() {
		return new RequestHandler(getPluginInfo());
	}
	
	protected void setupActionBar(boolean addHomeButton) {
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(getResources().getText(R.string.app_name));
		
		if(addHomeButton) {
			actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin.createIntent(this), R.drawable.mini_home));
		}
	}
}





















