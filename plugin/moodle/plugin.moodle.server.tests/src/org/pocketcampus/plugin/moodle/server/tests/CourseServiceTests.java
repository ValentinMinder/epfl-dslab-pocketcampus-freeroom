package org.pocketcampus.plugin.moodle.server.tests;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.pocketcampus.platform.server.Authenticator;
import org.pocketcampus.platform.server.HttpClient;
import org.pocketcampus.plugin.moodle.server.CourseService;
import org.pocketcampus.plugin.moodle.server.CourseServiceImpl;
import org.pocketcampus.plugin.moodle.shared.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Tests for CourseServiceImpl.
 * 
 * @author Solal Pirelli <solal.pirelli@gmail.com>
 */
public final class CourseServiceTests {
	@Test
	public void coursesAreNotFetchedWithoutAuth() {
		HttpClient client = new TestHttpClient("TestUserList.json", "TestCourseList.json");
		CourseService service = new CourseServiceImpl(new TestAuthenticator(null), client, "Token");

		MoodleCoursesResponse2 response = service.getCourses(new MoodleCoursesRequest2("fr"));

		assertEquals("The status code should be AUTHENTICATION_ERROR since the sciper is null.",
				MoodleStatusCode2.AUTHENTICATION_ERROR, response.getStatusCode());
	}

	@Test
	public void coursesAreFetched() {
		HttpClient client = new TestHttpClient("TestUserList.json", "TestCourseList.json");
		CourseService service = new CourseServiceImpl(new TestAuthenticator("123"), client, "Token");

		MoodleCoursesResponse2 response = service.getCourses(new MoodleCoursesRequest2("fr"));

		assertEquals("The status code should be OK.",
				MoodleStatusCode2.OK, response.getStatusCode());
		assertTrue("There should be courses in the returned list.",
				response.getCoursesSize() > 0);

		MoodleCourse2 course = response.getCourses().get(0);

		assertEquals("The course ID should be parsed correctly.",
				14174, course.getCourseId());
		assertEquals("The course name should be parsed correctly.",
				"Theory of computation", course.getName());
	}

	@Test
	public void invisibleCoursesAreHidden() {
		HttpClient client = new TestHttpClient("TestUserList.json", "TestCourseList.json");
		CourseService service = new CourseServiceImpl(new TestAuthenticator("123"), client, "Token");

		MoodleCoursesResponse2 response = service.getCourses(new MoodleCoursesRequest2("fr"));

		assertEquals("The number of courses should be 5. (6, minus 1 hidden)",
				5, response.getCoursesSize());

		for (MoodleCourse2 course : response.getCourses()) {
			assertFalse("No course should have ID 14153 since that course is hidden.",
					course.getCourseId() == 14153);
		}
	}

	@Test
	public void sectionsAreNotFetchedWithoutAuth() {
		HttpClient client = new TestHttpClient("TestSectionList.json");
		CourseService service = new CourseServiceImpl(new TestAuthenticator(null), client, "Token");

		MoodleCourseSectionsResponse2 response = service.getSections(new MoodleCourseSectionsRequest2("fr", 12345));

		assertEquals("The status code should be AUTHENTICATION_ERROR since the sciper is null.",
				MoodleStatusCode2.AUTHENTICATION_ERROR, response.getStatusCode());
	}

	@Test
	public void sectionsAreFetched() {
		HttpClient client = new TestHttpClient("TestSectionList.json");
		CourseService service = new CourseServiceImpl(new TestAuthenticator("123"), client, "Token");

		MoodleCourseSectionsResponse2 response = service.getSections(new MoodleCourseSectionsRequest2("fr", 123));

		assertEquals("The status code should be OK.",
				MoodleStatusCode2.OK, response.getStatusCode());
		assertTrue("There should be sections in the returned list.",
				response.getSectionsSize() > 0);
	}

