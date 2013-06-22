package org.pocketcampus.plugin.pushnotif.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.pushnotif.shared.PushNotifService.Iface;
import org.pocketcampus.plugin.pushnotif.android.PushNotifController;

import android.util.Log;

/**
 * DeleteMappingRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class DeleteMappingRequest extends Request<PushNotifController, Iface, String, Integer> {

	public DeleteMappingRequest() {
	}
	
	@Override
	protected Integer runInBackground(Iface client, String param) throws Exception {
		return client.deleteMapping(param);
	}

	@Override
	protected void onResult(PushNotifController controller, Integer result) {
		Log.v("PushNotif", (result == 200 ? "Mapping deleted successfully" : "Failed to delete mapping"));
		controller.deleteMappingReqFinished();
	}

	@Override
	protected void onError(PushNotifController controller, Exception e) {
		e.printStackTrace();
		controller.deleteMappingReqFinished();
	}
	
}
