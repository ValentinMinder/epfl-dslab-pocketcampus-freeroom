package org.pocketcampus.plugin.myedu.server;

public class MyEduServiceConfig {
	
	public final static String SERVICE_ROOT_URL = "https://myedu-staging.herokuapp.com";
	
	public final static String API_PATH = "/api";
	
	public final static String VERSION_PATH = "/v1";
	
	/* Authentication */
	
	public final static String CREATE_EPFL_SESSION_PATH = "/create_epfl";
	
	public final static String EPFL_LOGIN_PATH = "/epfl_login";
	
	/* List of course */
	
	public final static String SUBSCRIBED_COURSES_LIST_PATH = "/";
	
	/* */
	
	/* JSON definitions */
	
	public class SessionCreateEPFL {
		public String status;
		public String message;
		public String tequila_key;
	}
	
	public class SessionEPFLLogin {
		public String status;
		public String message;
	}
	
	public class CourseJson {
		public int id;
		public String code;
		public String title;
		public String description;
		public String created_at;
		public String updated_at;
	}
	
	
	/* Help methods */
	
	public static String getFullUrlForRootAccess(String path) {
		return SERVICE_ROOT_URL+path; 
	}
	
	public static String getFullUrlForAPIAccess(String path) {
		return SERVICE_ROOT_URL+API_PATH+VERSION_PATH+path;
	}
}
