package org.pocketcampus.plugin.freeroom.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.freeroom.android.FreeRoomController;
import org.pocketcampus.plugin.freeroom.android.iface.IFreeRoomView;
import org.pocketcampus.plugin.freeroom.shared.FRReply;
import org.pocketcampus.plugin.freeroom.shared.FRRequest;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomService.Iface;
import org.pocketcampus.plugin.freeroom.shared.RegisterUser;

/**
 * <code>RegisterASyncTask</code> is an extension of <code>Request</code> and
 * <code>ASyncTask</code> that is used to send register a user to the BETA on
 * the server.
 * <p>
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 */

public class RegisterASyncTask extends
		Request<FreeRoomController, Iface, RegisterUser, Boolean> {

	private IFreeRoomView callerView;

	public RegisterASyncTask(IFreeRoomView callerView) {
		this.callerView = callerView;
	}

	@Override
	protected Boolean runInBackground(Iface clientInterface, RegisterUser request)
			throws Exception {
		return clientInterface.registerUserSettings(request);
	}

	@Override
	protected void onResult(FreeRoomController mController, Boolean reply) {
		mController.registeredUser(reply);
	}

	@Override
	protected void onError(FreeRoomController mController, Exception e) {
		callerView.networkErrorHappened();
		callerView.anyError();
		e.printStackTrace();
	}
}