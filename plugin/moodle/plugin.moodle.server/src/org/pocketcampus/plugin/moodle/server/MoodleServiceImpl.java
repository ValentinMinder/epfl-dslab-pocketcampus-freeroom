package org.pocketcampus.plugin.moodle.server;

import javax.servlet.http.*;

import org.apache.thrift.TException;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;
import org.pocketcampus.platform.launcher.server.RawPlugin;
import org.pocketcampus.platform.sdk.server.HttpClientImpl;
import org.pocketcampus.plugin.moodle.shared.*;

/**
 * Moodle service that fetches courses, sections and files.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public class MoodleServiceImpl implements MoodleService.Iface, RawPlugin {
	private final CourseService courseService;
	private final FileService fileService;

	public MoodleServiceImpl(CourseService courseService, FileService fileService) {
		this.courseService = courseService;
		this.fileService = fileService;
	}

	public MoodleServiceImpl() {
		this(new CourseServiceImpl(new HttpClientImpl(), PC_SRV_CONFIG.getString("MOODLE_ACCESS_TOKEN")), new FileServiceImpl());
	}

	@Override
	public MoodleCoursesResponse2 getCourses(MoodleCoursesRequest2 request) throws TException {
		return courseService.getCourses(request);
	}

	@Override
	public MoodleCourseSectionsResponse2 getSections(MoodleCourseSectionsRequest2 request) throws TException {
		return courseService.getSections(request);
	}

	@SuppressWarnings("serial")
	@Override
	public HttpServlet getServlet() {
		return new HttpServlet() {
			@Override
			protected void doPost(HttpServletRequest request, HttpServletResponse response) {
				fileService.download(request, response);
			}
		};
	}

	// OLD STUFF - DO NOT TOUCH

	private final org.pocketcampus.plugin.moodle.server.old.MoodleServiceImpl oldService =
			new org.pocketcampus.plugin.moodle.server.old.MoodleServiceImpl();

	@Override
	public TequilaToken getTequilaTokenForMoodle() throws TException {
		return oldService.getTequilaTokenForMoodle();
	}

	@Override
	public MoodleSession getMoodleSession(TequilaToken iTequilaToken) throws TException {
		return oldService.getMoodleSession(iTequilaToken);
	}

	@Override
	public CoursesListReply getCoursesList(MoodleRequest iRequest) throws TException {
		return oldService.getCoursesList(iRequest);
	}

	@Override
	public SectionsListReply getCourseSections(MoodleRequest iRequest) throws TException {
		return oldService.getCourseSections(iRequest);
	}

	@Override
	public CoursesListReply getCoursesListAPI(String dummy) throws TException {
		return oldService.getCoursesListAPI(dummy);
	}

	@Override
	public SectionsListReply getCourseSectionsAPI(String courseId) throws TException {
		return oldService.getCourseSectionsAPI(courseId);
	}
}