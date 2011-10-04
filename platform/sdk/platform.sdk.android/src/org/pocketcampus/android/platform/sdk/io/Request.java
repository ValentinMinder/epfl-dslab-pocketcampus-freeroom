package org.pocketcampus.android.platform.sdk.io;

import org.apache.thrift.TException;

import android.os.AsyncTask;

/**
 * Request parameters:
 * <li>the Controller class
 * <li>the client interface class
 * <li>the class of the sent object
 * <li>the class of the returned object
 */
public abstract class Request<ControllerType, ClientType, SentType, ResultType> extends AsyncTask<SentType, Integer, ResultType> {
	private TException mException = null;
	private ClientType mClient;
	private ControllerType mController;

	public void start(ControllerType controller, ClientType client, SentType... params) {
		mController = controller;
		mClient = client;
		
		execute(params);
	}
	
	@Override
	protected ResultType doInBackground(SentType... params) {
		SentType param = null;
		
		if(params.length > 0) {
			param = params[0];
		}
		
		try {
			ResultType result = run(mClient, param);
			System.out.println("Received result: " + result);
			return result;
			
		} catch (TException e) {
			// makes sure onResult is never called
			mException = e;
			return null;
		}
	}

	protected final void onPostExecute(ResultType result) {
		if(mException != null) {
			onError(mController, mException);
			return;
		}
		
		onResult(mController, result);
	};
	
	protected abstract ResultType run(ClientType client, SentType param) throws TException;
	protected abstract void onResult(ControllerType controller, ResultType result);
	protected abstract void onError(ControllerType controller, TException e);
}
















