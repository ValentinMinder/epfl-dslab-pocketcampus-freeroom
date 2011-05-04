package org.pocketcampus.plugin.authentication;

import java.awt.Toolkit;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.SSLSocketFactory;
import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;

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
	public String login(HttpServletRequest request){
		String sessionId = null;

		String username = request.getParameter("username");
		String password = request.getParameter("password");

		try {
			SSLSocketFactory socketFactory = new SSLUtil(new TrustAllTrustManager()).createSSLSocketFactory();
			ldap_ = new LDAPConnection(socketFactory, "ldap.epfl.ch", 636);
			SearchResult searchResult = ldap_.search("o=epfl,c=ch", SearchScope.SUB, "(uid="+username+")");
			List<SearchResultEntry> entries = searchResult.getSearchEntries();

			if(!entries.isEmpty()) {
				String dn = entries.get(0).getDN();
				BindResult bResult = ldap_.bind(dn, password);

				if(bResult.getResultCode().intValue() == ResultCode.SUCCESS.intValue()) {
					sessionId = AuthenticationSessions.newSession(username);
				} else {
					sessionId = null;
				}
			} else {
				sessionId = null;
			}
		} catch(LDAPException e) {
			e.printStackTrace();
			sessionId = null;
		} catch(GeneralSecurityException e) {
			e.printStackTrace();
			sessionId = null;
		} finally {
			ldap_.close();
		}

		return sessionId;
	}
	
	@PublicMethod
	public String s(HttpServletRequest request) {
		Toolkit.getDefaultToolkit().beep();
		return AuthenticationSessions.newSession("tsouintsouin");
	}
	
	@PublicMethod
	public Collection<String> ss(HttpServletRequest request) {
		return AuthenticationSessions.ss();
	}
	
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
}
