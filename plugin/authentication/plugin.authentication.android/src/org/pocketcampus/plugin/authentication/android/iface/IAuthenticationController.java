package org.pocketcampus.plugin.authentication.android.iface;

import org.pocketcampus.plugin.authentication.shared.TypeOfService;

public interface IAuthenticationController {
	
	public void authenticateUserForService(TypeOfService tos);
	
}
