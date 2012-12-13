package org.pocketcampus.plugin.myedu.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.NoSuchObjectException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.thrift.TException;
import org.pocketcampus.platform.launcher.server.PocketCampusServer;
import org.pocketcampus.platform.sdk.shared.pushnotif.PushNotifRequest;
import org.pocketcampus.platform.sdk.shared.utils.Cookie;
import org.pocketcampus.plugin.myedu.server.MyEduServiceConfig.CourseDetailsJson;
import org.pocketcampus.plugin.myedu.server.MyEduServiceConfig.CourseJson;
import org.pocketcampus.plugin.myedu.server.MyEduServiceConfig.MaterialJson;
import org.pocketcampus.plugin.myedu.server.MyEduServiceConfig.ModuleDetailsJson;
import org.pocketcampus.plugin.myedu.server.MyEduServiceConfig.ModuleJson;
import org.pocketcampus.plugin.myedu.server.MyEduServiceConfig.ModuleRecordJson;
import org.pocketcampus.plugin.myedu.server.MyEduServiceConfig.SectionDetailsJson;
import org.pocketcampus.plugin.myedu.server.MyEduServiceConfig.SectionJson;
import org.pocketcampus.plugin.myedu.server.MyEduServiceConfig.SessionCreateEPFL;
import org.pocketcampus.plugin.myedu.server.MyEduServiceConfig.SessionEPFLLogin;
import org.pocketcampus.plugin.myedu.shared.MyEduCourse;
import org.pocketcampus.plugin.myedu.shared.MyEduCourseDetailsReply;
import org.pocketcampus.plugin.myedu.shared.MyEduCourseDetailsRequest;
import org.pocketcampus.plugin.myedu.shared.MyEduMaterial;
import org.pocketcampus.plugin.myedu.shared.MyEduMaterialType;
import org.pocketcampus.plugin.myedu.shared.MyEduModule;
import org.pocketcampus.plugin.myedu.shared.MyEduModuleDetailsReply;
import org.pocketcampus.plugin.myedu.shared.MyEduModuleDetailsRequest;
import org.pocketcampus.plugin.myedu.shared.MyEduModuleRecord;
import org.pocketcampus.plugin.myedu.shared.MyEduRequest;
import org.pocketcampus.plugin.myedu.shared.MyEduSection;
import org.pocketcampus.plugin.myedu.shared.MyEduSectionDetailsReply;
import org.pocketcampus.plugin.myedu.shared.MyEduSectionDetailsRequest;
import org.pocketcampus.plugin.myedu.shared.MyEduService;
import org.pocketcampus.plugin.myedu.shared.MyEduSession;
import org.pocketcampus.plugin.myedu.shared.MyEduSubmitFeedbackReply;
import org.pocketcampus.plugin.myedu.shared.MyEduSubmitFeedbackRequest;
import org.pocketcampus.plugin.myedu.shared.MyEduSubscribedCoursesListReply;
import org.pocketcampus.plugin.myedu.shared.MyEduTequilaToken;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * MyEduServiceImpl
 * 
 * The implementation of the server side of the MyEdu Plugin.
 * 
 * It fetches the user's MyEdu data from the MyEdu servers.
 * 
 * @author Loic <loic.gardiol@epfl.ch>
 *
 */
public class MyEduServiceImpl implements MyEduService.Iface {
	
	public MyEduServiceImpl() {
		System.out.println("Starting MyEdu plugin server ...");
	}

	@Override
	public MyEduTequilaToken getTequilaTokenForMyEdu() throws TException {
		System.out.println("getTequilaTokenForMyEdu");
		
		try {
			
			Gson gson = new Gson();
			
			HttpReply reply = getReply(MyEduServiceConfig.getFullUrlForAPIAccess(MyEduServiceConfig.CREATE_EPFL_SESSION_PATH), null);
			
			if (reply.getStatusCode() != 200) {
				throw new TException("Upstream server returned error "+reply.getStatusCode());
			}
			
			TypeToken<SessionCreateEPFL> replyType = new TypeToken<SessionCreateEPFL>() {};
			SessionCreateEPFL replyObj = gson.fromJson(reply.getReplyString(), replyType.getType());
			
			if (!replyObj.status.equals("OK")) { //ERROR
				throw new TException("Upstream server returned error. Message: "+replyObj.message);
			}
			
			MyEduTequilaToken token = new MyEduTequilaToken(replyObj.tequila_key);
			token.setILoginCookie(reply.cookie.cookie());
			
			return token;
			
			
		} catch (IOException ioException) {
			ioException.printStackTrace();
			throw new TException("Failed to getTequilaTokenForMyEdu from upstream server");
		}
		
	}

