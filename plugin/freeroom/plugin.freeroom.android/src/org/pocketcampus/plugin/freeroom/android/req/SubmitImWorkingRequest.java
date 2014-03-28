package org.pocketcampus.plugin.freeroom.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService.Iface;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingReply;
import org.pocketcampus.plugin.freeroom.shared.ImWorkingRequest;

import android.util.Log;

/**
 * SubmitImWorkingRequest class sends an HttpRequest using Thrift to the
 * PocketCampus server in order to indicate that you're going to be in that room
 * to work.
 * <p>
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */

public class SubmitImWorkingRequest extends
		Request<FreeRoomController, Iface, ImWorkingRequest, ImWorkingReply> {

	private IFreeRoomView caller;

	public SubmitImWorkingRequest(IFreeRoomView caller) {
		this.caller = caller;
	}

	@Override
	protected ImWorkingReply runInBackground(Iface client,
			ImWorkingRequest param) throws Exception {
		return client.indicateImWorking(param);
	}

	@Override
	protected void onResult(FreeRoomController controller, ImWorkingReply result) {
		int status = result.getStatus();
		if (status == 200) {
			Log.v(this.getClass().toString(), "server replied successfully");
			controller.validateImWorking(result);
		} else {
			controller.cannotValidateImWorking();
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