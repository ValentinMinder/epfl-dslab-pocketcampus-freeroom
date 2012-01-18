package org.pocketcampus.plugin.isacademia.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.authentication.shared.utils.Cookie;
import org.pocketcampus.plugin.isacademia.shared.IsaCourse;
import org.pocketcampus.plugin.isacademia.shared.IsaCoursesListReply;
import org.pocketcampus.plugin.isacademia.shared.IsaExam;
import org.pocketcampus.plugin.isacademia.shared.IsaExamsListReply;
import org.pocketcampus.plugin.isacademia.shared.IsaRequest;
import org.pocketcampus.plugin.isacademia.shared.IsaScheduleReply;
import org.pocketcampus.plugin.isacademia.shared.IsaSeance;
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
	
	final public static String ISA_URL = "https://isa.epfl.ch/imoniteur_ISAP/!PORTAL14S.portalCell?ww_k_cell=%s";
	
	public IsacademiaServiceImpl() {
		System.out.println("Starting IS-Academia plugin server ...");
	}

	@Override
	public IsaCoursesListReply getUserCourses(IsaRequest iRequest) throws TException {
		System.out.println("getUserCourses");
		Document doc = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(iRequest.getISessionId().getIsaCookie());
		
		try {
			String page = getPageWithCookie(String.format(ISA_URL, "1210075152"), cookie);
			if(page == null) {
				System.out.println("cookie timed out?");
				return new IsaCoursesListReply(407);
			}
			DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
			doc = dBuilder.parse(new InputSource(new StringReader(page)));
		} catch (Exception e) {
			e.printStackTrace();
			return new IsaCoursesListReply(404);
		}
		
		doc.getDocumentElement().normalize();
		LinkedList<IsaCourse> tCourses = new LinkedList<IsaCourse>();
		NodeList nList = doc.getElementsByTagName("tr");
		for(int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if(nNode.getAttributes() != null && nNode.getAttributes().getNamedItem("id") != null) {
				Element eElement = (Element) nNode;
				NodeList nlList = eElement.getElementsByTagName("td");
				IsaCourse crs = new IsaCourse();
				crs.setName(nlList.item(0).getTextContent());
				crs.setCode(getSubstringBetween(nlList.item(1).getTextContent(), "]", "["));
				crs.setInstructor(getSubstringBetween(nlList.item(2).getTextContent(), "]", "["));
				String rooms = "";
				for(String room : nlList.item(3).getTextContent().split(",")) {
					if(rooms.length() > 0)
						rooms += ", ";
					rooms += getSubstringBetween(room, "]", "[");
				}
				crs.setRooms(rooms);
				crs.setDateTime(nlList.item(4).getTextContent());
				crs.setCredits(Integer.parseInt(getSubstringBetween(nlList.item(5).getTextContent(), "]", "[")));
				tCourses.add(crs);
			}
		}
		
		IsaCoursesListReply clr = new IsaCoursesListReply(200);
		clr.setICourses(tCourses);
		return clr;
	}

	@Override
	public IsaExamsListReply getUserExams(IsaRequest iRequest) throws TException {
		System.out.println("getUserExams");
		Document doc = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(iRequest.getISessionId().getIsaCookie());
		
		try {
			String page = getPageWithCookie(String.format(ISA_URL, "1371525543"), cookie);
			if(page == null) {
				System.out.println("cookie timed out?");
				return new IsaExamsListReply(407);
			}
			DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
			doc = dBuilder.parse(new InputSource(new StringReader(page)));
		} catch (Exception e) {
			e.printStackTrace();
			return new IsaExamsListReply(404);
		}
		
		doc.getDocumentElement().normalize();
		LinkedList<IsaExam> tExams = new LinkedList<IsaExam>();
		NodeList nList = doc.getElementsByTagName("tr");
		for(int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if(nNode.getAttributes() != null && nNode.getAttributes().getNamedItem("id") != null) {
				Element eElement = (Element) nNode;
				NodeList nlList = eElement.getElementsByTagName("td");
				IsaExam exm = new IsaExam();
				exm.setCourse(nlList.item(0).getTextContent());
				exm.setCode(getSubstringBetween(nlList.item(1).getTextContent(), "]", "["));
				exm.setInstructor(getSubstringBetween(nlList.item(2).getTextContent(), "]", "["));
				String rooms = "";
				for(String room : nlList.item(3).getTextContent().split(",")) {
					if(rooms.length() > 0)
						rooms += ", ";
					rooms += getSubstringBetween(room, "]", "[");
				}
				exm.setRooms(rooms);
				exm.setDateTime(nlList.item(4).getTextContent());
				exm.setCredits(Integer.parseInt(getSubstringBetween(nlList.item(5).getTextContent(), "]", "[")));
				//crs.setGrade(nlList.item(6).getTextContent());
				exm.setSemester(nlList.item(7).getTextContent());
				exm.setAcademicYear(nlList.item(8).getTextContent());
				tExams.add(exm);
			}
		}
		
		IsaExamsListReply elr = new IsaExamsListReply(200);
		elr.setIExams(tExams);
		return elr;
	}

	@Override
	public IsaScheduleReply getUserSchedule(IsaRequest iRequest) throws TException {
		System.out.println("getUserSchedule");
		Document doc = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(iRequest.getISessionId().getIsaCookie());
		
		try {
			String page = getPageWithCookie(String.format(ISA_URL, "1210054559"), cookie);
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
		NodeList nList = doc.getElementsByTagName("seance");
		for(int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if(nNode.getAttributes() != null && nNode.getAttributes().getNamedItem("id") != null) {
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
			}
		}
		
		IsaScheduleReply sr = new IsaScheduleReply(200);
		sr.setISeances(tSeances);
		return sr;
	}
	

	/**
	 * HELPER FUNCTIONS
	 */
	
	private String executeCommand(String cmd) throws IOException {
		Runtime run = Runtime.getRuntime();
		Process pr = null;
		pr = run.exec(cmd);
		try {
			pr.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new IOException("executeCommand: waitFor Interrupted");
		}
		BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		int byteRead;
		StringBuilder builder = new StringBuilder();
		while ((byteRead = buf.read()) != -1)
			builder.append((char) byteRead);
		return builder.toString();
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
		throw new TException("mapSeanceType: Unknown Seance Type");
	}
	
	private String getPageWithCookie(String url, Cookie cookie) throws IOException {
		String cmdLine = "curl --sslv3 --include --cookie " + cookie.cookie() + " " + url;
		System.out.println(cmdLine);
		String resp = executeCommand(cmdLine);
		String[] full = resp.split("\r\n\r\n", 2);
		if(full.length != 2)
			throw new IOException("getPageWithCookie: no header or body in http response");
		if(full[0].contains("Set-Cookie"))
			return null;
		String page = full[1];
		System.out.println(page.substring(0, 60));
		return page;
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