	public void invisibleSectionsAreHidden() {
		HttpClient client = new TestHttpClient("TestSectionList.json");
		CourseService service = new CourseServiceImpl(new TestAuthenticator("123"), client, "Token");

		MoodleCourseSectionsResponse2 response = service.getSections(new MoodleCourseSectionsRequest2("fr", 123));

		assertEquals("There should be 15 sections in the returned list. (16, -1 hidden)",
				15, response.getSectionsSize());

		for (MoodleCourseSection2 section : response.getSections()) {
			assertFalse("The hidden section should not be displayed. (the name is checked)",
					"This should be hidden".equals(section.getTitle()));
		}
	}

	@Test
	public void sectionPropertiesAreFetched() {
		HttpClient client = new TestHttpClient("TestSectionList.json");
		CourseService service = new CourseServiceImpl(new TestAuthenticator("123"), client, "Token");

		MoodleCourseSectionsResponse2 response = service.getSections(new MoodleCourseSectionsRequest2("fr", 123));
		MoodleCourseSection2 section = response.getSections().get(0);

		assertEquals(
				"The section summary should be parsed correctly. (since it's HTML)",
				"<p><strong>Professeur : <a href=\"http://people.epfl.ch/peter.wittwer\" target=\"_blank\">Peter Wittwer</a></strong></p>\r\n<p><strong><br />Assistants :<br /></strong>Linda Frossard, MA : séries d'exercices, moodle.<br />Amos Sironi, Eduard Baranov, Kevin Barbieux, Volodymyr Kuznetsov, Farid Movahedi Naini, Jonas Wagner, Onur Yuruten, IN/SC : chefs des salles, tuteurs.</p>\r\n<p><br /><strong>Cours :</strong><br />lundi, 08h15 – 10h00, auditoire <a href=\"http://plan.epfl.ch/?lang=fr&amp;room=ce6\" target=\"_blank\"><span style=\"color: #0000ff;\">CE6</span></a><br />mercredi, 10h15 – 12h00, auditoire <a href=\"http://plan.epfl.ch/?lang=fr&amp;room=auditoire+co1\" target=\"_blank\"><span style=\"color: #0000ff;\">CO1</span></a><br /><br /><br /><strong>Exercices : </strong><br />mercredi, 15h15 – 17h00</p>\r\n<p><em style=\"font-size: 13px; line-height: 1.4;\">Attribution des assistants/tuteurs aux salles :</em></p>\r\n<p><strong>Salle 1 :</strong> <span style=\"color: #0000ff; text-decoration: line-through;\">CM 013</span> <strong><span style=\"color: #ff0000;\"> ⇒ </span><a href=\"http://plan.epfl.ch/?lang=fr&amp;room=CO+122\" target=\"_blank\"><span style=\"color: #ff0000;\">CO 122</span></a></strong> <span style=\"color: #ff0000;\">à partir du 24 mars 2014</span><br /> <strong>Salle 2 : <a href=\"http://plan.epfl.ch/?lang=fr&amp;room=CM+1221\" target=\"_blank\"><span style=\"color: #0000ff;\">CM 1221</span></a></strong> [3: <strong>Amos Sironi et</strong> <strong>Eduard Baranov</strong>, Billal Mahoubi, David Compain]<br /> <strong>Salle 3 : <a href=\"http://plan.epfl.ch/?lang=fr&amp;room=CO+016\" target=\"_blank\"><span style=\"color: #0000ff;\">CO 016</span></a></strong> [3: <strong>Kevin Barbieux</strong>, Guillaume Gin, Jean-Baptiste Cordonnier]<br /> <strong>Salle 4 : <a href=\"http://plan.epfl.ch/?lang=fr&amp;room=CO+017\" target=\"_blank\"><span style=\"color: #0000ff;\">CO 017</span></a></strong> [2: <strong>Volodymyr Kuznetsov</strong>, Mohammed Siwar]<br /> <strong>Salle 5 : <a href=\"http://plan.epfl.ch/?lang=fr&amp;room=CO+122\" target=\"_blank\"><span style=\"color: #0000ff;\">CO 122</span></a></strong> [3: <strong>Farid Movahedi Naini</strong>, Hugo Bordes, Othmane Tamri]<br /> <strong>Salle 6 : <a href=\"http://plan.epfl.ch/?lang=fr&amp;room=CO+123\" target=\"_blank\"><span style=\"color: #0000ff;\">CO 123</span></a></strong> [2: <strong>Jonas Wagner</strong>, Simon-Pierre Genot]<br /> <strong>Salle 7 : <a href=\"http://plan.epfl.ch/?lang=fr&amp;room=CO+124\" target=\"_blank\"><span style=\"color: #0000ff;\">CO 124</span></a></strong> [3: <strong>Onur Yuruten</strong>, Tarik Ouhmad, Zhivka Gucevska]</p>\r\n<p><em style=\"font-size: 13px; line-height: 1.4;\">Attribution des étudiants aux salles<em> (<span style=\"text-decoration: underline;\">selon nom de famille</span>)</em> :</em></p>\r\n<p><strong>Salle 1 : <a href=\"http://plan.epfl.ch/?lang=fr&amp;room=CM+013\" target=\"_blank\"><span style=\"color: #0000ff;\">CM 013</span></a></strong> Abassi – Boucaud<br /> <strong>Salle 2 : <a href=\"http://plan.epfl.ch/?lang=fr&amp;room=CM+1221\" target=\"_blank\"><span style=\"color: #0000ff;\">CM 1221</span></a></strong> Boudabous – Deloche<br /> <strong>Salle 3 : <a href=\"http://plan.epfl.ch/?lang=fr&amp;room=CO+016\" target=\"_blank\"><span style=\"color: #0000ff;\">CO 016</span></a></strong> Demierre – Hulliger<br /> <strong>Salle 4 : <a href=\"http://plan.epfl.ch/?lang=fr&amp;room=CO+017\" target=\"_blank\"><span style=\"color: #0000ff;\">CO 017</span></a></strong> Hüsler – Lamour<br /> <strong>Salle 5 : <a href=\"http://plan.epfl.ch/?lang=fr&amp;room=CO+122\" target=\"_blank\"><span style=\"color: #0000ff;\">CO 122</span></a></strong> Latécoère – Nickl<br /> <strong>Salle 6 : <a href=\"http://plan.epfl.ch/?lang=fr&amp;room=CO+123\" target=\"_blank\"><span style=\"color: #0000ff;\">CO 123</span></a></strong> Nicolet – Rosset<br /> <strong>Salle 7 : <a href=\"http://plan.epfl.ch/?lang=fr&amp;room=CO+124\" target=\"_blank\"><span style=\"color: #0000ff;\">CO 124</span></a></strong> Rossier – Zimmermann<br /><br /><br /></p>\r\n<p><strong><span style=\"color: #008000;\">Des polycopiés complets se trouvent ci-contre à droite.</span></strong></p>",
				section.getDetails());
	}

