package org.pocketcampus.platform.shared.utils;

import java.util.HashMap;
import java.util.Map.Entry;

/** Please don't use this unless you absolutely have to. */
public class Cookie {

	/**
	 * setCookie: adds or updates cookies in the store as per the received Set-Cookie header
	 * 
	 * @param l
	 *            : should be the output of HttpURLConnection.getHeaderFields().get("Set-Cookie")
	 */
	public void addFromHeader(String s) {
		if (s.length() == 0)
			return;
		if (s.contains("deleted"))
			return;
		addOrUpdateCookie(s.split(";", 2)[0]);
	}

	/********
	 * cookie: generates the Cookie string that must be sent to the server
	 * 
	 * @return: the Cookie string
	 */
	public String cookie() {
		StringBuilder b = new StringBuilder();
		for (Entry<String, String> e : cookie.entrySet()) {
			if (b.length() > 0)
				b.append(";");
			b.append(e.getKey() + "=" + e.getValue());
		}
		// System.out.println("cookie: " + b.toString());
		return b.toString();
	}

	/********
	 * importFromString: imports the Cookie String from a string
	 * 
	 * @param s
	 *            : the Cookie string that must be sent to the server
	 */
	public void importFromString(String s) {
		if (s.length() == 0)
			return;
		for (String c : s.split(";")) {
			if (c.length() == 0)
				continue;
			addOrUpdateCookie(c);
		}
	}

	private void addOrUpdateCookie(String c) {
		if (!c.contains("="))
			return;
		String[] v = c.split("=", 2);
		if (v[0].length() == 0)
			return;
		if (cookie.containsKey(v[0]))
			cookie.remove(v[0]);
		cookie.put(v[0], v[1]);
	}

	public HashMap<String, String> cookie = new HashMap<String, String>();

}
