package org.pocketcampus.plugin.authentication.server;

import org.pocketcampus.platform.server.Authenticator;

/**
 * Authenticator implementation using the existing static methods on PocketCampusServer. 
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class AuthenticatorImpl implements Authenticator {
	@Override
	public String getSciper() {
		return AuthenticationServiceImpl.authGetUserSciper();
	}
	
	@Override
	public String getGaspar(){
		return AuthenticationServiceImpl.authGetUserGaspar();
	}
}