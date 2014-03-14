package org.pocketcampus.plugin.freeroom.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteReply;
import org.pocketcampus.plugin.freeroom.shared.AutoCompleteRequest;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService.Iface;

import android.util.Log;

public class BuildingAutoCompleteRequest extends Request<FreeRoomController, Iface, AutoCompleteRequest, AutoCompleteReply> {

	private IFreeRoomView caller;
	
	public BuildingAutoCompleteRequest(IFreeRoomView caller) {
		this.caller = caller;
	}
	
	@Override
	protected AutoCompleteReply runInBackground(Iface client, AutoCompleteRequest param) throws Exception {
		return client.autoCompleteRoom(param);
	}

	@Override
	protected void onResult(FreeRoomController controller, AutoCompleteReply result) {
		int status = result.getStatus();
		if(status == 200) {
			Log.v(this.getClass().toString(), "server replied successfully");
			controller.setAutoCompleteResults(result);
		} else {
			controller.handleReplyError(caller, status, result.getStatusComment(), this.getClass().toString());
		}
	}

	@Override
	protected void onError(FreeRoomController controller, Exception e) {
		caller.networkErrorHappened();
		e.printStackTrace();
	}
}
