package org.pocketcampus.plugin.authentication.android.req;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.platform.sdk.shared.utils.StringUtils;
import org.pocketcampus.plugin.authentication.android.AuthenticationController;
import org.pocketcampus.plugin.authentication.android.AuthenticationModel;

/**
 * GetServiceDetailsRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetServiceDetailsRequest extends Request<AuthenticationController, DefaultHttpClient, String, List<String>> {
	
	@Override
	protected List<String> runInBackground(DefaultHttpClient client, String param) throws Exception {
		HttpGet get = new HttpGet(String.format(AuthenticationController.tequilaAuthTokenUrl, param));
		HttpResponse resp = client.execute(get);
		String res = IOUtils.toString(resp.getEntity().getContent(), "UTF-8");
		return StringUtils.getAllSubstringsBetween(res, "<tr>", "</th>", "</tr>");
	}

	@Override
	protected void onResult(AuthenticationController controller, List<String> result) {
		if(result.size() == 5) {
			String serviceName = StringUtils.getSubstringBetween(result.get(0), "<td>", "</td>").trim();
			String accessTo = StringUtils.getSubstringBetween(result.get(4), "<td>", "</td>").trim();
			((AuthenticationModel) controller.getModel()).setServiceName(serviceName);
			((AuthenticationModel) controller.getModel()).setServiceAccess(Arrays.asList(accessTo.split("<br>")));
			controller.fetchedServiceDetails();
		} else {
			controller.notifyInvalidToken();
		}
	}
	
	@Override
	protected void onError(AuthenticationController controller, Exception e) {
		e.printStackTrace();
		controller.notifyNetworkError();
	}
	
}
