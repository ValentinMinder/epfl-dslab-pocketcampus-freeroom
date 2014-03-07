package org.pocketcampus.plugin.freeroom.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService.Iface;
import org.pocketcampus.plugin.freeroom.shared.OccupancyReply;
import org.pocketcampus.plugin.freeroom.shared.OccupancyRequest;

import android.util.Log;

public class CheckOccupancyRequest extends Request<FreeRoomController, Iface, OccupancyRequest, OccupancyReply> {

	private IFreeRoomView caller;
	
	public CheckOccupancyRequest(IFreeRoomView caller) {
		this.caller = caller;
	}
	
	@Override
	protected OccupancyReply runInBackground(Iface client, OccupancyRequest param) throws Exception {
		return client.checkTheOccupancy(param);
	}

	@Override
	protected void onResult(FreeRoomController controller, OccupancyReply result) {
//		if(result.getStatus() == 200) {
			Log.v("freeroom", "server replied: try to add the result to the model");
			controller.setCheckOccupancyResults(result);
//		} else {
//			caller.freeRoomServersDown();
//		}
	}

	@Override
	protected void onError(FreeRoomController controller, Exception e) {
		caller.networkErrorHappened();
		e.printStackTrace();
	}
}
