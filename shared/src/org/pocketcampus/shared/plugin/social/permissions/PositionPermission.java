package org.pocketcampus.shared.plugin.social.permissions;

public class PositionPermission implements SocialPermission {

	@Override
	public String getId() {
		return "position";
	}

	@Override
	public String getName() {
		return "Position";
	}
}
