package org.pocketcampus.core.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.pocketcampus.core.plugin.PluginInfo;

import android.os.AsyncTask;
import android.util.Log;

public abstract class FileUpload extends AsyncTask<FileUploadParameters, Integer, String> {
	protected Exception exception_;
	protected PluginInfo pluginInfo_;
	protected String serverUrl_;
	protected String command_;
	
	/**
	 * Public constructor.
	 */
	public FileUpload() {
		
	}
	
	@Override
	protected final void onPostExecute(String result) {
		doInUiThread(result);
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
	
	/**
	 * Method called if the request was cancelled. The default implementation does nothing.
	 */
	protected void cancelled() {};

	/**
	 * Executes the Request.
	 * @param params request parameters
	 */
	void start(final FileUploadParameters... params) {
		execute(params);
		setupTimeout();
	}

	/**
	 * Setups a FutureTask that'll cancel the Request after a given time (timeout).
	 * The time before interruption is <code>timeoutDelay()</code> seconds.
	 * @return
	 */
	private void setupTimeout() {
		final FutureTask<Void> timeoutTask = new FutureTask<Void>(new Runnable() {
			@Override
			public void run() {
				try {
					get(timeoutDelay(), TimeUnit.SECONDS);
				} catch (TimeoutException e) {
					Log.d("Request", "Request timed out after "+timeoutDelay()+"s.");
					cancel(true);

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}, null);

		Executors.newSingleThreadExecutor().execute(timeoutTask);
	}
	
	@Override
	final protected void onCancelled() {
		super.onCancelled();
		Log.d("Request", "The request has been canceled (command: " + this.command_ + ")");
		cancelled();
	}

	/**
	 * @param pluginInfo
	 */
	public final void setPluginInfo(PluginInfo pluginInfo) {
		pluginInfo_ = pluginInfo;
	}

	/**
	 * @param serverUrl
	 */
	public final void setServerUrl(String serverUrl) {
		serverUrl_ = serverUrl;
	}

	/**
	 * @param command
	 */
	public final void setCommand(String command) {
		command_ = command;
	}
	
	protected final String getUrl() {
		return serverUrl_ + pluginInfo_.getId() + "/" + command_ + ".do";
	}
	
	protected int timeoutDelay() {
		// default 15 seconds (5 seemed to be too short)
		return 15;
	}
	
	@Override
	final protected String doInBackground(FileUploadParameters... params) {
		if ((params == null) || params.length < 1)
			throw new IllegalArgumentException("Not enough parameters");
		
		MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		mpEntity = params[0].setParameters(mpEntity);
		
		HttpPost request = new HttpPost(getUrl());
		request.setEntity(mpEntity);
		
		HttpClient client = new DefaultHttpClient();
		
		HttpResponse response;
		try {
			response = client.execute(request);
			return toString(response.getEntity().getContent());
			
		} catch (ClientProtocolException e) {
			exception_ = e;
		} catch (IOException e) {
			exception_ = e;
		}
		// If an exception was thrown
		return null;
	}
	
	private String toString(InputStream is) throws IOException {
		final char[] buffer = new char[0x10000];
		StringBuilder out = new StringBuilder();
		Reader in = new InputStreamReader(is, "UTF-8");
		int read;
		do {
		  read = in.read(buffer, 0, buffer.length);
		  if (read>0) {
		    out.append(buffer, 0, read);
		  }
		} while (read>=0);
		
		return out.toString();
	}
}
