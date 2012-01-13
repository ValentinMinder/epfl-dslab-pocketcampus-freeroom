package org.pocketcampus.plugin.directory.android.req;

//import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.directory.android.DirectoryController;
import org.pocketcampus.plugin.directory.android.DirectoryModel;
//import org.pocketcampus.plugin.directory.shared.NoPictureFound;
//import org.pocketcampus.plugin.directory.shared.Person;
import org.pocketcampus.plugin.directory.shared.DirectoryService.Iface;

import android.util.Log;

/**
 * Request to the server to get the url of a specifiec user using his sciper id
 * @author Pascal <pascal.scheiben@gmail.com>
 *
 */
public class DirectoryGetPictureRequest extends Request<DirectoryController, Iface, String, String>{

	/**
	 * Initiate the Request at the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters to be sent for the request : a string containing the sciper id
	 */
	protected String runInBackground(Iface client, String param)
			throws Exception {
		return client.getProfilePicture(param);
	}

	/**
	 * Tell the model the picture has been updated.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result
	 * @param result
	 *            the url of the picture gotten from the server
	 */
	protected void onResult(DirectoryController controller, String result) {
		Log.v("Directory", "Picture url found: " + result);
		((DirectoryModel) controller.getModel()).setProfilePicture(result);		
	}

	/**
	 * Notifies the Model that an error has occurred while processing the
	 * request.
	 * 
	 * @param controller
	 *            the controller that initiated the request
	 */
	protected void onError(DirectoryController controller, Exception e) {
		if(e != null){
			Log.e("Directory","no picture: onError "+e.getMessage());
			((DirectoryModel) controller.getModel()).setProfilePicture(null);
		}
	}

}
