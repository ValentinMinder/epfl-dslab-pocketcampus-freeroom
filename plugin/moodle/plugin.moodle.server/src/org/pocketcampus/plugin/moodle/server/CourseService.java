package org.pocketcampus.plugin.moodle.server;

import org.pocketcampus.plugin.moodle.shared.*;

/**
 * Courses service that fetches Moodle courses and sections.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public interface CourseService {
	MoodleCoursesResponse2 getCourses(MoodleCoursesRequest2 request);

	MoodleCourseSectionsResponse2 getSections(MoodleCourseSectionsRequest2 request);
}