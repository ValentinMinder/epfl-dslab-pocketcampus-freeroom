package org.pocketcampus.plugin.moodle.server;

import org.apache.thrift.TException;
import org.pocketcampus.platform.server.HttpClientImpl;
import org.pocketcampus.platform.server.RawPlugin;
import org.pocketcampus.platform.server.StateChecker;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.plugin.authentication.server.AuthenticatorImpl;
import org.pocketcampus.plugin.moodle.shared.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Moodle service that fetches courses, sections and files.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public class MoodleServiceImpl implements MoodleService.Iface, RawPlugin, StateChecker {
	private static final String MOODLE_ACCESS_TOKEN = PocketCampusServer.CONFIG.getString("MOODLE_ACCESS_TOKEN");
	
	private final CourseService courseService;
	private final FileService fileService;
	private final PrintFileService printService;

	public MoodleServiceImpl(final CourseService courseService, final FileService fileService, final PrintFileService printService) {
		this.courseService = courseService;
		this.fileService = fileService;
		this.printService = printService;
	}

	public MoodleServiceImpl() {
		this(new CourseServiceImpl(new AuthenticatorImpl(), new HttpClientImpl(), MOODLE_ACCESS_TOKEN),
			 new FileServiceImpl(MOODLE_ACCESS_TOKEN), new PrintFileServiceImpl(new AuthenticatorImpl(), 
					 MOODLE_ACCESS_TOKEN));
	}

	@Override
	public MoodleCoursesResponse2 getCourses(MoodleCoursesRequest2 request) throws TException {
		return courseService.getCourses(request);
	}

	@Override
	public MoodleCourseSectionsResponse2 getSections(MoodleCourseSectionsRequest2 request) throws TException {
		return courseService.getSections(request);
	}
	
	@Override
	public MoodlePrintFileResponse2 printFile(MoodlePrintFileRequest2 request) throws TException {
		return printService.printFile(request);
	}

	@SuppressWarnings("serial")
	@Override
	public HttpServlet getServlet() {
		return new HttpServlet() {
			@Override
			protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
				fileService.download(request, response);
			}
		};
	}
	
	@Override
	public int checkState() throws IOException {
		return (courseService.checkPocketCampusUser() ? 200 : 500 );
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