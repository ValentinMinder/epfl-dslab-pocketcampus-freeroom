package org.pocketcampus.plugin.library;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class RequestHandler {
	private NebisSession session_;

	public RequestHandler(NebisSession session) {
		session_ = session;
	}

	public Document loadNebisPage(String params) {
		String url = Nebis.BASE_URL + session_.getSessionId() + params;
		return loadUrl(url);
	}

	public static Document loadUrl(String url) {
		System.out.println(url);
		Document page = null;

		try {
			page = Jsoup.connect(url).timeout(15 * 1000).get();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return page;
	}
}