	@Override
	public MyEduSession getMyEduSession(MyEduTequilaToken iTequilaToken)
			throws TException {
		System.out.println("getMyEduSession");
		
		try {
	
			Gson gson = new Gson();
			
			HttpReply reply = getReply(MyEduServiceConfig.getFullUrlForAPIAccess(MyEduServiceConfig.EPFL_LOGIN_PATH), iTequilaToken.getILoginCookie());
			
			if (reply.getStatusCode() != 200) {
				throw new TException("Upstream server returned error "+reply.getStatusCode());
			}
			
			TypeToken<SessionEPFLLogin> replyType = new TypeToken<SessionEPFLLogin>() {};
			SessionEPFLLogin replyObj = gson.fromJson(reply.getReplyString(), replyType.getType());
			if (!replyObj.status.equals("OK")) { //ERROR
				throw new TException("Upstream server returned error. Message: "+replyObj.message);
			}
			
			MyEduSession session = new MyEduSession(reply.cookie.cookie());
			
			return session;
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to getMyEduSession from upstream server");
		}
		
	}
	
	@Override
	public MyEduSubscribedCoursesListReply getSubscribedCoursesList(
			MyEduRequest iMyEduRequest) throws TException {
		System.out.println("getSubscribedCoursesList");
		
		/*TEST*/
		/*
		try {
			PocketCampusServer.invokeOnPlugin("pushnotif", "pushMessage", new PushNotifRequest("myedu", Arrays.asList("gardiol"), "Hello, world!"));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		*/
		/*END TEST*/
		
		String json = null;
		
		try {
			HttpReply reply = getReplyForMyEduRequest(MyEduServiceConfig.SUBSCRIBED_COURSES_LIST_PATH, iMyEduRequest);
			if (reply.getStatusCode() != 200) {
				return new MyEduSubscribedCoursesListReply(reply.getStatusCode()); 
			}
			json = reply.getReplyString();
		} catch (IOException connExc) {
			throw new TException("Failed to connect to MyEdu server");
		} 
		
		Gson gson = new Gson();
		ArrayList<MyEduCourse> coursesList = new ArrayList<MyEduCourse>();
		
		try {
			TypeToken<ArrayList<CourseJson>> listType = new TypeToken<ArrayList<CourseJson>>() {};
			ArrayList<CourseJson> courses = gson.fromJson(json, listType.getType());
			
			for (CourseJson course : courses) {
				coursesList.add(getMyEduCourseForJson(course));
			}
			
		} catch (JsonSyntaxException jsonSyntaxException) {
			throw new TException("Error while parsing JSON");
		} catch (JsonParseException jsonSyntaxException) {
			throw new TException("Error while parsing JSON");
		}
		return new MyEduSubscribedCoursesListReply(200).setISubscribedCourses(coursesList);
	}
	
	
	@Override
	public MyEduCourseDetailsReply getCourseDetails(MyEduCourseDetailsRequest iMyEduCourseDetailsRequest)
			throws TException {
		System.out.println("getCourseDetails");
		
		String json = null;
		
		try {
			String path = String.format(MyEduServiceConfig.COURSE_DETAILS_PATH_WITH_FORMAT, iMyEduCourseDetailsRequest.iCourseCode);
			HttpReply reply = getReplyForMyEduRequest(path, iMyEduCourseDetailsRequest.iMyEduRequest);
			if (reply.getStatusCode() != 200) {
				return new MyEduCourseDetailsReply(reply.getStatusCode()); 
			}
			json = reply.getReplyString();
		} catch (IOException connExc) {
			throw new TException("Failed to connect to MyEdu server");
		} 
		
		Gson gson = new Gson();
		ArrayList<MyEduSection> sectionsList = new ArrayList<MyEduSection>();
		
		try {
			TypeToken<CourseDetailsJson> type = new TypeToken<CourseDetailsJson>() {};
			CourseDetailsJson courseDetails = gson.fromJson(json, type.getType());
			
			for (SectionJson section : courseDetails.sections) {
				sectionsList.add(getMyEduSectionForJson(section));
			}
			
		} catch (JsonSyntaxException jsonSyntaxException) {
			throw new TException("Error while parsing JSON");
		} catch (JsonParseException jsonSyntaxException) {
			throw new TException("Error while parsing JSON");
		}
		
		Collections.sort(sectionsList, new Comparator<MyEduSection>() {
			@Override
			public int compare(MyEduSection o1, MyEduSection o2) {
				return o1.getISequence()-o2.getISequence();
			}
		});
		
		return new MyEduCourseDetailsReply(200).setIMyEduSections(sectionsList);
	}
	
	
	@Override
	public MyEduSectionDetailsReply getSectionDetails(MyEduSectionDetailsRequest iMyEduSectionDetailsRequest)
			throws TException {
		System.out.println("getSectionDetails");
		
		String json = null;
		
		try {
			String path = String.format(MyEduServiceConfig.SECTION_DETAILS_PATH_WITH_FORMAT, iMyEduSectionDetailsRequest.iCourseCode, iMyEduSectionDetailsRequest.iSectionId);
			HttpReply reply = getReplyForMyEduRequest(path, iMyEduSectionDetailsRequest.iMyEduRequest);
			if (reply.getStatusCode() != 200) {
				return new MyEduSectionDetailsReply(reply.getStatusCode()); 
			}
			json = reply.getReplyString();
		} catch (IOException connExc) {
			throw new TException("Failed to connect to MyEdu server");
		} 
		
		Gson gson = new Gson();
		ArrayList<MyEduModule> modulesList = new ArrayList<MyEduModule>();
		
		try {
			TypeToken<SectionDetailsJson> type = new TypeToken<SectionDetailsJson>() {};
			SectionDetailsJson sectionDetails = gson.fromJson(json, type.getType());
			
			for (ModuleJson module : sectionDetails.modules) {
				modulesList.add(getMyEduModuleForJson(module));
			}
			
		} catch (JsonSyntaxException jsonSyntaxException) {
			throw new TException("Error while parsing JSON");
		} catch (JsonParseException jsonSyntaxException) {
			throw new TException("Error while parsing JSON");
		}
		
		Collections.sort(modulesList, new Comparator<MyEduModule>() {
			@Override
			public int compare(MyEduModule o1, MyEduModule o2) {
				return o1.getISequence()-o2.getISequence();
			}
		});
		
		return new MyEduSectionDetailsReply(200).setIMyEduModules(modulesList);
	}
	
