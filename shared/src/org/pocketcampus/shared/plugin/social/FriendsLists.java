package org.pocketcampus.shared.plugin.social;

import java.util.Collection;
import java.util.HashSet;

public class FriendsLists {
	private final Collection<UserInfo> users_;
	
	public FriendsLists(Collection<UserInfo> users) {
		users_ = users;
	}
	
	public Collection<User> getFriends() {
		HashSet<User> out = new HashSet<User>();
		for(UserInfo ui : users_) {
			if(!ui.isRequesting_()) out.add(ui.getUser_());
		}
		
		return out;
	}
	
	public Collection<User> getRequesting() {
		HashSet<User> out = new HashSet<User>();
		for(UserInfo ui : users_) {
			if(ui.isRequesting_()) out.add(ui.getUser_());
		}
		
		return out;
	}
	
	public Collection<User> getOnlineFriends() {
		HashSet<User> out = new HashSet<User>();
		for(UserInfo ui : users_) {
			if(!ui.isRequesting_() && ui.isOnline_()) out.add(ui.getUser_());
		}
		
		return out;
	}
	
	public Collection<User> getOfflineFriends() {
		HashSet<User> out = new HashSet<User>();
		for(UserInfo ui : users_) {
			if(!ui.isRequesting_() && !ui.isOnline_()) out.add(ui.getUser_());
		}
		
		return out;
	}
	
	public Collection<User> getRequestingPositionFriends() {
		HashSet<User> out = new HashSet<User>();
		for(UserInfo ui : users_) {
			if(!ui.isRequesting_() && ui.isRequestingPosition_()) out.add(ui.getUser_());
		}
		
		return out;
	}
}