	@Test
	public void sectionTitleIsSetWhenNotADate() {
		HttpClient client = new TestHttpClient("TestSectionList.json");
		CourseService service = new CourseServiceImpl(new TestAuthenticator("123"), client, "Token");

		MoodleCourseSectionsResponse2 response = service.getSections(new MoodleCourseSectionsRequest2("fr", 123));
		MoodleCourseSection2 section = response.getSections().get(0);

		assertEquals("The section title should be parsed correctly. (since it's not a date range)",
				"General", section.getTitle());
		assertFalse("The section start date should not be set.",
				section.isSetStartDate());
		assertFalse("The section end date should not be set.",
				section.isSetEndDate());
	}

	@Test
	public void sectionDatesAreSetWhenNeeded() {
		HttpClient client = new TestHttpClient("TestSectionList.json");
		CourseService service = new CourseServiceImpl(new TestAuthenticator("123"), client, "Token");

		MoodleCourseSectionsResponse2 response = service.getSections(new MoodleCourseSectionsRequest2("fr", 123));
		MoodleCourseSection2 section = response.getSections().get(1);

		assertFalse("The section title should not be set. (since it's a date range)",
				section.isSetTitle());
		assertEquals("The section start date should be set.",
				LocalDate.now().withDayOfMonth(17).withMonthOfYear(2).toDateTimeAtStartOfDay().getMillis(),
				section.getStartDate());
		assertEquals("The section end date should be set.",
				LocalDate.now().withDayOfMonth(23).withMonthOfYear(2).toDateTimeAtStartOfDay().getMillis(),
				section.getEndDate());
	}

