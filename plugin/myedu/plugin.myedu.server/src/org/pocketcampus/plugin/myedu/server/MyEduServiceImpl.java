package org.pocketcampus.plugin.myedu.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.thrift.TException;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.pocketcampus.platform.sdk.shared.utils.Cookie;
import org.pocketcampus.plugin.myedu.server.MyEduServiceConfig.CourseJson;
import org.pocketcampus.plugin.myedu.server.MyEduServiceConfig.SessionCreateEPFL;
import org.pocketcampus.plugin.myedu.server.MyEduServiceConfig.SessionEPFLLogin;
import org.pocketcampus.plugin.myedu.shared.MyEduCourse;
import org.pocketcampus.plugin.myedu.shared.MyEduRequest;
import org.pocketcampus.plugin.myedu.shared.MyEduService;
import org.pocketcampus.plugin.myedu.shared.MyEduSession;
import org.pocketcampus.plugin.myedu.shared.MyEduTequilaToken;
import org.pocketcampus.plugin.myedu.shared.SubscribedCoursesListReply;

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
		
		/*try {
			
			HttpURLConnection conn = (HttpURLConnection) new URL(MyEduServiceConfig.getFullUrlForAPIAccess(MyEduServiceConfig.CREATE_EPFL_SESSION_PATH)).openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.getInputStream();
			System.out.println(conn.getHeaderField("Location"));
			URL url = new URL(conn.getHeaderField("Location"));
			MultiMap<String> params = new MultiMap<String>();
			UrlEncoded.decodeTo(url.getQuery(), params, "UTF-8");
			MyEduTequilaToken teqToken = new MyEduTequilaToken(params.getString("requestkey"));
			Cookie cookie = new Cookie();
			cookie.setCookie(conn.getHeaderFields().get("Set-Cookie"));
			teqToken.setILoginCookie(cookie.cookie());
			return teqToken;
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to getTequilaToken from upstream server");
		}*/
		
		/*Gson gson = new Gson();
		String json = null;
		
		MyEduTequilaToken teqToken;
		try {
			System.out.println("fetching...");
			json = getPage(MyEduServiceConfig.CREATE_EPFL_SESSION_PATH, "");
		} catch (IOException connExc) {
			throw new TException("Failed to connect to MyEdu server");
		} catch (HttpQueryException queryExc) {
			throw new TException("Failed to connect to MyEdu server");
		}
		
		try {
			TypeToken<SessionCreateEPFL> replyType = new TypeToken<SessionCreateEPFL>() {};
			SessionCreateEPFL reply = gson.fromJson(json, replyType.getType());
			
			
			
			teqToken = new MyEduTequilaToken(params.getString("requestkey"));
			Cookie cookie = new Cookie();
			cookie.setCookie(conn.getHeaderFields().get("Set-Cookie"));
			teqToken.setILoginCookie(cookie.cookie());
			return teqToken;
			
			
		} catch (JsonSyntaxException jsonSyntaxException) {
			throw new TException("Error while parsing JSON");
		} catch (JsonParseException jsonSyntaxException) {
			throw new TException("Error while parsing JSON");
		}
		
		return new SubscribedCoursesListReply(200).setISubscribedCourses(coursesList);*/
		
		
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
			/*HttpURLConnection conn = (HttpURLConnection) new URL(MyEduServiceConfig.getFullUrlForAPIAccess(MyEduServiceConfig.EPFL_LOGIN_PATH)).openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.setRequestProperty("Cookie", iTequilaToken.getILoginCookie());
			conn.getInputStream();
			if(conn.getResponseCode() == 302) { //OK, means has redirected
				Cookie cookie = new Cookie();
				cookie.setCookie(conn.getHeaderFields().get("Set-Cookie"));
				return new MyEduSession(cookie.cookie());
			} else {
				throw new TException("Authentication failed");
			}*/
			
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
	public SubscribedCoursesListReply getSubscribedCoursesList(
			MyEduRequest iMyEduRequest) throws TException {
		System.out.println("getSubscribedCoursesList");
		
		Gson gson = new Gson();
		String json = null;
		ArrayList<MyEduCourse> coursesList = new ArrayList<MyEduCourse>();
		
		try {
			HttpReply reply = getReplyForMyEduRequest(MyEduServiceConfig.SUBSCRIBED_COURSES_LIST_PATH, iMyEduRequest);
			if (reply.getStatusCode() != 200) {
				return new SubscribedCoursesListReply(reply.getStatusCode()); 
			}
			json = reply.getReplyString();
		} catch (IOException connExc) {
			throw new TException("Failed to connect to MyEdu server");
		} 
		
		try {
			TypeToken<ArrayList<CourseJson>> listType = new TypeToken<ArrayList<CourseJson>>() {};
			ArrayList<CourseJson> courses = gson.fromJson(json, listType.getType());
			
			for (CourseJson course : courses) {
				MyEduCourse myEduCourse = new MyEduCourse(course.id, course.code, course.title, course.description);

				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); /*2012-10-22T10:30:28Z*/
				
				try {
					Date creationDate;
					creationDate =  dateFormat.parse(course.created_at);
					myEduCourse.setICreationTimestamp(creationDate.getTime()); 
				} catch (ParseException parseException1) {}
				
				
				try {
					Date updateDate;
					updateDate =  dateFormat.parse(course.updated_at);
					myEduCourse.setILastUpdateTimestamp(updateDate.getTime()); 
				} catch (ParseException parseException1) {}
				
				coursesList.add(myEduCourse);
			}
			
		} catch (JsonSyntaxException jsonSyntaxException) {
			throw new TException("Error while parsing JSON");
		} catch (JsonParseException jsonSyntaxException) {
			throw new TException("Error while parsing JSON");
		}
		
		return new SubscribedCoursesListReply(200).setISubscribedCourses(coursesList);
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

}
