package org.pocketcampus.plugin.authentication;

import java.security.GeneralSecurityException;
import java.util.List;

import javax.net.ssl.SSLSocketFactory;
import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.shared.plugin.authentication.AuthToken;
import org.pocketcampus.shared.plugin.authentication.SessionId;
import org.pocketcampus.shared.plugin.authentication.Username;

import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

public class Authentication {
	private LDAPConnection ldap_;

	@PublicMethod
	public AuthToken authenticate(HttpServletRequest request){
		AuthToken authToken = null;

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
					authToken = generateAuthToken(username);
				} else {
					authToken = null; 
				}
			} else {
				authToken = null;
			}
		} catch(LDAPException e) {
			e.printStackTrace();
			authToken = null;
		} catch(GeneralSecurityException e) {
			e.printStackTrace();
			authToken = null;
		} finally {
			ldap_.close();
		}

		return authToken;
	}

	private static AuthToken generateAuthToken(String username) {
		//create session
		SessionId sessionId = new SessionId(
				"11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
		return new AuthToken(new Username(username), sessionId);
	}
}
