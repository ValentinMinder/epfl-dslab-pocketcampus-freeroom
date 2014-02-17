package org.pocketcampus.plugin.authentication.server;

import org.apache.thrift.TException;
import org.pocketcampus.platform.launcher.server.PocketCampusServer;
import org.pocketcampus.plugin.authentication.shared.AuthSessionResponse;
import org.pocketcampus.plugin.authentication.shared.AuthStatusCode;
import org.pocketcampus.plugin.authentication.shared.AuthTokenResponse;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService;

import ch.epfl.tequila.client.model.TequilaPrincipal;

/**
 * AuthenticationServiceImpl
 * 
 * The implementation of the server side of the Authentication Plugin.
 * 
 * It authenticates users to the PocketCampus service.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 * 
 */
public class AuthenticationServiceImpl implements AuthenticationService.Iface {

	private final SessionManager _manager; // TODO use the DataStore instead

	public AuthenticationServiceImpl() {
		System.out.println("Starting Authentication plugin server ...");
		_manager = new SessionManager();
		new Thread(_manager.getCleaner()).start();
	}

	@Override
	public AuthTokenResponse getAuthTequilaToken() throws TException {
		String token = PocketCampusServer.authGetTequilaToken("authentication");
		if(token == null)
			return new AuthTokenResponse(AuthStatusCode.NETWORK_ERROR);
		return new AuthTokenResponse(AuthStatusCode.OK).setTequilaToken(token);
	}

	@Override
	public AuthSessionResponse getAuthSessionId(String tequilaToken) throws TException {
		try {
			TequilaPrincipal principal = PocketCampusServer.authGetTequilaPrincipal(tequilaToken);
			if(principal == null)
				return new AuthSessionResponse(AuthStatusCode.NETWORK_ERROR);
			String session = _manager.insert(principal.getUser(), principal.getAttribute("uniqueid"));
			return new AuthSessionResponse(AuthStatusCode.OK).setSessionId(session);
			
		} catch(SecurityException e) {
			return new AuthSessionResponse(AuthStatusCode.INVALID_SESSION);
		}
	}

	public String getGasparFromSession(String sess) {
		return _manager.getGaspar(sess);
	}
	
	public String getSciperFromSession(String sess) {
		return _manager.getSciper(sess);
	}

}
