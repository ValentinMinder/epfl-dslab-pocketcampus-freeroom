package org.pocketcampus.plugin.authentication;

import java.awt.Toolkit;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.net.ssl.SSLSocketFactory;
import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.shared.plugin.social.User;

import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

public class Authentication implements IPlugin {
	private LDAPConnection ldap_;

	@PublicMethod
	public User login(HttpServletRequest request){
		User user = null;

		String username = request.getParameter("username");
		String password = request.getParameter("password");

		try {
			SSLSocketFactory socketFactory = new SSLUtil(new TrustAllTrustManager()).createSSLSocketFactory();
			ldap_ = new LDAPConnection(socketFactory, "ldap.epfl.ch", 636);
			
			//Get username entry in LDAP
			SearchResult searchResult = ldap_.search("o=epfl,c=ch", SearchScope.SUB, "(uid="+username+")");
			List<SearchResultEntry> entries = searchResult.getSearchEntries();

			if(!entries.isEmpty()) {
				//Try to match with password
				String dn = entries.get(0).getDN();
				BindResult bResult = ldap_.bind(dn, password);

				if(bResult.getResultCode().intValue() == ResultCode.SUCCESS.intValue()) {
					String firstName = entries.get(0).getAttribute("givenName").getValue();
					String lastName = entries.get(0).getAttribute("sn").getValue();
					String sciper = entries.get(0).getAttribute("uniqueIdentifier").getValue();
					
					//Open a session
					String sessionId = AuthenticationSessions.newSession(username, password);
					if(sessionId != null) {
						user = new User(firstName, lastName, sciper);
						user.setSessionId(sessionId);
					} else {
						user = null;
					}
				} else {
					user = null;
				}
			} else {
				user = null;
			}
		} catch(LDAPException e) {
			e.printStackTrace();
			user = null;
		} catch(GeneralSecurityException e) {
			e.printStackTrace();
			user = null;
		} finally {
			ldap_.close();
		}

		return user;
	}
	
	@PublicMethod
	public String s(HttpServletRequest request) {
		Toolkit.getDefaultToolkit().beep();
		return AuthenticationSessions.newSession("tsouintsouin", "");
	}
	
//	@PublicMethod
//	public Collection<String> ss(HttpServletRequest request) {
//		return AuthenticationSessions.ss();
//	}
	
	@PublicMethod
	public boolean authenticate(HttpServletRequest request) {
		String username = request.getParameter("username");
		String sessionId = request.getParameter("sessionId");
		
		boolean authed = false;
		
		if(username != null && sessionId != null) {
			authed = AuthenticationSessions.authenticateSession(username, sessionId);
		}
		
		return authed;
	}
	
	@PublicMethod
	public boolean logout(HttpServletRequest request) {
		String username = request.getParameter("username");
		String sessionId = request.getParameter("sessionId");
		
		boolean status = false;
		
		if(username != null && sessionId != null) {
			if(AuthenticationSessions.authenticateSession(username, sessionId)) {
				AuthenticationSessions.freeSession(username);
				status = true;
			}
		}
		
		return status;
	}
	
	public static User identifyByUsername(String username) {
		User user = null;
		LDAPConnection ldap = null;
		
		try {
			SSLSocketFactory socketFactory = new SSLUtil(new TrustAllTrustManager()).createSSLSocketFactory();
			ldap = new LDAPConnection(socketFactory, "ldap.epfl.ch", 636);
			SearchResult searchResult = ldap.search("o=epfl,c=ch", SearchScope.SUB, "(uid="+username+")");
			List<SearchResultEntry> entries = searchResult.getSearchEntries();

			if(!entries.isEmpty()) {
				String firstName = entries.get(0).getAttribute("givenName").getValue();
				String lastName = entries.get(0).getAttribute("sn").getValue();
				String sciper = entries.get(0).getAttribute("uniqueIdentifier").getValue();
					
				user = new User(firstName, lastName, sciper);
				user.setSessionId(AuthenticationSessions.getSession(username));
			} else {
				user = null;
			}
		} catch(LDAPException e) {
			e.printStackTrace();
			user = null;
		} catch(GeneralSecurityException e) {
			e.printStackTrace();
			user = null;
		} finally {
			ldap.close();
		}

		return user;
	}
	
	public static User identifyBySciper(String sciper) {
		User user = null;
		LDAPConnection ldap = null;
		
		try {
			SSLSocketFactory socketFactory = new SSLUtil(new TrustAllTrustManager()).createSSLSocketFactory();
			ldap = new LDAPConnection(socketFactory, "ldap.epfl.ch", 636);
			SearchResult searchResult = ldap.search("o=epfl,c=ch", SearchScope.SUB, "(uniqueIdentifier="+sciper+")");
			List<SearchResultEntry> entries = searchResult.getSearchEntries();

			if(!entries.isEmpty()) {
				String firstName = entries.get(0).getAttribute("givenName").getValue();
				String lastName = entries.get(0).getAttribute("sn").getValue();
				String username = entries.get(0).getAttribute("uid").getValue();
					
				user = new User(firstName, lastName, sciper);
				String sessionId = AuthenticationSessions.getSession(username);
				if(sessionId != null) {
					user.setSessionId(sessionId);
				}
				
			} else {
				user = null;
			}
		} catch(LDAPException e) {
			e.printStackTrace();
			user = null;
		} catch(GeneralSecurityException e) {
			e.printStackTrace();
			user = null;
		} finally {
			ldap.close();
		}

		return user;
	}
}
