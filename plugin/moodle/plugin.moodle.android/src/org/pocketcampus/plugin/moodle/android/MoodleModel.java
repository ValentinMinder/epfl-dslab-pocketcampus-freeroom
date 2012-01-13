package org.pocketcampus.plugin.moodle.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleModel;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleView;
import org.pocketcampus.plugin.moodle.shared.MoodleCourse;

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
	
	/**
	 * Setter and getter for iCourses
	 */
	public List<MoodleCourse> getTransactions() {
		return iCourses;
	}
	public void setTransactions(List<MoodleCourse> trans) {
		iCourses = trans;
		mListeners.coursesListUpdated();
	}
	
	/**
	 * Setter and getter for moodleCookie
	 */
	public String getMoodleCookie() {
		return moodleCookie;
	}
	public void setMoodleCookie(String aMoodleCookie) {
		moodleCookie = aMoodleCookie;
		Editor editor = iStorage.edit();
		editor.putString(MOODLE_COOKIE_KEY, moodleCookie);
		editor.commit();
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
