package org.pocketcampus.plugin.authentication.server;

import java.util.Arrays;
import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.platform.launcher.server.PocketCampusServer;
import org.pocketcampus.plugin.authentication.shared.AuthSessionRequest;
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

	private final SessionManager _manager;

	public AuthenticationServiceImpl() {
		System.out.println("Starting Authentication plugin server ...");
		_manager = new SessionManagerImpl();
	}

	@Override
	public AuthTokenResponse getAuthTequilaToken() throws TException {
		String token = PocketCampusServer.authGetTequilaToken("authentication");
		if(token == null)
			return new AuthTokenResponse(AuthStatusCode.NETWORK_ERROR);
		return new AuthTokenResponse(AuthStatusCode.OK).setTequilaToken(token);
	}

	@Override
	public AuthSessionResponse getAuthSession(AuthSessionRequest req) throws TException {
		try {
			TequilaPrincipal principal = PocketCampusServer.authGetTequilaPrincipal(req.getTequilaToken());
			if(principal == null)
				return new AuthSessionResponse(AuthStatusCode.NETWORK_ERROR);
			String session = _manager.insert(principal, req.isRememberMe());
			return new AuthSessionResponse(AuthStatusCode.OK).setSessionId(session);
			
		} catch(SecurityException e) {
			return new AuthSessionResponse(AuthStatusCode.INVALID_SESSION);
		}
	}

	@Override
	@Deprecated
	public AuthSessionResponse getAuthSessionId(String tequilaToken) throws TException {
		try {
			TequilaPrincipal principal = PocketCampusServer.authGetTequilaPrincipal(tequilaToken);
			if(principal == null)
				return new AuthSessionResponse(AuthStatusCode.NETWORK_ERROR);
			String session = _manager.insert(principal, false);
			return new AuthSessionResponse(AuthStatusCode.OK).setSessionId(session);
			
		} catch(SecurityException e) {
			return new AuthSessionResponse(AuthStatusCode.INVALID_SESSION);
		}
	}

	public String getGasparFromSession(String sess) {
		return getFieldFromSession(sess, "`gaspar`");
	}
	
	public String getSciperFromSession(String sess) {
		return getFieldFromSession(sess, "`sciper`");
	}
	
	public String getFirstNameFromSession(String sess) {
		return getFieldFromSession(sess, "`firstname`");
	}
	
	public String getLastNameFromSession(String sess) {
		return getFieldFromSession(sess, "`lastname`");
	}
	
	/**
	 * Gets any fields/attributes related to the logged in user
	 * field names must be surrounded by MySQL quotes (`)
	 * returns null if the session has expired / does not exist
	 */
	public List<String> getUserFieldsFromSession(String sess, List<String> fields) {
		return _manager.getFields(sess, fields);
	}
	
	private String firstValue(String crap) {
		crap = crap.split("[,]")[0];
		return crap;
	}

	private String getFieldFromSession(String sess, String field) {
		List<String> list = _manager.getFields(sess, Arrays.asList(new String[]{field}));
		return (list == null ? null : firstValue(list.get(0)));
	}
	
}
