package org.pocketcampus.shared.plugin.social.permissions;

public class OtherPermission implements SocialPermission {

	@Override
	public String getId() {
		return "other";
	}

	@Override
	public String getName() {
		return "Other";
	}
	
}
