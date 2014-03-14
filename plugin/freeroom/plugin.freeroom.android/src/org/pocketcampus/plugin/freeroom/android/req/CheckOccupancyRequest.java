package org.pocketcampus.plugin.freeroom.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService.Iface;
import org.pocketcampus.plugin.freeroom.shared.OccupancyReply;
import org.pocketcampus.plugin.freeroom.shared.OccupancyRequest;

import android.util.Log;

/**
 * CheckOccupancyRequest class sends an HttpRequest using Thrift to the
 * PocketCampus server in order to get the Occupancy of a set of rooms, for a
 * given period of time.
 * <p>
 * 
 * @author FreeFroom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */

public class CheckOccupancyRequest extends
		Request<FreeRoomController, Iface, OccupancyRequest, OccupancyReply> {

	private IFreeRoomView caller;

	public CheckOccupancyRequest(IFreeRoomView caller) {
		this.caller = caller;
	}

	@Override
	protected OccupancyReply runInBackground(Iface client,
			OccupancyRequest param) throws Exception {
		return client.checkTheOccupancy(param);
	}

	@Override
	protected void onResult(FreeRoomController controller, OccupancyReply result) {
		int status = result.getStatus();
		if (status == 200) {
			Log.v(this.getClass().toString(), "server replied successfully");
			controller.setCheckOccupancyResults(result);
		} else {
			controller.handleReplyError(caller, status,
					result.getStatusComment(), this.getClass().toString());
		}
	}

	@Override
	protected void onError(FreeRoomController controller, Exception e) {
		caller.networkErrorHappened();
		e.printStackTrace();
	}
}
