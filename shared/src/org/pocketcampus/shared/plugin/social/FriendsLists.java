package org.pocketcampus.shared.plugin.social;

import java.util.Collection;

public class FriendsLists {
	private Collection<User> friends_;
	private Collection<User> requesting_;
	
	public FriendsLists(Collection<User> friends, Collection<User> requesting) {
		friends_ = friends;
		requesting_ = requesting;
	}
	
	public Collection<User> getFriends() {
		return friends_;
	}
	
	public Collection<User> getRequesting() {
		return requesting_;
	}
}
