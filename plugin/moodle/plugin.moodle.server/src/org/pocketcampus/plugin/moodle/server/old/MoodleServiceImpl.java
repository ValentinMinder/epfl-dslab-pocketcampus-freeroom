package org.pocketcampus.plugin.moodle.server.old;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.thrift.TException;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.platform.shared.utils.Cookie;
import org.pocketcampus.platform.shared.utils.PostDataBuilder;
import org.pocketcampus.platform.shared.utils.StringUtils;
import org.pocketcampus.plugin.authentication.server.AuthenticationServiceImpl;
import org.pocketcampus.plugin.moodle.server.old.MoodleServiceImpl.NodeJson.ItemJson;
import org.pocketcampus.plugin.moodle.server.old.MoodleServiceImpl.SectionNode.ModuleNode;
import org.pocketcampus.plugin.moodle.server.old.MoodleServiceImpl.SectionNode.ModuleNode.ModuleContent;
import org.pocketcampus.plugin.moodle.shared.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

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
public class MoodleServiceImpl {

	public static final String MOODLE_WEBSERVICE_URL = "http://moodle.epfl.ch/webservice/rest/server.php";

	public MoodleServiceImpl() {

	}
	
	public TequilaToken getTequilaTokenForMoodle() throws TException {
		try {
			HttpURLConnection conn2 = (HttpURLConnection) new URL("http://moodle.epfl.ch/auth/tequila/index.php").openConnection();
			conn2.setInstanceFollowRedirects(false);
			conn2.getInputStream();
			URL url = new URL(conn2.getHeaderField("Location"));
			MultiMap<String> params = new MultiMap<String>();
			UrlEncoded.decodeTo(url.getQuery(), params, "UTF-8");
			TequilaToken teqToken = new TequilaToken(params.getString("requestkey"));
			Cookie cookie = new Cookie();
			for (String header : conn2.getHeaderFields().get("Set-Cookie")) {
				cookie.addFromHeader(header);
			}
			teqToken.setLoginCookie(cookie.cookie());
			return teqToken;
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to getTequilaToken from upstream server");
		}
	}

	public MoodleSession getMoodleSession(TequilaToken iTequilaToken) throws TException {
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
	
	public CoursesListReply getCoursesList(MoodleRequest iRequest) throws TException {
		//iRequest.setICourseId(523);//tcpip
		//iRequest.setICourseId(225);//Course Demonstrator
		//iRequest.setICourseId(12101);//MI-023
		//iRequest.setICourseId(12271);//CF10100009
		//System.out.println(getCourseSections(iRequest));
		//System.out.println(getEventsList(iRequest));
		

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
			System.out.println("Moodle: not logged in");
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

	public CoursesListReply getCoursesListAPI(String dummy) throws TException {
		String sciper = AuthenticationServiceImpl.authGetUserSciper();
		if(sciper == null){
			return new CoursesListReply(407);
		}
		Gson gson = new Gson();

		LinkedList<MoodleCourse> tCourses = new LinkedList<MoodleCourse>();

		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(MOODLE_WEBSERVICE_URL).openConnection();
			PostDataBuilder pd = new PostDataBuilder().
					addParam("moodlewsrestformat", "json").
					addParam("wstoken", PocketCampusServer.CONFIG.getString("MOODLE_ACCESS_TOKEN")).
					addParam("wsfunction", "core_user_get_users").
					addParam("criteria[0][key]", "idnumber").
					addParam("criteria[0][value]", sciper);
			conn.setDoOutput(true);
			conn.getOutputStream().write(pd.toString().getBytes());
			String result = StringUtils.fromStream(conn.getInputStream(), "UTF-8");
			UsersNode usrNodes = gson.fromJson(result, UsersNode.class);
			int theId = usrNodes.users.get(0).id;
			
			conn = (HttpURLConnection) new URL(MOODLE_WEBSERVICE_URL).openConnection();
			pd = new PostDataBuilder().
					addParam("moodlewsrestformat", "json").
					addParam("wstoken", PocketCampusServer.CONFIG.getString("MOODLE_ACCESS_TOKEN")).
					addParam("wsfunction", "core_enrol_get_users_courses").
					addParam("userid", "" + theId);
			conn.setDoOutput(true);
			conn.getOutputStream().write(pd.toString().getBytes());
			result = StringUtils.fromStream(conn.getInputStream(), "UTF-8");
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

	public SectionsListReply getCourseSections(MoodleRequest iRequest) throws TException {
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
			System.out.println("Moodle: not logged in");
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
			long availablefrom;
			long availableuntil;
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

	public SectionsListReply getCourseSectionsAPI(String courseId) throws TException {
		if(courseId == null)
			return new SectionsListReply(405);
		String gaspar = AuthenticationServiceImpl.authGetUserGaspar();
		if(gaspar == null){
			// TODO check if user is enrolled in this course
			return new SectionsListReply(407);
		}
		Gson gson = new Gson();

		LinkedList<MoodleSection> msl = new LinkedList<MoodleSection>();

		try {
			
			HttpURLConnection conn = (HttpURLConnection) new URL(MOODLE_WEBSERVICE_URL).openConnection();
			PostDataBuilder pd = new PostDataBuilder().
					addParam("moodlewsrestformat", "json").
					addParam("wstoken", PocketCampusServer.CONFIG.getString("MOODLE_ACCESS_TOKEN")).
					addParam("wsfunction", "core_course_get_contents").
					addParam("courseid", courseId);
			conn.setDoOutput(true);
			conn.getOutputStream().write(pd.toString().getBytes());
			String result = StringUtils.fromStream(conn.getInputStream(), "UTF-8");
//			System.out.println(result);
			Type listType = new TypeToken<List<SectionNode>>() {}.getType();
			List<SectionNode> lsn = gson.fromJson(result, listType);
			for(SectionNode sn : lsn) {
				if(sn.visible != 1)
					continue;
				LinkedList<MoodleResource> mrl = new LinkedList<MoodleResource>();
				for(ModuleNode mn : sn.modules) {
					if(mn.visible != 1)
						continue;
					if(mn.availablefrom != 0 && mn.availablefrom * 1000 > System.currentTimeMillis())
						continue;
					if(mn.availableuntil != 0 && mn.availableuntil * 1000 < System.currentTimeMillis())
						continue;
					if(!"resource".equals(mn.modname) && !"folder".equals(mn.modname))
						continue;
					for(ModuleContent c : mn.contents)
						mrl.add(new MoodleResource(mn.name, c.fileurl));
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
			return new HttpPageReply(StringUtils.fromStream(conn.getInputStream(), "UTF-8"), null);
		if(conn.getResponseCode() / 100 == 3)
			return new HttpPageReply(null, conn.getHeaderField("Location"));
		return new HttpPageReply(null, null);
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
			System.out.println("Moodle: error while processing " + resourceUrl);
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
