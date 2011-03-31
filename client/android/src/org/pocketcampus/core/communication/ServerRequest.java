package org.pocketcampus.core.communication;

import org.pocketcampus.core.plugin.PluginInfo;

import android.os.AsyncTask;

public abstract class ServerRequest extends AsyncTask<RequestParameters, Integer, String> {
	protected Exception exception_;
	private PluginInfo pluginInfo_;
	private String serverUrl_;
	
	@Override
	protected String doInBackground(RequestParameters... params) {
		System.out.println(params[0]);
		System.out.println(pluginInfo_.getId());
		
		HttpRequest req = new HttpRequest(serverUrl_ + pluginInfo_.getId() + params[0].toString());
		
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
}