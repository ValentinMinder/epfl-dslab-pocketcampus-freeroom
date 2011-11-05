package org.pocketcampus.plugin.transport.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.transport.android.TransportController;
import org.pocketcampus.plugin.transport.android.TransportModel;
import org.pocketcampus.plugin.transport.shared.Location;
import org.pocketcampus.plugin.transport.shared.TransportService.Iface;

public class AutoCompleteRequest extends Request<TransportController, Iface, String, List<Location>> {

	@Override
	protected List<Location> runInBackground(Iface client, String constraint) throws Exception {
		return client.autocomplete(constraint);
	}

	@Override
	protected void onResult(TransportController controller, List<Location> result) {
		System.out.println(result);
		
		((TransportModel) controller.getModel()).addPreferredDestinations(result);
	}

	@Override
	protected void onError(TransportController controller, Exception e) {
		e.printStackTrace();
	}
	
}
