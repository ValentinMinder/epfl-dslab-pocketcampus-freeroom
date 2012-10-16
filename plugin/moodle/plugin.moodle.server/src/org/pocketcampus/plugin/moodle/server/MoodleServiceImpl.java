package org.pocketcampus.plugin.moodle.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.thrift.TException;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.pocketcampus.plugin.moodle.shared.TequilaToken;
import org.pocketcampus.platform.sdk.shared.utils.Cookie;
import org.pocketcampus.plugin.moodle.shared.CoursesListReply;
import org.pocketcampus.plugin.moodle.shared.EventsListReply;
import org.pocketcampus.plugin.moodle.shared.MoodleAssignment;
import org.pocketcampus.plugin.moodle.shared.MoodleCourse;
import org.pocketcampus.plugin.moodle.shared.MoodleEvent;
import org.pocketcampus.plugin.moodle.shared.MoodleEventType;
import org.pocketcampus.plugin.moodle.shared.MoodleRequest;
import org.pocketcampus.plugin.moodle.shared.MoodleResource;
import org.pocketcampus.plugin.moodle.shared.MoodleSection;
import org.pocketcampus.plugin.moodle.shared.MoodleService;
import org.pocketcampus.plugin.moodle.shared.MoodleSession;
import org.pocketcampus.plugin.moodle.shared.MoodleUserEvent;
import org.pocketcampus.plugin.moodle.shared.SectionsListReply;

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
	public TequilaToken getTequilaTokenForMoodle() throws TException {
		System.out.println("getTequilaTokenForMoodle");
		try {
			HttpURLConnection conn2 = (HttpURLConnection) new URL("http://moodle.epfl.ch/auth/tequila/index.php").openConnection();
			conn2.setInstanceFollowRedirects(false);
			conn2.getInputStream();
			URL url = new URL(conn2.getHeaderField("Location"));
			MultiMap<String> params = new MultiMap<String>();
			UrlEncoded.decodeTo(url.getQuery(), params, "UTF-8");
			TequilaToken teqToken = new TequilaToken(params.getString("requestkey"));
			Cookie cookie = new Cookie();
			cookie.setCookie(conn2.getHeaderFields().get("Set-Cookie"));
			teqToken.setLoginCookie(cookie.cookie());
			return teqToken;
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to getTequilaToken from upstream server");
		}
	}

	@Override
	public MoodleSession getMoodleSession(TequilaToken iTequilaToken) throws TException {
		System.out.println("getMoodleSession");
		try {
			HttpURLConnection conn2 = (HttpURLConnection) new URL("http://moodle.epfl.ch/auth/tequila/index.php").openConnection();
			conn2.setRequestProperty("Cookie", iTequilaToken.getLoginCookie());
			conn2.setInstanceFollowRedirects(false);
			conn2.getInputStream();
			if("http://moodle.epfl.ch/my/".equals(conn2.getHeaderField("Location")))
				return new MoodleSession(iTequilaToken.getLoginCookie());
			else
				throw new TException("Authentication failed");
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to getMoodleSession from upstream server");
		}
	}
	
	@Override
	public CoursesListReply getCoursesList(MoodleRequest iRequest) throws TException {
		//iRequest.setICourseId(523);//tcpip
		//iRequest.setICourseId(225);//Course Demonstrator
		//iRequest.setICourseId(12101);//MI-023
		//iRequest.setICourseId(12271);//CF10100009
		//System.out.println(getCourseSections(iRequest));
		//System.out.println(getEventsList(iRequest));
		
		
		System.out.println("getCoursesList");
		String page = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(iRequest.getISessionId().getMoodleCookie());
		
		try {
			page = getPageWithCookie("http://moodle.epfl.ch/?redirect=0", cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return new CoursesListReply(404);
		}
		if(page == null || page.indexOf("login/logout.php") == -1) {
			System.out.println("not logged in");
			return new CoursesListReply(407);
		}
		
		//page = getSubstringBetween(page, "block_course_overview", "block_course_list");
		LinkedList<MoodleCourse> tCourses = new LinkedList<MoodleCourse>();
		for (String i : getAllSubstringsBetween(page, "<a", "course/view.php", "a>")) {
			MoodleCourse mc = new MoodleCourse();
			//mc.setITitle(StringEscapeUtils.unescapeHtml4(getLastSubstringBetween(i, ">", "</a>")));
			//String data = getSubstringBetween(i, "course/view.php", "</a>");
			mc.setITitle(getSubstringBetween(i, "title=\"", "\""));
			mc.setIId(Integer.parseInt(getSubstringBetween(i, "id=", "\"")));
			tCourses.add(mc);
		}
		
		CoursesListReply cl = new CoursesListReply(200);
		cl.setICourses(tCourses);
		return cl;
	}

	@Override
	public EventsListReply getEventsList(MoodleRequest iRequest) throws TException {
		// TODO this method was not checked against the new moodle
		System.out.println("getEventsList");
		String page = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(iRequest.getISessionId().getMoodleCookie());
		
		try {
			page = getPageWithCookie("http://moodle.epfl.ch/calendar/view.php", cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return new EventsListReply(404);
		}
		if(page == null || page.indexOf("login/index.php") != -1) {
			System.out.println("not logged in");
			return new EventsListReply(407);
		}
		
		LinkedList<MoodleEvent> tEvents = new LinkedList<MoodleEvent>();
		for (String i : getAllSubstringsBetween(page, "&lt;div&gt;", "&lt;/div&gt;")) {
			MoodleEvent mev = parseMoodleEvent(i);
			if(mev.getIType() == MoodleEventType.MOODLE_EVENT_ASSIGNMENT) {
				mev.setIAssignment(getAssignment(mev, cookie)); // TODO check if null
			} else if(mev.getIType() == MoodleEventType.MOODLE_EVENT_USEREVENT) {
				mev.setIUserEvent(getUserEvent(mev, cookie)); // TODO check if null
			}
			tEvents.add(mev);
		}
		
		EventsListReply el = new EventsListReply(200);
		el.setIEvents(tEvents);
		return el;
	}

	@Override
	public SectionsListReply getCourseSections(MoodleRequest iRequest) throws TException {
		System.out.println("getCourseSections");
		String page = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(iRequest.getISessionId().getMoodleCookie());
		if(!iRequest.isSetICourseId()) {
			return new SectionsListReply(405);
		}
		
		try {
			page = getPageWithCookie("http://moodle.epfl.ch/course/view.php?id=" + iRequest.getICourseId(), cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return new SectionsListReply(404);
		}
		if(page == null || page.indexOf("login/index.php") != -1) {
			System.out.println("not logged in");
			return new SectionsListReply(407);
		}
		
		LinkedList<MoodleSection> msl = new LinkedList<MoodleSection>();
		// don't close the quote because some sections have more style classes such as hidden
		for (String i : getAllSubstringsBetween(page, "class=\"section main clearfix", "<!--class='section'-->")) {
			LinkedList<MoodleResource> mrl = new LinkedList<MoodleResource>();
			for (MoodleResource j : getLinks(i)) {
				if(j.getIUrl().indexOf("/pluginfile.php/") != -1) {
					// if it is a Moodle file, perfect
					mrl.add(j);
				} else if(j.getIUrl().indexOf("/mod/resource/view.php?") != -1 || j.getIUrl().indexOf("/mod/folder/view.php?") != -1) {
					// if it is a Moodle resource, get all files from it
					LinkedList<String> urls = getAllFilesFromMoodleResource(j.getIUrl(), cookie);
					for(String k : urls) {
						mrl.add(new MoodleResource(j.getIName(), k));
					}
				}
			}
			MoodleSection ms = new MoodleSection(mrl, stripHtmlTags("<" + i + ">"));
			// TODO add optional fields (start date and end date)
			ms.setICurrent(i.startsWith(" current"));
			msl.add(ms);
		}
		
		SectionsListReply sl = new SectionsListReply(200);
		sl.setISections(msl);
		return sl;
	}
	
	
	/**
	 * HELPER FUNCTIONS
	 */
	
	private class HttpPageReply {
		private String page;
		private String location;
		public HttpPageReply(String page, String location) {
			this.page = page;
			this.location = location;
		}
		public String getPage() {
			return page;
		}
		public String getLocation() {
			return location;
		}
	}
	
	private String getPageWithCookie(String url, Cookie cookie) throws IOException {
		return getHttpReplyWithCookie(url, cookie).getPage();
	}
	
	private HttpPageReply getHttpReplyWithCookie(String url, Cookie cookie) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestProperty("User-Agent", "Mozilla/5.0");
		conn.setInstanceFollowRedirects(false);
		conn.setRequestProperty("Cookie", cookie.cookie());
		if(conn.getResponseCode() != 200)
			return new HttpPageReply(null, conn.getHeaderField("Location"));
		return new HttpPageReply(IOUtils.toString(conn.getInputStream(), "UTF-8"), null);
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
	

	private LinkedList<String> getAllSubstringsBetween(String orig, String before, String after) {
		LinkedList<String> ssl = new LinkedList<String>();
		if(orig.length() == 0 || before.length() == 0 || after.length() == 0)
			return ssl;
		while(true) {
			int b = orig.indexOf(before);
			if(b == -1)
				return ssl;
			int a = orig.indexOf(after, b + before.length());
			if(a == -1)
				return ssl;
			b = orig.lastIndexOf(before, a - before.length());
			ssl.add(orig.substring(b + before.length(), a));
			orig = orig.substring(a + after.length());
		}
	}
	
	private LinkedList<String> getAllSubstringsBetween(String orig, String before, String middle, String after) {
		LinkedList<String> ssl = new LinkedList<String>();
		if(orig.length() == 0 || before.length() == 0 || middle.length() == 0 || after.length() == 0)
			return ssl;
		while(true) {
			int m = orig.indexOf(middle);
			if(m == -1)
				return ssl;
			int a = orig.indexOf(after, m + middle.length());
			if(a == -1)
				return ssl;
			int b = orig.lastIndexOf(before, m - before.length());
			if(b == -1)
				return ssl;
			ssl.add(orig.substring(b + before.length(), a));
			orig = orig.substring(a + after.length());
		}
	}
	
	
	private MoodleEvent parseMoodleEvent(String html) {
		int id = Integer.parseInt(getSubstringBetween(html, "event_", "&quot;"));
		
		String title = getSubstringBetween(html, "&quot;&gt;", "&lt;");
		
		String datePart = getSubstringBetween(html, "view=day", "#");
		int d = Integer.parseInt(getSubstringBetween(datePart, "_d=", "&"));
		int m = Integer.parseInt(getSubstringBetween(datePart, "_m=", "&"));
		int y = Integer.parseInt(getSubstringBetween(datePart, "_y=", "&"));
		Calendar cal = Calendar.getInstance();
		cal.set(y, m - 1, d);
		long date = cal.getTimeInMillis();
		//SimpleDateFormat sdf = new SimpleDateFormat("dd.mm.yy");
		//datePart = sdf.format(cal.getTimeInMillis()getTime());
		
		MoodleEventType type = MoodleEventType.MOODLE_EVENT_UNKNOWN;
		if(html.indexOf("c/user.gif") != -1) {
			type = MoodleEventType.MOODLE_EVENT_USEREVENT;
		} else if(html.indexOf("assignment/icon.gif") != -1) {
			type = MoodleEventType.MOODLE_EVENT_ASSIGNMENT;
		}
		
		return new MoodleEvent(id, title, date, type);
	}
	
	
	private MoodleAssignment getAssignment(MoodleEvent event, Cookie cookie) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(event.getIDate());
		String url = "http://moodle.epfl.ch/calendar/view.php?view=day&cal_d=" + cal.get(Calendar.DATE) +
				"&cal_m=" + (cal.get(Calendar.MONTH) + 1) + "&cal_y=" + cal.get(Calendar.YEAR);
		String page = null;
		try {
			page = getPageWithCookie(url, cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		if(page == null) {
			System.out.println("not logged in? now?");
			return null;
		}
		page = getSubstringBetween(page, "<a name=\"event_" + event.getIId() + "\"></a>", "</table>");
		page = getSubstringBetween(page, "assignment/view.php?id=", "\"");
		int id = Integer.parseInt(page);
		try {
			page = getPageWithCookie("http://moodle.epfl.ch/mod/assignment/view.php?id=" + id, cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		if(page == null) {
			System.out.println("not logged in? now?");
			return null;
		}
		String desc = getSubstringBetween(page, "id=\"intro\"", "id=\"dates\"");
		int b = desc.indexOf(">");
		desc = desc.substring(b + 1);
		int a = desc.lastIndexOf("</div>");
		desc = stripHtmlTags(desc.substring(0, a));
		// <div id="dates" class="box generalbox generalboxcontent boxaligncenter"><table><tr><td class="c0">Disponible dès le:</td>    <td class="c1">vendredi 9 décembre 2011, 13:40</td></tr><tr><td class="c0">À rendre jusqu'au:</td>    <td class="c1">samedi  24 décembre 2011, 00:00</td></tr></table></div>
		String dateHTML = getSubstringBetween(page, "id=\"dates\"", "</div>");
		LinkedList<String> byDate = getAllSubstringsBetween(dateHTML, "<td class=\"c1\">", "</td>");
		Long postingDate = null;
		Long dueDate = null;
		if(byDate.size() > 1) {
			postingDate = parseDate(byDate.get(0));
			dueDate = parseDate(byDate.get(1));
		} else if(byDate.size() > 0) {
			dueDate = parseDate(byDate.get(0));
		}
		if(dueDate == null) { // if cannot parse then keep previous imprecise date
			dueDate = cal.getTimeInMillis();
		}
		String courseName = getSubstringBetween(page, "&amp;label=", "&amp;");
		try {
			courseName = URLDecoder.decode(courseName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String courseIdStr = getSubstringBetween(page, "course/view.php?id=", "\"");
		int courseId = Integer.parseInt(courseIdStr);
		MoodleAssignment ma = new MoodleAssignment(id, event.getITitle(), desc, new MoodleCourse(courseId, courseName), dueDate);
		if(postingDate != null)
			ma.setIPostingDate(postingDate);
		// TODO add grade if existent
		return ma;
	}
	
	
	private MoodleUserEvent getUserEvent(MoodleEvent event, Cookie cookie) {
		String page = null;
		try {
			page = getPageWithCookie("http://moodle.epfl.ch/calendar/event.php?action=edit&id=" + event.getIId(), cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		if(page == null) {
			System.out.println("not logged in? now?");
			return null;
		}
		String desc = getSubstringBetween(page, "id=\"edit-description\"", "id=\"edit-description\"");
		desc = stripHtmlTags(getSubstringBetween(desc, ">", "</textarea>"));
		int startday = getSelectedValue(getSubstringBetween(page, "name=\"startday\"", "</select>"));
		int startmon = getSelectedValue(getSubstringBetween(page, "name=\"startmon\"", "</select>"));
		int startyr = getSelectedValue(getSubstringBetween(page, "name=\"startyr\"", "</select>"));
		int starthr = getSelectedValue(getSubstringBetween(page, "name=\"starthr\"", "</select>"));
		int startmin = getSelectedValue(getSubstringBetween(page, "name=\"startmin\"", "</select>"));
		Calendar cal = Calendar.getInstance();
		cal.set(startyr, startmon - 1, startday, starthr, startmin);
		MoodleUserEvent userEvent = new MoodleUserEvent(event.getIId(), event.getITitle(), desc, cal.getTimeInMillis());
		if(page.indexOf("id=\"duration_none\" checked") == -1) {
			int endday = getSelectedValue(getSubstringBetween(page, "name=\"endday\"", "</select>"));
			int endmon = getSelectedValue(getSubstringBetween(page, "name=\"endmon\"", "</select>"));
			int endyr = getSelectedValue(getSubstringBetween(page, "name=\"endyr\"", "</select>"));
			int endhr = getSelectedValue(getSubstringBetween(page, "name=\"endhr\"", "</select>"));
			int endmin = getSelectedValue(getSubstringBetween(page, "name=\"endmin\"", "</select>"));
			cal.set(endyr, endmon - 1, endday, endhr, endmin);
			userEvent.setIEndDate(cal.getTimeInMillis());
		}
		return userEvent;
	}
	
	
	private int getSelectedValue(String html) {
		html = getLastSubstringBetween(html, "value=", "selected=");
		html = getSubstringBetween(html, "\"", "\"");
		return Integer.parseInt(html);
	}
	
	
	private Long parseDate(String date) {
		// Try the 3 possible languages of Moodle.
		Calendar cal = Calendar.getInstance();
		try {
			// Monday, 9 January 2012, 06:05 PM
			SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy, hh:mm a", Locale.ENGLISH);
			cal.setTime(sdf.parse(date));
			return cal.getTimeInMillis();
		} catch (ParseException e1) {
			//e1.printStackTrace();
		}
		try {
			// lundi 9 janvier 2012, 18:05
			SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy, HH:mm", Locale.FRENCH);
			cal.setTime(sdf.parse(date));
			return cal.getTimeInMillis();
		} catch (ParseException e1) {
			//e1.printStackTrace();
		}
		try {
			// Montag, 9. Januar 2012, 18:05
			SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d. MMMM yyyy, HH:mm", Locale.GERMAN);
			cal.setTime(sdf.parse(date));
			return cal.getTimeInMillis();
		} catch (ParseException e1) {
			//e1.printStackTrace();
		}
		System.err.println("parseDate: failed to interpret date. what language you using? arabic?");
		return null;
	}
	
	
	private LinkedList<String> getAllFilesFromMoodleResource(String resourceUrl, Cookie cookie) {
		LinkedList<String> urls = new LinkedList<String>();
		HttpPageReply httpReply = null;
		try {
			httpReply = getHttpReplyWithCookie(resourceUrl, cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return urls; // failed, do not crash
		}
		if(httpReply.getPage() != null) {
			for (MoodleResource j : getLinks(httpReply.getPage())) {
				if(j.getIUrl().indexOf("/pluginfile.php/") != -1)
					if(!urls.contains(j.getIUrl()))
						urls.add(stripOffQueryString(j.getIUrl()));
			}
		} else {
			if(httpReply.getLocation().indexOf("/pluginfile.php/") != -1)
				urls.add(stripOffQueryString(httpReply.getLocation()));
		}
		return urls;
	}
	
	private String stripOffQueryString(String url) {
		if(url.indexOf("?") == -1)
			return url;
		return url.substring(0, url.indexOf("?"));
	}

	private LinkedList<MoodleResource> getLinks(String html) {
		LinkedList<MoodleResource> mrl = new LinkedList<MoodleResource>();
		for (String j : getAllSubstringsBetween(html, "href=\"", "</a>")) {
			String url = j.substring(0, j.indexOf("\"")); // target
			String name = stripHtmlTags(j.substring(j.indexOf(">") + 1)); // innerHTML
			mrl.add(new MoodleResource(name, url));
		}
		return mrl;
	}
	
	private String stripHtmlTags(String html) {
		// or keep it client-side
		// android.text.Html.fromHtml(instruction).toString()
		
		// should first remove invisible elements
		html = html.replaceAll("class=\"left side[^<]+<", "");
		html = html.replaceAll("class=\"weekdates[^<]+<", "");
		html = html.replaceAll("class=\"accesshide[^<]+<", "");
		
		html = html.replaceAll("<br />", "\n");
		html = html.replaceAll("<h2>", "\n");
		html = html.replaceAll("</h2>", "\n");
		html = html.replaceAll("<[^>]+>", "");
		
		html = StringEscapeUtils.unescapeHtml4(html);
		html = html.replaceAll("[\\xA0]+", " "); // replace non-breaking spaces (code 160) with normal spaces (code 32)
		html = html.replaceAll("[\\t\\r\\v\\f]+", ""); // remove some weird characters
		html = html.replaceAll("[\\n][ ]+", "\n"); // remove spaces at the beginning of a line
		html = html.replaceAll("[ ]+[\\n]", "\n"); // remove spaces at the end of a line
		html = html.replaceAll("[ ]+", " "); // remove consecutive spaces
		html = html.replaceAll("[\\n]+", "\n"); // remove consecutive new-lines
		html = html.replaceAll("^[\\n]+", ""); // remove new-line characters at the beginning
		html = html.replaceAll("[\\n]+$", ""); // remove new-line characters at the end
		return html.trim();
	}

}
