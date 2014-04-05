package org.pocketcampus.plugin.freeroom.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FRReply;
import org.pocketcampus.plugin.freeroom.shared.FRRequest;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService.Iface;

/**
 * FRRequestASyncTask class sends an HttpRequest using Thrift to the
 * PocketCampus server in order to get the Occupancy, for a given period of
 * time, for an optional set of rooms
 * <p>
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */

public class FRRequestASyncTask extends
		Request<FreeRoomController, Iface, FRRequest, FRReply> {

	private IFreeRoomView caller;

	public FRRequestASyncTask(IFreeRoomView caller) {
		this.caller = caller;
	}

	@Override
	protected FRReply runInBackground(Iface client, FRRequest request)
			throws Exception {
		return client.getOccupancy(request);
	}

	@Override
	protected void onResult(FreeRoomController controller, FRReply result) {
		int status = result.getStatus();
		if (status == 200) {
			controller.handleReplySuccess(caller, status,
					result.getStatusComment(), this.getClass().getName(),
					result.getClass().getSimpleName());
			controller.setOccupancyResults(result);
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
