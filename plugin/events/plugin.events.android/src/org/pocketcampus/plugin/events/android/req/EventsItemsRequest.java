package org.pocketcampus.plugin.events.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.events.android.EventsController;
import org.pocketcampus.plugin.events.android.EventsModel;
import org.pocketcampus.plugin.events.shared.EventsItem;
import org.pocketcampus.plugin.events.shared.EventsService.Iface;
import org.pocketcampus.plugin.events.shared.EventsService.getEventsItems_args;

/**
 * 
 * A request to the server to retrieve the eventsItems
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class EventsItemsRequest extends
		Request<EventsController, Iface, getEventsItems_args, List<EventsItem>> {
	/**
	 * Initiate the <code>getEventsItem</code> Request at the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters to be sent for the request. Not used.
	 * @return the Events Items
	 */
	@Override
	protected List<EventsItem> runInBackground(Iface client, getEventsItems_args param)
			throws Exception {
		System.out.println("<Events> Requesting events items");
		return client.getEventsItems(param.getLanguage(), param.getFeedsToGet());
	}

	/**
	 * Tell the model the EventsItems have been updated.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result
	 * @param result
	 *            the EventsItem list gotten from the server
	 */
	@Override
	protected void onResult(EventsController controller, List<EventsItem> result) {
		System.out.println("<EventsItemsRequest> onResult");
		((EventsModel) controller.getModel()).setEvents(result);
	}

	/**
	 * Notifies the Model that an error has occurred while processing the
	 * request.
	 * 
	 * @param controller
	 *            the controller that initiated the request
	 */
	@Override
	protected void onError(EventsController controller, Exception e) {
		System.out.println("onError");
		((EventsModel) controller.getModel()).notifyNetworkError();
		e.printStackTrace();
	}
}