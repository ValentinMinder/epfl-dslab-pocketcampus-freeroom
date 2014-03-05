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
//			if(result.getStatus() == 200) {
				Log.v("freeroom", "server replied: try to add the result to the model");
				controller.setFreeRoomResults(result);
//			} else {
//				caller.freeRoomServersDown();
//			}
		}

		@Override
		protected void onError(FreeRoomController controller, Exception e) {
			caller.networkErrorHappened();
			e.printStackTrace();
		}
		
	}