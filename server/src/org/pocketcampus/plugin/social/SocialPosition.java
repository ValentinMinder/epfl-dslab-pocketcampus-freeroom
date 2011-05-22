package org.pocketcampus.plugin.social;

import java.sql.Timestamp;

import org.pocketcampus.shared.plugin.map.Position;
import org.pocketcampus.shared.plugin.social.User;

public class SocialPosition {
	private final User user_;
	private final Position position_;
	private final Timestamp timestamp_;
	
	public SocialPosition(User user, Position position, Timestamp timestamp) {
		user_ = user;
		position_ = position;
		timestamp_ = timestamp;
	}
	
	public User getUser() {
		return user_;
	}
	
	public Position getPosition() {
		return position_;
	}
	
	public Timestamp getTimestamp() {
		return timestamp_;
	}
}
