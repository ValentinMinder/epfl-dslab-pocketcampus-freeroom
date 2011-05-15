package org.pocketcampus.core.communication.pcp;

import android.os.AsyncTask;

public class Test extends AsyncTask<String, Integer, String> {

	@Override
	protected String doInBackground(String... params) {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StringBuilder b = new StringBuilder();
		for (String s : params) {
			b.append(s);
		}
		return b.toString();
	}

}