	@Override
	public MyEduModuleDetailsReply getModuleDetails(MyEduModuleDetailsRequest iMyEduModuleDetailsRequest)
			throws TException {
		System.out.println("getModuleDetails");
		
		String json = null;
		
		try {
			String path = String.format(MyEduServiceConfig.MODULE_DETAILS_PATH_WITH_FORMAT, iMyEduModuleDetailsRequest.iCourseCode, iMyEduModuleDetailsRequest.iSectionId, iMyEduModuleDetailsRequest.iModuleId);
			HttpReply reply = getReplyForMyEduRequest(path, iMyEduModuleDetailsRequest.iMyEduRequest);
			if (reply.getStatusCode() != 200) {
				return new MyEduModuleDetailsReply(reply.getStatusCode()); 
			}
			json = reply.getReplyString();
		} catch (IOException connExc) {
			throw new TException("Failed to connect to MyEdu server");
		} 
		
		Gson gson = new Gson();
		ArrayList<MyEduMaterial> materialsList = new ArrayList<MyEduMaterial>();
		MyEduModuleRecord moduleRecord = new MyEduModuleRecord();
		
		try {
			TypeToken<ModuleDetailsJson> type = new TypeToken<ModuleDetailsJson>() {};
			ModuleDetailsJson moduleDetails = gson.fromJson(json, type.getType());
			
			for (MaterialJson material : moduleDetails.materials) {
				materialsList.add(getMyEduMaterialForJson(material, iMyEduModuleDetailsRequest));
			}
			
			moduleRecord = getMyEduRecordForJson(moduleDetails.module_record);
			
		} catch (JsonSyntaxException jsonSyntaxException) {
			throw new TException("Error while parsing JSON");
		} catch (JsonParseException jsonSyntaxException) {
			throw new TException("Error while parsing JSON");
		}
		
		
		MyEduModuleDetailsReply reply = new MyEduModuleDetailsReply(200);
		reply.setIMyEduMaterials(materialsList);
		reply.setIMyEduRecord(moduleRecord);
		return reply;
	}
	
	
	/**
	 * CONVERSION METHODS (JSON to MyEdu types)
	 */
	
