package org.pocketcampus.plugin.directory.android.req;

//import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.directory.android.DirectoryController;
import org.pocketcampus.plugin.directory.android.DirectoryModel;
//import org.pocketcampus.plugin.directory.shared.NoPictureFound;
//import org.pocketcampus.plugin.directory.shared.Person;
import org.pocketcampus.plugin.directory.shared.DirectoryService.Iface;

import android.util.Log;

public class DirectoryGetPictureRequest extends Request<DirectoryController, Iface, String, String>{

	@Override
	protected String runInBackground(Iface client, String param)
			throws Exception {
		return client.getProfilePicture(param);
	}

	@Override
	protected void onResult(DirectoryController controller, String result) {
		Log.v("Directory", "Picture url found: " + result);
		((DirectoryModel) controller.getModel()).setProfilePicture(result);		
	}

	@Override
	protected void onError(DirectoryController controller, Exception e) {
		if(e != null){
			Log.e("Directory","no picture: onError "+e.getMessage());
			((DirectoryModel) controller.getModel()).setProfilePicture(null);
		}
	}

}
