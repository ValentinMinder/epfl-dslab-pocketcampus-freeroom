package org.pocketcampus.shared.plugin.social;

import org.pocketcampus.shared.plugin.authentication.AuthToken;


public class User {
	private final String firstName_;
	private final String lastName_;
	private final String sciper_;
	private String sessionId_;
	
	public User(String firstName, String lastName, String sciper) {
		valid(firstName, lastName, sciper);
		
		this.firstName_ = firstName;
		this.lastName_ = lastName;
		this.sciper_ = sciper;
		this.sessionId_ = null;
	}
	
	public User(String id) {
		if(id == null)
			throw new IllegalArgumentException();
		
		String[] data = id.split("\\.");
		
		if(data.length != 3)
			throw new IllegalArgumentException();
		
		valid(data[0], data[1], data[2]);
		
		this.lastName_ = underscoreToNice(data[0]);
		this.firstName_ = underscoreToNice(data[1]);
		this.sciper_ = data[2];
		this.sessionId_ = null;
	}
	
	public String getSessionId() {
		return sessionId_;
	}
	
	public void setSessionId(String sessionId) {
		if(sessionId.length() != AuthToken.SESSION_ID_SIZE)
			throw new IllegalArgumentException();
		
		this.sessionId_ = sessionId;
	}
	
	public String getFirstName() {
		return firstName_;
	}
	
	public String getLastName() {
		return lastName_;
	}
	
	public String getSciper() {
		return sciper_;
	}
	
	private static void valid(String first, String last, String sciper) {
		
	}
	
	private static String niceToUnderscore(final String s) {
		String rtrn = "";
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if(c != ' ') rtrn+=c;
			else rtrn+='_';
		}
		
		return rtrn;
	}
	
	private static String underscoreToNice(final String s) {
		String rtrn = "";
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if(c != '_') rtrn+=c;
			else rtrn+=' ';
		}
		
		return rtrn;
	}
	
	public String getIdFormat() {
		return niceToUnderscore(lastName_) + "." + niceToUnderscore(firstName_) + "." + sciper_;
	}
	
	private String getNiceFormat() {
		return firstName_ + " " + lastName_;
	}
	
	@Override
	public int hashCode() {
		return getIdFormat().hashCode();
	}
	
	@Override
	public String toString() {
		return getNiceFormat();
	}
}
