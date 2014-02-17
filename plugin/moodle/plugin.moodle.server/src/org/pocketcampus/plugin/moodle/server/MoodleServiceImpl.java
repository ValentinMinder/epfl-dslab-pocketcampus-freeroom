package org.pocketcampus.plugin.moodle.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.thrift.TException;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.pocketcampus.plugin.moodle.common.MoodleConstants;
import org.pocketcampus.plugin.moodle.server.MoodleServiceImpl.NodeJson.ItemJson;
import org.pocketcampus.plugin.moodle.server.MoodleServiceImpl.SectionNode.ModuleNode;
import org.pocketcampus.plugin.moodle.shared.TequilaToken;
import org.pocketcampus.platform.launcher.server.PocketCampusServer;
import org.pocketcampus.platform.launcher.server.RawPlugin;
import org.pocketcampus.platform.sdk.shared.utils.Cookie;
import org.pocketcampus.platform.sdk.shared.utils.PostDataBuilder;
import org.pocketcampus.platform.sdk.shared.utils.StringUtils;
import org.pocketcampus.platform.sdk.shared.utils.URLBuilder;
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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

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
public class MoodleServiceImpl implements MoodleService.Iface, RawPlugin {

	public static final String MOODLE_WEBSERVICE_URL = "http://moodle.epfl.ch/webservice/rest/server.php";

	public MoodleServiceImpl() {
		System.out.println("Starting Moodle plugin server ...");
//		try {
//			getCoursesListAPI("");
//			getCourseSectionsAPI("225");
//		} catch (TException e) {
//			e.printStackTrace();
//		}
	}
	
