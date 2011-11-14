package org.pocketcampus.plugin.authentication.android.iface;

import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.TequilaKey;
import org.pocketcampus.plugin.authentication.shared.TypeOfService;

public interface IAuthenticationModel {
	
	// SessionId
	public SessionId getSessionIdForService(TypeOfService tos);
	public String getSessionIds(); // debugging purposes only
	
	// TequilaKey
	public TequilaKey getTequilaKey();
	
}
