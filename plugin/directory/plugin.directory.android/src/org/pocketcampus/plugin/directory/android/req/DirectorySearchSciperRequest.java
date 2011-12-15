package org.pocketcampus.plugin.directory.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.directory.shared.Person;
import org.pocketcampus.plugin.directory.android.DirectoryController;
import org.pocketcampus.plugin.directory.shared.DirectoryService.Iface;
import org.pocketcampus.plugin.directory.android.DirectoryModel;

import android.util.Log;

import java.util.*;

public class DirectorySearchSciperRequest extends Request<DirectoryController, Iface, Object, List<Person>> {

	@Override
	protected List<Person> runInBackground(Iface client, Object param) throws Exception {
		return client.searchPersons((String) param);
	}

	@Override
	protected void onResult(DirectoryController controller, List<Person> result) {
		((DirectoryModel) controller.getModel()).setResults(result);
	}

	@Override
	protected void onError(DirectoryController controller, Exception e) {
		Log.e("Directory", "error in the search sciper request");
		controller.getModel().notifyNetworkError();
	}

}
