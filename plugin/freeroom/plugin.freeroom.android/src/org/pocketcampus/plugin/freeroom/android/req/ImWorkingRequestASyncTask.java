package org.pocketcampus.plugin.freeroom.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.Constants;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService.Iface;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingReply;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingRequest;

import android.util.Log;

/**
 * <code>ImWorkingRequestASyncTask</code> is an extension of
 * <code>Request</code> and <code>ASyncTask</code> that is used to send a
 * <code>ImWorkingRequest</code> request to the server and handle the
 * <code>ImWorkingReply</code> reply received from the server, thru an Http and
 * Thrift exchange with the PocketCampus server.
 * <p>
 * It's used to let the user indicate that he's going to be in that room, at
 * that time. The data given by the users are used to display the probable
 * occupancies and display the best rooms to other users.
 * <p>
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */

public class ImWorkingRequestASyncTask extends
		Request<FreeRoomController, Iface, ImWorkingRequest, ImWorkingReply> {

	private IFreeRoomView callerView;

	public ImWorkingRequestASyncTask(IFreeRoomView callerView) {
		this.callerView = callerView;
	}

	@Override
	protected ImWorkingReply runInBackground(Iface clientInterface,
			ImWorkingRequest request) throws Exception {
		return clientInterface.indicateImWorking(request);
	}

	@Override
	protected void onResult(FreeRoomController mController, ImWorkingReply reply) {
		int status = reply.getStatus();
		callerView.refreshOccupancies();
		if (status == 200) {
			Log.v(this.getClass().toString(), "server replied successfully: ok");
			// in case of first submit
			mController.validateImWorking(reply);
		} else if (status == Constants.HTTP_UPDATED) {
			Log.v(this.getClass().toString(), "server replied successfully: updated");
			// in case of update
			mController.updateImWorking(reply);
		} else if (status == 409) {
			// in case of conflict with the same user.
			mController.conflictImWorking(reply);
		} else {
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