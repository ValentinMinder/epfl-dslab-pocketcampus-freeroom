package org.pocketcampus.plugin.isacademia.server.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.http.cookie.Cookie;

import org.pocketcampus.plugin.isacademia.server.*;
import org.pocketcampus.plugin.isacademia.shared.*;

import org.joda.time.*;

/**
 * Tests for ScheduleImpl.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class ScheduleTests {
	// All days are taken into account (even Monday which isn't in the file)
	@Test
	public void allDaysAreParsed() {
		List<StudyDay> days = getDays("fr");

		assertEquals(5, days.size());
		assertEquals(new DateTime(2013, 10, 14, 00, 00, 00).getMillis(), days.get(0).getDay());
		assertEquals(new DateTime(2013, 10, 15, 00, 00, 00).getMillis(), days.get(1).getDay());
		assertEquals(new DateTime(2013, 10, 16, 00, 00, 00).getMillis(), days.get(2).getDay());
		assertEquals(new DateTime(2013, 10, 17, 00, 00, 00).getMillis(), days.get(3).getDay());
		assertEquals(new DateTime(2013, 10, 18, 00, 00, 00).getMillis(), days.get(4).getDay());
	}

	// Simple lecture, one room
	@Test
	public void simpleLecture() {
		StudyPeriod period = getDays("fr").get(1).getPeriods().get(1);

		assertEquals("Architecture des ordinateurs I", period.getName());
		assertEquals(StudyPeriodType.LECTURE, period.getPeriodType());
		assertEquals(new DateTime(2013, 10, 15, 13, 15, 00), new DateTime(period.getStartTime()));
		assertEquals(new DateTime(2013, 10, 15, 15, 00, 00), new DateTime(period.getEndTime()));
		assertEquals(Arrays.asList("CO 3"), period.getRooms());
	}

	// Exercise session, two rooms
	@Test
	public void exerciseSessionWithManyRooms() {
		StudyPeriod period = getDays("fr").get(1).getPeriods().get(0);

		assertEquals(Arrays.asList("INF 2", "BC 07-08"), period.getRooms());
	}

	// English works, too
	@Test
	public void englishWorks() {
		StudyPeriod period = getDays("en").get(1).getPeriods().get(2);

		assertEquals("State and human rights", period.getName());
	}

	private static List<StudyDay> getDays(String lang) {
		try {
			return new ScheduleImpl(new TestHttpsClient()).get(new LocalDate(2013, 10, 14), lang, new ScheduleToken()).getDays();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static final class TestHttpsClient implements HttpsClient {
		private static final String RETURN_VALUE = getFileContents("ExampleSchedule.xml");

		@Override
		public HttpResult get(String url, Charset charset, List<Cookie> cookies) throws Exception {
			return new HttpResult(new ArrayList<Cookie>(), url, RETURN_VALUE);
		}

		@SuppressWarnings("resource")
		private static String getFileContents(String name) {
			Scanner s = null;

			try {
				InputStream stream = new ScheduleTests().getClass().getResourceAsStream(name);
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

				// smart trick from http://stackoverflow.com/a/5445161
				s = new Scanner(reader).useDelimiter("\\A");
				return s.hasNext() ? s.next() : "";
			} finally {
				if (s != null) {
					s.close();
				}
			}
		}
	}
}