	private MyEduCourse getMyEduCourseForJson(CourseJson course) throws TException {
		
		Date creationDate;
		Date updateDate;
		try {
			creationDate =  MyEduServiceConfig.getDateForString(course.created_at); 
		} catch (ParseException parseException1) {
			throw new TException("Error while reading JSON of creation date"); 
		}
		
		try {
			updateDate =  MyEduServiceConfig.getDateForString(course.updated_at);
		} catch (ParseException parseException1) {
			throw new TException("Error while reading JSON of update date"); 
		}
		
		MyEduCourse myEduCourse = new MyEduCourse();
		myEduCourse.setIId(course.id);
		myEduCourse.setICode(course.code);
		myEduCourse.setITitle(course.title);
		myEduCourse.setIDescription(course.description);
		myEduCourse.setICreationTimestamp(creationDate.getTime());
		myEduCourse.setILastUpdateTimestamp(updateDate.getTime());
		
		return myEduCourse;
	}
	
	private MyEduSection getMyEduSectionForJson(SectionJson section) throws TException {
		
		Date creationDate;
		Date updateDate;
		try {
			creationDate =  MyEduServiceConfig.getDateForString(section.created_at); 
		} catch (ParseException parseException1) {
			throw new TException("Error while reading JSON of creation date"); 
		}
		
		try {
			updateDate =  MyEduServiceConfig.getDateForString(section.updated_at);
		} catch (ParseException parseException1) {
			throw new TException("Error while reading JSON of update date"); 
		}
		
		MyEduSection myEduSection = new MyEduSection();
		myEduSection.setIId(section.id);
		myEduSection.setICourseId(section.cours_id);
		myEduSection.setITitle(section.title);
		myEduSection.setIDescription(section.description);
		myEduSection.setISequence(section.sequence);
		myEduSection.setICreationTimestamp(creationDate.getTime());
		myEduSection.setILastUpdateTimestamp(updateDate.getTime());
		
		return myEduSection;
	}
	
	private MyEduModule getMyEduModuleForJson(ModuleJson module) throws TException {
		
		Date creationDate;
		Date updateDate;
		try {
			creationDate =  MyEduServiceConfig.getDateForString(module.created_at); 
		} catch (ParseException parseException1) {
			throw new TException("Error while reading JSON of creation date"); 
		}
		
		try {
			updateDate =  MyEduServiceConfig.getDateForString(module.updated_at);
		} catch (ParseException parseException1) {
			throw new TException("Error while reading JSON of update date"); 
		}
		
		MyEduModule myEduModule = new MyEduModule();
		
		myEduModule.setIId(module.id);
		myEduModule.setISectionId(module.section_id);
		myEduModule.setITitle(module.title);
		myEduModule.setISequence(module.sequence);
		myEduModule.setICreationTimestamp(creationDate.getTime());
		myEduModule.setILastUpdateTimestamp(updateDate.getTime());
		myEduModule.setIVisible(module.is_visible);
		myEduModule.setITextContent(module.text_content);
		myEduModule.setIVideoSourceProvider(module.video_source);
		myEduModule.setIVideoID(module.video_url); //normal, video_url is actually id
		
		if (module.video_source.equals("vimeo")) {
			myEduModule.setIVideoDownloadURL("http://pocketcampus.epfl.ch/vimeo/vimeo.php?vid="+myEduModule.getIVideoID());
		} else {
			//download with other providers not suported
		}
		
		return myEduModule;
	}
	
