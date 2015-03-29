package org.pocketcampus.platform.android.io;

import java.util.concurrent.RejectedExecutionException;

import org.apache.thrift.TBase;
import org.pocketcampus.platform.android.cache.RequestCache;
import org.pocketcampus.platform.android.core.GlobalContext;
import org.pocketcampus.platform.android.core.PluginController;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

/**
 * Base Request class.
 * 
 * Request parameters:
 * <li>the Controller class
 * <li>the client interface class
 * <li>the class of the sent object
 * <li>the class of the returned object
 * 
 * @author Florian <florian.laurent@epfl.ch>
 * @author Amer <amer.chamseddine@epfl.ch>
 */
public abstract class Request<ControllerType extends PluginController, ClientType, SentType, ResultType> extends AsyncTask<SentType, Integer, ResultType> {
	protected GlobalContext mGlobalContext;
	private Exception mException = null;
	private ClientType mClient;
	private ControllerType mController;
	private Handler mHandler;
	
	private SentType iParam;
	private ResultType iResult;
	private boolean iBypassCache = false;
	private boolean iFoundInCache;
	private boolean iServedFromCache;
	
	public void start(ControllerType controller, ClientType client, SentType param) {
		mController = controller;
		mGlobalContext = (GlobalContext) mController.getApplicationContext();
		mHandler = new Handler(mController.getMainLooper());
		mClient = client;

		// Increments the global request operation counter.
		if(shouldCountRequest()) {
			mHandler.post(new Runnable() {
			    public void run() {
			    	mGlobalContext.incrementRequestCounter();
			    }
			});
		}
		
		try {
			execute(param);
		} catch (RejectedExecutionException e) {
			// Requests are being fired too quickly. This request will be ignored.
			// Mostly happens when using monkey...
			Log.e(this.toString(), "Too many requests running, execution rejected.");
		}
	}

	@Override
	protected final ResultType doInBackground(SentType... params) {
		iParam = null;

		if(params.length > 0) {
			iParam = params[0];
		}

		try {
			iServedFromCache = true;
			
			ResultType cachedResult = (ResultType) RequestCache.queryCache(mGlobalContext, this.getClass().getName(), iParam);
			iFoundInCache = (cachedResult != null);
			if(iFoundInCache && !iBypassCache)
				return cachedResult;
			
			iServedFromCache = false;
			
			ResultType result = runInBackground(mClient, iParam);
			
			return result;

		} catch (Exception e) {
			mException = e;
			return null;
		}
	}

	protected final void onPostExecute(final ResultType result) {
		if(shouldCountRequest()) {
			mHandler.post(new Runnable() {
			    public void run() {
			    	mGlobalContext.decrementRequestCounter();
			    }
			});
		}

		if(mException != null) {
			mHandler.post(new Runnable() {
			    public void run() {
			    	onError(mController, mException);
			    }
			});
			
			return;
		}

		iResult = result;
		
		mHandler.post(new Runnable() {
		    public void run() {
		    	onResult(mController, result);
		    }
		});
	};
	
	@Override
	protected final void onCancelled() {
		if(shouldCountRequest()) {
			mHandler.post(new Runnable() {
			    public void run() {
			    	mGlobalContext.decrementRequestCounter();
			    }
			});
		}
	};
	
	/**
	 * Call this if you want to bypass the cache
	 */
	public Request<ControllerType, ClientType, SentType, ResultType> setBypassCache(boolean val) {
		iBypassCache = val;
		return this;
	}

	/**
	 * Instructs that the current server reply should be saved in cache
	 * Should be called if the data was fetched successfully from the server (without errors)
	 */
	protected void keepInCache() {
		if(iServedFromCache)
			return;
		RequestCache.pushToCache(mGlobalContext, this.getClass().getName(), iParam, (TBase) iResult);
	}
	
	/**
	 * Use this in onResult to know if the request was found in cache
	 */
	protected boolean foundInCache() {
		return iFoundInCache;
	}

	/**
	 * Call this if the request was serviced from cache, but you would like to refresh the data additionally
	 */
	@Deprecated
	protected void refreshAsWell() {
		try {
			if(foundInCache())
				this.getClass().newInstance().setBypassCache(true).start(mController, mClient, iParam);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
	}

	protected abstract ResultType runInBackground(ClientType client, SentType param) throws Exception;
	protected abstract void onResult(ControllerType controller, ResultType result);
	protected abstract void onError(ControllerType controller, Exception e);
	
	protected boolean shouldCountRequest() {
		return true;
	}
}
















