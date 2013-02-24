package org.pocketcampus.plugin.events.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.events.android.EventsController;
import org.pocketcampus.plugin.events.android.EventsModel;
import org.pocketcampus.plugin.events.shared.Constants;
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

	@Override
	protected EventItemReply runInBackground(Iface client, EventItemRequest param) throws Exception {
		return client.getEventItem(param);
	}

	@Override
	protected void onResult(EventsController controller, EventItemReply result) {
		if(result.getStatus() == 200) {
			((EventsModel) controller.getModel()).addEventPools(result.getChildrenPools());
			((EventsModel) controller.getModel()).addEventItem(result.getEventItem());
			if(result.isSetCategs()) {
				Constants.EVENTS_CATEGS.clear();
				Constants.EVENTS_CATEGS.putAll(result.getCategs());
			}
			if(result.isSetTags()) {
				Constants.EVENTS_TAGS.clear();
				Constants.EVENTS_TAGS.putAll(result.getTags());
			}
			keepInCache();
		} else if(result.getStatus() == 407) {
			((EventsModel) controller.getModel()).getListenersToNotify().identificationRequired();
		} else {
			((EventsModel) controller.getModel()).getListenersToNotify().mementoServersDown();
		}
	}

	@Override
	protected void onError(EventsController controller, Exception e) {
		if(foundInCache())
			((EventsModel) controller.getModel()).getListenersToNotify().networkErrorCacheExists();
		else
			controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
	
}
