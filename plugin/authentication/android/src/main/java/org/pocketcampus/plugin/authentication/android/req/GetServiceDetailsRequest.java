package org.pocketcampus.plugin.authentication.android.req;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.platform.shared.utils.StringUtils;
import org.pocketcampus.plugin.authentication.android.AuthenticationController;
import org.pocketcampus.plugin.authentication.android.AuthenticationModel;

/**
 * GetServiceDetailsRequest
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetServiceDetailsRequest extends Request<AuthenticationController, DefaultHttpClient, String, String> {
	
	@Override
	protected String runInBackground(DefaultHttpClient client, String param) throws Exception {
		HttpGet get = new HttpGet(String.format(AuthenticationController.tequilaAuthTokenUrl, param));
		HttpResponse resp = client.execute(get);
		return StringUtils.fromStream(resp.getEntity().getContent(), "UTF-8");
	}

	@Override
	protected void onResult(AuthenticationController controller, String result) {
		List<String> serviceDetails = StringUtils.getAllSubstringsBetween(result, "<tr>", "</th>", "</tr>");
		if(serviceDetails.size() == 5) {
			String serviceName = StringUtils.getSubstringBetween(serviceDetails.get(0), "<td>", "</td>").trim();
			String accessTo = StringUtils.getSubstringBetween(serviceDetails.get(4), "<td>", "</td>").trim();
			((AuthenticationModel) controller.getModel()).setServiceName(serviceName);
			((AuthenticationModel) controller.getModel()).setServiceAccess(Arrays.asList(accessTo.split("<br>")));
			Map<String, String> orgs = null;
			if(result.contains("shibform")) {
				String shi = StringUtils.getSubstringBetween(result, "name=\"shibform\"", "</form>");
				List<String> shis = StringUtils.getAllSubstringsBetween(shi, "<option", "option>");
				orgs = new HashMap<String, String>();
				for(String s : shis) {
					String key = StringUtils.getSubstringBetween(s, ">", "<").trim();
					String val = StringUtils.getSubstringBetween(s, "value=\"", "\"");
					if(!"-".equals(val))
						orgs.put(key, val);
				}
			}
			((AuthenticationModel) controller.getModel()).setServiceOrgs(orgs);
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
