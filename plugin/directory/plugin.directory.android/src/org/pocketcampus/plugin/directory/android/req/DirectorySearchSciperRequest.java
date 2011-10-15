package org.pocketcampus.plugin.directory.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.directory.shared.Person;
import org.pocketcampus.plugin.directory.android.DirectoryController;
import org.pocketcampus.plugin.directory.shared.DirectoryService.Iface;
import org.pocketcampus.plugin.directory.android.DirectoryModel;

import java.util.*;

public class DirectorySearchSciperRequest extends Request<DirectoryController, Iface, Object, List<Person>> {

	@Override
	protected List<Person> runInBackground(Iface client, Object param) throws Exception {
		return client.search((String) param);
	}

	@Override
	protected void onResult(DirectoryController controller, List<Person> result) {
		((DirectoryModel) controller.getModel()).setResults(result);
	}

	@Override
	protected void onError(DirectoryController controller, Exception e) {
		System.out.println("onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

}
