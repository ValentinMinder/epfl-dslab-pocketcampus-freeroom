package org.pocketcampus.plugin.moodle.server;

import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.pocketcampus.plugin.moodle.shared.*;
import org.pocketcampus.platform.launcher.server.PocketCampusServer;
import org.pocketcampus.platform.sdk.server.HttpClient;
import org.pocketcampus.platform.sdk.shared.utils.PostDataBuilder;

import com.google.gson.*;

/**
 * Implementation of CourseService using Moodle's REST API.
 * 
 * TODO: In getSections, make sure the user has access to the course.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class CourseServiceImpl implements CourseService {
	// The Moodle JSON service URL and its charset
	private static final String SERVICE_URL = "http://moodle.epfl.ch/webservice/rest/server.php?";
	private static final Charset CHARSET = Charset.forName("UTF-8");

	// The request format parameter (Moodle's XML is absolutely awful, don't even think about using it)
	private static final String FORMAT_KEY = "moodlewsrestformat";
	private static final String FORMAT_VALUE = "json";

	// The token parameter key
	private static final String TOKEN_KEY = "wstoken";

	// The requested method (because URL mapping is for n00bs)
	private static final String METHOD_KEY = "wsfunction";

	// Constant for visibility; who needs booleans when you have integers?
	private static final int VISIBLE = 1;
	// Constant for HTML summaries... probably...
	private static final int SUMMARY_FORMAT_HTML = 1;

	// Request an user...
	private static final String METHOD_VALUE_GET_USER = "core_user_get_users";
	// ...with the only criterion being its ID...
	private static final String GET_USER_CRITERION_KEY = "criteria[0][key]";
	private static final String GET_USER_CRITERION_VALUE = "idnumber";
	// ...which is the sciper.
	private static final String GET_USER_SCIPER_KEY = "criteria[0][value]";

	/**
	 * 
	 * {
	 * "users":
	 * [
	 * {
	 * "id":202901,
	 * "fullname":"Solal Pirelli",
	 * "email":"solal.pirelli@epfl.ch",
	 * "skype":"live:solal.pirelli",
	 * "idnumber":"223572",
	 * "firstaccess":1342777439,
	 * "lastaccess":1403683338,
	 * "description":"",
	 * "descriptionformat":1,
	 * "city":"Lausanne",
	 * "url":"https:\/\/bitbucket.org\/SolalPirelli",
	 * "country":"CH",
	 * "profileimageurlsmall":"http:\/\/moodle.epfl.ch\/pluginfile.php\/1489941\/user\/icon\/f2",
	 * "profileimageurl":"http:\/\/moodle.epfl.ch\/pluginfile.php\/1489941\/user\/icon\/f1"
	 * }
	 * ],
	 * "warnings":
	 * []
	 * }
	 * 
	 */

	// Request all courses an user is enrolled in...
	private static final String METHOD_VALUE_GET_COURSES = "core_enrol_get_users_courses";
	// ...this time, with a much simpler selection method.
	private static final String GET_COURSES_USERID_KEY = "userid";

	/**
	 * [
	 * {
	 * "id": 14174,
	 * "shortname": "CS-251",
	 * "fullname": "Theory of computation",
	 * "enrolledusercount": 166,
	 * "idnumber": "M1704192615_G1378362175_G1378365806_P200602_P217799_P217986_P237044",
	 * "visible": 1
	 * },
	 * {
	 * "id": 14153,
	 * "shortname": "ArchOrd2",
	 * "fullname": "Architecture des ordinateurs II",
	 * "enrolledusercount": 47,
	 * "idnumber": "",
	 * "visible": 1
	 * },
	 * {
	 * "id": 14084,
	 * "shortname": "ArchOrd1",
	 * "fullname": "Architecture des ordinateurs I",
	 * "enrolledusercount": 152,
	 * "idnumber": "M1771839_G1378362148_G1378365780_P101954_P183817_P196536_P206309_P221493",
	 * "visible": 1
	 * },
	 * {
	 * "id": 13768,
	 * "shortname": "CS-250",
	 * "fullname": "Algorithms",
	 * "enrolledusercount": 290,
	 * "idnumber":
	 * "M2258712_G736311150_G1378362148_G1378365780_G1378438904_G1650773612_G1654125707_P200246_P200365_P203131_P210783_P213225_P217986_P226032_P226149",
	 * "visible": 1
	 * },
	 * {
	 * "id": 2671,
	 * "shortname": "CS-206",
	 * "fullname": "Concurrence",
	 * "enrolledusercount": 174,
	 * "idnumber": "M1772852_G1378362175_G1378365806_P106377_P179262_P194219_P211297_P221727",
	 * "visible": 1
	 * },
	 * {
	 * "id": 6731,
	 * "shortname": "ProgOrientSyst",
	 * "fullname": "Programmation orientée système",
	 * "enrolledusercount": 199,
	 * "idnumber": "M71645784_G736309037_G736312585_P112547",
	 * "visible": 1
	 * },
	 * {
	 * "id": 13726,
	 * "shortname": "EE-202(b)",
	 * "fullname": "Electronique I",
	 * "enrolledusercount": 163,
	 * "idnumber": "M1773492_G1378362148_G1378365780_P106795",
	 * "visible": 1
	 * },
	 * {
	 * "id": 14155,
	 * "shortname": "PHYS-114",
	 * "fullname": "Physique Générale II (pour IC)",
	 * "enrolledusercount": 214,
	 * "idnumber": "",
	 * "visible": 1
	 * },
	 * {
	 * "id": 14030,
	 * "shortname": "PHYS-113",
	 * "fullname": "Physique Générale I pour IC",
	 * "enrolledusercount": 160,
	 * "idnumber": "",
	 * "visible": 1
	 * },
	 * {
	 * "id": 5191,
	 * "shortname": "EE-204",
	 * "fullname": "Circuits et Systèmes I",
	 * "enrolledusercount": 166,
	 * "idnumber": "M348715502_G1378341373_G1378362148_G1378365780_P199128_P200700_P213980_P217987_P221971",
	 * "visible": 1
	 * },
	 * {
	 * "id": 13839,
	 * "shortname": "HUM-257",
	 * "fullname": "Santé, Population, Société",
	 * "enrolledusercount": 82,
	 * "idnumber": "M1580215967_G1378366144_P243244",
	 * "visible": 1
	 * }
	 * ]
	 */

	// Request all sections of a course...
	private static final String METHOD_VALUE_GET_SECTIONS = "core_course_get_contents";
	// ...again, with a simple selection method
	private static final String GET_SECTIONS_COURSEID_KEY = "courseid";

	// For sections whose name is a date range, the separator between these dates and their format
	private static final String SECTION_NAME_DATE_SEPARATOR = " - ";
	private static final DateTimeFormatter SECTION_NAME_DATE_FORMAT = DateTimeFormat.forPattern("dd mmmm");

	// The module types
	private static final String MODULE_FILE = "resource";
	private static final String MODULE_URL = "url";
	private static final String MODULE_FOLDER = "folder";

	private final HttpClient client;
	private final String token;

	public CourseServiceImpl(HttpClient client, String token) {
		this.client = client;
		this.token = token;
	}

	@Override
	public MoodleCoursesResponse2 getCourses(MoodleCoursesRequest2 request) {
		final String sciper = PocketCampusServer.authGetUserSciper(request);
		if (sciper == null) {
			return new MoodleCoursesResponse2(MoodleStatusCode2.AUTHENTICATION_ERROR, new ArrayList<MoodleCourse2>());
		}

		final String usersQueryParams = new PostDataBuilder()
				.addParam(FORMAT_KEY, FORMAT_VALUE)
				.addParam(TOKEN_KEY, token)
				.addParam(METHOD_KEY, METHOD_VALUE_GET_USER)
				.addParam(GET_USER_CRITERION_KEY, GET_USER_CRITERION_VALUE)
				.addParam(GET_USER_SCIPER_KEY, sciper)
				.toString();

		int userId = -1;
		try {
			final String usersResponseString = client.getString(SERVICE_URL + usersQueryParams, CHARSET);
			final JsonUsersResponse usersResponse = new Gson().fromJson(usersResponseString, JsonUsersResponse.class);

			if (usersResponse.users.length == 0) {
				throw new Exception("User not found.");
			}

			userId = usersResponse.users[0].id;
		} catch (Exception _) {
			return new MoodleCoursesResponse2(MoodleStatusCode2.NETWORK_ERROR, new ArrayList<MoodleCourse2>());
		}

		final String coursesQueryParams = new PostDataBuilder()
				.addParam(FORMAT_KEY, FORMAT_VALUE)
				.addParam(TOKEN_KEY, token)
				.addParam(METHOD_KEY, METHOD_VALUE_GET_COURSES)
				.addParam(GET_COURSES_USERID_KEY, Integer.toString(userId))
				.toString();

		JsonCourse[] courses = null;
		try {
			final String coursesResponseString = client.getString(SERVICE_URL + coursesQueryParams, CHARSET);
			courses = new Gson().fromJson(coursesResponseString, JsonCourse[].class);
		} catch (Exception _) {
			return new MoodleCoursesResponse2(MoodleStatusCode2.NETWORK_ERROR, new ArrayList<MoodleCourse2>());
		}

		final MoodleCoursesResponse2 response = new MoodleCoursesResponse2(MoodleStatusCode2.OK, new ArrayList<MoodleCourse2>());
		for (final JsonCourse course : courses) {
			if (course.visible == VISIBLE) {
				response.addToCourses(new MoodleCourse2(course.id, course.fullname));
			}
		}

		return response;
	}

	@Override
	public MoodleCourseSectionsResponse2 getSections(MoodleCourseSectionsRequest2 request) {
		final String sciper = PocketCampusServer.authGetUserSciper(request);
		if (sciper == null) {
			// basic check, but it's not enough, see TODO in class javadoc
			return new MoodleCourseSectionsResponse2(MoodleStatusCode2.AUTHENTICATION_ERROR, new ArrayList<MoodleCourseSection2>());
		}

		// for visibility checks
		final DateTime now = DateTime.now();

		final String queryParams = new PostDataBuilder()
				.addParam(FORMAT_KEY, FORMAT_VALUE)
				.addParam(TOKEN_KEY, token)
				.addParam(METHOD_KEY, METHOD_VALUE_GET_SECTIONS)
				.addParam(GET_SECTIONS_COURSEID_KEY, Integer.toString(request.getCourseId()))
				.toString();

		JsonSection[] sections = null;
		try {
			final String responseString = client.getString(SERVICE_URL + queryParams, CHARSET);
			sections = new Gson().fromJson(responseString, JsonSection[].class);
		} catch (Exception _) {
			return new MoodleCourseSectionsResponse2(MoodleStatusCode2.NETWORK_ERROR, new ArrayList<MoodleCourseSection2>());
		}

		final MoodleCourseSectionsResponse2 response = new MoodleCourseSectionsResponse2(MoodleStatusCode2.OK, new ArrayList<MoodleCourseSection2>());

		for (final JsonSection section : sections) {
			if (section.visible == VISIBLE) {
				final MoodleCourseSection2 moodleSection = new MoodleCourseSection2(new ArrayList<MoodleResource2>());

				if (section.summary != null && section.summaryformat == SUMMARY_FORMAT_HTML) {
					moodleSection.setDetails(section.summary);
				}

				// Annoyingly, there's no way to know if the section title is a range of dates or not
				if (section.name.contains(SECTION_NAME_DATE_SEPARATOR)) {
					final String[] dates = section.name.split(SECTION_NAME_DATE_SEPARATOR);
					if (dates.length == 2) {
						try {
							final DateTime startDate = SECTION_NAME_DATE_FORMAT.parseDateTime(dates[0]);
							final DateTime endDate = SECTION_NAME_DATE_FORMAT.parseDateTime(dates[1]);

							moodleSection.setStartDate(startDate.getMillis());
							moodleSection.setEndDate(endDate.getMillis());
						} catch (IllegalArgumentException _) {
							// nothing; fall back to using the entire name
						}
					}
				}
				if (!moodleSection.isSetStartDate()) {
					moodleSection.setTitle(section.name);
				}

				for (final JsonSection.Module module : section.modules) {
					if (module.visible == VISIBLE
							&& (module.availablefrom == 0 || module.availablefrom * 1000 >= now.getMillis())
							&& (module.availableuntil == 0 || module.availableuntil * 1000 <= now.getMillis())) {
						if (module.modname == MODULE_FILE) {
							// The module name is more descriptive but doesn't always have an extension, which we need to display it nicely
							// The file name has one, but is less descriptive
							// Also, note that FilenameUtils returns extensions without the separator.
							String fileName = FilenameUtils.getBaseName(module.name);
							String fileExt = FilenameUtils.getExtension(module.contents[0].filename);
							String fullName = fileName + FilenameUtils.EXTENSION_SEPARATOR_STR + fileExt;

							MoodleFile2 file = new MoodleFile2(fullName, module.contents[0].fileurl);
							MoodleResource2 resource = new MoodleResource2().setFile(file);
							moodleSection.addToResources(resource);
						} else if (module.modname == MODULE_URL && module.contents.length == 1) {
							MoodleUrl2 url = new MoodleUrl2(module.name, module.contents[0].fileurl);
							MoodleResource2 resource = new MoodleResource2().setUrl(url);
							moodleSection.addToResources(resource);
						} else if (module.modname == MODULE_FOLDER) {
							MoodleFolder2 folder = new MoodleFolder2(module.name, new ArrayList<MoodleFile2>());
							for (final JsonSection.Module.Content content : module.contents) {
								// in this case the names have an extension
								MoodleFile2 file = new MoodleFile2(content.filename, content.fileurl);
								folder.addToFiles(file);
							}
							MoodleResource2 resource = new MoodleResource2().setFolder(folder);
							moodleSection.addToResources(resource);
						}
					}
				}

				if (moodleSection.getResourcesSize() > 0) {
					response.addToSections(moodleSection);
				}
			}
		}

		return response;
	}

	// Helper classes for JSON deserialization
	private static final class JsonUsersResponse {
		public JsonUser[] users;

		public static final class JsonUser {
			public int id;
		}
	}

	private static final class JsonCourse {
		public int id;
		public String fullname;
		public int visible;
	}

	private static final class JsonSection {
		public String name;
		public int visible;
		public String summary;
		public int summaryformat;
		public Module[] modules;

		public static final class Module {
			public String name;
			public int visible;
			public String modname;
			public int availablefrom;
			public int availableuntil;
			public Content[] contents;

			public static final class Content {
				public String filename;
				public String fileurl;
			}
		}
	}
}