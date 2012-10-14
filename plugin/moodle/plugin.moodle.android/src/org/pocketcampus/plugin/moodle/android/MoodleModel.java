package org.pocketcampus.plugin.moodle.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.moodle.shared.TequilaToken;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleModel;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleView;
import org.pocketcampus.plugin.moodle.shared.MoodleCourse;
import org.pocketcampus.plugin.moodle.shared.MoodleEvent;
import org.pocketcampus.plugin.moodle.shared.MoodleSection;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * MoodleModel - The Model that stores the data of this plugin.
 * 
 * This is the Model associated with the Moodle plugin.
 * It stores the data required for the correct functioning of the plugin.
 * Some data is persistent. e.g.moodleCookie.
 * Other data are temporary.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class MoodleModel extends PluginModel implements IMoodleModel {
	
	/**
	 * Some constants.
	 */
	private static final String MOODLE_STORAGE_NAME = "MOODLE_STORAGE_NAME";
	private static final String MOODLE_COOKIE_KEY = "MOODLE_COOKIE_KEY";
	
	/**
	 * Utility class ResourceCookieComplex
	 */
	public static class ResourceCookieComplex {
		public String resource;
		public String cookie;
	}
	
	/**
	 * SharedPreferences object responsible for the persistent data storage.
	 */
	private SharedPreferences iStorage;

	/**
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	IMoodleView mListeners = (IMoodleView) getListeners();
	
	/**
	 * Member variables containing required data for the plugin.
	 */
	private List<MoodleCourse> iCourses;
	private List<MoodleEvent> iEvents;
	private List<MoodleSection> iSections;
	private TequilaToken tequilaToken;
	private boolean forceReauth;
	
	/**
	 * Data that need to be persistent.
	 */
	private String moodleCookie;
	
	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public MoodleModel(Context context) {
		iStorage = context.getSharedPreferences(MOODLE_STORAGE_NAME, 0);
		moodleCookie = iStorage.getString(MOODLE_COOKIE_KEY, null);
		
	}
	
	public boolean getForceReauth() {
		return forceReauth;
	}
	public void setForceReauth(boolean val) {
		forceReauth = val;
	}
	
	/**
	 * Setter and getter for iCourses
	 */
	public List<MoodleCourse> getCourses() {
		return iCourses;
	}
	public void setCourses(List<MoodleCourse> obj) {
		iCourses = obj;
		mListeners.coursesListUpdated();
	}
	
	/**
	 * Setter and getter for iEvents
	 */
	public List<MoodleEvent> getEvents() {
		return iEvents;
	}
	public void setEvents(List<MoodleEvent> obj) {
		iEvents = obj;
		mListeners.eventsListUpdated();
	}
	
	/**
	 * Setter and getter for iSections
	 */
	public List<MoodleSection> getSections() {
		return iSections;
	}
	public void setSections(List<MoodleSection> obj) {
		iSections = obj;
		mListeners.sectionsListUpdated();
	}
	
	/**
	 * Setter and getter for tequilaToken
	 */
	public TequilaToken getTequilaToken() {
		return tequilaToken;
	}
	public void setTequilaToken(TequilaToken arg) {
		tequilaToken = arg;
	}
	
	/**
	 * Setter and getter for moodleCookie
	 */
	public String getMoodleCookie() {
		return moodleCookie;
	}
	public void setMoodleCookie(String aMoodleCookie) {
		moodleCookie = aMoodleCookie;
		if(!forceReauth) {
			Editor editor = iStorage.edit();
			editor.putString(MOODLE_COOKIE_KEY, moodleCookie);
			editor.commit();
		}
	}
	
	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IMoodleView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public IMoodleView getListenersToNotify() {
		return mListeners;
	}
	
}
