package org.pocketcampus.plugin.freeroom.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FROccupancyReply;
import org.pocketcampus.plugin.freeroom.shared.FROccupancyRequest;
import org.pocketcampus.plugin.freeroom.shared.FRStatusCode;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService.Iface;

/**
 * <code>FRRequestASyncTask</code> is an extension of <code>Request</code> and
 * <code>ASyncTask</code> that is used to send a <code>FRRequest</code> request
 * to the server and handle the <code>FRReply</code> reply received from the
 * server, thru an Http and Thrift exchange with the PocketCampus server.
 * <p>
 * It's used to get the detailed occupancy, for a given period of time, for an
 * optional set of rooms.
 * <p>
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */

public class FRRequestASyncTask extends
		Request<FreeRoomController, Iface, FROccupancyRequest, FROccupancyReply> {

	private IFreeRoomView callerView;

	public FRRequestASyncTask(IFreeRoomView callerView) {
		this.callerView = callerView;
	}

	@Override
	protected FROccupancyReply runInBackground(Iface clientInterface, FROccupancyRequest request)
			throws Exception {
		return clientInterface.getOccupancy(request);
	}

	@Override
	protected void onResult(FreeRoomController mController, FROccupancyReply reply) {
		FRStatusCode status = reply.getStatus();
		if (status == FRStatusCode.HTTP_OK) {
			mController.handleReplySuccess(callerView, status, reply
					.getStatusComment(), this.getClass().getName(), reply
					.getClass().getSimpleName());
			mController.setOccupancyResults(reply);
		} else {
			callerView.anyError();
			mController.handleReplyError(callerView, status,
					reply.getStatusComment(), this.getClass().toString());
		}
	}

	@Override
	protected void onError(FreeRoomController mController, Exception e) {
		callerView.networkErrorHappened();
		e.printStackTrace();
	}
}