package org.pocketcampus.plugin.isacademia.android;

import java.util.Locale;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;
import org.pocketcampus.plugin.isacademia.android.iface.IIsacademiaController;
import org.pocketcampus.plugin.isacademia.android.req.GetUserCoursesRequest;
import org.pocketcampus.plugin.isacademia.android.req.GetUserExamsRequest;
import org.pocketcampus.plugin.isacademia.android.req.GetUserScheduleRequest;
import org.pocketcampus.plugin.isacademia.shared.IsaRequest;
import org.pocketcampus.plugin.isacademia.shared.IsacademiaService.Iface;
import org.pocketcampus.plugin.isacademia.shared.IsacademiaService.Client;

/**
 * IsacademiaController - Main logic for the Isacademia Plugin.
 * 
 * This class issues requests to the Isacademia PocketCampus
 * server to get the Isacademia data of the logged in user.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class IsacademiaController extends PluginController implements IIsacademiaController{

	private String mPluginName = "isacademia";
	
	private IsacademiaModel mModel;
	private Iface mClientC;
	private Iface mClientE;
	private Iface mClientS;
	
	@Override
	public void onCreate() {
		mModel = new IsacademiaModel(getApplicationContext());
		mClientC = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientE = (Iface) getClient(new Client.Factory(), mPluginName);
		mClientS = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	@Override
	public PluginModel getModel() {
		return mModel;
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
	
	private IsaRequest buildSessionId() {
		SessionId sessId = new SessionId(TypeOfService.SERVICE_ISA);
		sessId.setIsaCookie(mModel.getIsacademiaCookie());
		IsaRequest ir = new IsaRequest();
		ir.setISessionId(sessId);
		ir.setILanguage(Locale.getDefault().getLanguage());
		return ir;
	}
	
}
