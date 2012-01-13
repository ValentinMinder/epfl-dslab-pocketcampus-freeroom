package org.pocketcampus.plugin.directory.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.directory.android.DirectoryController;
import org.pocketcampus.plugin.directory.android.DirectoryModel;
import org.pocketcampus.plugin.directory.shared.DirectoryService.Iface;

import android.util.Log;

/**
 * Request to the server to get a list of suggestions corresponding
 * to a parial name
 * @author Pascal <pascal.scheiben@gmail.com>
 *
 */
public class DirectoryAutoCompleteRequest extends
		Request<DirectoryController, Iface, String, List<String>> {

	/**
	 * Initiate the <code>autocomplete</code> Request at the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters to be sent for the request : a string,
	 *            constraint for the autocompletion
	 */
	protected List<String> runInBackground(Iface client, String constraint)
			throws Exception {
		return client.autocomplete(constraint);
	}

	/**
	 * Tell the model the suggestions have been updated.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result
	 * @param result
	 *            the list of auto completed destinations gotten from the server
	 */
	protected void onResult(DirectoryController controller,
			List<String> result) {
		Log.v("Directory",result.toString());

		((DirectoryModel) controller.getModel()).setAutocompleteSuggestions(result);
	}

	/**
	 * Notifies the Model that an error has occurred while processing the
	 * request.
	 * 
	 * @param controller
	 *            the controller that initiated the request
	 */
	protected void onError(DirectoryController controller, Exception e) {
		e.printStackTrace();
	}

}