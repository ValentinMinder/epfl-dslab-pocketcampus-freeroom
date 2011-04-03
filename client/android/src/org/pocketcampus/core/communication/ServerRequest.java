package org.pocketcampus.core.communication;

import org.pocketcampus.core.plugin.PluginInfo;

import android.os.AsyncTask;
import android.util.Log;

public abstract class ServerRequest extends AsyncTask<RequestParameters, Integer, String> {
	protected Exception exception_;
	private PluginInfo pluginInfo_;
	private String serverUrl_;
	private String command_;
	
	@Override
	protected String doInBackground(RequestParameters... params) {
		System.out.println(params[0]);
		System.out.println(pluginInfo_.getId());
		
		String url = serverUrl_ + pluginInfo_.getId() + "/" + command_ + params[0].toString();
		
		HttpRequest req = new HttpRequest(url);
		
		Log.d(this.getClass().toString(), url);
		
		try {
			System.out.println("Sending query...");
			String resp = req.getContent();
			return resp;
		} catch (Exception e) {
			exception_ = e;
			return null;
		}
	}
	
	@Override
	protected abstract void onPostExecute(String result);
	
	public void setPluginInfo(PluginInfo pluginInfo) {
		pluginInfo_ = pluginInfo;
	}

	public void setServerUrl(String serverUrl) {
		serverUrl_ = serverUrl;
	}

	public void setCommand(String command) {
		command_ = command;
	}
}