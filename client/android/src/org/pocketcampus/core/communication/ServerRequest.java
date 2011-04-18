package org.pocketcampus.core.communication;

import org.pocketcampus.core.plugin.PluginInfo;

import android.os.AsyncTask;
import android.util.Log;

public abstract class ServerRequest extends AsyncTask<RequestParameters, Integer, String> {
	protected Exception exception_;
	private PluginInfo pluginInfo_;
	private String serverUrl_;
	private String command_;
	
	public String getUrl() {
		return serverUrl_ + pluginInfo_.getId() + "/" + command_ + ".do";
	}
	
	@Override
	protected String doInBackground(RequestParameters... params) {

		String url = getUrl();
		
		if(params[0] != null) {
			url += params[0].toString();
		}
		
		HttpRequest req = new HttpRequest(url);
		
		Log.d(this.getClass().toString(), url);
		
		String result = null;
		try {
			result = req.getContent();
		} catch (Exception e) {
			exception_ = e;
		}
		
		doInBackgroundThread(result);
		
		return result;
	}
	
	@Override
	protected final void onPostExecute(String result) {
		doInUiThread(result);
	}
	
	public void setPluginInfo(PluginInfo pluginInfo) {
		pluginInfo_ = pluginInfo;
	}

	public void setServerUrl(String serverUrl) {
		serverUrl_ = serverUrl;
	}

	public void setCommand(String command) {
		command_ = command;
	}

	/**
	 * Method called to handle the content coming from the server.
	 * Do not change the UI in this method.
	 * This method can be slow, since it is not running on the UI thread.
	 * 
	 * @param result Data from the server
	 */
	protected void doInBackgroundThread(String result) {};
	
	/**
	 * Method called after {@link doInBackgroundThread}, can be used to update the UI.
	 * This method runs on the UI thread so it must be fast! 
	 * 
	 * @param result Data from the server
	 */
	protected void doInUiThread(String result) {};
}






