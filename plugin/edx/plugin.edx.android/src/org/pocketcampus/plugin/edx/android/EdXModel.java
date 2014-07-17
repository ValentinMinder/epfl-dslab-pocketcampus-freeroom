package org.pocketcampus.plugin.edx.android;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.edx.android.iface.IEdXModel;
import org.pocketcampus.plugin.edx.android.iface.IEdXView;
import org.pocketcampus.plugin.edx.shared.EdxCourse;
import org.pocketcampus.plugin.edx.shared.EdxLoginReq;
import org.pocketcampus.plugin.edx.shared.EdxSection;
import org.pocketcampus.plugin.edx.shared.EdxSequence;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * EdXModel - The Model that stores the data of this plugin.
 * 
 * This is the Model associated with the EdX plugin.
 * It stores the data required for the correct functioning of the plugin.
 * Some data is persistent. e.g.edxCookie.
 * Other data are temporary.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class EdXModel extends PluginModel implements IEdXModel {
	
	
	/**
	 * Some constants.
	 */
	private static final String EDX_STORAGE_NAME = "EDX_STORAGE";
	
	private static final String EDX_EMAIL_KEY = "EDX_EMAIL";
	private static final String EDX_PASSWORD_KEY = "EDX_PASSWORD";
	private static final String EDX_SESSION_KEY = "EDX_SESSION";
	private static final String EDX_USER_NAME_KEY = "EDX_USER_NAME";
	
	/**
	 * SharedPreferences object responsible for the persistent data storage.
	 */
	private SharedPreferences iStorage;
	
	/**
	 * Reference to the Views that need to be notified when the stored data changes.
	 */
	IEdXView mListeners = (IEdXView) getListeners();
	
	

	/**
	 * Member variables containing required data for the plugin.
	 */
	private List<EdxCourse> userCourses;
	private List<EdxSection> courseSections;
	private List<EdxSequence> moduleDetails;
	private List<ActiveRoom> activeRooms;
	
	private Map<String, String> videoDesc;
	
	/**
	 * Member variables that need to be persistent
	 */
	private String edxEmail;
	private String edxPassword;
	private String edxSession;
	private String edxUserName;
	
	
	/**
	 * Constructor with reference to the context.
	 * 
	 * We need the context to be able to instantiate
	 * the SharedPreferences object in order to use
	 * persistent storage.
	 * 
	 * @param context is the Application Context.
	 */
	public EdXModel(Context context) {
		iStorage = context.getSharedPreferences(EDX_STORAGE_NAME, 0);
		
		edxEmail = iStorage.getString(EDX_EMAIL_KEY, null);
		edxPassword = iStorage.getString(EDX_PASSWORD_KEY, null);
		edxSession = iStorage.getString(EDX_SESSION_KEY, null);
		edxUserName = iStorage.getString(EDX_USER_NAME_KEY, null);
		
		userCourses = new LinkedList<EdxCourse>();
		courseSections = new LinkedList<EdxSection>();
		moduleDetails = new LinkedList<EdxSequence>();
		
		videoDesc = new HashMap<String, String>();

	}
	
	


	/**
	 * Setter and getter for member variables
	 */
	public List<EdxCourse> getUserCourses() {
		return userCourses;
	}
	public void setUserCourses(List<EdxCourse> courses) {
		userCourses = courses;
		mListeners.userCoursesUpdated();
	}
	public List<EdxSection> getCourseSections() {
		return courseSections;
	}
	public void setCourseSections(List<EdxSection> sections) {
		courseSections = sections;
		mListeners.courseSectionsUpdated();
	}
	public List<EdxSequence> getModuleDetails() {
		return moduleDetails;
	}
	public void setModuleDetails(List<EdxSequence> details) {
		moduleDetails = details;
		mListeners.moduleDetailsUpdated();
	}
	public List<ActiveRoom> getActiveRooms() {
		return activeRooms;
	}
	public void setActiveRooms(List<ActiveRoom> details) {
		activeRooms = details;
		mListeners.activeRoomsUpdated();
	}
	
	public String getVideoDesc(String vidId) {
		return videoDesc.get(vidId);
	}
	public void setVideoDesc(String vidId, String vidDesc) {
		videoDesc.put(vidId, vidDesc);
	}
	
	
	
	

	/**
	 * Setter and getter for persistent stuff
	 */
	public String getEmail() {
		return edxEmail;
	}
	public EdxLoginReq getCredentials() {
		if(edxEmail == null || edxPassword == null)
			return null;
		return new EdxLoginReq(edxEmail, edxPassword);
	}
	public void setCredentials(String email, String password) {
		edxEmail = email;
		edxPassword = password;
		savePrefs();
		mListeners.userCredentialsUpdated();
	}
	public String getSession() {
		return edxSession;
	}
	public void setSession(String session) {
		edxSession = session;
		savePrefs();
	}
	public String getUserName() {
		return edxUserName;
	}
	public void setUserName(String name) {
		edxUserName = name;
		savePrefs();
	}
	
	private void savePrefs() {
		iStorage.edit()
				.putString(EDX_EMAIL_KEY, edxEmail)
				.putString(EDX_PASSWORD_KEY, edxPassword)
				.putString(EDX_SESSION_KEY, edxSession)
				.putString(EDX_USER_NAME_KEY, edxUserName)
				.commit();
	}
	
	
	
	
	
	/**
	 * Returns the Type of the Views associated with this plugin.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IEdXView.class;
	}

	/**
	 * Returns the registered listeners to by notified.
	 */
	public IEdXView getListenersToNotify() {
		return mListeners;
	}
	
	/**
	 * HELPER CLASSES
	 *
	 */
	
	public static class ActiveRoom {
		public String name;
		public int occupancy;
		public ActiveRoom(String n, int o) { name = n; occupancy = o; }
	}
	
	public static class MyMenuItem {
		public int id;
		public String title;
		public String vidID;
		public String roomNbr;
		public boolean prompt;
		public MyMenuItem(int id, String title, String vidID, String roomNbr, boolean prompt) { 
			this.id = id;
			this.title = title;
			this.vidID = vidID;
			this.roomNbr = roomNbr;
			this.prompt = prompt;
		}
	}
	
}
