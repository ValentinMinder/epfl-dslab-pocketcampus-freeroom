package org.pocketcampus.platform.launcher.server;

/**
 * Authenticator implementation using the existing static methods on PocketCampusServer. 
 * Hack.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class AuthenticatorImpl implements Authenticator {
	@Override
	public String getSciper(Object queryParameter) {
		return PocketCampusServer.authGetUserSciper(queryParameter);
	}
}