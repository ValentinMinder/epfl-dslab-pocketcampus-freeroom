package org.pocketcampus.plugin.library;

public class Nebis {
	static {
		session_ = new NebisSession();
	}
	
	public static final String BASE_URL = "http://opac.nebis.ch:80/F/";
	
	public static final String RENEW_SESSION_URL = "?local_base=nebis&func=option-update-lng&p_con_lng=FRE&file_name=find-b";
	
	private static final NebisSession session_;
	
	public static NebisSession getSession() {
		return session_;
	}
}
