package org.pocketcampus.plugin.authentication.android.req;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.authentication.android.AuthenticationController;
import org.pocketcampus.plugin.authentication.android.AuthenticationModel;
import org.pocketcampus.plugin.authentication.android.AuthenticationModel.TokenCredentialsComplex;

/**
 * AuthenticateTokenWithTequilaRequest
 * 
 * This class sends an HttpRequest to the Tequila server directly
 * in order to authenticate the token the we got for a specific service.
 * This class connects to Tequila over a secure HTTPS connection.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class AuthenticateTokenWithTequilaRequest extends Request<AuthenticationController, DefaultHttpClient, TokenCredentialsComplex, Header> {
	
	@Override
	protected Header runInBackground(DefaultHttpClient client, TokenCredentialsComplex param) throws Exception {
		HttpPost post = new HttpPost(AuthenticationController.tequilaLoginUrl);
		List<NameValuePair> l = new LinkedList<NameValuePair>();
		l.add(new BasicNameValuePair("username", param.username));
		l.add(new BasicNameValuePair("password", param.password));
		l.add(new BasicNameValuePair("requestkey", param.token));
		post.setEntity(new UrlEncodedFormEntity(l));
		HttpResponse resp = client.execute(post);
		Header location = resp.getFirstHeader("Location");
		resp.getEntity().getContent().close();
		return location;
	}

	@Override
	protected void onResult(AuthenticationController controller, Header result) {
		if(result != null) {
			if(((AuthenticationModel) controller.getModel()).getFromBrowser())
				((AuthenticationModel) controller.getModel()).setCallbackUrl(result.getValue());
			controller.tokenAuthenticationFinished();
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
