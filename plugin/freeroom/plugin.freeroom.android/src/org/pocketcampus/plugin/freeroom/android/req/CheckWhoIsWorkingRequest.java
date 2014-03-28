package org.pocketcampus.plugin.freeroom.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService.Iface;
import org.pocketcampus.plugin.freeroom.shared.WhoIsWorkingReply;
import org.pocketcampus.plugin.freeroom.shared.WhoIsWorkingRequest;

import android.util.Log;

/**
 * CheckWhoIsWorkingRequestRequest class sends an HttpRequest using Thrift to
 * the PocketCampus server in order to retrieves who's working during a given
 * period, at a given place and/or a given subject.
 * <p>
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */

public class CheckWhoIsWorkingRequest
		extends
		Request<FreeRoomController, Iface, WhoIsWorkingRequest, WhoIsWorkingReply> {

	private IFreeRoomView caller;

	public CheckWhoIsWorkingRequest(IFreeRoomView caller) {
		this.caller = caller;
	}

	@Override
	protected WhoIsWorkingReply runInBackground(Iface client,
			WhoIsWorkingRequest param) throws Exception {
		return client.whoIsWorking(param);
	}

	@Override
	protected void onResult(FreeRoomController controller,
			WhoIsWorkingReply result) {
		int status = result.getStatus();
		if (status == 200) {
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