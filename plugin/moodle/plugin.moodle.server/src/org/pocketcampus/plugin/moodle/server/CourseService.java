package org.pocketcampus.plugin.moodle.server;

import org.pocketcampus.plugin.moodle.shared.MoodleCourseSectionsRequest2;
import org.pocketcampus.plugin.moodle.shared.MoodleCourseSectionsResponse2;
import org.pocketcampus.plugin.moodle.shared.MoodleCoursesRequest2;
import org.pocketcampus.plugin.moodle.shared.MoodleCoursesResponse2;

/**
 * Courses service that fetches Moodle courses and sections.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public interface CourseService {
	MoodleCoursesResponse2 getCourses(final MoodleCoursesRequest2 request);

	MoodleCourseSectionsResponse2 getSections(final MoodleCourseSectionsRequest2 request);

	boolean checkPocketCampusUser();
}