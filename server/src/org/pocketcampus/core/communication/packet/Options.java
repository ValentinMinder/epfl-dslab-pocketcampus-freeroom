package org.pocketcampus.core.communication.packet;

/**
 * Defines the set of Options used by both the client and the server
 */
public class Options implements Cloneable {
	
	/**
	 * Instance creator. Use it with the builder pattern.
	 */
	public Options() {}
	
	/**
	 * Clones the given Options. Intended for deep cloning by subclasses.
	 * @param that the Option to clone
	 */
	protected Options(Options that) {
		// Copy into this Options each field of that
	}
	
	
	public Options clone() {
		return new Options(this);
	}
}
