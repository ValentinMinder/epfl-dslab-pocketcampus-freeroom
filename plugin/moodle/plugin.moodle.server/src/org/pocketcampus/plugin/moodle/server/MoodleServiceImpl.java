package org.pocketcampus.plugin.moodle.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.thrift.TException;
import org.pocketcampus.plugin.authentication.shared.utils.Cookie;
import org.pocketcampus.plugin.moodle.shared.CoursesListReply;
import org.pocketcampus.plugin.moodle.shared.MoodleCourse;
import org.pocketcampus.plugin.moodle.shared.MoodleRequest;
import org.pocketcampus.plugin.moodle.shared.MoodleService;

/**
 * MoodleServiceImpl
 * 
 * The implementation of the server side of the Moodle Plugin.
 * 
 * It fetches the user's Moodle data from the Moodle servers.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class MoodleServiceImpl implements MoodleService.Iface {
	
	public MoodleServiceImpl() {
		System.out.println("Starting Moodle plugin server ...");
	}
	
	@Override
	public CoursesListReply getCoursesList(MoodleRequest iRequest) throws TException {
		System.out.println("getCoursesList");
		String page = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(iRequest.getISessionId().getMoodleCookie());
		
		try {
			page = getPageWithCookie("http://moodle.epfl.ch/my/", cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return new CoursesListReply(404);
		}
		if(page == null) {
			System.out.println("not logged in");
			return new CoursesListReply(407);
		}
		
		LinkedList<MoodleCourse> tCourses = new LinkedList<MoodleCourse>();
		page = getSubstringBetween(page, "middle-column", "</td>");
		for (String i : page.split("</h2>")) {
			if(i.indexOf("<h2") != -1) {
				MoodleCourse mc = new MoodleCourse();
				mc.setITitle(StringEscapeUtils.unescapeHtml4(getLastSubstringBetween(i, ">", "</a>")));
				mc.setIInstructor("instructor");
				tCourses.add(mc);
			}
		}
		
		CoursesListReply cl = new CoursesListReply(200);
		cl.setICourses(tCourses);
		return cl;
	}

	
	/**
	 * HELPER FUNCTIONS
	 */
	
	private String getPageWithCookie(String url, Cookie cookie) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setInstanceFollowRedirects(false);
		conn.setRequestProperty("Cookie", cookie.cookie());
		if(conn.getResponseCode() != 200)
			return null;
		return IOUtils.toString(conn.getInputStream(), "UTF-8");
	}
	
	private String getSubstringBetween(String orig, String before, String after) {
		int b = orig.indexOf(before);
		if(b != -1) {
			orig = orig.substring(b + before.length());
		}
		int a = orig.indexOf(after);
		if(a != -1) {
			orig = orig.substring(0, a);
		}
		return orig;
	}
	
	private String getLastSubstringBetween(String orig, String before, String after) {
		int a = orig.lastIndexOf(after);
		if(a != -1) {
			orig = orig.substring(0, a);
		}
		int b = orig.lastIndexOf(before);
		if(b != -1) {
			orig = orig.substring(b + before.length());
		}
		return orig;
	}

}