	@Test
	public void resourcesAreFetched() {
		HttpClient client = new TestHttpClient("TestSectionList.json");
		CourseService service = new CourseServiceImpl(new TestAuthenticator("123"), client, "Token");

		MoodleCourseSectionsResponse2 response = service.getSections(new MoodleCourseSectionsRequest2("fr", 123));
		MoodleCourseSection2 section = response.getSections().get(0);

		assertEquals("The resource count should be 2. (3 modules, minus 1 label)",
				2, section.getResourcesSize());
	}

	@Test
	public void filePropertiesAreFetched() {
		HttpClient client = new TestHttpClient("TestSectionList.json");
		CourseService service = new CourseServiceImpl(new TestAuthenticator("123"), client, "Token");

		MoodleCourseSectionsResponse2 response = service.getSections(new MoodleCourseSectionsRequest2("fr", 123));
		MoodleCourseSection2 section = response.getSections().get(0);
		MoodleResource2 resource = section.getResources().get(0);

		assertTrue("The first resource should be a file.",
				resource.isSetFile());
		assertFalse("The first resource should not be an URL.",
				resource.isSetUrl());
		assertFalse("The first resource should not be a folder.",
				resource.isSetFolder());
		assertEquals("The first resource's name should be fetched properly.",
				"Attribution Aux Salles Examen Analyse II",
				resource.getFile().getName());
		assertEquals("the first resource's extension should be fetched properly",
				"pdf",
				resource.getFile().getExtension());
		assertEquals("The first resource's URL should be fetched properly.",
				"http://moodle.epfl.ch/webservice/pluginfile.php/1570918/mod_resource/content/1/AttributionAuxSallesExamenAnalyseII.pdf?forcedownload=1",
				resource.getFile().getUrl());
		assertEquals("The first resource's icon URL should be fetched properly and the token should be inserted.",
				"http://moodle.epfl.ch/theme/image.php/epfl_sb/core/1377260229/f/pdf-{size}",
				resource.getFile().getIcon());
	}

	@Test
	public void urlPropertiesAreFetched() {
		HttpClient client = new TestHttpClient("TestSectionList.json");
		CourseService service = new CourseServiceImpl(new TestAuthenticator("123"), client, "Token");

		MoodleCourseSectionsResponse2 response = service.getSections(new MoodleCourseSectionsRequest2("fr", 123));
		MoodleCourseSection2 section = response.getSections().get(2);
		MoodleResource2 resource = section.getResources().get(4);

		assertTrue("The fourth resource should be an URL.",
				resource.isSetUrl());
		assertFalse("The fourth resource should not be a file.",
				resource.isSetFile());
		assertFalse("The fourth resource should not be a folder.",
				resource.isSetFolder());
		assertEquals("The fourth resource's name should be fetched properly.",
				"Accès au calendrier, supports de cours et exercices",
				resource.getUrl().getName());
		assertEquals("The fourth resource's URL should be fetched properly.",
				"http://progos.epfl.ch/moodle-pointofentry.html",
				resource.getUrl().getUrl());
	}

