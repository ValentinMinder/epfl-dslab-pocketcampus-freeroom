package org.pocketcampus.core.plugin;

import org.pocketcampus.core.communication.HttpRequest;

import android.app.Activity;
import android.os.AsyncTask;

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
	
	/**
	 * Inner class to extend to do JSON requests.
	 */
	protected abstract class RawTextRequest extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {
			System.out.println(params);
			System.out.println(getPluginInfo());
			
			HttpRequest req = new HttpRequest("http://128.178.252.49:8080/pocketcampus-server/SampleServlet?q=" + params[0].toString());
			
			try {
				return req.getContent();
			} catch (Exception e) {
				e.printStackTrace();
				return "Error!";
			}
		}
		
		@Override
		protected abstract void onPostExecute(String result);
	}
}
