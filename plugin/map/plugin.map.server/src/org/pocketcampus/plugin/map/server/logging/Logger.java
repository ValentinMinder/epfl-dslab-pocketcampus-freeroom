package org.pocketcampus.plugin.map.server.logging;

/**
 * Logs messages and exceptions in server logfiles. Intended for development and production
 * mode.
 */
public class Logger {
	public static enum Type {
		Access, Error
	}
	
	/**
	 * Logs an error or exception in the Error log, along with a message
	 * @param throwable
	 * @param message
	 */
	public static void log(Throwable throwable, String message) {
		// TODO
	}
	
	/**
	 * Logs an error or exception in the Error log
	 * @param throwable
	 */
	public static void log(Throwable throwable) {
		// TODO
	}
	
	/**
	 * Logs a message according to its type
	 * @param message
	 */
	public static void log(Type type, String message) {
		// TODO
	}
}
