package org.pocketcampus.plugin.authentication.android;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationModel;
import org.pocketcampus.plugin.authentication.android.iface.IAuthenticationView;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.TequilaKey;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

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
	private static final String STAYSIGNEDIN_KEY = "STAYSIGNEDIN_KEY";
	
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
	private TequilaKey iTequilaKey;
	private SessionId iSessionId;
	
	/**
	 * Data that need to be persistent.
	 */
	private String tequilaCookie;
	private boolean staySignedIn;

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
		staySignedIn = iStorage.getBoolean(STAYSIGNEDIN_KEY, false);
	}

	/**
	 * Setter and getter for iTequilaKey.
	 */
	public TequilaKey getTequilaKey() {
		return iTequilaKey;
	}
	public void setTequilaKey(TequilaKey value) {
		iTequilaKey = value;
		mListeners.gotTequilaKey();
	}

	/**
	 * Setter and getter for iSessionId.
	 */
	public SessionId getSessionId() {
		return iSessionId;
	}
	public void setSessionId(SessionId value) {
		iSessionId = value;
		mListeners.gotSessionId();
	}

	/**
	 * Setter and getter for tequilaCookie.
	 */
	public boolean getStaySignedIn() {
		return staySignedIn;
	}
	public void setStaySignedIn(boolean value) {
		staySignedIn = value;
		savePersistentStuff();
	}
	
	/**
	 * Setter and getter for tequilaCookie.
	 */
	public String getTequilaCookie() {
		return tequilaCookie;
	}
	public void setTequilaCookie(String value) {
		tequilaCookie = value;
		savePersistentStuff();
		mListeners.gotTequilaCookie();
	}
	public void destroyTequilaCookie() {
		// Should not call gotTequilaCookie here
		tequilaCookie = null;
		savePersistentStuff();
	}

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
	public IAuthenticationView getListenersToNotify() {
		return mListeners;
	}
	
	/**
	 * Helper function to save persistent stuff.
	 */
	private void savePersistentStuff() {
		Editor editor = iStorage.edit();
		editor.putString(TEQUILA_COOKIE_KEY, tequilaCookie);
		editor.putBoolean(STAYSIGNEDIN_KEY, staySignedIn);
		editor.commit();
	}
	
}
