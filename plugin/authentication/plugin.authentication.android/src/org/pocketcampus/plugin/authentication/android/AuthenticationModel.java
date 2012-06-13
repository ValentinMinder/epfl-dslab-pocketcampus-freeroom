package org.pocketcampus.plugin.authentication.android;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationModel;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationView;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.TequilaKey;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * AuthenticationModel - The Model that stores the data of this plugin.
 * 
 * This is the Model associated with the Authentication plugin.
 * It stores the data required for the correct functioning of the plugin.
 * Some data is persistent. e.g.tequilaCookie.
 * Other data are temporary.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class AuthenticationModel extends PluginModel implements IAuthenticationModel {
	
	/**
	 * Some constants.
	 */
	private static final String AUTH_STORAGE_NAME = "AUTH_STORAGE_NAME";
	private static final String TEQUILA_COOKIE_KEY = "TEQUILA_COOKIE_KEY";
	private static final String TEQUILA_SERVICE_PREFIX = "TEQUILA_SERVICE_";
	//private static final String STAYSIGNEDIN_KEY = "STAYSIGNEDIN_KEY";
	
	/**
	 * Some utility classes.
	 */
	public static class LocalCredentials {
		public String username;
		public String password;
	}
	/*public class TOSCredentialsComplex {
		public TypeOfService tos;
		public LocalCredentials credentials;
	}*/
	public static class TokenCookieComplex {
		public String token;
		public String cookie;
	}
	
	/**
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	IAuthenticationView mListeners = (IAuthenticationView) getListeners();

	/**
	 * SharedPreferences object responsible for the persistent data storage.
	 */
	private SharedPreferences iStorage;

	/**
	 * Member variables containing required data for the plugin.
	 */
	//private TequilaKey iTequilaKey;
	//private SessionId iSessionId;
	private String tequilaToken;
	private String callbackUrl;
	private String shortName;
	private String longName;
	private LocalCredentials iLocalCredentials = new LocalCredentials();
	private boolean staySignedIn;
	
	/**
	 * Data that need to be persistent.
	 */
	private String tequilaCookie;
	//private boolean staySignedIn;

	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public AuthenticationModel(Context context) {
		iStorage = context.getSharedPreferences(AUTH_STORAGE_NAME, 0);
		tequilaCookie = iStorage.getString(TEQUILA_COOKIE_KEY, null);
		//staySignedIn = iStorage.getBoolean(STAYSIGNEDIN_KEY, false);
	}

	/**
	 * Setters and getters
	 */
	public String getTequilaToken() {
		return tequilaToken;
	}
	public void setTequilaToken(String value) {
		tequilaToken = value;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}
	public void setCallbackUrl(String value) {
		callbackUrl = value;
	}

	public String getShortName() {
		return shortName;
	}
	public void setShortName(String value) {
		shortName = value;
	}

	public String getLongName() {
		return longName;
	}
	public void setLongName(String value) {
		longName = value;
	}
	
	public LocalCredentials getLocalCredentials() {
		return iLocalCredentials;
	}
	public void setLocalCredentials(LocalCredentials val) {
		iLocalCredentials = val;
	}
	
	public boolean getStaySignedIn() {
		return staySignedIn;
	}
	public void setStaySignedIn(boolean val) {
		staySignedIn = val;
	}

	/**
	 * Setter and getter for tequilaCookie.
	 */
	public String getTequilaCookie() {
		return tequilaCookie;
	}
	public void setTequilaCookie(String value, boolean save) {
		tequilaCookie = value;
		if(save) {
			Editor editor = iStorage.edit();
			editor.putString(TEQUILA_COOKIE_KEY, tequilaCookie);
			editor.commit();
		}
		//savePersistentStuff();
		//mListeners.gotTequilaCookie();
	}
	/*public void destroyTequilaCookie() {
		// Should not call gotTequilaCookie here
		tequilaCookie = null;
		savePersistentStuff();
	}*/
	
	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IAuthenticationView.class;
	}
	
	/**
	 * Returns the registered listeners to by notified.
	 */
	/*public IAuthenticationView getListenersToNotify() {
		return mListeners;
	}*/
	
	/**
	 * Helper function to save persistent stuff.
	 */
	/*private void savePersistentStuff() {
		Editor editor = iStorage.edit();
		editor.putString(TEQUILA_COOKIE_KEY, tequilaCookie);
		//editor.putBoolean(STAYSIGNEDIN_KEY, staySignedIn);
		editor.commit();
	}*/
	
	public void setServiceAllowedLevel(String shortName, int level) {
		Editor editor = iStorage.edit();
		editor.putInt(TEQUILA_SERVICE_PREFIX + shortName, level);
		editor.commit();
	}
	
	public int getServiceAllowedLevel(String shortName) {
		return iStorage.getInt(TEQUILA_SERVICE_PREFIX + shortName, 0);
	}
	
}
