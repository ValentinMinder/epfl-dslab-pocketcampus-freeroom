package org.pocketcampus.plugin.moodle.android;

import java.util.Locale;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;
import org.pocketcampus.plugin.moodle.android.iface.IMoodleController;
import org.pocketcampus.plugin.moodle.android.req.CoursesListRequest;
import org.pocketcampus.plugin.moodle.android.req.EventsListRequest;
import org.pocketcampus.plugin.moodle.android.req.SectionsListRequest;
import org.pocketcampus.plugin.moodle.android.MoodleModel;
import org.pocketcampus.plugin.moodle.shared.MoodleRequest;
import org.pocketcampus.plugin.moodle.shared.MoodleService.Client;
import org.pocketcampus.plugin.moodle.shared.MoodleService.Iface;

/**
 * MoodleController - Main logic for the Moodle Plugin.
 * 
 * This class issues requests to the Moodle PocketCampus
 * server to get the Moodle data of the logged in user.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class MoodleController extends PluginController implements IMoodleController{

	private String mPluginName = "moodle";
	
	private MoodleModel mModel;
	private Iface mClientCL;
	private Iface mClientEL;
	private Iface mClientSL;
	
	@Override
	public void onCreate() {
		mModel = new MoodleModel(getApplicationContext());
		mClientCL = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientEL = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientSL = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	public void refreshCoursesList() {
		if(mModel.getMoodleCookie() == null)
			return;
		new CoursesListRequest().start(this, mClientCL, buildSessionId(null));
	}
	
	public void refreshEventsList() {
		if(mModel.getMoodleCookie() == null)
			return;
		new EventsListRequest().start(this, mClientEL, buildSessionId(null));
	}
	
	public void refreshSectionsList(Integer courseId) {
		if(mModel.getMoodleCookie() == null)
			return;
		if(courseId == null)
			return;
		new SectionsListRequest().start(this, mClientSL, buildSessionId(courseId));
	}
	
	private MoodleRequest buildSessionId(Integer courseId) {
		SessionId sessId = new SessionId(TypeOfService.SERVICE_MOODLE);
		sessId.setMoodleCookie(mModel.getMoodleCookie());
		MoodleRequest cr = new MoodleRequest();
		cr.setILanguage(Locale.getDefault().getLanguage());
		cr.setISessionId(sessId);
		if(courseId != null)
			cr.setICourseId(courseId);
		return cr;
	}
	
}
