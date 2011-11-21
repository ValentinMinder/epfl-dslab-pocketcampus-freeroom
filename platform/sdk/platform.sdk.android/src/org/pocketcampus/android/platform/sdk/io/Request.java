package org.pocketcampus.android.platform.sdk.io;

import java.util.concurrent.RejectedExecutionException;

import org.pocketcampus.android.platform.sdk.core.GlobalContext;
import org.pocketcampus.android.platform.sdk.core.PluginController;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

/**
 * Request parameters:
 * <li>the Controller class
 * <li>the client interface class
 * <li>the class of the sent object
 * <li>the class of the returned object
 */
public abstract class Request<ControllerType extends PluginController, ClientType, SentType, ResultType> extends AsyncTask<SentType, Integer, ResultType> {
	GlobalContext mGlobalContext;
	private Exception mException = null;
	private ClientType mClient;
	private ControllerType mController;
	private Handler mHandler;

	public void start(ControllerType controller, ClientType client, SentType... params) {
		mController = controller;
		mGlobalContext = (GlobalContext) mController.getApplicationContext();
		mHandler = new Handler(mController.getMainLooper());
		mClient = client;

		// Increments the global request operation counter.
		mHandler.post(new Runnable() {
		    public void run() {
		    	mGlobalContext.incrementRequestCounter();
		    }
		});
		
		try {
			execute(params);
		} catch (RejectedExecutionException e) {
			// Requests are being fired too quickly. This request will be ignored.
			// Mostly happens when using monkey...
			Log.e(this.toString(), "Too many requests running, exectuion rejected.");
		}
	}

	@Override
	protected final ResultType doInBackground(SentType... params) {
		SentType param = null;

		if(params.length > 0) {
			param = params[0];
		}

		try {
			ResultType result = runInBackground(mClient, param);
			System.out.println("Received result: " + result);
			return result;

		} catch (Exception e) {
			mException = e;
			return null;
		}
	}

	protected final void onPostExecute(final ResultType result) {
		mHandler.post(new Runnable() {
		    public void run() {
		    	mGlobalContext.decrementRequestCounter();
		    }
		});

		if(mException != null) {
			mHandler.post(new Runnable() {
			    public void run() {
			    	onError(mController, mException);
			    }
			});
			
			return;
		}

		mHandler.post(new Runnable() {
		    public void run() {
		    	onResult(mController, result);
		    }
		});
	};

	protected abstract ResultType runInBackground(ClientType client, SentType param) throws Exception;
	protected abstract void onResult(ControllerType controller, ResultType result);
	protected abstract void onError(ControllerType controller, Exception e);
}
















