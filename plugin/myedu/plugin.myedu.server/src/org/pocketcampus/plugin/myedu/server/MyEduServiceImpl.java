package org.pocketcampus.plugin.myedu.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.thrift.TException;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.pocketcampus.platform.sdk.shared.utils.Cookie;
import org.pocketcampus.plugin.myedu.server.MyEduServiceConfig.CourseJson;
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
		
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(MyEduServiceConfig.getFullUrl(MyEduServiceConfig.CREATE_EPFL_SESSION_PATH)).openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.getInputStream();
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
		}
	}

	@Override
	public MyEduSession getMyEduSession(MyEduTequilaToken iTequilaToken)
			throws TException {
		System.out.println("getMyEduSession");
		
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(MyEduServiceConfig.getFullUrl(MyEduServiceConfig.EPFL_LOGIN_PATH)).openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.setRequestProperty("Cookie", iTequilaToken.getILoginCookie());
			conn.getInputStream();
			if(conn.getResponseCode() == 302) { //OK, means has redirected
				Cookie cookie = new Cookie();
				cookie.setCookie(conn.getHeaderFields().get("Set-Cookie"));
				return new MyEduSession(cookie.cookie());
			} else {
				throw new TException("Authentication failed");
			}
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
			System.out.println("fetching...");
			json = getPage(MyEduServiceConfig.SUBSCRIBED_COURSES_LIST_PATH, iMyEduRequest);
			System.out.println(json);
		} catch (IOException connExc) {
			throw new TException("Failed to connect to MyEdu server");
		} catch (HttpQueryException queryExc) {
			return new SubscribedCoursesListReply(queryExc.getStatusCode());
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
	
	private String getPage(String myEduPath, MyEduRequest iMyEduRequest) throws IOException, HttpQueryException {
		return getPage(MyEduServiceConfig.getFullUrl(myEduPath), iMyEduRequest.iMyEduSession.iMyEduCookie);
	}
	
	private String getPage(String url, String cookie) throws IOException, HttpQueryException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestProperty("User-Agent", "Mozilla/5.0");
		conn.setInstanceFollowRedirects(false);
		conn.setRequestProperty("Cookie", cookie);
		int statusCode = conn.getResponseCode();
		if(statusCode != 200) {
			throw new HttpQueryException(statusCode);
		}
		return IOUtils.toString(conn.getInputStream(), "UTF-8");
	}
	
	
	/**
	 * HELPER CLASSES
	 */
	
	private class HttpQueryException extends Exception {

		private static final long serialVersionUID = 1L;
		private int statusCode;
		
		public HttpQueryException(int statusCode) {
			this.statusCode = statusCode;
		}
		
		public int getStatusCode() {
			return statusCode;
		}
	}
}
