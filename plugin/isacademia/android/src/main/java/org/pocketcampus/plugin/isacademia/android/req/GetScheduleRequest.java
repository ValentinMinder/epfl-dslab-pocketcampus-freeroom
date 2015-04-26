package org.pocketcampus.plugin.isacademia.android.req;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.isacademia.android.IsAcademiaController;
import org.pocketcampus.plugin.isacademia.android.IsAcademiaModel;
import org.pocketcampus.plugin.isacademia.android.iface.IIsAcademiaView;
import org.pocketcampus.plugin.isacademia.shared.IsAcademiaService.Iface;
import org.pocketcampus.plugin.isacademia.shared.IsaStatusCode;
import org.pocketcampus.plugin.isacademia.shared.ScheduleRequest;
import org.pocketcampus.plugin.isacademia.shared.ScheduleResponse;
import org.pocketcampus.plugin.isacademia.shared.StudyDay;

/**
 * GetScheduleRequest
 * 
 * This class sends an HttpRequest using Thrift to the PocketCampus server
 * in order to get the IsAcademia User Course Schedule.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetScheduleRequest extends Request<IsAcademiaController, Iface, String, ScheduleResponse> {

	private IIsAcademiaView caller;
	private SimpleDateFormat keyFormatter;
	
	public GetScheduleRequest(IIsAcademiaView caller) {
		this.caller = caller;
		
		// convert back and forth between dayKey and timestamp in server's timezone
		keyFormatter = new SimpleDateFormat("yyyyMMdd", Locale.US);
		keyFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
	}
	
	@Override
	protected ScheduleResponse runInBackground(Iface client, String param) throws Exception {
		ScheduleRequest req = new ScheduleRequest();
		req.setLanguage(Locale.getDefault().getLanguage());
		req.setWeekStart(keyFormatter.parse(param).getTime());
		return client.getSchedule(req);
	}

	@Override
	protected void onResult(IsAcademiaController controller, ScheduleResponse result) {
		if(result.getStatusCode() == IsaStatusCode.OK) {
			IsAcademiaModel m = ((IsAcademiaModel) controller.getModel());
			for(StudyDay d : result.getDays()) {
				String key = keyFormatter.format(new Date(d.getDay()));
//				System.out.println("key " + key + " " + d.getPeriodsSize());
				m.putDay(key, d);
			}
			m.getListenersToNotify().scheduleUpdated();
			
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
