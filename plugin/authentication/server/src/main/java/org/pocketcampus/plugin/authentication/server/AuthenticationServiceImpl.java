package org.pocketcampus.plugin.authentication.server;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.thrift.TException;
import org.pocketcampus.platform.server.RawPlugin;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.platform.shared.PCConstants;
import org.pocketcampus.platform.shared.utils.StringUtils;
import org.pocketcampus.plugin.authentication.shared.AuthSessionRequest;
import org.pocketcampus.plugin.authentication.shared.AuthSessionResponse;
import org.pocketcampus.plugin.authentication.shared.AuthStatusCode;
import org.pocketcampus.plugin.authentication.shared.AuthTokenResponse;
import org.pocketcampus.plugin.authentication.shared.AuthenticationService;
import org.pocketcampus.plugin.authentication.shared.LogoutRequest;
import org.pocketcampus.plugin.authentication.shared.LogoutResponse;
import org.pocketcampus.plugin.authentication.shared.UserAttributesRequest;
import org.pocketcampus.plugin.authentication.shared.UserAttributesResponse;
import org.pocketcampus.plugin.authentication.shared.AuthenticationConstants;

import ch.epfl.tequila.client.model.ClientConfig;
import ch.epfl.tequila.client.model.TequilaPrincipal;
import ch.epfl.tequila.client.service.TequilaService;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
public class AuthenticationServiceImpl implements AuthenticationService.Iface, RawPlugin {

	private final SessionManager _manager;

	public AuthenticationServiceImpl() {
		System.out.println("Starting Authentication plugin server ...");
		_manager = new SessionManagerOAuth2();
	}


