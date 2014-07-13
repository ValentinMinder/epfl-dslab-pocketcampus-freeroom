package org.pocketcampus.platform.server;

import org.pocketcampus.platform.server.launcher.PocketCampusServer;

/**
 * Authenticator implementation using the existing static methods on PocketCampusServer. 
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class AuthenticatorImpl implements Authenticator {
	@Override
	public String getSciper() {
		return PocketCampusServer.authGetUserSciper();
	}
}