package org.pocketcampus.plugin.myedu.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.thrift.TException;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.pocketcampus.platform.sdk.shared.utils.Cookie;
import org.pocketcampus.plugin.myedu.shared.MyEduService;
import org.pocketcampus.plugin.myedu.shared.MyEduSession;
import org.pocketcampus.plugin.myedu.shared.MyEduTequilaToken;

/**
 * MyEduServiceImpl
 * 
 * The implementation of the server side of the MyEdu Plugin.
 * 
 * It fetches the user's MyEdu data from the MyEdu servers.
 * 
 * @author Loic <loic.gardiol@epfl.ch>
 *
 */
public class MyEduServiceImpl implements MyEduService.Iface {
	
	public MyEduServiceImpl() {
		System.out.println("Starting MyEdu plugin server ...");
	}

	@Override
	public MyEduTequilaToken getTequilaTokenForMyEdu() throws TException {
		System.out.println("getTequilaTokenForMyEdu");
		
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(MyEduServiceConfig.getFullUrl(MyEduServiceConfig.CREATE_EPFL_SESSION_PATH)).openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.getInputStream();
			URL url = new URL(conn.getHeaderField("Location"));
			MultiMap<String> params = new MultiMap<String>();
			UrlEncoded.decodeTo(url.getQuery(), params, "UTF-8");
			MyEduTequilaToken teqToken = new MyEduTequilaToken(params.getString("requestkey"));
			Cookie cookie = new Cookie();
			cookie.setCookie(conn.getHeaderFields().get("Set-Cookie"));
			teqToken.setILoginCookie(cookie.cookie());
			return teqToken;
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to getTequilaToken from upstream server");
		}
	}

	@Override
	public MyEduSession getMyEduSession(MyEduTequilaToken iTequilaToken)
			throws TException {
		System.out.println("getMyEduSession");
		
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(MyEduServiceConfig.getFullUrl(MyEduServiceConfig.EPFL_LOGIN_PATH)).openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.setRequestProperty("Cookie", iTequilaToken.getILoginCookie());
			conn.getInputStream();
			if(conn.getResponseCode() == 302) { //OK, means has redirected
				Cookie cookie = new Cookie();
				cookie.setCookie(conn.getHeaderFields().get("Set-Cookie"));
				return new MyEduSession(cookie.cookie());
			} else {
				throw new TException("Authentication failed");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to getMyEduSession from upstream server");
		}
		
	}
	
	
	/**
	 * HELPER FUNCTIONS
	 */
	
	
	
}
