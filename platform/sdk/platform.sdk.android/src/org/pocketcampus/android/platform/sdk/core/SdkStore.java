package org.pocketcampus.android.platform.sdk.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SdkStore {

	/**
	 * Some constants.
	 */
	private static final String SDK_STORAGE_NAME = "SDK_STORAGE";
	private static final String SDK_PC_SESSIONN_KEY = "SDK_PC_SESSION";


	/**
	 * SharedPreferences object responsible for the persistent data storage.
	 */
	private SharedPreferences iStorage;

	/**
	 * Data that need to be persistent.
	 */
	private String pcSessionId;


	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public SdkStore(Context context) {
		iStorage = context.getSharedPreferences(SDK_STORAGE_NAME, 0);
		pcSessionId = iStorage.getString(SDK_PC_SESSIONN_KEY, null);
		
	}


	/**
	 * Persistent stuff
	 */
	public String getPcSessionId() {
		return pcSessionId;
	}
	public void setPcSessionId(String val, boolean permanently) {
		pcSessionId = val;
		if(permanently) {
			Editor editor = iStorage.edit();
			editor.putString(SDK_PC_SESSIONN_KEY, pcSessionId);
			editor.commit();
		}
	}
	
	
}
