package org.pocketcampus.plugin.isacademia.android;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;
import org.pocketcampus.plugin.isacademia.android.iface.IIsacademiaController;
import org.pocketcampus.plugin.isacademia.android.req.GetUserCoursesRequest;
import org.pocketcampus.plugin.isacademia.android.req.GetUserExamsRequest;
import org.pocketcampus.plugin.isacademia.android.req.GetUserScheduleRequest;
import org.pocketcampus.plugin.isacademia.shared.IsacademiaService.Iface;
import org.pocketcampus.plugin.isacademia.shared.IsacademiaService.Client;

import android.util.Log;

public class IsacademiaController extends PluginController implements IIsacademiaController{

	private String mPluginName = "isacademia";
	

	@Override
	public void onCreate() {
		Log.v("DEBUG", "onCreate called on CamiproController");
		// Initializing the model is part of the controller's job...
		mModel = IsacademiaModel.getInstance();
		
		// ...as well as initializing the client.
		// The "client" is the connection we use to access the service.
		mClientC = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientE = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientS = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	public void setIsacademiaCookie(String sessId) {
		mModel.setIsacademiaCookie(sessId);
	}
	
	public String getIsacademiaCookie() {
		return mModel.getIsacademiaCookie();
	}
	
	public void refreshCourses() {
		if(mModel.getIsacademiaCookie() == null)
			return;
		new GetUserCoursesRequest().start(this, mClientC, buildSessionId());
	}
	
	public void refreshExams() {
		if(mModel.getIsacademiaCookie() == null)
			return;
		new GetUserExamsRequest().start(this, mClientE, buildSessionId());
	}
	
	public void refreshSchedule() {
		if(mModel.getIsacademiaCookie() == null)
			return;
		new GetUserScheduleRequest().start(this, mClientS, buildSessionId());
	}
	
	private SessionId buildSessionId() {
		SessionId sessId = new SessionId(TypeOfService.SERVICE_ISA);
		sessId.setIsaCookie(mModel.getIsacademiaCookie());
		return sessId;
	}
	
	private IsacademiaModel mModel;
	private Iface mClientC;
	private Iface mClientE;
	private Iface mClientS;
	
}
