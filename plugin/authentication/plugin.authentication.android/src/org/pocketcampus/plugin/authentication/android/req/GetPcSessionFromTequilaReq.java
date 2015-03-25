package org.pocketcampus.plugin.authentication.android.req;

import java.net.HttpURLConnection;
import java.net.URL;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.authentication.android.AuthenticationController;
import org.pocketcampus.plugin.authentication.android.AuthenticationModel;
import org.pocketcampus.plugin.authentication.shared.AuthSessionRequest;
import org.pocketcampus.plugin.authentication.shared.AuthSessionResponse;
import org.pocketcampus.plugin.authentication.shared.AuthStatusCode;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService.Iface;

import android.net.Uri;

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
public class GetPcSessionFromTequilaReq extends Request<AuthenticationController, Iface, String, AuthSessionResponse> {
	
	@Override
	protected AuthSessionResponse runInBackground(Iface client, String param) throws Exception {

		//HttpURLConnection conn = (HttpURLConnection) new URL(AuthenticationController.OAUTH2_AUTH_URL).openConnection();
		HttpURLConnection conn = (HttpURLConnection) new URL(AuthenticationController.OAUTH2_AUTH_URL + "&doauth=Approve").openConnection();
		conn.setRequestProperty("Cookie", "Tequila_req_OAuth2IdP=" + param);
		conn.setInstanceFollowRedirects(false);
		conn.getInputStream();
		String loc = conn.getHeaderField("Location");
		if(loc == null) {
			return new AuthSessionResponse(AuthStatusCode.INVALID_SESSION);
		}
		//Location=https://tequila.epfl.ch/tequila/auth?requestkey=4egsuaahk1skykqxalvhli4xgychen1n
		String code = Uri.parse(loc).getQueryParameter("code");
		if(code == null) {
			return new AuthSessionResponse(AuthStatusCode.INVALID_SESSION);
		}
		return client.getOAuth2TokensFromCode(new AuthSessionRequest(code));
		
		//https://dev-tequila.epfl.ch/cgi-bin/OAuth2IdP/auth?client_id=1b74e3837e50e21afaf2005f@epfl.ch&redirect_uri=https://pocketcampus.epfl.ch/&scope=Tequila.profile,Moodle.read,ISA.read&response_type=code
		//Tequila_OAuth2IdP=removed; path=/; expires=Tue 20-Jan-2015 17:56:54 GMT;
		//Tequila_req_OAuth2IdP=ym7jcgsnybx3wafcatpsknwj0vl5egdx; path=/;
		//URL=https://dev-tequila.epfl.ch/cgi-bin/OAuth2IdP/auth?response_type=code&redirect_uri=https%3A%2F%2Fpocketcampus.epfl.ch%2F&client_id=1b74e3837e50e21afaf2005f%40epfl.ch&state=&scope=Tequila.profile%2CMoodle.read%2CISA.read&doauth=Approve
	}

	@Override
	protected void onResult(AuthenticationController controller, AuthSessionResponse result) {
		if(result.getStatusCode() == AuthStatusCode.OK) {
			if(AuthenticationModel.verifyOAuth2SessionId(result.getSessionId()))
				controller.pcAuthenticationFinished(result.getSessionId());
			else 
				controller.notifyInvalidToken();
		} else if(result.getStatusCode() == AuthStatusCode.INVALID_SESSION) {
			controller.notifyInvalidToken();
		} else {
			controller.notifyNetworkError();
		}
	}
	
	@Override
	protected void onError(AuthenticationController controller, Exception e) {
		e.printStackTrace();
		controller.notifyNetworkError();
	}
	
}
