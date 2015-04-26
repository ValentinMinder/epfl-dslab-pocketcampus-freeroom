package org.pocketcampus.plugin.events.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.events.android.EventsController;
import org.pocketcampus.plugin.events.android.iface.IEventsView;
import org.pocketcampus.plugin.events.shared.EventsService.Iface;
import org.pocketcampus.plugin.events.shared.SendEmailReply;
import org.pocketcampus.plugin.events.shared.SendEmailRequest;

/**
 * SendFavoritesByEmailRequest
 * 
 * This class sends an HttpRequest using Thrift to the PocketCampus server
 * in order to request the starred eventItems to be sent by email.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class SendFavoritesByEmailRequest extends Request<EventsController, Iface, SendEmailRequest, SendEmailReply> {

	private IEventsView caller;
	
	public SendFavoritesByEmailRequest(IEventsView caller) {
		this.caller = caller;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		caller.showLoading();
	}
	
	@Override
	protected SendEmailReply runInBackground(Iface client, SendEmailRequest param) throws Exception {
		return client.sendStarredItemsByEmail(param);
	}

	@Override
	protected void onResult(EventsController controller, SendEmailReply result) {
		caller.hideLoading();
		caller.sendEmailRequestFinished(result.getStatus() == 200);
	}

	@Override
	protected void onError(EventsController controller, Exception e) {
		caller.hideLoading();
		caller.networkErrorHappened();
		e.printStackTrace();
	}
	
}
