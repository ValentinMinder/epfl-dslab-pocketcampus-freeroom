package org.pocketcampus.platform.launcher.server;

/**
 * Authenticates requests; lets consumers get a SCIPER identifier from a query parameter.
 * 
 * TODO: Find a way to make it less hack-y; can we implement a Thrift iface on a Jetty controller? 
 *       Then we could get the headers directly...
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public interface Authenticator {
	String getSciper(Object queryParameter);
}