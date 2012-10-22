package org.pocketcampus.plugin.myedu.server;

public class MyEduServiceConfig {
	
	public final static String SERVICE_ROOT_URL = " https://myedu-staging.herokuapp.com";
	
	/* Authentication */
	
	public final static String CREATE_EPFL_SESSION_PATH = "/sessions/create_epfl";
	
	public final static String EPFL_LOGIN_PATH = "/sessions/epfl_login";
	
	/* Courses */
	
	public final static String SUBSCRIBED_COURSES_LIST_PATH = "/api.json";
	
	
	/* JSON definitions */
	
	public class CourseJson {
		public int id;
		public String code;
		public String title;
		public String description;
		public String created_at;
		public String updated_at;
	}
	
	
	/* Help methods */
	
	public static String getFullUrl(String path) {
		return SERVICE_ROOT_URL+path;
	}
}
