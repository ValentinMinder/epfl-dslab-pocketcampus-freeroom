package org.pocketcampus.plugin.isacademia.server;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.thrift.TException;
import org.pocketcampus.plugin.isacademia.shared.TequilaToken;
import org.pocketcampus.platform.sdk.shared.utils.Cookie;
import org.pocketcampus.plugin.isacademia.shared.IsaCoursesListReply;
import org.pocketcampus.plugin.isacademia.shared.IsaExamsListReply;
import org.pocketcampus.plugin.isacademia.shared.IsaRequest;
import org.pocketcampus.plugin.isacademia.shared.IsaScheduleReply;
import org.pocketcampus.plugin.isacademia.shared.IsaSeance;
import org.pocketcampus.plugin.isacademia.shared.IsaSession;
import org.pocketcampus.plugin.isacademia.shared.IsacademiaService;
import org.pocketcampus.plugin.isacademia.shared.SeanceType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * IsacademiaServiceImpl
 * 
 * The implementation of the server side of the Isacademia Plugin.
 * 
 * It fetches the user's Isacademia data from the Isacademia servers.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class IsacademiaServiceImpl implements IsacademiaService.Iface {
	
	final public static String ISA_TIMETABLE_URL = "https://isa.epfl.ch/service/secure/student/timetable/week";
	
	public IsacademiaServiceImpl() {
		System.out.println("Starting IS-Academia plugin server ...");
	}

	@Override
	public TequilaToken getTequilaTokenForIsa() throws TException {
		System.out.println("getTequilaTokenForIsa");
		try {
			String page = getHeadersWithCookie(ISA_TIMETABLE_URL, new Cookie());
			//System.out.println("getTequilaTokenForIsa page=" + page);
			String reqKey = getSubstringBetween(page, "requestkey=", "\r\n");
			String kuki = getSubstringBetween(page, "Set-Cookie: ", ";");
			//System.out.println("getTequilaTokenForIsa reqKey=" + reqKey);
			//System.out.println("getTequilaTokenForIsa kuki=" + kuki);
			TequilaToken teqToken = new TequilaToken(reqKey);
			teqToken.setLoginCookie(kuki);
			return teqToken;
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to getTequilaToken from upstream server");
		}
	}

	@Override
	public IsaSession getIsaSession(TequilaToken iTequilaToken) throws TException {
		System.out.println("getIsaSession");
		return new IsaSession(iTequilaToken.getLoginCookie());
		/*try {
			Cookie cookie = new Cookie();
			cookie.importFromString(iTequilaToken.getLoginCookie());
			String page = getHeadersWithCookie(ISA_TIMETABLE_URL, cookie);
			String kuki = getSubstringBetween(page, "Set-Cookie: ", ";");
			//System.out.println("getIsaSession kuki=" + kuki);
			return new IsaSession(kuki);
		} catch (IOException e) {
			e.printStackTrace();
			throw new TException("Failed to getIsaSession from upstream server");
		}*/
	}
	
	@Override
	public IsaCoursesListReply getUserCourses(IsaRequest iRequest) throws TException {
		System.out.println("getUserCourses");
		return null;
	}

	@Override
	public IsaExamsListReply getUserExams(IsaRequest iRequest) throws TException {
		System.out.println("getUserExams");
		return null;
	}

	@Override
	public IsaScheduleReply getUserSchedule(IsaRequest iRequest) throws TException {
		System.out.println("getUserSchedule");
		
		Document doc = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(iRequest.getIsaSession().getIsaCookie());
		
		try {
			String page = getPageWithCookie(ISA_TIMETABLE_URL, cookie);
			if(page == null) {
				System.out.println("cookie timed out?");
				return new IsaScheduleReply(407);
			}
			DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
			doc = dBuilder.parse(new InputSource(new StringReader(page)));
		} catch (Exception e) {
			e.printStackTrace();
			return new IsaScheduleReply(404);
		}
		
		doc.getDocumentElement().normalize();
		LinkedList<IsaSeance> tSeances = new LinkedList<IsaSeance>();
		NodeList nList = doc.getElementsByTagName("study-period");
		for(int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			System.out.println(getElementByTagName((Element) nNode, "id").getTextContent());
			IsaSeance scnc = new IsaSeance();
			scnc.setSeanceDate(getElementByTagName((Element) nNode, "date").getTextContent());
			scnc.setWeekDay(Integer.parseInt(getElementByTagName((Element) nNode, "day").getTextContent()));
			scnc.setStartTime(getElementByTagName((Element) nNode, "startTime").getTextContent());
			scnc.setEndTime(getElementByTagName((Element) nNode, "endTime").getTextContent());
			scnc.setSeanceType(mapSeanceType(getElementByTagName(getElementByTagName((Element) nNode, "type"), "text").getTextContent()));
			scnc.setCourseName(getElementByTagName(getElementByTagName(getElementByTagName((Element) nNode, "course"), "name"), "text").getTextContent());
			// TODO sometimes there are many rooms
			scnc.setSeanceRoom(getElementByTagName(getElementByTagName((Element) nNode, "room"), "code").getTextContent());
			
			tSeances.add(scnc);
			/*if(nNode.getAttributes() != null && nNode.getAttributes().getNamedItem("id") != null) {
				Element eElement = (Element) nNode;
				IsaSeance scnc = new IsaSeance();
				scnc.setCourse(eElement.getElementsByTagName("matiere").item(0).getTextContent());
				Element instructorElement = (Element) eElement.getElementsByTagName("infobulle").item(0);
				scnc.setInstructor(instructorElement.getElementsByTagName("lib").item(0).getTextContent());
				String typeStr = eElement.getElementsByTagName("type").item(0).getTextContent();
				scnc.setType(mapSeanceType(typeStr));
				scnc.setWeekDay(Integer.parseInt(eElement.getElementsByTagName("n_day").item(0).getTextContent()));
				scnc.setTimeStart(Integer.parseInt(eElement.getElementsByTagName("n_heuredebut").item(0).getTextContent()));
				String rooms = "";
				Element roomsElement = (Element) eElement.getElementsByTagName("libelle").item(0);
				for(String room : roomsElement.getElementsByTagName("lib").item(1).getTextContent().split(",")) {
					if(rooms.length() > 0)
						rooms += ", ";
					rooms += getSubstringBetween(room, "]", "[");
				}
				scnc.setRoom(rooms);
				tSeances.add(scnc);
			}*/
		}
		
		IsaScheduleReply sr = new IsaScheduleReply(200);
		sr.setISeances(tSeances);
		return sr;
	}
	

	/**
	 * HELPER FUNCTIONS
	 */
	
	private Element getElementByTagName(Element e, String tag) {
		return (Element) e.getElementsByTagName(tag).item(0);
	}
	
	private String executeCommand(String cmd) throws IOException {
		Runtime run = Runtime.getRuntime();
		Process pr = run.exec(cmd);
		try {
			pr.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new IOException("executeCommand: waitFor Interrupted");
		}
		// IS-Academia always returns data in ISO-8859-15 encoding
		return IOUtils.toString(pr.getInputStream(), "ISO-8859-1");
	}
	
	private SeanceType mapSeanceType(String tp) throws TException {
		if("LIP_COURS".equals(tp)) {
			return SeanceType.SEANCE_LECTURE;
		} else if ("LIP_EXERCICE".equals(tp)) {
			return SeanceType.SEANCE_EXERCISE;
		} else if ("LIP_LABO".equals(tp)) {
			return SeanceType.SEANCE_LAB;
		} else if ("LIP_PROJET".equals(tp)) {
			return SeanceType.SEANCE_PROJECT;
		} else if ("LIP_TP".equals(tp)) {
			return SeanceType.SEANCE_PRACTICE;
		} else if ("CONFLIT".equals(tp)) {
			return SeanceType.SEANCE_CONFLICT;
		}
		return SeanceType.SEANCE_CONFLICT;
		//throw new TException("mapSeanceType: Unknown Seance Type");
	}
	
	/*private class HttpPageReply {
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
	}*/
	
	/*private String getPageWithCookie(String url, Cookie cookie) throws IOException {
		return getHttpReplyWithCookie(url, cookie).getPage();
	}
	
	private HttpPageReply getHttpReplyWithCookie(String url, Cookie cookie) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestProperty("User-Agent", "Mozilla/5.0");
		conn.setInstanceFollowRedirects(false);
		conn.setRequestProperty("Cookie", cookie.cookie());
		if(conn.getResponseCode() != 200)
			return new HttpPageReply(null, conn.getHeaderField("Location"));
		return new HttpPageReply(IOUtils.toString(conn.getInputStream(), "ISO-8859-1"), null);
	}*/
	
	private String getPageWithCookie(String url, Cookie cookie) throws IOException {
		String[] full = getReplyWithCookie(url, cookie);
		if(full[0].contains("Location"))
			return null;
		return full[1];
	}
	
	private String getHeadersWithCookie(String url, Cookie cookie) throws IOException {
		String[] full = getReplyWithCookie(url, cookie);
		return full[0];
	}
	
	private String[] getReplyWithCookie(String url, Cookie cookie) throws IOException {
		String cookieStr = cookie.cookie().trim();
		if(cookieStr.length() > 0)
			cookieStr = "--cookie " + cookieStr + " ";
		String cmdLine = "curl --sslv3 --include " + cookieStr + url;
		System.out.println(cmdLine);
		String resp = executeCommand(cmdLine);
		String[] full = resp.split("\r\n\r\n", 2);
		return full;
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

}
