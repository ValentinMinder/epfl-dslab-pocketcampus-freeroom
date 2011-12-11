package org.pocketcampus.plugin.isacademia.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.authentication.shared.SessionId;
import org.pocketcampus.plugin.authentication.shared.utils.Cookie;
import org.pocketcampus.plugin.isacademia.shared.Course;
import org.pocketcampus.plugin.isacademia.shared.Exam;
import org.pocketcampus.plugin.isacademia.shared.IsacademiaService;
import org.pocketcampus.plugin.isacademia.shared.Seance;
import org.pocketcampus.plugin.isacademia.shared.SeanceType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class IsacademiaServiceImpl implements IsacademiaService.Iface {
	
	
	public static DocumentBuilderFactory docBuilderFactory;
	public static DocumentBuilder docBuilder;
	
	final public static String ISA_URL = "https://isa.epfl.ch/imoniteur_ISAP/!PORTAL14S.portalCell?ww_k_cell=%s";
	
	public IsacademiaServiceImpl() {
		System.out.println("Starting IS-Academia plugin server ...");
	}

	@Override
	public List<Course> getUserCourses(SessionId aSessionId) throws TException {
		System.out.println("getUserCourses");
		Document doc = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(aSessionId.getIsaCookie());
		
		try {
			String page = getPageWithCookie(String.format(ISA_URL, "1210075152"), cookie);
			DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
			doc = dBuilder.parse(new InputSource(new StringReader(page)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new TException("getUserCourses: Failed to get data from IS-Academia upstream server");
		}
		
		doc.getDocumentElement().normalize();
		LinkedList<Course> tCourses = new LinkedList<Course>();
		NodeList nList = doc.getElementsByTagName("tr");
		for(int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if(nNode.getAttributes() != null && nNode.getAttributes().getNamedItem("id") != null) {
				Element eElement = (Element) nNode;
				NodeList nlList = eElement.getElementsByTagName("td");
				Course crs = new Course();
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
		return tCourses;
	}


	@Override
	public List<Exam> getUserExams(SessionId aSessionId) throws TException {
		System.out.println("getUserExams");
		Document doc = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(aSessionId.getIsaCookie());
		
		try {
			String page = getPageWithCookie(String.format(ISA_URL, "1371525543"), cookie);
			DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
			doc = dBuilder.parse(new InputSource(new StringReader(page)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new TException("getUserExams: Failed to get data from IS-Academia upstream server");
		}
		
		doc.getDocumentElement().normalize();
		LinkedList<Exam> tExams = new LinkedList<Exam>();
		NodeList nList = doc.getElementsByTagName("tr");
		for(int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if(nNode.getAttributes() != null && nNode.getAttributes().getNamedItem("id") != null) {
				Element eElement = (Element) nNode;
				NodeList nlList = eElement.getElementsByTagName("td");
				Exam exm = new Exam();
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
		return tExams;
	}


	@Override
	public List<Seance> getUserSchedule(SessionId aSessionId) throws TException {
		System.out.println("getUserSchedule");
		Document doc = null;
		Cookie cookie = new Cookie();
		cookie.importFromString(aSessionId.getIsaCookie());
		
		try {
			String page = getPageWithCookie(String.format(ISA_URL, "1210054559"), cookie);
			DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
			doc = dBuilder.parse(new InputSource(new StringReader(page)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new TException("getUserSchedule: Failed to get data from IS-Academia upstream server");
		}
		
		doc.getDocumentElement().normalize();
		LinkedList<Seance> tSeances = new LinkedList<Seance>();
		NodeList nList = doc.getElementsByTagName("seance");
		for(int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if(nNode.getAttributes() != null && nNode.getAttributes().getNamedItem("id") != null) {
				Element eElement = (Element) nNode;
				Seance scnc = new Seance();
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
		return tSeances;
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
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestProperty("Cookie", cookie.cookie());
		BufferedInputStream buffer = new BufferedInputStream(conn.getInputStream());
		StringBuilder builder = new StringBuilder();
		int byteRead;
		while ((byteRead = buffer.read()) != -1)
			builder.append((char) byteRead);
		buffer.close();
		conn.disconnect();
		return builder.toString();
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
	
	/*private String getLastSubstringBetween(String orig, String before, String after) {
		int a = orig.lastIndexOf(after);
		if(a != -1) {
			orig = orig.substring(0, a);
		}
		int b = orig.lastIndexOf(before);
		if(b != -1) {
			orig = orig.substring(b + before.length());
		}
		return orig;
	}*/



	static {
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (Exception e) {
			System.out.println("grrrr, exception while running static code");
			e.printStackTrace();
		}
	}

}
