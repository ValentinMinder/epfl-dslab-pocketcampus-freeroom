package org.pocketcampus.authentication.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.thrift.TException;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.TequilaKey;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;
import org.pocketcampus.plugin.authentication.shared.utils.Cookie;

import ch.epfl.tequila.client.model.ClientConfig;
import ch.epfl.tequila.client.model.TequilaPrincipal;
import ch.epfl.tequila.client.service.TequilaService;

/**
 * AuthenticationServiceImpl
 * 
 * The implementation of the server side of the Authentication Plugin.
 * 
 * The server side of the Authentication Plugin must implement two functions:
 * - getTequilaKeyForService
 * - getSessionIdForService
 * Each one of these function must be implemented as a helper function
 * for every service that we support.
 * For now we support three services:
 * - PocketCampus
 * - Moodle
 * - Camipro
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class AuthenticationServiceImpl implements AuthenticationService.Iface {
	
	/**
	 * Gets a Tequila Token from the server of the service that is
	 * requesting authentication.
	 * Dispatches the job to the corresponding method.
	 */
	@Override
	public TequilaKey getTequilaKeyForService(TypeOfService aService) throws TException {
		System.out.println("getTequilaKeyForService");
		try {
			switch (aService) {
			case SERVICE_POCKETCAMPUS:
				return getTequilaKeyForPocketCampus();
			case SERVICE_MOODLE:
				return getTequilaKeyForMoodle();
			case SERVICE_CAMIPRO:
				return getTequilaKeyForCamipro();
			default:
				throw new IOException("getTequilaKeyForService: Cannot understand this TypeOfService");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to getTequilaKeyForService from upstream server");
		}
	}
	
	/**
	 * Helper function to get Tequila Token for PocketCampus.
	 */
	private TequilaKey getTequilaKeyForPocketCampus() throws IOException {
	    ClientConfig config = new ClientConfig();
	    config.setHost("tequila.epfl.ch");
	    //config.setOrg("PocketCampus");
	    config.setService("PocketCampus");
	    config.setRequest("name firstname email title unit office phone username uniqueid unixid groupid where");
	    //config.setAllows("categorie=epfl-guests");
	    String keyStr = TequilaService.instance().createRequest(config, "pocketcampus-redirect://login.pocketcampus.org");
		return new TequilaKey(TypeOfService.SERVICE_POCKETCAMPUS, keyStr);
	}

	/**
	 * Helper function to get Tequila Token for Moodle.
	 */
	private TequilaKey getTequilaKeyForMoodle() throws IOException {
        HttpURLConnection conn2 = (HttpURLConnection) new URL("http://moodle.epfl.ch/auth/tequila/index.php").openConnection();
        conn2.setInstanceFollowRedirects(false);
        conn2.getInputStream();
        URL url = new URL(conn2.getHeaderField("Location"));
		MultiMap<String> params = new MultiMap<String>();
		UrlEncoded.decodeTo(url.getQuery(), params, "UTF-8");
		return new TequilaKey(TypeOfService.SERVICE_MOODLE, params.getString("requestkey"));
	}

	/**
	 * Helper function to get Tequila Token for Camipro.
	 */
	private TequilaKey getTequilaKeyForCamipro() throws IOException {
		Cookie cookie = new Cookie();
        HttpURLConnection conn2 = (HttpURLConnection) new URL("https://cmp2www.epfl.ch/ws/balance").openConnection();
        conn2.setInstanceFollowRedirects(false);
        conn2.getInputStream();
        URL url = new URL(conn2.getHeaderField("Location"));
        cookie.setCookie(conn2.getHeaderFields().get("Set-Cookie"));
		MultiMap<String> params = new MultiMap<String>();
		UrlEncoded.decodeTo(url.getQuery(), params, "UTF-8");
		TequilaKey teqKey = new TequilaKey(TypeOfService.SERVICE_CAMIPRO, params.getString("requestkey"));
		teqKey.setLoginCookie(cookie.cookie());
		return teqKey;
	}

	/**
	 * Gets a valid SessionId from the server of the service that is
	 * requesting authentication, by providing it with
	 * the Tequila-authenticated token.
	 * Dispatches the job to the corresponding method.
	 */
	@Override
	public SessionId getSessionIdForService(TequilaKey aTequilaKey) throws TException {
		System.out.println("getSessionIdForService");
		try {
			switch (aTequilaKey.getTos()) {
			case SERVICE_POCKETCAMPUS:
				return getSessionIdForPocketCampus(aTequilaKey);
			case SERVICE_MOODLE:
				return getSessionIdForMoodle(aTequilaKey);
			case SERVICE_CAMIPRO:
				return getSessionIdForCamipro(aTequilaKey);
			default:
				throw new IOException("getSessionIdForService: Cannot understand this TypeOfService");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to getSessionIdForService from upstream server");
		}
	}
	
	/**
	 * Helper function to get SessionId for PocketCampus.
	 */
	private SessionId getSessionIdForPocketCampus(TequilaKey aTequilaKey) throws IOException {
	    if(aTequilaKey.getTos() != TypeOfService.SERVICE_POCKETCAMPUS)
	    	throw new IOException("getSessionIdForPocketCampus: Called with wrong TypeOfService");
	    
	    ClientConfig clientConfig = new ClientConfig();
	    clientConfig.setHost("tequila.epfl.ch");

	    TequilaPrincipal principal = TequilaService.instance().validateKey(clientConfig, aTequilaKey.getITequilaKey());
	    
		//System.out.println("principal = " + principal);
		// principal = [user=chamsedd, org=EPFL, host=128.178.236.75, attributes={phone=+41 21 6938188, status=ok, firstname=Amer, where=IN-MA1/IN-S/ETU/EPFL/CH, requesthost=128.178.77.233, version=2.1.1, unit=IN-MA1,Section d'informatique - Master semestre 1, uniqueid=211338, username=chamsedd,chamsedd@in-ma1, email=amer.chamseddine@epfl.ch, name=Chamseddine, authorig=cookie, unixid=112338, groupid=30132}]

	    // Create session contents
	    TequilaSession ts = new TequilaSession();
	    ts.setTequilaPrincipal(principal);

	    // Create new session
	    String newSessId = TequilaSessions.newSession(ts);
	    // TODO need a way to delete inactive sessions

	    // send back the session id
	    SessionId si = new SessionId();
	    si.setTos(TypeOfService.SERVICE_POCKETCAMPUS);
	    si.setPocketCampusSessionId(newSessId);
		return si;
	}

	/**
	 * Helper function to get SessionId for Moodle.
	 */
	private SessionId getSessionIdForMoodle(TequilaKey aTequilaKey) throws IOException {
	    if(aTequilaKey.getTos() != TypeOfService.SERVICE_MOODLE)
	    	throw new IOException("getSessionIdForMoodle: Called with wrong TypeOfService");
	    
		Cookie cookie = new Cookie();
        HttpURLConnection conn = (HttpURLConnection) new URL("http://moodle.epfl.ch").openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.getInputStream();
        cookie.setCookie(conn.getHeaderFields().get("Set-Cookie"));
        
        HttpURLConnection conn2 = (HttpURLConnection) new URL("http://moodle.epfl.ch/auth/tequila/teq_return.php?key=" + aTequilaKey.getITequilaKey()).openConnection();
        conn2.setRequestProperty("Cookie", cookie.cookie());
        conn2.setInstanceFollowRedirects(false);
        conn2.getInputStream();
        cookie.setCookie(conn2.getHeaderFields().get("Set-Cookie"));
        
	    // send back the session id
	    SessionId si = new SessionId();
	    si.setTos(TypeOfService.SERVICE_MOODLE);
	    si.setMoodleCookie(cookie.cookie());
		return si;
	}
	
	/**
	 * Helper function to get SessionId for Camipro.
	 */
	private SessionId getSessionIdForCamipro(TequilaKey aTequilaKey) throws IOException {
	    if(aTequilaKey.getTos() != TypeOfService.SERVICE_CAMIPRO)
	    	throw new IOException("getSessionIdForCamipro: Called with wrong TypeOfService");
	    
		Cookie cookie = new Cookie();
		String loginCookie = aTequilaKey.getLoginCookie();
	    if(loginCookie == null)
	    	throw new IOException("getSessionIdForCamipro: loginCookie is null");
	    cookie.importFromString(loginCookie);
		
        HttpURLConnection conn2 = (HttpURLConnection) new URL("https://cmp2www.epfl.ch/ws/balance").openConnection();
        conn2.setRequestProperty("Cookie", cookie.cookie());
        conn2.setInstanceFollowRedirects(false);
        conn2.getInputStream();
        if(conn2.getHeaderField("Location") != null)
        	System.out.println("getSessionIdForCamipro: WARNING got redirected, this should not happen, authentication has probably failed");
        
	    // send back the session id
	    SessionId si = new SessionId();
	    si.setTos(TypeOfService.SERVICE_CAMIPRO);
	    si.setCamiproCookie(cookie.cookie());
		return si;
	}
	
}
