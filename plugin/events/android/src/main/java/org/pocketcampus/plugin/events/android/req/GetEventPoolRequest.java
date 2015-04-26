package org.pocketcampus.plugin.events.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.events.android.EventsController;
import org.pocketcampus.plugin.events.android.EventsModel;
import org.pocketcampus.plugin.events.android.iface.IEventsView;
import org.pocketcampus.plugin.events.shared.EventPoolReply;
import org.pocketcampus.plugin.events.shared.EventPoolRequest;
import org.pocketcampus.plugin.events.shared.EventsService.Iface;

/**
 * GetEventPoolChildrenRequest
 * 
 * This class sends an HttpRequest using Thrift to the PocketCampus server
 * in order to get the list of EventPools.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetEventPoolRequest extends Request<EventsController, Iface, EventPoolRequest, EventPoolReply> {

	private IEventsView caller;
	
	public GetEventPoolRequest(IEventsView caller) {
		this.caller = caller;
	}
	
	@Override
	protected EventPoolReply runInBackground(Iface client, EventPoolRequest param) throws Exception {
		return client.getEventPool(param);
	}

	@Override
	protected void onResult(EventsController controller, EventPoolReply result) {
		if(result.getStatus() == 200) {
			if(result.isSetCategs())
				EventsController.updateEventCategs(result.getCategs());
			if(result.isSetTags())
				EventsController.updateEventTags(result.getTags());
			
			((EventsModel) controller.getModel()).addEventItems(result.getChildrenItems());
			((EventsModel) controller.getModel()).addEventPool(result.getEventPool());

			keepInCache();
		} else {
			caller.mementoServersDown();
		}
	}

	@Override
	protected void onError(EventsController controller, Exception e) {
		if(foundInCache())
			caller.networkErrorCacheExists();
		else
			caller.networkErrorHappened();
		e.printStackTrace();
	}
	
}
