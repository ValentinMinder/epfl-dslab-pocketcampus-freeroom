package org.pocketcampus.plugin.authentication.server;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.platform.launcher.server.PocketCampusServer;
import org.pocketcampus.platform.launcher.server.PocketCampusServer.AuthUserDetailsReq;
import org.pocketcampus.platform.launcher.server.PocketCampusServer.AuthUserDetailsResp;
import org.pocketcampus.plugin.authentication.shared.AuthSessionRequest;
import org.pocketcampus.plugin.authentication.shared.AuthSessionResponse;
import org.pocketcampus.plugin.authentication.shared.AuthStatusCode;
import org.pocketcampus.plugin.authentication.shared.AuthTokenResponse;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService;
import org.pocketcampus.plugin.authentication.shared.LogoutRequest;
import org.pocketcampus.plugin.authentication.shared.LogoutResponse;
import org.pocketcampus.plugin.authentication.shared.UserAttributesRequest;
import org.pocketcampus.plugin.authentication.shared.UserAttributesResponse;

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
	public LogoutResponse destroyAllUserSessions(LogoutRequest req) throws TException {
		String sciper = getSciperFromSession(req.getSessionId());
		if(sciper == null)
			return new LogoutResponse(AuthStatusCode.INVALID_SESSION);
		Integer resp = _manager.destroySessions(sciper);
		if(resp == null)
			return new LogoutResponse(AuthStatusCode.SERVER_ERROR);
		LogoutResponse ret = new LogoutResponse(AuthStatusCode.OK);
		ret.setDeletedSessionsCount(resp);
		return ret;
	}

	@Override
	public UserAttributesResponse getUserAttributes(UserAttributesRequest req) throws TException {
		List<String> fields = new LinkedList<String>();
		for(String s : req.getAttributeNames())
			fields.add("`" + s + "`");
		AuthUserDetailsResp resp = getUserFieldsFromSession(new AuthUserDetailsReq(req.getSessionId(), fields));
		if(resp == null)
			return new UserAttributesResponse(AuthStatusCode.INVALID_SESSION);
		UserAttributesResponse ret = new UserAttributesResponse(AuthStatusCode.INVALID_SESSION);
		ret.setUserAttributes(resp.fieldValues);
		return ret;
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

	/**
	 * INTER-PLUGIN INTERFACE BEGIN
	 */
	
	public String getGasparFromSession(String sess) {
		return getFieldFromSession(sess, "`gaspar`");
	}
	
	public String getSciperFromSession(String sess) {
		return getFieldFromSession(sess, "`sciper`");
	}
	
	/**
	 * Gets any fields/attributes related to the logged in user
	 * field names must be surrounded by MySQL quotes (`)
	 * returned list is null if the session has expired / does not exist
	 */
	public AuthUserDetailsResp getUserFieldsFromSession(AuthUserDetailsReq req) {
		return new AuthUserDetailsResp(_manager.getFields(req.sessionId, req.requestedFields));
	}
	
	/**
	 * INTER-PLUGIN INTERFACE END
	 */
	
	private String firstValue(String crap) {
		crap = crap.split("[,]")[0];
		return crap;
	}

	private String getFieldFromSession(String sess, String field) {
		List<String> list = _manager.getFields(sess, Arrays.asList(new String[]{field}));
		return (list == null ? null : firstValue(list.get(0)));
	}

}
