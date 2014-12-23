package org.pocketcampus.plugin.freeroom.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FRAutoCompleteReply;
import org.pocketcampus.plugin.freeroom.shared.FRAutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.FRStatusCode;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService.Iface;

import android.util.Log;

/**
 * <code>AutoCompleteRequestASyncTask</code> is an extension of
 * <code>Request</code> and <code>ASyncTask</code> that is used to send a
 * <code>AutoCompleteRequest</code> request to the server and handle the
 * <code>AutoCompleteReply</code> reply received from the server, thru an Http
 * and Thrift exchange with the PocketCampus server.
 * <p>
 * It's used to get auto-complete suggestions on the room name given.
 * <p>
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */

public class AutoCompleteRequestASyncTask
		extends
		Request<FreeRoomController, Iface, FRAutoCompleteRequest, FRAutoCompleteReply> {

	private IFreeRoomView callerView;

	public AutoCompleteRequestASyncTask(IFreeRoomView callerView) {
		this.callerView = callerView;
	}

	@Override
	protected FRAutoCompleteReply runInBackground(Iface client,
			FRAutoCompleteRequest request) throws Exception {
		return client.autoCompleteRoom(request);
	}

	@Override
	protected void onResult(FreeRoomController mController,
			FRAutoCompleteReply reply) {
		FRStatusCode status = reply.getStatus();
		if (status == FRStatusCode.HTTP_OK) {
			Log.v(this.getClass().toString(), "server replied successfully");
			mController.setAutoCompleteResults(reply);
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