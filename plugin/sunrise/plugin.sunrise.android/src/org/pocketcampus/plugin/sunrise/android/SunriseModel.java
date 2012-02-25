package org.pocketcampus.plugin.sunrise.android;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;

import org.pocketcampus.plugin.sunrise.android.SunriseController.LocalCredentials;
import org.pocketcampus.plugin.sunrise.android.iface.ISunriseModel;
import org.pocketcampus.plugin.sunrise.android.iface.ISunriseView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SunriseModel - The Model that stores the data of this plugin.
 * 
 * This is the Model associated with the Sunrise plugin.
 * It stores the data required for the correct functioning of the plugin.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class SunriseModel extends PluginModel implements ISunriseModel {
	
	/**
	 * Some constants.
	 */
	private static final String SUNRISE_STORAGE_NAME = "SUNRISE_STORAGE_NAME";
	private static final String SUNRISE_USERNAME_KEY = "SUNRISE_USERNAME_KEY";
	private static final String SUNRISE_PASSWORD_KEY = "SUNRISE_PASSWORD_KEY";
	private static final String SUNRISE_REMAINING_KEY = "SUNRISE_REMAINING_KEY";
	
	/**
	 * SharedPreferences object responsible for the persistent data storage.
	 */
	private SharedPreferences iStorage;

	/**
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	ISunriseView mListeners = (ISunriseView) getListeners();
	
	/**
	 * Data that need to be persistent.
	 */
	private LocalCredentials localCredentials = null;
	private int remainingFreeSms;
	
	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public SunriseModel(Context context) {
		iStorage = context.getSharedPreferences(SUNRISE_STORAGE_NAME, 0);
		String user = iStorage.getString(SUNRISE_USERNAME_KEY, null);
		String pass = iStorage.getString(SUNRISE_PASSWORD_KEY, null);
		if(user != null && pass != null) {
			localCredentials = new LocalCredentials();
			localCredentials.username = user;
			localCredentials.password = pass;
		}
		remainingFreeSms = iStorage.getInt(SUNRISE_REMAINING_KEY, -1);
	}

	/**
	 * Setter and getter for remainingFreeSms
	 */
	public int getRemainingFreeSms() {
		return remainingFreeSms;
	}
	public void setRemainingFreeSms(int rfs) {
		remainingFreeSms = rfs;
		Editor editor = iStorage.edit();
		editor.putInt(SUNRISE_REMAINING_KEY, rfs);
		editor.commit();
		mListeners.remainingFreeSmsUpdated();
	}
	
	/**
	 * Setter and getter for localCredentials
	 */
	public LocalCredentials getSunriseCredentials() {
		return localCredentials;
	}
	public void setSunriseCredentials(LocalCredentials lc) {
		localCredentials = lc;
		if(lc != null) {
			Editor editor = iStorage.edit();
			editor.putString(SUNRISE_USERNAME_KEY, localCredentials.username);
			editor.putString(SUNRISE_PASSWORD_KEY, localCredentials.password);
			editor.commit();
			mListeners.loginSucceeded();
		} else {
			Editor editor = iStorage.edit();
			editor.remove(SUNRISE_USERNAME_KEY);
			editor.remove(SUNRISE_PASSWORD_KEY);
			editor.commit();
		}
	}
	
	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return ISunriseView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public ISunriseView getListenersToNotify() {
		return mListeners;
	}
	
}
