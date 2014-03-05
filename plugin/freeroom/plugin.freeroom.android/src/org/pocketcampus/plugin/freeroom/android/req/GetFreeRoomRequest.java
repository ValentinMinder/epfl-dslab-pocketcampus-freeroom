package org.pocketcampus.plugin.freeroom.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.FreeRoomModel;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomReply;
import org.pocketcampus.plugin.freeroom.shared.FRRoom;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService.Iface;

import android.widget.Toast;

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
			
			System.out.println("Server replied... adding to model");
			((FreeRoomModel) controller.getModel()).setResults(result.getRooms());
			
			for (FRRoom room : result.getRooms()) {
				System.out.println("Free Room:" + room.getBuilding() + room.getNumber());
			}
//			if(result.getStatus() == 200) {
//				if(result.isSetCategs())
//					FreeRoomController.updateEventCategs(result.getCategs());
//				if(result.isSetTags())
//					FreeRoomController.updateEventTags(result.getTags());
//				
//				((FreeRoomModel) controller.getModel()).addEventPools(result.getChildrenPools());
//				((FreeRoomModel) controller.getModel()).addEventItem(result.getEventItem());
//				
//				keepInCache();
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