	@Override
	public HttpServlet getServlet() {
		return new HttpServlet() {


			class ConfigRequest {
				long timestamp = System.currentTimeMillis();
				String sess;
				String email;
				String gaspar;
				String sciper;
				String lang; // can be null
				String type; // email or vpn
				String getfile;
			}

			Map<String, ConfigRequest> map = new ConcurrentHashMap<String, ConfigRequest>();

			private void cleanup() {
				Iterator<Map.Entry<String, ConfigRequest>> i = map.entrySet().iterator();
				while(i.hasNext()) {
					Map.Entry<String, ConfigRequest> e = i.next();
					if(System.currentTimeMillis() - e.getValue().timestamp > 60000) {
						i.remove();
					}
				}
			}

			@Override
			protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				cleanup();

				ConfigRequest cr = new ConfigRequest();
				cr.sess = req.getHeader(PCConstants.HTTP_HEADER_AUTH_PCSESSID);
				if(cr.sess != null) {
					cr.email = getFieldFromSession(cr.sess, "`email`");
					cr.gaspar = getFieldFromSession(cr.sess, "`gaspar`");
					cr.sciper = getFieldFromSession(cr.sess, "`sciper`");
				}
				cr.lang = req.getHeader(PCConstants.HTTP_HEADER_USER_LANG_CODE);
				cr.type = req.getParameter("config");
				cr.getfile = req.getParameter("getfile");
				if(cr.getfile != null) {
					ConfigRequest mapped = map.get(cr.getfile);
					if(mapped == null) {
						resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
						return;
					}
					cr = mapped;
				}


				if (cr.sess == null || cr.gaspar == null || cr.email == null || cr.sciper == null) {
					resp.setStatus(HttpURLConnection.HTTP_PROXY_AUTH);
					return;
				}


				String pemFile = PocketCampusServer.CONFIG.getString("IOS_PROV_PROFILE_SIGNING_PEM_FILE");

				Map<String, String> xml = IosProvisionningProfiles.XML_MAP.get(cr.type);

				if(xml == null) {
					resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
					return;

				}



				String nameForUuid = "ios epfl config " + cr.type + " " + cr.sciper;

				UUID configurationPayloadUuid = UUID.nameUUIDFromBytes((nameForUuid + " configuration payload uuid").getBytes());
				UUID accountPayloadUuid = UUID.nameUUIDFromBytes((nameForUuid + " account payload uuid").getBytes());

				String body = xml.get(cr.lang);
				if(body == null) {
					body = xml.get("en");
				}
				body = body.replace("USER_EMAIL", cr.email)
						.replace("USER_GASPAR", cr.gaspar)
						.replace("USER_SCIPER", cr.sciper)
						.replace("CONFIGURATION_PAYLOAD_UUID", configurationPayloadUuid.toString())
						.replace("ACCOUNT_PAYLOAD_UUID", accountPayloadUuid.toString());

				if(cr.getfile == null) {
					cr.getfile = UUID.randomUUID().toString();
					map.put(cr.getfile, cr);
					JsonObject reply = new JsonObject();
					reply.addProperty("getfile", cr.getfile);
					resp.setContentType("application/json");
					resp.setCharacterEncoding("UTF-8");
					resp.getOutputStream().write(new Gson().toJson(reply).getBytes("UTF-8"));

				} else {
					map.remove(cr.getfile);

					resp.setContentType("application/x-apple-aspen-config");
					resp.setCharacterEncoding("UTF-8");
					resp.setHeader("Content-Disposition", "attachment; filename=\"pocketcampus.mobileconfig\"");
					IosProvisionningProfiles.sign(pemFile, body, resp.getOutputStream());
				}


			}


		};
	}

	@Override
	public AuthTokenResponse getAuthTequilaToken() throws TException {
		System.out.println("getAuthTequilaToken");
		String token = authGetTequilaToken("authentication");
		if(token == null)
			return new AuthTokenResponse(AuthStatusCode.NETWORK_ERROR);
		return new AuthTokenResponse(AuthStatusCode.OK).setTequilaToken(token);
	}

	@Override
	public AuthSessionResponse getOAuth2TokensFromCode(AuthSessionRequest req) throws TException {
		System.out.println("getOAuth2TokensFromCode");
		try {
			JsonObject map = new JsonObject();
			for (String scope : AuthenticationConstants.OAUTH2_SCOPES) {
				HttpURLConnection conn = (HttpURLConnection) new URL(SessionManagerOAuth2.OAUTH2_TOKEN_URL + "&scope=" + scope + "&code=" + req.getTequilaToken()).openConnection();
				JsonObject obj = new JsonParser().parse(StringUtils.fromStream(conn.getInputStream(), "UTF-8")).getAsJsonObject();
//				if(obj.get("error") != null) {
//					return new AuthSessionResponse(AuthStatusCode.INVALID_SESSION);
//				}
//				map.addProperty(obj.get("scope").getAsString(), obj.get("access_token").getAsString());
				if(obj.get("error") == null) {
					map.addProperty(obj.get("scope").getAsString(), obj.get("access_token").getAsString());
				}
			}
			if(map.entrySet().size() > 0) {
				return new AuthSessionResponse(AuthStatusCode.OK).setSessionId(new Gson().toJson(map));
			} else {
				return new AuthSessionResponse(AuthStatusCode.INVALID_SESSION);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new AuthSessionResponse(AuthStatusCode.NETWORK_ERROR);
		}
	}
	
	@Override
	public AuthSessionResponse getAuthSession(AuthSessionRequest req) throws TException {
		System.out.println("getAuthSession");
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
		System.out.println("destroyAllUserSessions");
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
		System.out.println("getUserAttributes");
		List<String> fields = new LinkedList<String>();
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
	
	public static String getAccessTokenForScope(String scope) {
		String pcSessionId = PocketCampusServer.getRequestHeaders().get(PCConstants.HTTP_HEADER_AUTH_PCSESSID);
		if (pcSessionId == null) return null;
		Map<String, String> map = SessionManagerOAuth2.parseOAuth2Session(pcSessionId);
		if(map == null) return null;
		return map.get(scope);
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
		public String sessionId;
		public List<String> requestedFields;

		public AuthUserDetailsReq(String sessionId, List<String> requestedFields) {
			this.sessionId = sessionId;
			this.requestedFields = requestedFields;
		}
	}

	public static class AuthUserDetailsResp {
		public List<String> fieldValues;

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
		List<String> list = _manager.getFields(sess, Arrays.asList(new String[]{field}));
		return (list == null ? null : firstValue(list.get(0)));
	}

}