	@Test
	public void foldersAreFetched() {
		HttpClient client = new TestHttpClient("TestSectionList.json");
		CourseService service = new CourseServiceImpl(new TestAuthenticator("123"), client, "Token");

		MoodleCourseSectionsResponse2 response = service.getSections(new MoodleCourseSectionsRequest2("fr", 123));
		MoodleCourseSection2 section = response.getSections().get(5);
		MoodleResource2 resource = section.getResources().get(2);

		assertTrue("The third resource should be a folder.",
				resource.isSetFolder());
		assertFalse("The third resource should not be a file.",
				resource.isSetFile());
		assertFalse("The third resource should not be an URL.",
				resource.isSetUrl());
		assertEquals("The third resource's name should be fetched properly.",
				"Fichiers .bmp de la semaine 5",
				resource.getFolder().getName());

		MoodleFile2 file = resource.getFolder().getFiles().get(0);

		assertEquals("The name of the first file in the folder should be fetched properly.",
				"convergence",
				file.getName());
		assertEquals("The extension of the first file in the folder should be fetched properly.",
				"bmp",
				file.getExtension());
		assertEquals("The URL of first file in the folder should be fetched properly.",
				"http://moodle.epfl.ch/webservice/pluginfile.php/1562364/mod_folder/content/2/convergence.bmp?forcedownload=1",
				file.getUrl());
	}

	@Test
	public void invisibleResourcesAreHidden() {
		HttpClient client = new TestHttpClient("TestSectionList.json");
		CourseService service = new CourseServiceImpl(new TestAuthenticator("123"), client, "Token");

		MoodleCourseSectionsResponse2 response = service.getSections(new MoodleCourseSectionsRequest2("fr", 123));
		MoodleCourseSection2 section = response.getSections().get(1);

		assertEquals("The resource count should be 1. (3 files, minus 2 hidden ones)",
				1, section.getResourcesSize());

		assertEquals("The only resource should be a file with the name 'Update_Semaine_01_14'.",
				"Update_Semaine_01_14", section.getResources().get(0).getFile().getName());
	}

	// THIS IS REALLY IMPORTANT
	// DO NOT EVER DEPLOY IF THIS DOES NOT PASS
	// It ensures that the "availablefrom" and "availableuntil" timestamps are respected; they're used to hide e.g. corrections of graded homeworks.
	@Test
	public void VERY_IMPORTANT_unavailableResourcesAreHidden() {
		HttpClient client = new TestHttpClient("TestSectionList.json");
		CourseService service = new CourseServiceImpl(new TestAuthenticator("123"), client, "Token");

		MoodleCourseSectionsResponse2 response = service.getSections(new MoodleCourseSectionsRequest2("fr", 123));
		MoodleCourseSection2 section = response.getSections().get(3);

		assertEquals("The resource count should be 1. (4 files, minus 3 unavailable ones)",
				1, section.getResourcesSize());

		assertEquals("Only one file should be there. (checked by name)",
				"Serie02v04", section.getResources().get(0).getFile().getName());
	}

	private static final class TestHttpClient implements HttpClient {
		private final String[] returnValues;
		private int index;

		public TestHttpClient(String... returnValues) {
			this.returnValues = returnValues;
			this.index = 0;
		}

		@Override
		public String get(String url, Charset charset) throws IOException {
			return getFileContents(returnValues[index++]);
		}

		@Override
		public String post(String url, byte[] body, Charset charset) throws IOException {
			throw new RuntimeException("post(String, byte[], Charset) should not be called.");
		}

		@SuppressWarnings("resource")
		private static String getFileContents(String name) {
			Scanner s = null;

			try {
				InputStream stream = new TestHttpClient().getClass().getResourceAsStream(name);
				s = new Scanner(stream, "UTF-8").useDelimiter("\\A");
				return s.hasNext() ? s.next() : "";
			} finally {
				if (s != null) {
					s.close();
				}
			}
		}
	}

	private static final class TestAuthenticator implements Authenticator {
		private final String sciper;

		public TestAuthenticator(String sciper) {
			this.sciper = sciper;
		}

		@Override
		public String getSciper() {
			return sciper;
		}

		@Override
		public String getGaspar() {
			return null;
		}
	}
}