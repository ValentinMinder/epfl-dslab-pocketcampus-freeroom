package org.pocketcampus.plugin.authentication.server;

import ch.epfl.tequila.client.model.ClientConfig;
import ch.epfl.tequila.client.model.TequilaPrincipal;
import ch.epfl.tequila.client.service.TequilaService;
import org.apache.thrift.TException;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.platform.shared.PCConstants;
import org.pocketcampus.plugin.authentication.shared.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
		_manager = new SessionManagerImpl();
	}

	@Override
	public AuthTokenResponse getAuthTequilaToken() throws TException {
		String token = authGetTequilaToken("authentication");
		if(token == null)
			return new AuthTokenResponse(AuthStatusCode.NETWORK_ERROR);
		return new AuthTokenResponse(AuthStatusCode.OK).setTequilaToken(token);
	}

	@Override
	public AuthSessionResponse getAuthSession(AuthSessionRequest req) throws TException {
		try {
			TequilaPrincipal principal = authGetTequilaPrincipal(req.getTequilaToken());
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
		List<String> fields = new LinkedList<>();
		for(String s : req.getAttributeNames())
			fields.add("`" + s + "`");
		AuthUserDetailsResp resp = getUserFieldsFromSession(new AuthUserDetailsReq(req.getSessionId(), fields));
		if(resp.fieldValues == null)
			return new UserAttributesResponse(AuthStatusCode.INVALID_SESSION);
		UserAttributesResponse ret = new UserAttributesResponse(AuthStatusCode.OK);
		ret.setUserAttributes(resp.fieldValues);
		return ret;
	}
	
	@Override
	@Deprecated
	public AuthSessionResponse getAuthSessionId(String tequilaToken) throws TException {
		try {
			TequilaPrincipal principal = authGetTequilaPrincipal(tequilaToken);
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
	
	public static String authGetTequilaToken(String plugin) {

		ClientConfig config = new ClientConfig();
		config.setHost("tequila.epfl.ch");
		// config.setOrg("PocketCampusOrg");
		config.setService(plugin + "@pocketcampus");
		config.setRequest("name firstname email title unit office phone username uniqueid unixid groupid where categorie");
		config.setAllows("categorie=Shibboleth|categorie=epfl-guests");
		// config.setAuthstrength("2");

		try {
			return TequilaService.instance().createRequest(config, "pocketcampus://" + plugin + ".plugin.pocketcampus.org/authenticated");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static TequilaPrincipal authGetTequilaPrincipal(String token) {
		ClientConfig config = new ClientConfig();
		config.setHost("tequila.epfl.ch");

		try {
			return TequilaService.instance().validateKey(config, token);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String authGetUserGaspar() {
		return callOnAuthPlugin("getGasparFromSession");
	}

	public static String authGetUserGasparFromReq(HttpServletRequest request) {
		String pcSessionId = request.getHeader(PCConstants.HTTP_HEADER_AUTH_PCSESSID);
		try {
			return (String) PocketCampusServer.invokeOnPlugin("authentication", "getGasparFromSession", pcSessionId);
		} catch (Exception e) {
		}
		return null;
	}

	public static String authGetUserSciper() {
		return callOnAuthPlugin("getSciperFromSession");
	}


	public static List<String> authGetUserAttributes(List<String> attr) {
		String pcSessionId = PocketCampusServer.getRequestHeaders().get(PCConstants.HTTP_HEADER_AUTH_PCSESSID);
		if (pcSessionId == null)
			return null;
		try {
			AuthUserDetailsReq dReq = new AuthUserDetailsReq(pcSessionId, attr);
			AuthUserDetailsResp dResp = (AuthUserDetailsResp) PocketCampusServer.invokeOnPlugin("authentication", "getUserFieldsFromSession", dReq);
			return dResp.fieldValues;
		} catch (Exception e) {
		}
		return null;
	}

	private static String callOnAuthPlugin(String func) {
		String pcSessionId = PocketCampusServer.getRequestHeaders().get(PCConstants.HTTP_HEADER_AUTH_PCSESSID);
		if (pcSessionId == null)
			return null;
		try {
			return (String) PocketCampusServer.invokeOnPlugin("authentication", func, pcSessionId);
		} catch (Exception e) {
		}
		return null;
	}

	public static class AuthUserDetailsReq {
		public final String sessionId;
		public final List<String> requestedFields;

		public AuthUserDetailsReq(String sessionId, List<String> requestedFields) {
			this.sessionId = sessionId;
			this.requestedFields = requestedFields;
		}
	}

	public static class AuthUserDetailsResp {
		public final List<String> fieldValues;

		public AuthUserDetailsResp(List<String> fieldValues) {
			this.fieldValues = fieldValues;
		}
	}
	
	/**
	 * INTER-PLUGIN INTERFACE END
	 */
	
	private String firstValue(String crap) {
		crap = crap.split("[,]")[0];
		return crap;
	}

	private String getFieldFromSession(String sess, String field) {
		List<String> list = _manager.getFields(sess, Arrays.asList(field));
		return (list == null ? null : firstValue(list.get(0)));
	}

}
