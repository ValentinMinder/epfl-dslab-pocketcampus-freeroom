package org.pocketcampus.core.communication;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.pocketcampus.core.plugin.PluginInfo;

import android.os.AsyncTask;

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
	
	public Request() {
		cacheManager_ = CacheManager.getInstance();
	}

	void start(final RequestParameters... params) {
		Runnable timedExecution = new Runnable() {

			@Override
			public void run() {
				final FutureTask<Void> futureTask = new FutureTask<Void>(new Runnable() {

					@Override
					public void run() {
						execute(params);
						
						try {
							get();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					}
				}, null);

				try {
					Executors.newSingleThreadExecutor().execute(futureTask);
					futureTask.get(timeoutDelay(), TimeUnit.SECONDS);
					
				} catch (TimeoutException e) {
					cancel(true);
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		
		Executors.newSingleThreadExecutor().execute(timedExecution);
	}

	@Override
	protected final A doInBackground(RequestParameters... params) {
		String url = getUrl();

		if(params[0] != null) {
			url += params[0].toString();
		}

		A cachedValue = (A) cacheManager_.getFromCache(url);
		if(cachedValue != null) {
			return cachedValue;
		}

		A result = loadFromServer(url);

		cacheManager_.putInCache(url, result, expirationDelay());

		doInBackgroundThread(result);

		return result;
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
