package org.pocketcampus.plugin.freeroom.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FRStatusCode;
import org.pocketcampus.plugin.freeroom.shared.FRWhoIsWorkingReply;
import org.pocketcampus.plugin.freeroom.shared.FRWhoIsWorkingRequest;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService.Iface;

import android.util.Log;

/**
 * CheckWhoIsWorkingRequestRequest class sends an HttpRequest using Thrift to
 * the PocketCampus server in order to retrieves who's working during a given
 * period, at a given place and/or a given subject.
 * <p>
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */

public class CheckWhoIsWorkingRequest
		extends
		Request<FreeRoomController, Iface, FRWhoIsWorkingRequest, FRWhoIsWorkingReply> {

	private IFreeRoomView caller;

	public CheckWhoIsWorkingRequest(IFreeRoomView caller) {
		this.caller = caller;
	}

	@Override
	protected FRWhoIsWorkingReply runInBackground(Iface client,
			FRWhoIsWorkingRequest param) throws Exception {
		return client.getUserMessages(param);
	}

	@Override
	protected void onResult(FreeRoomController controller,
			FRWhoIsWorkingReply result) {
		FRStatusCode status = result.getStatus();
		if (status == FRStatusCode.HTTP_OK) {
			Log.v(this.getClass().toString(), "server replied successfully");
			controller.setWhoIsWorkingReply(result);
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