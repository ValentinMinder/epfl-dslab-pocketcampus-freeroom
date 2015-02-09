package org.pocketcampus.platform.server;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.pocketcampus.platform.shared.utils.PostDataBuilder;
import org.pocketcampus.platform.shared.utils.StringUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class SmsSender {

	public static boolean sendSms(String body) {
		try {
			PostDataBuilder reqParams = new PostDataBuilder();
			reqParams.addParam("From", "+12016465002");
			reqParams.addParam("To", "+41765327045");
			reqParams.addParam("Body", body);
			HttpURLConnection conn = (HttpURLConnection) new URL("https://api.twilio.com/2010-04-01/Accounts/AC30707d0b5757aac3faff6a97b908a75a/SMS/Messages.json").openConnection();
			String encoded = new String(Base64.encodeBase64("AC30707d0b5757aac3faff6a97b908a75a:42957b835dcd223c7aae18d043bd3685".getBytes()));
			conn.setRequestProperty("Authorization", "Basic " + encoded);
			conn.setDoOutput(true);
			conn.getOutputStream().write(reqParams.toString().getBytes(Charset.forName("UTF-8")));
			String result = StringUtils.fromStream(conn.getInputStream(), "UTF-8");
			boolean ok = "queued".equals(new JSONObject(result).getString("status"));
			if(!ok) { // shit
				System.out.println(result);
			}
			return ok;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

}
