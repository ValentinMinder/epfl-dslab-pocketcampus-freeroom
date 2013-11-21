package org.pocketcampus.plugin.isacademia.server;

import java.io.ByteArrayInputStream;
import java.nio.charset.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.pocketcampus.plugin.isacademia.server.HttpsClient.HttpResult;
import org.pocketcampus.plugin.isacademia.shared.*;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.joda.time.*;
import org.joda.time.format.*;

/**
 * Retrieves a student's schedule from IS-Academia.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class ScheduleImpl implements Schedule {
	private static final String ISA_SCHEDULE_URL = "https://isa.epfl.ch/service/secure/student/timetable/period";
	// The encoding of IS-Academia's schedule API.
	private static final Charset ISA_CHARSET = StandardCharsets.ISO_8859_1;
	// The parameters of IS-Academia's API.
	private static final String URL_FROM_PARAMETER = "from", URL_TO_PARAMETER = "to", URL_KEY_PARAMETER = "key";
	// The date format for IS-Academia's API.
	private static final String URL_PARAMETER_FORMAT = "dd.MM.yyyy";

	// The separator for the request key in a Tequila URL
	private static final String TEQUILA_URL_KEY_SEPARATOR = "requestkey=";

	// The properties of the cookie for IS-Academia
	private static final String ISA_COOKIE_NAME = "JSESSIONID";
	private static final String ISA_COOKIE_DOMAIN = "isa.epfl.ch";
	private static final String COOKIE_PATH_ALL = "/";

	// The default language for localized text.
	private static final String DEFAULT_LANGUAGE = "en";

	// The format of dates in IS-Academia's XML.
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy");
	// The format of times in IS-Academia's XML.
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");

	// The various element and attribute names in IS-Academia's XML.
	private static final String STUDY_PERIOD_TAG = "study-period";
	private static final String DATE_ELEMENT = "date";
	private static final String START_TIME_ELEMENT = "startTime";
	private static final String END_TIME_ELEMENT = "endTime";
	private static final String PERIOD_TYPE_ELEMENT = "type";
	private static final String TEXT_TAG = "text";
	private static final String LANGUAGE_ATTRIBUTE = "lang";
	private static final String COURSE_ELEMENT = "course";
	private static final String NAME_ELEMENT = "name";
	private static final String ROOM_ELEMENT = "room";

	// Maps study period type names (in the default language) from IS-Academia's XML to the types.
	private static final Map<String, StudyPeriodType> STUDY_PERIOD_TYPES = new HashMap<String, StudyPeriodType>();

	static {
		STUDY_PERIOD_TYPES.put("Lecture", StudyPeriodType.LECTURE);
		STUDY_PERIOD_TYPES.put("Exercises", StudyPeriodType.EXERCISES);
		STUDY_PERIOD_TYPES.put("Project", StudyPeriodType.PROJECT);
		STUDY_PERIOD_TYPES.put("Practical work", StudyPeriodType.LAB);
	}

	private final HttpsClient _client;

	public ScheduleImpl(HttpsClient client) {
		_client = client;
	}

	@Override
	public ScheduleTokenResponse getToken() throws Exception {
		HttpResult result;
		try {
			result = _client.get(ISA_SCHEDULE_URL, ISA_CHARSET, new ArrayList<Cookie>());
		} catch (Exception e) {
			return new ScheduleTokenResponse().setErrorCode(ScheduleErrorCode.NETWORK_ERROR);
		}

		String tequilaKey = result.url.split(TEQUILA_URL_KEY_SEPARATOR)[1];
		String sessionId = null;
		for (Cookie cookie : result.cookies) {
			if (cookie.getName().equals(ISA_COOKIE_NAME)) {
				sessionId = cookie.getValue();
			}
		}
		if (sessionId == null) {
			throw new Exception("ScheduleImpl#getToken: No ISA session ID found.");
		}

		return new ScheduleTokenResponse().setToken(new ScheduleToken(tequilaKey, sessionId));
	}

	@Override
	public ScheduleResponse get(LocalDate weekBeginning, String language, ScheduleToken token) throws Exception {
		LocalDate weekEnd = weekBeginning.plusDays(6);
		String url = ISA_SCHEDULE_URL
				+ "?" + URL_FROM_PARAMETER + "=" + weekBeginning.toString(URL_PARAMETER_FORMAT)
				+ "&" + URL_TO_PARAMETER + "=" + weekEnd.toString(URL_PARAMETER_FORMAT)
				+ "&" + URL_KEY_PARAMETER + "=" + token.getTequilaToken();

		List<Cookie> cookies = new ArrayList<Cookie>();
		BasicClientCookie isaCookie = new BasicClientCookie(ISA_COOKIE_NAME, token.getSessionId());
		isaCookie.setDomain(ISA_COOKIE_DOMAIN);
		isaCookie.setPath(COOKIE_PATH_ALL);
		cookies.add(isaCookie);

		String xml = null;
		try {
			HttpResult result = _client.get(url, ISA_CHARSET, cookies);

			if (!result.url.contains(ISA_SCHEDULE_URL)) {
				return new ScheduleResponse().setErrorCode(ScheduleErrorCode.INVALID_SESSION);
			}

			xml = result.content;
		} catch (Exception e) {
			return new ScheduleResponse().setErrorCode(ScheduleErrorCode.NETWORK_ERROR);
		}

		Element xdoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)))
				.getDocumentElement();

		List<StudyPeriod> periods = new ArrayList<StudyPeriod>();

		for (Node periodNode : getNodes(xdoc, STUDY_PERIOD_TAG)) {
			StudyPeriod period = new StudyPeriod();

			LocalDate periodDate = LocalDate.parse(getText(periodNode, DATE_ELEMENT), DATE_FORMATTER);
			LocalTime startTime = LocalTime.parse(getText(periodNode, START_TIME_ELEMENT), TIME_FORMATTER);
			LocalTime endTime = LocalTime.parse(getText(periodNode, END_TIME_ELEMENT), TIME_FORMATTER);
			period.setStartTime(periodDate.toDateTime(startTime).getMillis());
			period.setEndTime(periodDate.toDateTime(endTime).getMillis());

			period.setPeriodType(STUDY_PERIOD_TYPES.get(getLocalizedText(getNode(periodNode, PERIOD_TYPE_ELEMENT), DEFAULT_LANGUAGE)));

			period.setName(getLocalizedText(getNode(getNode(periodNode, COURSE_ELEMENT), NAME_ELEMENT), language));

			List<String> rooms = new ArrayList<String>();
			for (Node roomNode : getNodes((Element) periodNode, ROOM_ELEMENT)) {
				rooms.add(getLocalizedText(roomNode, language));
			}
			period.setRooms(rooms);

			periods.add(period);
		}

		return new ScheduleResponse().setDays(groupPeriodsByDay(weekBeginning, periods));
	}

	private static List<StudyDay> groupPeriodsByDay(LocalDate weekBegin, List<StudyPeriod> periods) {
		SortedMap<LocalDate, StudyDay> days = new TreeMap<LocalDate, StudyDay>();
		// First, add all days of the working week; we want to display them even if they're empty
		for (int n = 0; n < 5; n++) {
			LocalDate date = weekBegin.plusDays(n);
			days.put(date, new StudyDay(date.toDateTimeAtStartOfDay().getMillis(), new ArrayList<StudyPeriod>()));
		}

		// Then add periods to them, adding new days as needed
		for (StudyPeriod period : periods) {
			LocalDate date = new LocalDate(period.getStartTime());
			if (days.containsKey(date)) {
				days.get(date).addToPeriods(period);
			} else {
				days.put(date, new StudyDay(date.toDateTimeAtStartOfDay().getMillis(), new ArrayList<StudyPeriod>()));
			}
		}

		return new ArrayList<StudyDay>(days.values());
	}

	/** Gets the text from the specified child of the specified XML node. */
	private static String getText(Node node, String elementName) {
		return ((Element) node).getElementsByTagName(elementName).item(0).getTextContent().trim();
	}

	/** Gets the text from the specified XML node containing text nodes tagged with languages. */
	private static String getLocalizedText(Node node, String language)
	{
		NodeList children = ((Element) node).getElementsByTagName(TEXT_TAG);
		String defaultResult = null;
		String result = null;
		for (int n = 0; n < children.getLength(); n++) {
			Element child = (Element) children.item(n);
			String childLang = child.getAttribute(LANGUAGE_ATTRIBUTE);
			String text = child.getTextContent().trim();
			if (childLang.equals(language)) {
				result = text;
				break;
			} else if (childLang.equals(DEFAULT_LANGUAGE) || defaultResult == null) {
				defaultResult = text;
			}
		}

		return result == null ? defaultResult : result;
	}

	/** Gets the child node with the specified name from the specified parent XML node. */
	private static Node getNode(Node parent, String name) {
		return ((Element) parent).getElementsByTagName(name).item(0);
	}

	/** Like getElementsByTagName, but returns an iterable class instead of a NodeList. */
	private static List<Node> getNodes(Element parent, String name) {
		NodeList nodes = parent.getElementsByTagName(name);
		List<Node> retVal = new ArrayList<Node>(nodes.getLength());
		for (int n = 0; n < nodes.getLength(); n++) {
			retVal.add(nodes.item(n));
		}
		return retVal;
	}
}