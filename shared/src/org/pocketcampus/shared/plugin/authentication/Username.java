package org.pocketcampus.shared.plugin.authentication;

import java.io.Serializable;

public class Username implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8882511133816637542L;
	private String username;
	
	public Username(String username) {
		if (username == null)
			throw new IllegalArgumentException("Argument 'username' cannot be null");
		
		this.username = username;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Username) {
			Username that = (Username) o;
			
			return this.username.equals(that.username);
		}
		return false;
	}
	
	public int hashCode() {
		return this.username.hashCode();
	}
	
	public String toString() {
		return this.username;
	}
}
