package org.pocketcampus.shared.plugin.social;

public class UserInfo {
	
	private final User user_;
	
	private final boolean requesting_;
	private final boolean online_;
	private final boolean requestingPosition_;
	
	public UserInfo(User user, boolean requesting, boolean online, boolean requestingPosition) {
		this.user_ = user;
		this.requesting_ = requesting;
		this.online_ = online;
		this.requestingPosition_ = requestingPosition;
		
		valid();
	}
	
	public User getUser_() {
		return user_;
	}

	public boolean isRequesting_() {
		return requesting_;
	}

	public boolean isOnline_() {
		return online_;
	}

	public boolean isRequestingPosition_() {
		return requestingPosition_;
	}
	
	private void valid() {
		if(requesting_ && (online_ || requestingPosition_))
			throw new IllegalArgumentException();
	}
}
