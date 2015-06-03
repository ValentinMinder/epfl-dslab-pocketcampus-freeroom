package org.pocketcampus.plugin.events.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.events.android.EventsController;
import org.pocketcampus.plugin.events.android.EventsModel;
import org.pocketcampus.plugin.events.android.iface.IEventsView;
import org.pocketcampus.plugin.events.shared.EventItemReply;
import org.pocketcampus.plugin.events.shared.EventItemRequest;
import org.pocketcampus.plugin.events.shared.EventsService.Iface;

/**
 * GetEventItemChildrenRequest
 * 
 * This class sends an HttpRequest using Thrift to the PocketCampus server
 * in order to get the list of EventItems.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetEventItemRequest extends Request<EventsController, Iface, EventItemRequest, EventItemReply> {

	private IEventsView caller;
	
	public GetEventItemRequest(IEventsView caller) {
		this.caller = caller;
	}
	
	@Override
	protected EventItemReply runInBackground(Iface client, EventItemRequest param) throws Exception {
		return client.getEventItem(param);
	}

	@Override
	protected void onResult(EventsController controller, EventItemReply result) {
		if(result.getStatus() == 200) {
			if(result.isSetCategs())
				EventsController.updateEventCategs(result.getCategs());
			if(result.isSetTags())
				EventsController.updateEventTags(result.getTags());
			
			((EventsModel) controller.getModel()).addEventPools(result.getChildrenPools());
			((EventsModel) controller.getModel()).addEventItem(result.getEventItem());
			
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
