package org.pocketcampus.plugin.authentication.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.thrift.TException;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.pocketcampus.platform.sdk.shared.utils.Cookie;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.TequilaKey;
import org.pocketcampus.plugin.authentication.shared.TequilaSession;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;

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
	
	private AuthDB authDB;
	
	/**
	 * Constructor
	 */
	public AuthenticationServiceImpl() {
		System.out.println("Starting Authentication plugin server ...");
		authDB = new AuthDB();
	}
	
	@Override
	public int startRefresh(TequilaSession aTequilaSession) throws TException {
		System.out.println("startRefresh");
		try {
	        HttpURLConnection conn = (HttpURLConnection) new URL("https://tequila.epfl.ch/cgi-bin/tequila/requestauth").openConnection();
	        conn.setRequestProperty("Cookie", aTequilaSession.getTequilaCookie());
	        String res = IOUtils.toString(conn.getInputStream(), "UTF-8");
	        if(res.indexOf("https://tequila.epfl.ch/cgi-bin/tequila/logout") == -1) {
		        // TODO fix me (tequila requires session to be from same ip)
	        	System.out.println("NOOOOOOOOOOOOOO");
	        	//return 400;
	        }
		} catch(IOException e) {
			e.printStackTrace();
			System.out.println("Failed to contact Tequila");
			return 500;
		}
        authDB.insertTequilaCookie(aTequilaSession.getTequilaCookie());
		return 200;
	}

	@Override
	public int stopRefresh(TequilaSession aTequilaSession) throws TException {
		System.out.println("stopRefresh");
        authDB.deleteTequilaCookie(aTequilaSession.getTequilaCookie());
		return 200;
	}

	/**
	 * Gets a Tequila Token from the server of the service that is
	 * requesting authentication.
	 * Dispatches the job to the corresponding method.
	 */
	@Override
	@Deprecated
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
		return new TequilaKey(TypeOfService.SERVICE_POCKETCAMPUS, getTokenForPcFromTequila());
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
		TequilaKey teqKey = new TequilaKey();
		teqKey.setTos(TypeOfService.SERVICE_MOODLE);
		teqKey.setITequilaKey(params.getString("requestkey"));
		teqKey.setITequilaKeyForPc(getTokenForPcFromTequila());
		return teqKey;
	}

	/**
	 * Helper function to get Tequila Token for Camipro.
	 */
	private TequilaKey getTequilaKeyForCamipro() throws IOException {
		String cmdLine = "curl --include https://cmp2www.epfl.ch/ws/balance";
		String resp = executeCommand(cmdLine, "UTF-8");
		Cookie cookie = new Cookie();
		TequilaKey teqKey = new TequilaKey();
		teqKey.setTos(TypeOfService.SERVICE_CAMIPRO);
		for(String header : resp.split("\r\n")) {
			String shdr[] = header.split(":", 2);
			if(shdr.length != 2)
				continue;
			if("Set-Cookie".equalsIgnoreCase(shdr[0])) {
				cookie.setCookie(Arrays.asList(new String[]{shdr[1].trim()}));
			} else if("Location".equalsIgnoreCase(shdr[0])) {
		        URL url = new URL(shdr[1].trim());
				MultiMap<String> params = new MultiMap<String>();
				UrlEncoded.decodeTo(url.getQuery(), params, "UTF-8");
				teqKey.setITequilaKey(params.getString("requestkey"));
			}
		}
		teqKey.setLoginCookie(cookie.cookie());
		teqKey.setITequilaKeyForPc(getTokenForPcFromTequila());
		return teqKey;
	}

	/**
	 * Gets a valid SessionId from the server of the service that is
	 * requesting authentication, by providing it with
	 * the Tequila-authenticated token.
	 * Dispatches the job to the corresponding method.
	 */
	@Override
	@Deprecated
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
		String sciper = getUserDataFromTequilaAndInsertInDB(aTequilaKey.getITequilaKey());
		String sessId = UUID.randomUUID().toString();
		System.out.println("PC SESS ID: " + sessId);
		authDB.updateCookie("pc_cookie", sessId, sciper);
	    // send back the session id
	    SessionId si = new SessionId();
	    si.setTos(TypeOfService.SERVICE_POCKETCAMPUS);
	    si.setPocketCampusSessionId(sessId);
		return si;
	}

	/**
	 * Helper function to get SessionId for Moodle.
	 */
	private SessionId getSessionIdForMoodle(TequilaKey aTequilaKey) throws IOException {
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
        
        if(aTequilaKey.isSetITequilaKeyForPc()) {
        	String sciper = getUserDataFromTequilaAndInsertInDB(aTequilaKey.getITequilaKeyForPc());
        	if(sciper != null) {
        		authDB.updateCookie("moodle_cookie", cookie.cookie(), sciper);
        	}
        }
        
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
        if(aTequilaKey.isSetITequilaKeyForPc()) {
        	String sciper = getUserDataFromTequilaAndInsertInDB(aTequilaKey.getITequilaKeyForPc());
        	if(sciper != null) {
        		authDB.updateCookie("camipro_cookie", aTequilaKey.getLoginCookie(), sciper);
        	}
        }
        
	    SessionId si = new SessionId();
	    si.setTos(TypeOfService.SERVICE_CAMIPRO);
	    si.setCamiproCookie(aTequilaKey.getLoginCookie());
		return si;
	}
	
	@Override
	@Deprecated
	public int logOutSession(SessionId aSessionId) throws TException {
		System.out.println("logOutSession");
		try {
			switch (aSessionId.getTos()) {
			case SERVICE_POCKETCAMPUS:
				return authDB.killCookie("pc_cookie", aSessionId.getPocketCampusSessionId());
			case SERVICE_MOODLE:
				return authDB.killCookie("moodle_cookie", aSessionId.getMoodleCookie());
			case SERVICE_CAMIPRO:
				return authDB.killCookie("camipro_cookie", aSessionId.getCamiproCookie());
			default:
				return 404;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 500;
		}
	}
	
	/**
	 * Helper function to execute a UNIX command
	 */
	private String executeCommand(String cmd, String encoding) throws IOException {
		Runtime run = Runtime.getRuntime();
		Process pr = run.exec(cmd);
		try {
			pr.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new IOException("executeCommand: waitFor Interrupted");
		}
		return IOUtils.toString(pr.getInputStream(), encoding);
	}
	
	/**
	 * Helper function to get a TequilaKey from Tequila for PocketCampus
	 */
	private String getTokenForPcFromTequila() throws IOException {
	    ClientConfig config = new ClientConfig();
	    config.setHost("tequila.epfl.ch");
	    //config.setOrg("PocketCampus");
	    config.setService("PocketCampus");
	    config.setRequest("name firstname email title unit office phone username uniqueid unixid groupid where");
	    //config.setAllows("categorie=epfl-guests");
	    return TequilaService.instance().createRequest(config, "pocketcampus-redirect://login.pocketcampus.org");
	}
	
	/**
	 * Helper function to get user data from tequila and store it db
	 */
	private String getUserDataFromTequilaAndInsertInDB(String teqKey) throws IOException {
	    
	    ClientConfig clientConfig = new ClientConfig();
	    clientConfig.setHost("tequila.epfl.ch");

	    try {
	    	TequilaPrincipal principal = TequilaService.instance().validateKey(clientConfig, teqKey);
		    return authDB.insertUser(principal);
	    } catch(SecurityException e) {
	    	return null;
	    }
	    
		//System.out.println("principal = " + principal);
		// principal = [user=chamsedd, org=EPFL, host=128.178.236.75, attributes={phone=+41 21 6938188, status=ok, firstname=Amer, where=IN-MA1/IN-S/ETU/EPFL/CH, requesthost=128.178.77.233, version=2.1.1, unit=IN-MA1,Section d'informatique - Master semestre 1, uniqueid=211338, username=chamsedd,chamsedd@in-ma1, email=amer.chamseddine@epfl.ch, name=Chamseddine, authorig=cookie, unixid=112338, groupid=30132}]

	}

}
