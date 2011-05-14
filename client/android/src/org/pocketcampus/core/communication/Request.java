package org.pocketcampus.core.communication;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.pocketcampus.core.plugin.PluginInfo;

import android.os.AsyncTask;
import android.util.Log;

public abstract class Request<A> extends AsyncTask<RequestParameters, Integer, A> {
	private CacheManager<?> cacheManager_;
	protected Exception exception_;
	protected PluginInfo pluginInfo_;
	protected String serverUrl_;
	protected String command_;

	protected abstract int expirationDelay();
	protected abstract int timeoutDelay();
	abstract A loadFromServer(String url);
	protected abstract String getUrl();
	
	public Request() {
		cacheManager_ = CacheManager.getInstance();
	}
	
	@Override
	protected final void onPostExecute(A result) {
		doInUiThread(result);
	}

	/**
	 * Method called to handle the content coming from the server.
	 * Do not change the UI in this method.
	 * This method can be slow, since it is not running on the UI thread.
	 * 
	 * @param result Data from the server
	 */
	protected void doInBackgroundThread(A result) {};

	/**
	 * Method called after {@link doInBackgroundThread}, can be used to update the UI.
	 * This method runs on the UI thread so it must be fast! 
	 * 
	 * @param result Data from the server
	 */
	protected void doInUiThread(A result) {};

	void start(final RequestParameters... params) {
		execute(params);
	}

	/**
	 * Setups a FutureTask that'll cancel the Request after a given time (timeout).
	 * The time before interruption is <code>timeoutDelay()</code> seconds.
	 * @return
	 */
	private FutureTask<Void> setupTimeoutTimer() {
		final FutureTask<Void> futureTask = new FutureTask<Void>(new Runnable() {
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

		return futureTask;
	}	

	@Override
	protected final A doInBackground(RequestParameters... params) {
		FutureTask<Void> timeoutTimer = setupTimeoutTimer();
		Executors.newSingleThreadExecutor().execute(timeoutTimer);

		String url = getUrl();

		if(params!=null && params[0]!=null) {
			url += params[0].toString();
		}

		@SuppressWarnings("unchecked")
		A cachedValue = (A) cacheManager_.getFromCache(url);
		if(cachedValue != null) {
			doInBackgroundThread(cachedValue);
			return cachedValue;
		}

		A result = loadFromServer(url);

		if(result != null) {
			cacheManager_.putInCache(url, result, expirationDelay());
		}

		if(!isCancelled()) {
			doInBackgroundThread(result);
		}

		return result;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		Log.d("Request", "The request has been canceled (command: " + this.command_ + ")");
	}

	public final void setPluginInfo(PluginInfo pluginInfo) {
		pluginInfo_ = pluginInfo;
	}

	public final void setServerUrl(String serverUrl) {
		serverUrl_ = serverUrl;
	}

	public final void setCommand(String command) {
		command_ = command;
	}
}
