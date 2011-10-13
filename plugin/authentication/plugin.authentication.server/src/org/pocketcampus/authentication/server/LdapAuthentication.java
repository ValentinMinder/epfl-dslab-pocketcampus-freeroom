package org.pocketcampus.authentication.server;

import java.security.GeneralSecurityException;
import java.util.List;

import javax.net.ssl.SSLSocketFactory;

import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

public class LdapAuthentication {
	private LdapConfig mConfig;

	public LdapAuthentication(LdapConfig config) {
		mConfig = config;
	}

	public boolean authenticate(String username, String password) {
		try {
			String baseDn = mConfig.getBaseDn();
			int port = mConfig.getPort();
			SSLSocketFactory socketFactory = new SSLUtil(new TrustAllTrustManager()).createSSLSocketFactory();

			LDAPConnection ldap = new LDAPConnection(socketFactory, mConfig.getHost(), port);

			String filter = "(uid=" + username + ")";
			SearchResult searchResult = ldap.search(baseDn, SearchScope.SUB, filter);

			List<SearchResultEntry> entries = searchResult.getSearchEntries();

			if(entries.size() == 1) {
				String dn = entries.get(0).getDN();
				BindResult bindResult = ldap.bind(dn, password);

				if(bindResult.getResultCode().equals(ResultCode.SUCCESS)) {
					return true;
				}
			}

		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} catch (LDAPException e) {
			e.printStackTrace();
		}

		return false;
	}
}















