package org.pocketcampus.plugin.isacademia.server;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.pocketcampus.platform.server.XElement;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.plugin.isacademia.shared.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Retrieves a student's schedule from IS-Academia.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class ScheduleImpl implements Schedule {
	private static final String ISA_SCHEDULE_URL = PocketCampusServer.CONFIG.getString("ISA_SCHEDULE_URL");
	// The encoding of IS-Academia's schedule API.
	private static final Charset ISA_CHARSET = Charset.forName("ISO-8859-1");
	// The time zone for the IS-Academia replies
	private static final DateTimeZone ISA_TIME_ZONE = DateTimeZone.forID("Europe/Zurich");
	// The parameters of IS-Academia's API.
	private static final String URL_FROM_PARAMETER = "from", URL_TO_PARAMETER = "to", URL_SCIPER_PARAMETER = "sciper";
	// The date format for IS-Academia's API.
	private static final String URL_PARAMETER_FORMAT = "dd.MM.yyyy";

	// The default language for localized text.
	private static final String DEFAULT_LANGUAGE = "en";
	// The language for room names (names in other languages may have problems)
	private static final String ROOM_NAME_LANGUAGE = "fr";

	// The format of dates in IS-Academia's XML.
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy").withZone(ISA_TIME_ZONE);
	// The format of times in IS-Academia's XML.
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm").withZone(ISA_TIME_ZONE);

	// The various element and attribute names in IS-Academia's XML.
	private static final String STATUS_ATTRIBUTE = "status";
	private static final String STATUS_OK_VALUE = "Terminé";
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
	public ScheduleResponse get(LocalDate weekBeginning, String language, String sciper) {
		if (sciper == null) {
			return new ScheduleResponse(IsaStatusCode.INVALID_SESSION);
		}

		LocalDate weekEnd = weekBeginning.plusDays(6);
		String url = ISA_SCHEDULE_URL
				+ "?" + URL_FROM_PARAMETER + "=" + weekBeginning.toString(URL_PARAMETER_FORMAT)
				+ "&" + URL_TO_PARAMETER + "=" + weekEnd.toString(URL_PARAMETER_FORMAT)
				+ "&" + URL_SCIPER_PARAMETER + "=" + sciper;

		XElement rootElem = null;
		try {
			String xml = _client.get(url, ISA_CHARSET);
			rootElem = XElement.parse(xml);
		} catch (IOException e) {
			return new ScheduleResponse(IsaStatusCode.NETWORK_ERROR);
		}

		if (!STATUS_OK_VALUE.equals(rootElem.attribute(STATUS_ATTRIBUTE))) {
			return new ScheduleResponse(IsaStatusCode.ISA_ERROR);
		}

		List<StudyPeriod> periods = new ArrayList<StudyPeriod>();

		for (XElement periodElem : rootElem.children(STUDY_PERIOD_TAG)) {
			StudyPeriod period = new StudyPeriod();

			LocalDate periodDate = LocalDate.parse(periodElem.child(DATE_ELEMENT).text(), DATE_FORMATTER);
			LocalTime startTime = LocalTime.parse(periodElem.child(START_TIME_ELEMENT).text(), TIME_FORMATTER);
			LocalTime endTime = LocalTime.parse(periodElem.child(END_TIME_ELEMENT).text(), TIME_FORMATTER);
			period.setStartTime(periodDate.toDateTime(startTime, ISA_TIME_ZONE).getMillis());
			period.setEndTime(periodDate.toDateTime(endTime, ISA_TIME_ZONE).getMillis());

			period.setPeriodType(STUDY_PERIOD_TYPES.get(getLocalizedText(periodElem.child(PERIOD_TYPE_ELEMENT), DEFAULT_LANGUAGE)));

			period.setName(getLocalizedText(periodElem.child(COURSE_ELEMENT).child(NAME_ELEMENT), language));

			List<String> rooms = new ArrayList<String>();
			for (XElement roomElem : periodElem.children(ROOM_ELEMENT)) {
				String roomNames = getLocalizedText(roomElem, ROOM_NAME_LANGUAGE);
				for (String actualName : RoomUtil.parseRoomNames(roomNames)) {
					rooms.add(actualName);
				}
			}
			period.setRooms(rooms);

			periods.add(period);
		}

		return new ScheduleResponse(IsaStatusCode.OK).setDays(groupPeriodsByDay(weekBeginning, periods));
	}

	private static List<StudyDay> groupPeriodsByDay(LocalDate weekBegin, List<StudyPeriod> periods) {
		SortedMap<LocalDate, StudyDay> days = new TreeMap<LocalDate, StudyDay>();
		// First, add all days of the working week; we want to display them even if they're empty
		for (int n = 0; n < 5; n++) {
			LocalDate date = weekBegin.plusDays(n);
			days.put(date, new StudyDay(date.toDateTimeAtStartOfDay(ISA_TIME_ZONE).getMillis(), new ArrayList<StudyPeriod>()));
		}

		// Then add periods to them, adding new days as needed
		for (StudyPeriod period : periods) {
			LocalDate date = new LocalDate(period.getStartTime());
			if (!days.containsKey(date)) {
				days.put(date, new StudyDay(date.toDateTimeAtStartOfDay(ISA_TIME_ZONE).getMillis(), new ArrayList<StudyPeriod>()));
			}
			days.get(date).addToPeriods(period);
		}

		return new ArrayList<StudyDay>(days.values());
	}

	/** Gets the text from the specified XML node containing text nodes tagged with languages. */
	private static String getLocalizedText(XElement elem, String language)
	{
		String defaultResult = null;
		String result = null;
		for (XElement textElem : elem.children(TEXT_TAG)) {
			String text = textElem.text();
			String textLang = textElem.attribute(LANGUAGE_ATTRIBUTE);

			if (textLang.equals(language)) {
				result = text;
				break;
			} else if (textLang.equals(DEFAULT_LANGUAGE) || defaultResult == null) {
				defaultResult = text;
			}
		}

		return result == null ? defaultResult : result;
	}
}