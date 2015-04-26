package org.pocketcampus.plugin.events.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.events.android.EventsController;
import org.pocketcampus.plugin.events.android.iface.IEventsView;
import org.pocketcampus.plugin.events.shared.EventsService.Iface;
import org.pocketcampus.plugin.events.shared.ExchangeReply;
import org.pocketcampus.plugin.events.shared.ExchangeRequest;

/**
 * GetEventItemChildrenRequest
 * 
 * This class sends an HttpRequest using Thrift to the PocketCampus server
 * in order to get the list of EventItems.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class ExchangeContactsRequest extends Request<EventsController, Iface, ExchangeRequest, ExchangeReply> {

	private IEventsView caller;
	
	public ExchangeContactsRequest(IEventsView caller) {
		this.caller = caller;
	}
	
	@Override
	protected ExchangeReply runInBackground(Iface client, ExchangeRequest param) throws Exception {
		return client.exchangeContacts(param);
	}

	@Override
	protected void onResult(EventsController controller, ExchangeReply result) {
		caller.exchangeContactsFinished(result.getStatus() == 200);
	}

	@Override
	protected void onError(EventsController controller, Exception e) {
		caller.networkErrorHappened();
		e.printStackTrace();
	}
	
}
