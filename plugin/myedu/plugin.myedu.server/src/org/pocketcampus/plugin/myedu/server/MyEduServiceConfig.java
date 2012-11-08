package org.pocketcampus.plugin.myedu.server;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyEduServiceConfig {
	
	public final static String SERVICE_ROOT_URL = "https://myedu-test.epfl.ch/courses";
	
	public final static String API_PATH = "/api";
	
	public final static String VERSION_PATH = "/v1";
	
	/* Authentication */
	
	public final static String CREATE_EPFL_SESSION_PATH = "/create_epfl";
	
	public final static String EPFL_LOGIN_PATH = "/epfl_login";
	
	/* List of course */
	
	public final static String SUBSCRIBED_COURSES_LIST_PATH = "/";
	
	/* Course details */
	
	public final static String COURSE_DETAILS_PATH_WITH_FORMAT = "/%s"; 
	//<course_code>
	
	/* Section details */
	
	public final static String SECTION_DETAILS_PATH_WITH_FORMAT = "/%s/%d"; 
	//<course_code>, <section_id>
	
	/* Module details */
	
	public final static String MODULE_DETAILS_PATH_WITH_FORMAT = "/%s/%d/%d"; 
	//<course_code>, <section_id>, <module_id>
	
	/* Material file download */
	
	public final static String MATERIAL_FILE_DOWNLOAD_PATH_WITH_FORMAT = "/%s/%d/%d/load_material?material_id=%d";
	//<course_code>, <section_id>, <module_id>, <material_id>
	/* Submit feedback */
	
	public final static String SUBMIT_MODULE_FEEDBACK_PATH_WITH_FORMAT = "/%s/%d/%d/submit_vote?feedback=%s&rating=%s";
	//<course_code>, <section_id>, <module_id>, <feedback_text>, <star_rating>
	
	/* Date format */
	
	public final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"; /*2012-10-22T10:30:28Z*/
	
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
		public String code;
		public String created_at;
		public String description;
		public int id;
		public String title;
		public String updated_at;
	}
	
	public class CourseDetailsJson {
		public CourseJson course;
		public List<SectionJson> sections;
	}
	
	public class SectionJson {
		public int cours_id;
		public String created_at;
		public String description;
		public int id;
		public String title;
		public int sequence;
		public String updated_at;
	}
	
	public class SectionDetailsJson {
		public SectionJson section;
		public List<ModuleJson> modules;
	}
	
	public class ModuleJson {
		public String created_at;
		public int id;
		public String title;
		public Boolean is_visible;
		public int section_id;
		public int sequence;
		public String text_content;
		public String updated_at;
		public String video_source;
		public String video_url;
	}
	
	public class ModuleDetailsJson {
		public ModuleJson module;
		public List<MaterialJson> materials;
		public ModuleRecordJson module_record;
	}
	
	public class MaterialJson {
		public String created_at;
		public int id;
		public int module_id;
		public String name;
		public String updated_at;
		public String url;
	}
	
	public class ModuleRecordJson {
		public String created_at;
		public String feedback;
		public String feedback_time;
		public int id;
		public boolean is_completed;
		public int module_id;
		public int rating;
		public String updated_at;
		public int user_id;
	}
	
	
	/* Help methods */
	
	public static String getFullUrlForRootAccess(String path) {
		return SERVICE_ROOT_URL+path; 
	}
	
	public static String getFullUrlForAPIAccess(String path) {
		return SERVICE_ROOT_URL+API_PATH+VERSION_PATH+path;
	}
	
	public static Date getDateForString(String stringDate) throws ParseException {
		return new SimpleDateFormat(DATE_FORMAT).parse(stringDate);
	}
}
