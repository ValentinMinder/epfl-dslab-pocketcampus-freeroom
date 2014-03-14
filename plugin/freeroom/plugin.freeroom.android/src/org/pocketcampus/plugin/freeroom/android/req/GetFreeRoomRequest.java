package org.pocketcampus.plugin.freeroom.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomReply;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService.Iface;

import android.util.Log;

public class GetFreeRoomRequest extends Request<FreeRoomController, Iface, FreeRoomRequest, FreeRoomReply> {

		private IFreeRoomView caller;
		
		public GetFreeRoomRequest(IFreeRoomView caller) {
			this.caller = caller;
		}
		
		@Override
		protected FreeRoomReply runInBackground(Iface client, FreeRoomRequest param) throws Exception {
			return client.getFreeRoomFromTime(param);
		}

		@Override
		protected void onResult(FreeRoomController controller, FreeRoomReply result) {
			int status = result.getStatus();
			if(status == 200) {
				Log.v(this.getClass().toString(), "server replied successfully");
				controller.setFreeRoomResults(result);
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