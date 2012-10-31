package org.pocketcampus.plugin.myedu.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.thrift.TException;
import org.pocketcampus.platform.sdk.shared.utils.Cookie;
import org.pocketcampus.plugin.myedu.server.MyEduServiceConfig.CourseDetailsJson;
import org.pocketcampus.plugin.myedu.server.MyEduServiceConfig.CourseJson;
import org.pocketcampus.plugin.myedu.server.MyEduServiceConfig.SectionJson;
import org.pocketcampus.plugin.myedu.server.MyEduServiceConfig.SessionCreateEPFL;
import org.pocketcampus.plugin.myedu.server.MyEduServiceConfig.SessionEPFLLogin;
import org.pocketcampus.plugin.myedu.shared.MyEduCourse;
import org.pocketcampus.plugin.myedu.shared.MyEduCourseDetailsReply;
import org.pocketcampus.plugin.myedu.shared.MyEduCourseDetailsRequest;
import org.pocketcampus.plugin.myedu.shared.MyEduModuleDetailsReply;
import org.pocketcampus.plugin.myedu.shared.MyEduModuleDetailsRequest;
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

				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); /*2012-10-22T10:30:28Z*/
				
				Date creationDate;
				Date updateDate;
				try {
					creationDate =  dateFormat.parse(course.created_at); 
				} catch (ParseException parseException1) {
					throw new TException("Error while reading JSON of creation date"); 
				}
				
				
				try {
					updateDate =  dateFormat.parse(course.updated_at);
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
				coursesList.add(myEduCourse);
			}
			
		} catch (JsonSyntaxException jsonSyntaxException) {
			throw new TException("Error while parsing JSON");
		} catch (JsonParseException jsonSyntaxException) {
			throw new TException("Error while parsing JSON");
		}
		
		return new MyEduSubscribedCoursesListReply(200).setISubscribedCourses(coursesList);
	}
	
	
	@Override
	public MyEduCourseDetailsReply getCourseDetails(MyEduRequest iMyEduRequest,
			MyEduCourseDetailsRequest iMyEduCourseDetailsRequest)
			throws TException {
		System.out.println("getCourseDetails");
		
		String json = null;
		
		try {
			String path = String.format(MyEduServiceConfig.COURSE_DETAILS_PATH_WITH_FORMAT, iMyEduCourseDetailsRequest.iCourseCode);
			HttpReply reply = getReplyForMyEduRequest(path, iMyEduRequest);
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

				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); /*2012-10-22T10:30:28Z*/
				
				Date creationDate;
				Date updateDate;
				try {
					creationDate =  dateFormat.parse(section.created_at); 
				} catch (ParseException parseException1) {
					throw new TException("Error while reading JSON of creation date"); 
				}

				try {
					updateDate =  dateFormat.parse(section.updated_at);
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
				sectionsList.add(myEduSection);
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
	public MyEduSectionDetailsReply getSectionDetails(
			MyEduRequest iMyEduRequest,
			MyEduSectionDetailsRequest iMyEduSectionDetailsRequest)
			throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MyEduModuleDetailsReply getModuleDetails(MyEduRequest iMyEduRequest,
			MyEduModuleDetailsRequest iMyEduModuleDetailsRequest)
			throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MyEduSubmitFeedbackReply submitFeedback(MyEduRequest iMyEduRequest,
			MyEduSubmitFeedbackRequest iMyEduSubmitFeedbackRequest)
			throws TException {
		// TODO Auto-generated method stub
		return null;
	}

}
