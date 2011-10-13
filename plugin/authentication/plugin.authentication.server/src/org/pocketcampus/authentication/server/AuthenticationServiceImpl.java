package org.pocketcampus.authentication.server;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService;
import org.pocketcampus.plugin.authentication.shared.SessionToken;

/**
 * TODO use exceptions to give more information when something goes wrong.
 * @author Florian
 *
 */
public class AuthenticationServiceImpl implements AuthenticationService.Iface {
	private LdapAuthentication mLdapAuth = new LdapAuthentication(new EpflLdapConfig());
	private SessionManager mSessionManager;

	@Override
	public SessionToken login(String username, String password) throws TException {
		boolean authenticationResult = mLdapAuth.authenticate(username, password);

		if(authenticationResult) {
			System.out.println("Login successful.");
			SessionToken token = mSessionManager.openSession(username);
			return token;
		}

		System.out.println("Login failure.");
		return (SessionToken)null;
	}

	@Override
	public boolean authenticate(SessionToken token) throws TException {
		return mSessionManager.checkSession(token);
	}

	@Override
	public boolean logout(SessionToken token) throws TException {
		return mSessionManager.closeSession(token);
	}

}
