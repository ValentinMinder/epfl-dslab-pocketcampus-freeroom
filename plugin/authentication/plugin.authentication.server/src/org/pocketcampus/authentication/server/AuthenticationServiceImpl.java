package org.pocketcampus.authentication.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.thrift.TException;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.UrlEncoded;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService;
import org.pocketcampus.plugin.authentication.shared.LoginException;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.SessionToken;
import org.pocketcampus.plugin.authentication.shared.TequilaKey;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;

import ch.epfl.tequila.client.model.ClientConfig;
import ch.epfl.tequila.client.model.TequilaPrincipal;
import ch.epfl.tequila.client.service.TequilaService;

public class AuthenticationServiceImpl implements AuthenticationService.Iface {
	
	@Override
	public TequilaKey getTequilaKeyForService(TypeOfService aService) throws TException {
		System.out.println("getTequilaKeyForService");
		
		TequilaKey teqKey = new TequilaKey();
		teqKey.setTos(aService);
		try {
			switch (aService) {
			case SERVICE_POCKETCAMPUS:
				teqKey.setITequilaKey(getTequilaKeyForPocketCampus());
				break;
			case SERVICE_MOODLE:
				teqKey.setITequilaKey(getTequilaKeyForMoodle());
				break;
			default:
				throw new IOException("getTequilaKeyForService: Cannot understand this TypeOfService");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to getTequilaKeyForService from upstream server");
		}
		
		return teqKey;
	}
	
	private String getTequilaKeyForPocketCampus() throws IOException {
	    ClientConfig config = new ClientConfig();
	    config.setHost("tequila.epfl.ch");
	    //config.setOrg("PocketCampus");
	    config.setService("PocketCampus");
	    config.setRequest("name firstname email title unit office phone username uniqueid unixid groupid where");
	    //config.setAllows("categorie=epfl-guests");

		return TequilaService.instance().createRequest(config, "pocketcampus-redirect://login.pocketcampus.org");
	    //System.out.println("https://tequila.epfl.ch/cgi-bin/tequila/requestauth?requestkey=" + key);
	}

	private String getTequilaKeyForMoodle() throws IOException {
		/**
		 * GET http://moodle.epfl.ch/auth/tequila/index.php
		 * get back
		 * Set-Cookie[TequilaPHP=bfxy2kp4wlppax0vzfnhjevdxvftxt6g]
		 * Location[https://tequila.epfl.ch/cgi-bin/tequila/requestauth?requestkey=bfxy2kp4wlppax0vzfnhjevdxvftxt6g]
		 */

        
		
        HttpURLConnection conn2 = (HttpURLConnection) new URL("http://moodle.epfl.ch/auth/tequila/index.php").openConnection();
        conn2.setInstanceFollowRedirects(false);
        conn2.getInputStream();
        //System.out.println("getTequilaKeyForMoodle: Set-Cookie: " + conn2.getHeaderField("Set-Cookie"));
        //System.out.println("getTequilaKeyForMoodle: Set-Cookie: " + conn2.getHeaderFields().get("Set-Cookie").toString());
        //System.out.println(conn.getHeaderField("Location"));
        URL url = new URL(conn2.getHeaderField("Location"));
		MultiMap<String> params = new MultiMap<String>();
		UrlEncoded.decodeTo(url.getQuery(), params, "UTF-8");
		return params.getString("requestkey");
	}

	private String getTequilaKeyForCamipro() throws IOException {
        HttpURLConnection conn2 = (HttpURLConnection) new URL("http://camipro.epfl.ch/cms/engineName/tequila_login/site/camipro/pid/6801").openConnection();
        conn2.setInstanceFollowRedirects(false);
        conn2.getInputStream();
        URL url = new URL(conn2.getHeaderField("Location"));
		MultiMap<String> params = new MultiMap<String>();
		UrlEncoded.decodeTo(url.getQuery(), params, "UTF-8");
		return params.getString("requestkey");
	}

	
	
/*
	
    public static String openUrlGetNoRedirect(String url) throws MalformedURLException, IOException {
          String endLine = "\r\n";

          OutputStream os;

          HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
          conn.setInstanceFollowRedirects(false);

          read(conn.getInputStream());
          conn.getHeaderField("Location");
          return response;
      }

      private static String read(InputStream in) throws IOException {
          StringBuilder sb = new StringBuilder();
          BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
          for (String line = r.readLine(); line != null; line = r.readLine()) {
              sb.append(line);
          }
          in.close();
          return sb.toString();
      }
	*/
	
	
	


	@Override
	public SessionId getSessionIdForService(TequilaKey aTequilaKey) throws TException {
		System.out.println("getSessionIdForService");
		
	    String tKey = aTequilaKey.getITequilaKey();
	    TypeOfService tService = aTequilaKey.getTos();
	    
		try {
			switch (tService) {
			case SERVICE_POCKETCAMPUS:
				return getSessionIdForPocketCampus(tKey);
			case SERVICE_MOODLE:
				return getSessionIdForMoodle(tKey);
			default:
				throw new IOException("getSessionIdForService: Cannot understand this TypeOfService");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to getSessionIdForService from upstream server");
		}
		
	}
	
	private SessionId getSessionIdForPocketCampus(String key) throws IOException {
	    ClientConfig clientConfig = new ClientConfig();
	    clientConfig.setHost("tequila.epfl.ch");

	    TequilaPrincipal principal = TequilaService.instance().validateKey(clientConfig, key);
	    
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

	private SessionId getSessionIdForMoodle(String key) throws IOException {
		// Location=http://moodle.epfl.ch/auth/tequila/teq_return.php?key=tbeojh8jikfmzyau8mof6boynsmh5ypd
	    /* session id for moodle
	     * MoodleSession=c50krlv62gif18j2v4lputgo55;
	     * MoodleSessionTest=tbhRwvg9NY;
	     * MOODLEID_=%25E0%25C4%251FA%25A0x%25B4%250A
	    */
		Cookie cookie = new Cookie();
		
        HttpURLConnection conn = (HttpURLConnection) new URL("http://moodle.epfl.ch").openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.getInputStream();
        //System.out.println("getSessionIdForMoodle: Set-Cookie: " + conn.getHeaderFields().get("Set-Cookie").toString());
        cookie.setCookie(conn.getHeaderFields().get("Set-Cookie"));
        
        
        HttpURLConnection conn2 = (HttpURLConnection) new URL("http://moodle.epfl.ch/auth/tequila/teq_return.php?key=" + key).openConnection();
        conn2.setRequestProperty("Cookie", cookie.cookie());
        conn2.setInstanceFollowRedirects(false);
        conn2.getInputStream();
        cookie.setCookie(conn2.getHeaderFields().get("Set-Cookie"));
        //System.out.println("getSessionIdForMoodle: Location: " + conn2.getHeaderField("Location"));
        
	    // send back the session id
	    SessionId si = new SessionId();
	    si.setTos(TypeOfService.SERVICE_MOODLE);
	    si.setMoodleCookie(cookie.cookie());
		return si;
	}
	
	private SessionId getSessionIdForCamipro(String key) throws IOException {
		// TODO do it (this is copy-paste from Moodle)
		/******
		 * tequilaPHP=b30e3m40u52uooeklfiutkaijt0x0nft;
		 * servicesEPFL=db47eb6d2s9gp2aimodbsvpfv5;
		 */
		Cookie cookie = new Cookie();
		
        HttpURLConnection conn = (HttpURLConnection) new URL("http://moodle.epfl.ch").openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.getInputStream();
        //System.out.println("getSessionIdForMoodle: Set-Cookie: " + conn.getHeaderFields().get("Set-Cookie").toString());
        cookie.setCookie(conn.getHeaderFields().get("Set-Cookie"));
        
        
        HttpURLConnection conn2 = (HttpURLConnection) new URL("http://camipro.epfl.ch/cms/engineName/tequila_login/site/camipro/pid/6801?key=" + key).openConnection();
        conn2.setRequestProperty("Cookie", cookie.cookie());
        conn2.setInstanceFollowRedirects(false);
        conn2.getInputStream();
        cookie.setCookie(conn2.getHeaderFields().get("Set-Cookie"));
        //System.out.println("getSessionIdForMoodle: Location: " + conn2.getHeaderField("Location"));
        
	    // send back the session id
	    SessionId si = new SessionId();
	    si.setTos(TypeOfService.SERVICE_MOODLE);
	    si.setMoodleCookie(cookie.cookie());
		return si;
	}
	


	private SessionManager mSessionManager = new SessionManager();

	/*private LdapAuthentication mLdapAuth = new LdapAuthentication(new EpflLdapConfig());
	private SessionManager mSessionManager = new SessionManager();

	@Override
	public SessionToken login(String username, String password) throws TException, LoginException {
		System.out.println("Trying to login using " + username + ", " + password);
		
		boolean authenticationResult = mLdapAuth.authenticate(username, password);

		if(authenticationResult) {
			System.out.println("Login successful.");
			SessionToken token = mSessionManager.openSession(username);
			return token;
		}

		System.out.println("Login failure.");
		throw new LoginException();
	}

	@Override
	public boolean authenticate(SessionToken token) throws TException {
		return mSessionManager.checkSession(token);
	}

	@Override
	public boolean logout(SessionToken token) throws TException {
		return mSessionManager.closeSession(token);
	}*/

}
