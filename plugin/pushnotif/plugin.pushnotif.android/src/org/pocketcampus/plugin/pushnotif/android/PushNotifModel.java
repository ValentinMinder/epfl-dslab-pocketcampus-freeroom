package org.pocketcampus.plugin.pushnotif.android;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.pushnotif.android.iface.IPushNotifModel;
import org.pocketcampus.plugin.pushnotif.android.iface.IPushNotifView;

import android.content.Context;

/**
 * PushNotifModel - The Model that stores the data of this plugin.
 * 
 * This is the Model associated with the PushNotif plugin.
 * It stores the data required for the correct functioning of the plugin.
 * Some data is persistent. e.g.pushnotifCookie.
 * Other data are temporary.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class PushNotifModel extends PluginModel implements IPushNotifModel {
	
	/**
	 * Some constants.
	 */
	//private static final String PUSHNOTIF_STORAGE_NAME = "PUSHNOTIF_STORAGE_NAME";
	//private static final String PUSHNOTIF_COOKIE_KEY = "PUSHNOTIF_COOKIE_KEY";
	
	/**
	 * Utility class ResourceCookieComplex
	 */
	/*public static class ResourceCookieComplex {
		public String resource;
		public String cookie;
	}*/
	
	/**
	 * SharedPreferences object responsible for the persistent data storage.
	 */
	//private SharedPreferences iStorage;

	/**
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	IPushNotifView mListeners = (IPushNotifView) getListeners();
	
	/**
	 * Member variables containing required data for the plugin.
	 */
	//private TequilaToken tequilaToken;
	//private boolean forceReauth;
	
	/**
	 * Data that need to be persistent.
	 */
	//private String pushnotifCookie;
	
	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public PushNotifModel(Context context) {
		//iStorage = context.getSharedPreferences(PUSHNOTIF_STORAGE_NAME, 0);
		//pushnotifCookie = iStorage.getString(PUSHNOTIF_COOKIE_KEY, null);
		
	}
	/*
	public boolean getForceReauth() {
		return forceReauth;
	}
	public void setForceReauth(boolean val) {
		forceReauth = val;
	}
	*/
	/**
	 * Setter and getter for iCourses
	 */
	/*public List<PushNotifCourse> getCourses() {
		return iCourses;
	}
	public void setCourses(List<PushNotifCourse> obj) {
		iCourses = obj;
		mListeners.coursesListUpdated();
	}*/
	
	/**
	 * Setter and getter for iEvents
	 */
	/*public List<PushNotifEvent> getEvents() {
		return iEvents;
	}
	public void setEvents(List<PushNotifEvent> obj) {
		iEvents = obj;
		mListeners.eventsListUpdated();
	}*/
	
	/**
	 * Setter and getter for iSections
	 */
	/*public List<PushNotifSection> getSections() {
		return iSections;
	}
	public void setSections(List<PushNotifSection> obj) {
		iSections = obj;
		mListeners.sectionsListUpdated();
	}*/
	
	/**
	 * Setter and getter for tequilaToken
	 */
	/*public TequilaToken getTequilaToken() {
		return tequilaToken;
	}
	public void setTequilaToken(TequilaToken arg) {
		tequilaToken = arg;
	}*/
	
	/**
	 * Setter and getter for pushnotifCookie
	 */
	/*public String getPushNotifCookie() {
		return pushnotifCookie;
	}
	public void setPushNotifCookie(String aPushNotifCookie) {
		pushnotifCookie = aPushNotifCookie;
		if(!forceReauth) {
			Editor editor = iStorage.edit();
			editor.putString(PUSHNOTIF_COOKIE_KEY, pushnotifCookie);
			editor.commit();
		}
	}*/
	
	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IPushNotifView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public IPushNotifView getListenersToNotify() {
		return mListeners;
	}
	
}