	@Override
	public HttpServlet getServlet() {
		return new HttpServlet() {
			private static final long serialVersionUID = -2572366584222819828L;
			protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				//InputStream in = request.getInputStream();
				//request.get
//				response.setStatus(500);
//				System.out.println(request.getQueryString());
//				OutputStream out = response.getOutputStream();
//				out.write("OK1".getBytes());
//				out.flush();
//				doPost(request, response);
			}
			protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				String gaspar = PocketCampusServer.authGetUserGasparFromReq(req);
				if(gaspar == null) return;
				
				String action = req.getParameter(MoodleConstants.MOODLE_RAW_ACTION_KEY);
				if(MoodleConstants.MOODLE_RAW_ACTION_DOWNLOAD_FILE.equals(action)) {
					String fp = req.getParameter(MoodleConstants.MOODLE_RAW_FILE_PATH);
					if(fp == null) return;
					
					fp = StringUtils.getSubstringBetween(fp, "pluginfile.php", "?");
					//http://moodle.epfl.ch/webservice/pluginfile.php/1525234/mod_resource/content/4/hwk2_sol.pdf?wstoken=9a00f999e5d329b417a1e578ac489b68
//					if(fp.indexOf("?") != -1)
//						fp = fp.substring(0, fp.indexOf("?"));
//					URLBuilder url = new URLBuilder(fp).addParam("token", PC_SRV_CONFIG.getString("MOODLE_ACCESS_TOKEN"));
					fp = "http://moodle.epfl.ch/webservice/pluginfile.php" + fp;
					HttpURLConnection conn = (HttpURLConnection) new URL(fp).openConnection();
					conn.setDoOutput(true);
					PostDataBuilder pd = new PostDataBuilder().
							addParam("token", PC_SRV_CONFIG.getString("MOODLE_ACCESS_TOKEN"));
					conn.getOutputStream().write(pd.toBytes());
					OutputStream out = resp.getOutputStream();
					InputStream in = conn.getInputStream();
					IOUtils.copy(in, out);
//					out.flush();
//					in.close();
//					out.close();
				} 
			}
		};
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
	

	
	public static class NodeJson {
		public List<ItemJson> children;
		public static class ItemJson {
			public String key;
			public String name;
			public String title;
			public int type;
			public String link;
		}
	}
	
	static Gson gson = new Gson();
	static JsonParser jsonParser = new JsonParser();
	
	private List<ItemJson> fetchNode(Cookie cookie, String reqKey, int reqType, int filterType) {
		//System.out.println("fetching id=" + reqKey + "&type=" + reqType);
		try {
			
			List<ItemJson> children = new LinkedList<ItemJson>();
			String page = getPageWithCookie("http://moodle.epfl.ch/lib/ajax/getnavbranch.php?id=" + reqKey + "&type=" + reqType, cookie);
			
			
			JsonObject jo = jsonParser.parse(page).getAsJsonObject();
			if(jo.has("children")) {
				JsonArray ja = jo.get("children").getAsJsonArray();
				for(int i = 0; i < ja.size(); i++) {
					if(!ja.get(i).isJsonObject())
						continue;
					JsonObject jo2 = ja.get(i).getAsJsonObject();
					if(!jo2.has("type"))
						continue;
					if(jo2.get("type").getAsInt() != filterType) 
						continue;
					ItemJson ij = new ItemJson();
					ij.type = filterType;
					if(jo2.has("key")) ij.key = jo2.get("key").getAsString();
					if(jo2.has("name")) ij.name = jo2.get("name").getAsString();
					if(jo2.has("title")) ij.title = jo2.get("title").getAsString();
					if(jo2.has("link")) ij.link = jo2.get("link").getAsString();
					children.add(ij);
				}
			}
			
			/*NodeJson node = gson.fromJson(page, NodeJson.class);
			if(node.children != null) {
				for(ItemJson i : node.children) {
					if(i.type == filterType)
						children.add(i);
				}
			}*/
			
			return children;
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
//		Gson gson = new Gson();
		//NodeJson courses = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(iRequest.getISessionId().getMoodleCookie());
		
		try {
			page = getPageWithCookie("http://moodle.epfl.ch/?redirect=0", cookie);
			//page2 = getPageWithCookie("http://moodle.epfl.ch/lib/ajax/getnavbranch.php?id=mycourses&type=0", cookie);
		} catch (IOException e) {
			e.printStackTrace();
			return new CoursesListReply(404);
		}
		if(page == null || page.indexOf("login/logout.php") == -1) {
			System.out.println("not logged in");
			return new CoursesListReply(407);
		}
		
		List<ItemJson> courses = fetchNode(cookie, "mycourses", 0, 20);
		if(courses == null) {
			return new CoursesListReply(404);
		}
		
		LinkedList<MoodleCourse> tCourses = new LinkedList<MoodleCourse>();
		for(ItemJson mcj : courses) {
			MoodleCourse mc = new MoodleCourse();
			mc.setITitle(StringEscapeUtils.unescapeHtml4(mcj.title));
			mc.setIId(Integer.parseInt(mcj.key));
			tCourses.add(mc);
		}
		
		CoursesListReply cl = new CoursesListReply(200);
		cl.setICourses(tCourses);
		return cl;
	}

	public static class CourseNode {
		int id;
		String shortname;
		String fullname;
		int enrolledusercount;
		String idnumber;
		int visible;
	}
//	public static interface CourseList extends List<CourseNode> {	
//	}
	public static class UsersNode {
		List<UserNode> users;
		public static class UserNode {
			int id;
			String username;
			String firstname;
			String lastname;
			String fullname;
			String email;
			String address;
			String phone1;
			String department;
			String institution;
			String idnumber;
			String url;
			String city;
			String country;
			String profileimageurlsmall;
		}
		
	}

	@Override
	public CoursesListReply getCoursesListAPI(String dummy) throws TException {
		String gaspar = PocketCampusServer.authGetUserGaspar(dummy);
		if(gaspar == null){
			return new CoursesListReply(407);
		}
		Gson gson = new Gson();

		LinkedList<MoodleCourse> tCourses = new LinkedList<MoodleCourse>();

		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(MOODLE_WEBSERVICE_URL).openConnection();
			PostDataBuilder pd = new PostDataBuilder().
					addParam("moodlewsrestformat", "json").
					addParam("wstoken", PC_SRV_CONFIG.getString("MOODLE_ACCESS_TOKEN")).
					addParam("wsfunction", "core_user_get_users").
					addParam("criteria[0][key]", "username").
					addParam("criteria[0][value]", gaspar);
			conn.setDoOutput(true);
			conn.getOutputStream().write(pd.toBytes());
			String result = IOUtils.toString(conn.getInputStream(), "UTF-8");
			UsersNode usrNodes = gson.fromJson(result, UsersNode.class);
			int theId = usrNodes.users.get(0).id;
			
			conn = (HttpURLConnection) new URL(MOODLE_WEBSERVICE_URL).openConnection();
			pd = new PostDataBuilder().
					addParam("moodlewsrestformat", "json").
					addParam("wstoken", PC_SRV_CONFIG.getString("MOODLE_ACCESS_TOKEN")).
					addParam("wsfunction", "core_enrol_get_users_courses").
					addParam("userid", "" + theId);
			conn.setDoOutput(true);
			conn.getOutputStream().write(pd.toBytes());
			result = IOUtils.toString(conn.getInputStream(), "UTF-8");
			Type listType = new TypeToken<List<CourseNode>>() {}.getType();
			List<CourseNode> lcn = gson.fromJson(result, listType);
			for(CourseNode mcj : lcn) {
				if(mcj.visible != 1)
					continue;
				MoodleCourse mc = new MoodleCourse();
				mc.setITitle(mcj.fullname);
				mc.setIId(mcj.id);
				tCourses.add(mc);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
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
		
		List<ItemJson> sections = fetchNode(cookie, "" + iRequest.getICourseId(), 20, 30);
		if(sections == null) {
			return new SectionsListReply(404);
		}
		LinkedList<MoodleSection> msl = new LinkedList<MoodleSection>();
		for(ItemJson sj : sections) {
			LinkedList<MoodleResource> mrl = new LinkedList<MoodleResource>();
			List<ItemJson> resources = fetchNode(cookie, sj.key, 30, 40);
			if(resources == null) {
				continue;
			}
			for(ItemJson rj : resources) {
				if(rj.link.indexOf("/mod/resource/view.php?") != -1 || rj.link.indexOf("/mod/folder/view.php?") != -1) {
					// if it is a Moodle resource, get all files from it
					LinkedList<String> urls = getAllFilesFromMoodleResource(rj.link, cookie);
					for(String k : urls) {
						mrl.add(new MoodleResource(rj.name, k));
					}
				}
			}
			
			MoodleSection ms = new MoodleSection(mrl, sj.name);
			msl.add(ms);
			
		}

		
		SectionsListReply sl = new SectionsListReply(200);
		sl.setISections(msl);
		return sl;
	}
	
	public static class SectionNode {
		int id;
		String name;
		int visible;
		String summary;
		int summaryformat;
		List<ModuleNode> modules;
		public static class ModuleNode {
			int id;
			String url;
			String name;
			int visible;
			String modicon;
			String modname;
			String modplural;
			int availablefrom;
			int availableuntil;
			int indent;
			String description;
			List<ModuleContent> contents;
			public static class ModuleContent {
				String author;
				String filename;
				String filepath;
				int filesize;
				String fileurl;
				String license;
				int sortorder;
				long timecreated;
				long timemodified;
				String type;
				int userid;
			}
		}
	}

	@Override
	public SectionsListReply getCourseSectionsAPI(String courseId) throws TException {
		if(courseId == null)
			return new SectionsListReply(405);
		String gaspar = PocketCampusServer.authGetUserGaspar(courseId);
		if(gaspar == null){
			return new SectionsListReply(407);
		}
		Gson gson = new Gson();

		LinkedList<MoodleSection> msl = new LinkedList<MoodleSection>();

		try {
			
			HttpURLConnection conn = (HttpURLConnection) new URL(MOODLE_WEBSERVICE_URL).openConnection();
			PostDataBuilder pd = new PostDataBuilder().
					addParam("moodlewsrestformat", "json").
					addParam("wstoken", PC_SRV_CONFIG.getString("MOODLE_ACCESS_TOKEN")).
					addParam("wsfunction", "core_course_get_contents").
					addParam("courseid", courseId);
			conn.setDoOutput(true);
			conn.getOutputStream().write(pd.toBytes());
			String result = IOUtils.toString(conn.getInputStream(), "UTF-8");
			//System.out.println(result);
			Type listType = new TypeToken<List<SectionNode>>() {}.getType();
			List<SectionNode> lsn = gson.fromJson(result, listType);
			for(SectionNode sn : lsn) {
				if(sn.visible != 1)
					continue;
				LinkedList<MoodleResource> mrl = new LinkedList<MoodleResource>();
				for(ModuleNode mn : sn.modules) {
					if(mn.visible != 1)
						continue;
					if(!"resource".equals(mn.modname))
						continue;
					mrl.add(new MoodleResource(mn.name, mn.contents.get(0).fileurl));
				}
				MoodleSection ms = new MoodleSection(mrl, sn.name);
				msl.add(ms);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
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
		if(conn.getResponseCode() == 200)
			return new HttpPageReply(IOUtils.toString(conn.getInputStream(), "UTF-8"), null);
		if(conn.getResponseCode() / 100 == 3)
			return new HttpPageReply(null, conn.getHeaderField("Location"));
		return new HttpPageReply(null, null);
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
		// <div id="dates" class="box generalbox generalboxcontent boxaligncenter"><table><tr><td class="c0">Disponible d��s le:</td>    <td class="c1">vendredi 9 d��cembre 2011, 13:40</td></tr><tr><td class="c0">�� rendre jusqu'au:</td>    <td class="c1">samedi  24 d��cembre 2011, 00:00</td></tr></table></div>
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
		} else if(httpReply.getLocation() != null) {
			if(httpReply.getLocation().indexOf("/pluginfile.php/") != -1)
				urls.add(stripOffQueryString(httpReply.getLocation()));
		} else {
			System.out.println("error while processing " + resourceUrl);
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
