package org.pocketcampus.core.communication;

import android.os.AsyncTask;

public abstract class ImageRequest extends AsyncTask<String, Integer, String> {
	
	@Override
	protected String doInBackground(String... params) {
		System.out.println(params);
		
		// TODO handle images loading
		
		return null;
	}
	
	@Override
	protected abstract void onPostExecute(String result);
}