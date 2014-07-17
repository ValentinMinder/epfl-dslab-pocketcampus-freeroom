package org.pocketcampus.plugin.moodle.android;

import java.util.List;

import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleModel;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleView;
import org.pocketcampus.plugin.moodle.shared.MoodleCourse2;
import org.pocketcampus.plugin.moodle.shared.MoodleCourseSection2;

import android.content.Context;

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
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	IMoodleView mListeners = (IMoodleView) getListeners();
	
	/**
	 * Member variables containing required data for the plugin.
	 */
	private List<MoodleCourse2> iCourses;
	private List<MoodleCourseSection2> iSections;
	
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
	}
		
	/**
	 * Setter and getter for iCourses
	 */
	public List<MoodleCourse2> getCourses() {
		return iCourses;
	}
	public void setCourses(List<MoodleCourse2> obj) {
		iCourses = obj;
		mListeners.coursesListUpdated();
	}
	
	/**
	 * Setter and getter for iSections
	 */
	public List<MoodleCourseSection2> getSections() {
		return iSections;
	}
	public void setSections(List<MoodleCourseSection2> obj) {
		iSections = obj;
		mListeners.sectionsListUpdated();
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
