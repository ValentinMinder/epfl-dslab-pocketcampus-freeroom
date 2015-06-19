package org.pocketcampus.plugin.isacademia.android;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.isacademia.android.iface.IIsAcademiaModel;
import org.pocketcampus.plugin.isacademia.android.iface.IIsAcademiaView;
import org.pocketcampus.plugin.isacademia.shared.SemesterGrades;
import org.pocketcampus.plugin.isacademia.shared.StudyDay;
import org.pocketcampus.plugin.isacademia.shared.StudyPeriod;

import android.content.Context;

/**
 * IsAcademiaModel - The Model that stores the data of this plugin.
 * 
 * This is the Model associated with the IsAcademia plugin.
 * It stores the data required for the correct functioning of the plugin.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class IsAcademiaModel extends PluginModel implements IIsAcademiaModel {
	
	/**
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	IIsAcademiaView mListeners = (IIsAcademiaView) getListeners();
	
	/**
	 * Member variables containing required data for the plugin.
	 */
	private Map<String, StudyDay> iSchedule;
	private List<SemesterGrades> iGrades;
	
	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public IsAcademiaModel(Context context) {
		iSchedule = new HashMap<String, StudyDay>();
	}
		
	/**
	 * Setter and getter for iSchedule
	 */
	public void putDay(String key, StudyDay day) {
		iSchedule.put(key, day);
	}
	public List<StudyPeriod> getDay(String key) {
		StudyDay d = iSchedule.get(key);
		return (d == null ? null : d.getPeriods());
	}
	/**
	 * Setter and getter for iGrades
	 */
	public void setGrades(List<SemesterGrades> g) {
		iGrades = g;
	}
	public List<SemesterGrades> getGrades() {
		return iGrades;
	}

	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IIsAcademiaView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public IIsAcademiaView getListenersToNotify() {
		return mListeners;
	}
	
}
