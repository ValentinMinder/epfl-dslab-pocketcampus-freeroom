package org.pocketcampus.plugin.authentication.android.req;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.authentication.android.AuthenticationController;
import org.pocketcampus.plugin.authentication.android.AuthenticationController.LocalCredentials;
import org.pocketcampus.plugin.authentication.android.AuthenticationController.TokenCookieComplex;
import org.pocketcampus.plugin.authentication.android.AuthenticationController.TokenCredentialsComplex;
import org.pocketcampus.plugin.authentication.android.AuthenticationModel;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService.Iface;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.TequilaKey;

import android.util.Log;

public class AuthenticateTokenWithTequilaRequest extends Request<AuthenticationController, DefaultHttpClient, TokenCookieComplex, Boolean> {
	@Override
	protected Boolean runInBackground(DefaultHttpClient client, TokenCookieComplex param) throws Exception {
		HttpGet get = new HttpGet(String.format(AuthenticationController.tequilaAuthTokenUrl, param.token.getITequilaKey()));
		get.addHeader("Cookie", AuthenticationController.tequilaCookieName + "=" + param.cookie);
		HttpResponse resp = client.execute(get);
		return (resp.getFirstHeader("Location") != null);
	}

	@Override
	protected void onResult(AuthenticationController controller, Boolean result) {
		if(result) {
			((AuthenticationModel) controller.getModel()).setAuthState(4);
		} else {
			// TODO do more checks to know if token is invalid or cookie has timed out
			((AuthenticationModel) controller.getModel()).setAuthState(0);
			((AuthenticationModel) controller.getModel()).setIntState(0);
			((AuthenticationModel) controller.getModel()).getListenersToNotify().notifyBadToken();
		}
	}
	
	@Override
	protected void onError(AuthenticationController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
}