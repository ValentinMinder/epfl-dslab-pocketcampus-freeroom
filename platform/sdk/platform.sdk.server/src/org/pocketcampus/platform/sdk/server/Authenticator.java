package org.pocketcampus.platform.sdk.server;

/**
 * Authenticates requests; lets consumers get a SCIPER identifier from a query parameter.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public interface Authenticator {
	String getSciper();
}