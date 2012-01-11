package org.pocketcampus.plugin.authentication.android.req;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.authentication.android.AuthenticationController;
import org.pocketcampus.plugin.authentication.android.AuthenticationController.LocalCredentials;
import org.pocketcampus.plugin.authentication.android.AuthenticationController.TOSCredentialsComplex;
import org.pocketcampus.plugin.authentication.android.AuthenticationModel;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;
import org.pocketcampus.plugin.authentication.shared.utils.Cookie;

/**
 * GetSessionIdDirectlyFromProviderRequest
 * 
 * This class sends an HttpRequest directly to the server of the specific service
 * that is requesting authentication, in order to authenticate the user to that service.
 * Usually this is used when the service is not Tequila enabled,
 * so the user must provide their username and password for this service in particular.
 * For every service that is not Tequila-enabled a helper function
 * that logs in the user must be implemented here. e.g. loginToIsa.
 * This function takes as arguments the HttpClient to be used to connect to the server
 * and the LocalCredentials consisting of the user's username and password.
 * This function must return a valid SessionId if the authentication succeeds,
 * otherwise it returns null.
 * If there is a network error (such as no connection to the Internet) the
 * function can throw an IOException.
 * It is advised that this function uses HTTPS connection to send the user's
 * credentials. e.g. loginToIsa.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetSessionIdDirectlyFromProviderRequest extends Request<AuthenticationController, DefaultHttpClient, TOSCredentialsComplex, SessionId> {
	
	@Override
	protected SessionId runInBackground(DefaultHttpClient client, TOSCredentialsComplex param) throws Exception {
		switch(param.tos) {
		case SERVICE_ISA:
			return loginToIsa(client, param.credentials);
		default:
			throw new RuntimeException("GetSessionIdDirectlyFromProviderRequest: Not implemeted for this service");
		}
	}

	@Override
	protected void onResult(AuthenticationController controller, SessionId result) {
		if(result != null) {
			((AuthenticationModel) controller.getModel()).setSessionId(result);
		} else {
			((AuthenticationModel) controller.getModel()).getListenersToNotify().notifyBadCredentials();
		}
	}
	
	@Override
	protected void onError(AuthenticationController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

	/**
	 * Helper function that logs in the user directly to ISA.
	 * 
	 * It uses the user's username and password and sends them
	 * directly to ISA. If the authentication succeeds, it returns
	 * a valid SessionId for the ISA service. Otherwise it returns
	 * null. If there is no Internet connection, it throws
	 * an IOException.
	 * 
	 * @param client is the HTTP client used to connect to ISA server.
	 * @param credentials is the LocalCredentials containing the user's
	 * username and password.
	 * @return a valid SessionId if authentication succeeds or null otherwise.
	 * @throws IOException in case it could not reach the ISA server.
	 */
	private SessionId loginToIsa(DefaultHttpClient client, LocalCredentials credentials) throws IOException {
		client.setRedirectHandler(AuthenticationController.redirectNoFollow);
		HttpPost post = new HttpPost(AuthenticationController.isaLoginUrl);
		List<NameValuePair> l = new LinkedList<NameValuePair>();
		l.add(new BasicNameValuePair("ww_x_username", credentials.username));
		l.add(new BasicNameValuePair("ww_x_password", credentials.password));
		post.setEntity(new UrlEncodedFormEntity(l));
		HttpResponse resp = client.execute(post);
		LinkedList<String> ckz = new LinkedList<String>();
		for(Header h : resp.getAllHeaders()) {
			if("Set-Cookie".equals(h.getName()))
				ckz.add(h.getValue());
		}
		if(ckz.size() > 0) {
			Cookie ck = new Cookie();
			ck.setCookie(ckz);
			SessionId sessId = new SessionId(TypeOfService.SERVICE_ISA);
			sessId.setIsaCookie(ck.cookie());
			return sessId;
		}
		return null;
	}
	
}
