package org.pocketcampus.plugin.isacademia.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.isacademia.shared.TequilaToken;
import org.pocketcampus.plugin.isacademia.android.iface.IIsacademiaModel;
import org.pocketcampus.plugin.isacademia.android.iface.IIsacademiaView;
import org.pocketcampus.plugin.isacademia.shared.IsaCourse;
import org.pocketcampus.plugin.isacademia.shared.IsaExam;
import org.pocketcampus.plugin.isacademia.shared.IsaSeance;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * IsacademiaModel - The Model that stores the data of this plugin.
 * 
 * This is the Model associated with the Isacademia plugin.
 * It stores the data required for the correct functioning of the plugin.
 * Some data is persistent. e.g. isacademiaCookie.
 * Other data are temporary.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class IsacademiaModel extends PluginModel implements IIsacademiaModel {
	
	/**
	 * Some constants.
	 */
	private static final String ISA_STORAGE_NAME = "ISA_STORAGE_NAME";
	private static final String ISA_COOKIE_KEY = "ISA_COOKIE_KEY";
	
	/**
	 * SharedPreferences object responsible for the persistent data storage.
	 */
	private SharedPreferences iStorage;

	/**
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	IIsacademiaView mListeners = (IIsacademiaView) getListeners();
	
	/**
	 * Member variables containing required data for the plugin.
	 */
	private List<IsaCourse> iCourses;
	private List<IsaExam> iExams;
	private List<IsaSeance> iSchedule;
	private boolean forceReauth;
	private TequilaToken tequilaToken;
	
	/**
	 * Data that need to be persistent.
	 */
	private String isacademiaCookie;
	
	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public IsacademiaModel(Context context) {
		iStorage = context.getSharedPreferences(ISA_STORAGE_NAME, 0);
		isacademiaCookie = iStorage.getString(ISA_COOKIE_KEY, null);
	}
	
	public boolean getForceReauth() {
		return forceReauth;
	}
	public void setForceReauth(boolean val) {
		forceReauth = val;
	}
	
	public TequilaToken getTequilaToken() {
		return tequilaToken;
	}
	public void setTequilaToken(TequilaToken arg) {
		tequilaToken = arg;
	}
	
	/**
	 * Getter and setter for iCourses
	 */
	public List<IsaCourse> getCourses() {
		return iCourses;
	}
	public void setCourses(List<IsaCourse> arg) {
		iCourses = arg;
		mListeners.coursesUpdated();
	}

	/**
	 * Getter and setter for iExams
	 */
	public List<IsaExam> getExams() {
		return iExams;
	}
	public void setExams(List<IsaExam> arg) {
		iExams = arg;
		mListeners.examsUpdated();
	}
	
	/**
	 * Getter and setter for iSchedule
	 */
	public List<IsaSeance> getSchedule() {
		return iSchedule;
	}
	public void setSchedule(List<IsaSeance> arg) {
		iSchedule = arg;
		mListeners.scheduleUpdated();
	}

	/**
	 * Getter and setter for isacademiaCookie
	 */
	public String getIsacademiaCookie() {
		return isacademiaCookie;
	}
	public void setIsacademiaCookie(String aCookie) {
		isacademiaCookie = aCookie;
		if(!forceReauth) {
			Editor editor = iStorage.edit();
			editor.putString(ISA_COOKIE_KEY, isacademiaCookie);
			editor.commit();
		}
	}
	
	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IIsacademiaView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public IIsacademiaView getListenersToNotify() {
		return mListeners;
	}
	
}
