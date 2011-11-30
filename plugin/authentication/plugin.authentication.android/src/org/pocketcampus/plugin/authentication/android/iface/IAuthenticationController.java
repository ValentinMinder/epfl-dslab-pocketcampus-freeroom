package org.pocketcampus.plugin.authentication.android.iface;

import org.pocketcampus.plugin.authentication.shared.TequilaKey;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;

public interface IAuthenticationController {
	
	public void authenticateUserForTequilaEnabledService(TypeOfService tos);
	public void signInUserLocallyToTequila(TequilaKey teqKey);
	
	public void authenticateUserForNonTequilaService(TypeOfService tos);
	
	public void setLocalCredentials(String user, String pass);
	
}
