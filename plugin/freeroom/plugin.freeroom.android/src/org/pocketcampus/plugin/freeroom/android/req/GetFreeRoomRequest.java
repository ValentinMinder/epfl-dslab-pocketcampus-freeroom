package org.pocketcampus.plugin.freeroom.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomReply;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService.Iface;

import android.util.Log;

/**
 * TODO: to be deleted as 2014.04.04 new interface
 * <p>
 * GetFreeRoomRequest class sends an HttpRequest using Thrift to the
 * PocketCampus server in order to get the Free Rooms for a given period of
 * time.
 * <p>
 * 
 * @author FreeRoom Project Team - Julien WEBER <julien.weber@epfl.ch> and
 *         Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */

public class GetFreeRoomRequest extends
		Request<FreeRoomController, Iface, FreeRoomRequest, FreeRoomReply> {

	private IFreeRoomView caller;

	public GetFreeRoomRequest(IFreeRoomView caller) {
		this.caller = caller;
	}

	@Override
	protected FreeRoomReply runInBackground(Iface client, FreeRoomRequest param)
			throws Exception {
		return null;
	}

	@Override
	protected void onResult(FreeRoomController controller, FreeRoomReply result) {
		int status = result.getStatus();
		if (status == 200) {
			Log.v(this.getClass().toString(), "server replied successfully");
			controller.setFreeRoomResults(result);
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