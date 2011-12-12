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

public class AuthenticationServiceImpl implements AuthenticationService.Iface {
	
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
	
	private TequilaKey getTequilaKeyForPocketCampus() throws IOException {
	    ClientConfig config = new ClientConfig();
	    config.setHost("tequila.epfl.ch");
	    //config.setOrg("PocketCampus");
	    config.setService("PocketCampus");
	    config.setRequest("name firstname email title unit office phone username uniqueid unixid groupid where");
	    //config.setAllows("categorie=epfl-guests");

	    //System.out.println("https://tequila.epfl.ch/cgi-bin/tequila/requestauth?requestkey=" + key);
	    String keyStr = TequilaService.instance().createRequest(config, "pocketcampus-redirect://login.pocketcampus.org");
		return new TequilaKey(TypeOfService.SERVICE_POCKETCAMPUS, keyStr);
	}

	private TequilaKey getTequilaKeyForMoodle() throws IOException {
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
		return new TequilaKey(TypeOfService.SERVICE_MOODLE, params.getString("requestkey"));
	}

	private TequilaKey getTequilaKeyForCamipro() throws IOException {
		Cookie cookie = new Cookie();
		
        HttpURLConnection conn2 = (HttpURLConnection) new URL("https://cmp2www.epfl.ch/client/serhome-en").openConnection();
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

	private SessionId getSessionIdForMoodle(TequilaKey aTequilaKey) throws IOException {
	    if(aTequilaKey.getTos() != TypeOfService.SERVICE_MOODLE)
	    	throw new IOException("getSessionIdForMoodle: Called with wrong TypeOfService");
	    
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
        
        
        HttpURLConnection conn2 = (HttpURLConnection) new URL("http://moodle.epfl.ch/auth/tequila/teq_return.php?key=" + aTequilaKey.getITequilaKey()).openConnection();
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
	
	private SessionId getSessionIdForCamipro(TequilaKey aTequilaKey) throws IOException {
	    if(aTequilaKey.getTos() != TypeOfService.SERVICE_CAMIPRO)
	    	throw new IOException("getSessionIdForCamipro: Called with wrong TypeOfService");
	    
		/******
		 * tequilaPHP=b30e3m40u52uooeklfiutkaijt0x0nft;
		 * servicesEPFL=db47eb6d2s9gp2aimodbsvpfv5;
		 */
		Cookie cookie = new Cookie();
		String loginCookie = aTequilaKey.getLoginCookie();
	    if(loginCookie == null)
	    	throw new IOException("getSessionIdForCamipro: loginCookie is null");
	    cookie.importFromString(loginCookie);
		
        HttpURLConnection conn2 = (HttpURLConnection) new URL("https://cmp2www.epfl.ch/client/serhome-en").openConnection();
        conn2.setRequestProperty("Cookie", cookie.cookie());
        conn2.setInstanceFollowRedirects(false);
        conn2.getInputStream();
        //cookie.setCookie(conn2.getHeaderFields().get("Set-Cookie"));
        if(!"https://cmp2www.epfl.ch:443/client/serhome".equals(conn2.getHeaderField("Location")))
        	System.out.println("getSessionIdForCamipro: WARNING Location field is not as expected, authentication has probably failed");
        
	    // send back the session id
	    SessionId si = new SessionId();
	    si.setTos(TypeOfService.SERVICE_CAMIPRO);
	    si.setCamiproCookie(cookie.cookie());
		return si;
	}
	



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
