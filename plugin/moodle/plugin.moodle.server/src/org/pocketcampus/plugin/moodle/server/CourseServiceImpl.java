package org.pocketcampus.plugin.moodle.server;

import com.google.gson.Gson;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.pocketcampus.platform.server.Authenticator;
import org.pocketcampus.platform.server.HttpClient;
import org.pocketcampus.platform.shared.utils.PostDataBuilder;
import org.pocketcampus.plugin.moodle.shared.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;

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

	// Request all courses an user is enrolled in...
	private static final String METHOD_VALUE_GET_COURSES = "core_enrol_get_users_courses";
	// ...this time, with a much simpler selection method.
	private static final String GET_COURSES_USERID_KEY = "userid";

	// Request all sections of a course...
	private static final String METHOD_VALUE_GET_SECTIONS = "core_course_get_contents";
	// ...again, with a simple selection method
	private static final String GET_SECTIONS_COURSEID_KEY = "courseid";

	// For sections whose name is a date range, the separator between these dates and their format
	private static final String SECTION_NAME_DATE_SEPARATOR = " - ";
	private static final DateTimeFormatter SECTION_NAME_DATE_FORMAT =
			DateTimeFormat.forPattern("d MMMM")
					.withLocale(Locale.ENGLISH)
					.withDefaultYear(DateTime.now().getYear());

	// Replacement token for the size in a file icon URL
	private static final String FILE_ICON_SIZE = "-24";
	private static final String FILE_ICON_SIZE_TOKEN = "-{size}";

	// The module types
	private static final String MODULE_FILE = "resource";
	private static final String MODULE_URL = "url";
	private static final String MODULE_FOLDER = "folder";

	private final Authenticator authenticator;
	private final HttpClient client;
	private final String token;

	public CourseServiceImpl(final Authenticator authenticator, final HttpClient client, final String token) {
		this.authenticator = authenticator;
		this.client = client;
		this.token = token;
	}
	
	@Override
	public boolean checkPocketCampusUser() {
		final String usersQueryParams = new PostDataBuilder()
				.addParam(FORMAT_KEY, FORMAT_VALUE)
				.addParam(TOKEN_KEY, token)
				.addParam(METHOD_KEY, METHOD_VALUE_GET_USER)
				.addParam(GET_USER_CRITERION_KEY, "username")
				.addParam(GET_USER_SCIPER_KEY, "pocketcampus").toString();
		try {
			final String usersResponseString = client.get(SERVICE_URL + usersQueryParams, CHARSET);
			final JsonUsersResponse usersResponse = new Gson().fromJson(usersResponseString, JsonUsersResponse.class);
			return (usersResponse.users.length > 0);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public MoodleCoursesResponse2 getCourses(final MoodleCoursesRequest2 request) {
		final String sciper = authenticator.getSciper();
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
			final String usersResponseString = client.get(SERVICE_URL + usersQueryParams, CHARSET);
			final JsonUsersResponse usersResponse = new Gson().fromJson(usersResponseString, JsonUsersResponse.class);

			if (usersResponse.users.length == 0) {
				return new MoodleCoursesResponse2(MoodleStatusCode2.OK, new ArrayList<MoodleCourse2>());
			}

			userId = usersResponse.users[0].id;
		} catch (IOException e) {
			e.printStackTrace();
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
			final String coursesResponseString = client.get(SERVICE_URL + coursesQueryParams, CHARSET);
			courses = new Gson().fromJson(coursesResponseString, JsonCourse[].class);
		} catch (IOException e) {
			e.printStackTrace();
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
	public MoodleCourseSectionsResponse2 getSections(final MoodleCourseSectionsRequest2 request) {
		final String sciper = authenticator.getSciper();
		if (sciper == null) {
			// basic check, but it's not enough, see TODO in class javadoc
			return new MoodleCourseSectionsResponse2(MoodleStatusCode2.AUTHENTICATION_ERROR, new ArrayList<MoodleCourseSection2>());
		}

		final String queryParams = new PostDataBuilder()
				.addParam(FORMAT_KEY, FORMAT_VALUE)
				.addParam(TOKEN_KEY, token)
				.addParam(METHOD_KEY, METHOD_VALUE_GET_SECTIONS)
				.addParam(GET_SECTIONS_COURSEID_KEY, Integer.toString(request.getCourseId()))
				.toString();

		JsonSection[] sections = null;
		try {
			final String responseString = client.get(SERVICE_URL + queryParams, CHARSET);
			sections = new Gson().fromJson(responseString, JsonSection[].class);
		} catch (IOException e) {
			e.printStackTrace();
			return new MoodleCourseSectionsResponse2(MoodleStatusCode2.NETWORK_ERROR, new ArrayList<MoodleCourseSection2>());
		}

		// for visibility checks
		final DateTime now = DateTime.now();

		final MoodleCourseSectionsResponse2 response = new MoodleCourseSectionsResponse2(MoodleStatusCode2.OK, new ArrayList<MoodleCourseSection2>());

		for (final JsonSection section : sections) {
			if (section.visible == VISIBLE) {
				final MoodleCourseSection2 moodleSection = new MoodleCourseSection2(new ArrayList<MoodleResource2>());

				if (section.summary != null && section.summaryformat == SUMMARY_FORMAT_HTML) {
					moodleSection.setDetails(section.summary);
				}

				// Annoyingly, there's no way to know if the section title is a range of dates or not
				// TODO: Make sure that the dates are always in this format/language
				if (section.name.contains(SECTION_NAME_DATE_SEPARATOR)) {
					final String[] dates = section.name.split(SECTION_NAME_DATE_SEPARATOR);
					if (dates.length == 2) {
						try {
							final DateTime startDate = SECTION_NAME_DATE_FORMAT.parseDateTime(dates[0]);
							final DateTime endDate = SECTION_NAME_DATE_FORMAT.parseDateTime(dates[1]);

							moodleSection.setStartDate(startDate.getMillis());
							moodleSection.setEndDate(endDate.getMillis());
						} catch (IllegalArgumentException e) {
							// nothing; fall back to using the entire name
						}
					}
				}
				if (!moodleSection.isSetStartDate()) {
					moodleSection.setTitle(section.name);
				}

				for (final JsonSection.Module module : section.modules) {
					if (module == null // Gson doing weird things, I guess...
							|| module.visible != VISIBLE
							|| (module.availablefrom != 0 && module.availablefrom * 1000 > now.getMillis())
							|| (module.availableuntil != 0 && module.availableuntil * 1000 < now.getMillis())) {
						continue;
					}

					// Moodle escapes the names for no reason...
					String moduleName = StringEscapeUtils.unescapeHtml4(module.name);

					// > 0 rather than == 1 for file and URL because Moodle allows multiple files inside a file...
					if (module.modname.equals(MODULE_FILE) && module.contents != null && module.contents.length > 0) {
						final String name = FilenameUtils.removeExtension(moduleName);
						final String extension = FilenameUtils.getExtension(module.contents[0].filename);
						final String iconUrl = module.modicon.replace(FILE_ICON_SIZE, FILE_ICON_SIZE_TOKEN);

						final MoodleFile2 file = new MoodleFile2(name, extension, module.contents[0].fileurl).setIcon(iconUrl);
						moodleSection.addToResources(new MoodleResource2().setFile(file));
					} else if (module.modname.equals(MODULE_URL) && module.contents != null && module.contents.length > 0) {
						// TODO some legit URL modules don't have the contents field and encode the url in a "url" field
						// e.g. http://moodle.epfl.ch/mod/url/view.php?id=870185
						// we should get the actual url from pinging this moodle url
						// for now, we ignore such urls
						final MoodleUrl2 url = new MoodleUrl2(moduleName, module.contents[0].fileurl);
						moodleSection.addToResources(new MoodleResource2().setUrl(url));
					} else if (module.modname.equals(MODULE_FOLDER) && module.contents != null) {
						final MoodleFolder2 folder = new MoodleFolder2(moduleName, new ArrayList<MoodleFile2>());
						for (final JsonSection.Module.Content content : module.contents) {
							// in this case the names have an extension
							final String name = FilenameUtils.getBaseName(content.filename);
							final String extension = FilenameUtils.getExtension(content.filename);
							folder.addToFiles(new MoodleFile2(name, extension, content.fileurl));
						}
						if (folder.getFilesSize() > 0) {
							moodleSection.addToResources(new MoodleResource2().setFolder(folder));
						}
					}
				}

				if (moodleSection.getResourcesSize() > 0 || moodleSection.getDetails() != null) {
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
			public String modicon;
			public long availablefrom;
			public long availableuntil;
			public Content[] contents;

			public static final class Content {
				public String filename;
				public String fileurl;
			}
		}
	}
}