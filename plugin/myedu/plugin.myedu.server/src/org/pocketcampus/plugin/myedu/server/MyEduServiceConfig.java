package org.pocketcampus.plugin.myedu.server;

public class MyEduServiceConfig {
	
	public final static String SERVICE_ROOT_URL = "https://myedu.epfl.ch/courses";
	
	public final static String CREATE_EPFL_SESSION_PATH = "/sessions/create_epfl";
	
	public final static String EPFL_LOGIN_PATH = "/sessions/epfl_login";
	
	
	public static String getFullUrl(String path) {
		return SERVICE_ROOT_URL+path;
	}
}
