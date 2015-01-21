package org.pocketcampus.plugin.authentication.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.pocketcampus.platform.shared.utils.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class SessionManagerOAuth2 extends SessionManagerImpl {
	
	private static final String OAUTH2_USERINFO_URL = "https://dev-tequila.epfl.ch/cgi-bin/OAuth2IdP/userinfo?access_token=";
	
	@Override
	public List<String> getFields(String sessionId, List<String> fields) {
		Map<String, String> sess = parseOAuth2Session(sessionId);
		if(sess == null || sess.get("Tequila.profile") == null)
			return super.getFields(sessionId, fields);
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(OAUTH2_USERINFO_URL + URLEncoder.encode(sess.get("Tequila.profile"), "UTF-8")).openConnection();
			JsonObject obj = new JsonParser().parse(StringUtils.fromStream(conn.getInputStream(), "UTF-8")).getAsJsonObject();
			if(obj.get("error") != null) {
				return null;
			}
			Map<String, String> person = parseOAuth2Object(obj);
			List<String> res = new LinkedList<String>();
			for (String f : fields) {
				res.add(person.get(f.replaceAll("[`]", "").trim()));
			}
			return res;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Map<String, String> parseOAuth2Object(JsonObject obj) {
		Map<String, String> map = new HashMap<String, String>();
		for(Entry<String, JsonElement> e : obj.entrySet()) {
			String key = translateOAuth2FieldName(e.getKey());
			if(key != null) {
				map.put(key, e.getValue().getAsString());
			}
		}
		return map;
	}
	
	private String translateOAuth2FieldName(String name) {
		if("Sciper".equals(name))
			return "sciper";
		if("Username".equals(name))
			return "gaspar";
		if("Firstname".equals(name))
			return "firstname";
		if("Name".equals(name))
			return "lastname";
		if("Email".equals(name))
			return "email";
		return null;
	}

	public static Map<String, String> parseOAuth2Session(String sess) {
		Map<String, String> ret = new HashMap<String, String>();
		JsonElement json = new JsonParser().parse(sess);
		if(!json.isJsonObject()) {
			return null;
		}
		for(Entry<String, JsonElement> e : json.getAsJsonObject().entrySet()) {
			ret.put(e.getKey(), e.getValue().getAsString());
		}
		return ret;
	}
	
}
