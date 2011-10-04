package org.pocketcampus.android.platform.sdk.io;

import org.apache.thrift.TException;
import org.pocketcampus.android.platform.sdk.core.GlobalContext;
import org.pocketcampus.android.platform.sdk.core.PluginController;

import android.os.AsyncTask;

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

	public void start(ControllerType controller, ClientType client, SentType... params) {
		mController = controller;
		mClient = client;
		
		// increments the global request operation counter
		mGlobalContext = (GlobalContext) mController.getApplicationContext();
		mGlobalContext.incrementRequestCounter();
		
		execute(params);
	}
	
	@Override
	protected ResultType doInBackground(SentType... params) {
		SentType param = null;
		
		if(params.length > 0) {
			param = params[0];
		}
		
		try {
			ResultType result = runInBackground(mClient, param);
			System.out.println("Received result: " + result);
			return result;
			
		} catch (Exception e) {
			// makes sure onResult is never called
			mException = e;
			return null;
		}
	}

	protected final void onPostExecute(ResultType result) {
		mGlobalContext.decrementRequestCounter();
		
		if(mException != null) {
			onError(mController, mException);
			return;
		}
		
		onResult(mController, result);
	};
	
	protected abstract ResultType runInBackground(ClientType client, SentType param) throws Exception;
	protected abstract void onResult(ControllerType controller, ResultType result);
	protected abstract void onError(ControllerType controller, Exception e);
}
















