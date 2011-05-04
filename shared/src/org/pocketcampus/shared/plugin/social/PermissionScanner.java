package org.pocketcampus.shared.plugin.social;

import java.util.ArrayList;

import org.pocketcampus.shared.plugin.social.permissions.OtherPermission;
import org.pocketcampus.shared.plugin.social.permissions.PositionPermission;
import org.pocketcampus.shared.plugin.social.permissions.SocialPermission;

public class PermissionScanner {
	
	public static ArrayList<SocialPermission> scanPermissions() {
		ArrayList<SocialPermission> list = new ArrayList<SocialPermission>();
		
		//ADD DECLARATIONS HERE
		list.add(new PositionPermission());
		list.add(new OtherPermission());
		
		
		return list;
	}
}
