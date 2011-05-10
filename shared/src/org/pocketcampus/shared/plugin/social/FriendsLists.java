package org.pocketcampus.shared.plugin.social;

import java.util.LinkedList;

public class FriendsLists {
	private LinkedList<User> friends_;
	private LinkedList<User> requesting_;
	
	public FriendsLists(LinkedList<User> friends, LinkedList<User> requesting) {
		friends_ = friends;
		requesting_ = requesting;
	}
	
	public LinkedList<User> getFriends() {
		return friends_;
	}
	
	public LinkedList<User> getRequesting() {
		return requesting_;
	}
}
