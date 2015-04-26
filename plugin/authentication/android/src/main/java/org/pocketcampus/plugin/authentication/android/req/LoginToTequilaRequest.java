package org.pocketcampus.plugin.authentication.android.req;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.authentication.android.AuthenticationController;
import org.pocketcampus.plugin.authentication.android.AuthenticationModel.LocalCredentials;

/**
 * LoginToTequilaRequest
 * 
 * This class sends an HttpRequest to the Tequila server directly
 * in order to login the user to Tequila. It gets back a TequilaCookie,
 * that can be used later on to authenticate tokens for different services.
 * This class sends the user credentials to Tequila over a secure HTTPS connection.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class LoginToTequilaRequest extends Request<AuthenticationController, DefaultHttpClient, LocalCredentials, String> {
	
	@Override
	protected String runInBackground(DefaultHttpClient client, LocalCredentials param) throws Exception {
		HttpPost post = new HttpPost(AuthenticationController.tequilaLoginUrl);
		List<NameValuePair> l = new LinkedList<NameValuePair>();
		l.add(new BasicNameValuePair("username", param.username));
		l.add(new BasicNameValuePair("password", param.password));
		post.setEntity(new UrlEncodedFormEntity(l));
		client.execute(post).getEntity().getContent().close();
		List<Cookie> lc = client.getCookieStore().getCookies();
		for(Cookie c : lc) {
			if(AuthenticationController.tequilaCookieName.equals(c.getName())) {
				return AuthenticationController.tequilaCookieName + "=" + c.getValue();
			}
		}
		return null;
	}

	@Override
	protected void onResult(AuthenticationController controller, String result) {
		if(result != null) {
			controller.tequilaLoginFinished(result);
		} else {
			controller.notifyBadCredentials();
		}
	}
	
	@Override
	protected void onError(AuthenticationController controller, Exception e) {
		e.printStackTrace();
		controller.notifyNetworkError();
	}
	
}
