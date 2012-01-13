package org.pocketcampus.plugin.directory.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.directory.shared.Person;
import org.pocketcampus.plugin.directory.android.DirectoryController;
import org.pocketcampus.plugin.directory.shared.DirectoryService.Iface;
import org.pocketcampus.plugin.directory.android.DirectoryModel;

import android.util.Log;

import java.util.*;

/**
 *	Request to the server to get a list of <code>Person</code> corresponding to the parameter
 * 
 * @author Pascal <pascal.scheiben@gmail.com>
 */
public class DirectorySearchNameRequest extends Request<DirectoryController, Iface, String, List<Person>> {

	/**
	 * Tell the model the results have been updated.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result
	 * @param result
	 *            the list of <code>Person</code> gotten from the server
	 */
	protected void onResult(DirectoryController controller, List<Person> result) {
		((DirectoryModel) controller.getModel()).setResults(result);
	}

	/**
	 * Notifies the Model that an error has occurred while processing the
	 * request.
	 * 
	 * @param controller
	 *            the controller that initiated the request
	 */
	protected void onError(DirectoryController controller, Exception e) {
		
		if(e != null ){
			Log.e("Directory","onError "+e.getMessage());
			if(e.getMessage().equals("too many results"))
				((DirectoryModel) controller.getModel()).notifyTooManyResults(1337);
		}
		
		controller.getModel().notifyNetworkError();
		
	}

	/**
	 * Initiate the search Request at the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters to be sent for the request : a string,
	 *            constraint for the search, should be a partial name (partial first or/and last name), full name or a sciper number
	 */
	protected List<Person> runInBackground(Iface client, String param) throws Exception {
		return client.searchPersons(param);
	}
	

}