	private MyEduMaterial getMyEduMaterialForJson(MaterialJson material, MyEduModuleDetailsRequest request) throws TException {
		
		Date creationDate;
		Date updateDate;
		try {
			creationDate =  MyEduServiceConfig.getDateForString(material.created_at); 
		} catch (ParseException parseException1) {
			throw new TException("Error while reading JSON of creation date"); 
		}
		
		try {
			updateDate =  MyEduServiceConfig.getDateForString(material.updated_at);
		} catch (ParseException parseException1) {
			throw new TException("Error while reading JSON of update date"); 
		}
		
		MyEduMaterial myEduMaterial = new MyEduMaterial();
		
		myEduMaterial.setIId(material.id);
		myEduMaterial.setIModuleId(material.module_id);
		myEduMaterial.setIName(material.name);
		myEduMaterial.setICreationTimestamp(creationDate.getTime());
		myEduMaterial.setILastUpdateTimestamp(updateDate.getTime());
		
		if (material.url == null || material.url.equals("")) {
			myEduMaterial.setIType(MyEduMaterialType.MATERIAL_TYPE_DOCUMENT);
			String path = String.format(MyEduServiceConfig.MATERIAL_FILE_DOWNLOAD_PATH_WITH_FORMAT, request.iCourseCode, request.iSectionId, request.iModuleId, material.id);
			myEduMaterial.setIURL(MyEduServiceConfig.getFullUrlForAPIAccess(path));
		} else {
			myEduMaterial.setIType(MyEduMaterialType.MATERIAL_TYPE_WEBSITE);
			myEduMaterial.setIURL(material.url);
		}
		
		return myEduMaterial;
	}
	
	private MyEduModuleRecord getMyEduRecordForJson(ModuleRecordJson record) throws TException {
		Date creationDate;
		Date updateDate;
		Date feedbackDate;
		try {
			creationDate =  MyEduServiceConfig.getDateForString(record.created_at); 
		} catch (ParseException parseException1) {
			throw new TException("Error while reading JSON of creation date"); 
		}
		
		try {
			updateDate =  MyEduServiceConfig.getDateForString(record.updated_at);
		} catch (ParseException parseException1) {
			throw new TException("Error while reading JSON of update date"); 
		}
		
		try {
			feedbackDate =  MyEduServiceConfig.getDateForString(record.feedback_time);
		} catch (ParseException parseException1) {
			throw new TException("Error while reading JSON of feedback date"); 
		}
		
		MyEduModuleRecord myEduModuleRecord = new MyEduModuleRecord();
		
		myEduModuleRecord.setIId(record.id);
		myEduModuleRecord.setIModuleId(record.module_id);
		myEduModuleRecord.setIFeedbackText(record.feedback);
		myEduModuleRecord.setIFeedbackTimestamp(feedbackDate.getTime());
		myEduModuleRecord.setIModuleCompleted(record.is_completed);
		myEduModuleRecord.setIRating(record.rating);
		myEduModuleRecord.setIUserId(record.user_id);
		myEduModuleRecord.setICreationTimestamp(creationDate.getTime());
		myEduModuleRecord.setILastUpdateTimestamp(updateDate.getTime());
		
		return myEduModuleRecord;
	}
	
	/**
	 * HELPER METHODS
	 */
	
	private HttpReply getReplyForMyEduRequest(String myEduPath, MyEduRequest iMyEduRequest) throws IOException {
		return getReply(MyEduServiceConfig.getFullUrlForAPIAccess(myEduPath), iMyEduRequest.iMyEduSession.iMyEduCookie);
	}
	
	private HttpReply getReply(String url, String cookie) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestProperty("User-Agent", "Mozilla/5.0");
		conn.setInstanceFollowRedirects(false);
		if (cookie != null) {
			conn.setRequestProperty("Cookie", cookie);
		}
		conn.getInputStream();
		Cookie cookieReceived = null;
		List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
		if (cookies != null) {
			cookieReceived = new Cookie();
			cookieReceived.setCookie(conn.getHeaderFields().get("Set-Cookie"));
		}
		HttpReply reply = new HttpReply(conn.getResponseCode(), IOUtils.toString(conn.getInputStream(), "UTF-8"), cookieReceived, conn.getHeaderField("Location")); 
		return reply;
	}
	
	
	/**
	 * HELPER CLASSES
	 */
	
	private class HttpReply {
		
		private int statusCode;
		private String replyString;
		private Cookie cookie;
		private String redirectionURL;
		
		public HttpReply(int statusCode, String replyString, Cookie cookie, String redirectionURL) {
			this.statusCode = statusCode;
			this.replyString = replyString;
			this.cookie = cookie;
			this.redirectionURL = redirectionURL;
		}

		public int getStatusCode() {
			return statusCode;
		}

		public String getReplyString() {
			return replyString;
		}

		public Cookie getCookie() {
			return cookie;
		}
		
		public String getRedirectionURL() {
			return redirectionURL;
		}
		
	}


	@Override
	public MyEduSubmitFeedbackReply submitFeedback(MyEduSubmitFeedbackRequest iMyEduSubmitFeedbackRequest)
			throws TException {
		// TODO Auto-generated method stub
		return null;
	}

}
