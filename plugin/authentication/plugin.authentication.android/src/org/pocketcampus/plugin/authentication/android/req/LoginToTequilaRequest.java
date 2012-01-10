package org.pocketcampus.plugin.authentication.android.req;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.authentication.android.AuthenticationController;
import org.pocketcampus.plugin.authentication.android.AuthenticationController.LocalCredentials;
import org.pocketcampus.plugin.authentication.android.AuthenticationModel;

public class LoginToTequilaRequest extends Request<AuthenticationController, DefaultHttpClient, LocalCredentials, String> {
	@Override
	protected String runInBackground(DefaultHttpClient client, LocalCredentials param) throws Exception {
		client.setRedirectHandler(AuthenticationController.redirectNoFollow);
		HttpPost post = new HttpPost(AuthenticationController.tequilaLoginUrl);
		List<NameValuePair> l = new LinkedList<NameValuePair>();
		l.add(new BasicNameValuePair("username", param.username));
		l.add(new BasicNameValuePair("password", param.password));
		post.setEntity(new UrlEncodedFormEntity(l)); // with list of key-value pairs
		client.execute(post);
		List<Cookie> lc = client.getCookieStore().getCookies();
		for(Cookie c : lc) {
			System.out.println("cookie=" + c.getName() + ": " + c.getValue());
			if(AuthenticationController.tequilaCookieName.equals(c.getName())) {
				return c.getValue();
			}
		}
		return null;
	}

	@Override
	protected void onResult(AuthenticationController controller, String result) {
		if(result != null) {
			((AuthenticationModel) controller.getModel()).setTequilaCookie(result);
		} else {
			((AuthenticationModel) controller.getModel()).getListenersToNotify().notifyBadCredentials();
		}
	}
	
	@Override
	protected void onError(AuthenticationController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
}