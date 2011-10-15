package org.pocketcampus.plugin.directory.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.directory.shared.Person;
import org.pocketcampus.plugin.directory.android.DirectoryController;
import org.pocketcampus.plugin.directory.shared.DirectoryService.Iface;
import org.pocketcampus.plugin.directory.android.DirectoryModel;

import java.util.*;

public class DirectorySearchNameRequest extends Request<DirectoryController, Iface, String, List<Person>> {

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

	@Override
	protected List<Person> runInBackground(Iface client, String param) throws Exception {
		return client.search(param);
	}
	

}
