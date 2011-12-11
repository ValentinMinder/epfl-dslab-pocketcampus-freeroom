package org.pocketcampus.plugin.isacademia.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.isacademia.android.iface.IIsacademiaModel;
import org.pocketcampus.plugin.isacademia.android.iface.IIsacademiaView;
import org.pocketcampus.plugin.isacademia.shared.Course;
import org.pocketcampus.plugin.isacademia.shared.Exam;
import org.pocketcampus.plugin.isacademia.shared.Seance;

public class IsacademiaModel extends PluginModel implements IIsacademiaModel {
	
	private IsacademiaModel() {
		
	}
	
	public static IsacademiaModel getInstance(){
		if(self == null)
			self = new IsacademiaModel();
		return self;
	}
	
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IIsacademiaView.class;
	}

	public List<Course> getCourses() {
		return iCourses;
	}

	public List<Exam> getExams() {
		return iExams;
	}

	public List<Seance> getSchedule() {
		return iSchedule;
	}

	public void setCourses(List<Course> arg) {
		iCourses = arg;
		mListeners.coursesUpdated();
	}
	
	public void setExams(List<Exam> arg) {
		iExams = arg;
		mListeners.examsUpdated();
	}
	
	public void setSchedule(List<Seance> arg) {
		iSchedule = arg;
		mListeners.scheduleUpdated();
	}

	IIsacademiaView mListeners = (IIsacademiaView) getListeners();
	
	public void setIsacademiaCookie(String aCookie) {
		isacademiaCookie = aCookie;
	}
	
	public String getIsacademiaCookie() {
		return isacademiaCookie;
	}
	
	//TODO have IS-Academia cookie saved in storage
	private String isacademiaCookie = null;
	
	private List<Course> iCourses = null;
	private List<Exam> iExams = null;
	private List<Seance> iSchedule = null;
	
	private static IsacademiaModel self = null;

}
