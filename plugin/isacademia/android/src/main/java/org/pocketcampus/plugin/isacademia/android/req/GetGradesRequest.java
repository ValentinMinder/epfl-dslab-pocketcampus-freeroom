package org.pocketcampus.plugin.isacademia.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.isacademia.android.IsAcademiaController;
import org.pocketcampus.plugin.isacademia.android.IsAcademiaModel;
import org.pocketcampus.plugin.isacademia.android.iface.IIsAcademiaView;
import org.pocketcampus.plugin.isacademia.shared.*;
import org.pocketcampus.plugin.isacademia.shared.IsAcademiaService.Iface;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * GetGradesRequest
 * 
 * This class sends an HttpRequest using Thrift to the PocketCampus server
 * in order to get the IsAcademia User Course grades.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetGradesRequest extends Request<IsAcademiaController, Iface, Void, IsaGradesResponse> {

	private IIsAcademiaView caller;

	public GetGradesRequest(IIsAcademiaView caller) {
		this.caller = caller;
		
	}
	
	@Override
	protected IsaGradesResponse runInBackground(Iface client, Void param) throws Exception {
		return client.getGrades();
	}

	@Override
	protected void onResult(IsAcademiaController controller, IsaGradesResponse result) {
		if(result.getStatusCode() == IsaStatusCode.OK) {

			IsAcademiaModel m = ((IsAcademiaModel) controller.getModel());
			m.setGrades(result.getSemesters());


			m.getListenersToNotify().gradesUpdated();
			
			keepInCache();
			
		} else if(result.getStatusCode() == IsaStatusCode.INVALID_SESSION) {
			caller.notLoggedIn();
			
		} else {
//			System.out.println(result.getStatusCode().name());
			caller.isacademiaServersDown();
			
		}
	}

	@Override
	protected void onError(IsAcademiaController controller, Exception e) {
		if(foundInCache())
			caller.networkErrorCacheExists();
		else
			caller.networkErrorHappened();
		e.printStackTrace();
	}
	
}
