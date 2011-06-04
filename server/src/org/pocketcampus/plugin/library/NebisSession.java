package org.pocketcampus.plugin.library;

import java.io.IOException;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pocketcampus.shared.utils.StringUtils;

public class NebisSession {
	/** Session duration in ms. */
	private final long sessionDuration_ = 3 * 60 * 1000;
	
	/** Date of emission of this session. */
	private Date emissionDate_;
	
	/** Is this session already useable? */
	private boolean initialized_ = false;

	/** Session unique id, generated on Nebis server. */
	private String sessionId_;
	
	private boolean validate() {
		if(!initialized_) {
			return renew();
		}
		
		if(isExpired()) {
			return renew();
		}
		
		return true;
	}
	
	private boolean isExpired() {
		return new Date().getTime() - emissionDate_.getTime() > sessionDuration_;
	}

	private boolean renew() {
		String url = Nebis.BASE_URL + Nebis.RENEW_SESSION_URL;
		
		Document renewPage = RequestHandler.loadUrl(url);
		
		Element cookieScript = renewPage.getElementsByTag("script").get(0);
		sessionId_ = StringUtils.stringBetween(cookieScript.toString(), "ALEPH_SESSION_ID = ", "; path");
		
		if(sessionId_.length() != 50) {
			return false;
		}
		
		emissionDate_ = new Date();
		initialized_ = true;
		
		return true;
	}
	
	public String getSessionId() {
		validate();
		return sessionId_;
	}
}

















