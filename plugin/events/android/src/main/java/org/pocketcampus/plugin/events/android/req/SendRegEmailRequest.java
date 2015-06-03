package org.pocketcampus.plugin.events.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.events.android.EventsController;
import org.pocketcampus.plugin.events.android.iface.IEventsView;
import org.pocketcampus.plugin.events.shared.AdminSendRegEmailReply;
import org.pocketcampus.plugin.events.shared.EventsService.Iface;
import org.pocketcampus.plugin.events.shared.AdminSendRegEmailRequest;

/**
 * AdminSendRegEmailRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class SendRegEmailRequest extends Request<EventsController, Iface, AdminSendRegEmailRequest, AdminSendRegEmailReply> {

	private IEventsView caller;
	
	public SendRegEmailRequest(IEventsView caller) {
		this.caller = caller;
	}
	
	@Override
	protected AdminSendRegEmailReply runInBackground(Iface client, AdminSendRegEmailRequest param) throws Exception {
		return client.adminSendRegistrationEmail(param);
	}

	@Override
	protected void onResult(EventsController controller, AdminSendRegEmailReply result) {
		caller.sendAdminRegEmailFinished(result.getStatus() == 200);
	}

	@Override
	protected void onError(EventsController controller, Exception e) {
		caller.networkErrorHappened();
		e.printStackTrace();
	}
	
}
