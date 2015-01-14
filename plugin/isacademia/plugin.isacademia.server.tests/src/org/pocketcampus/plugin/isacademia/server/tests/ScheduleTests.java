package org.pocketcampus.plugin.isacademia.server.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.pocketcampus.plugin.isacademia.server.HttpsClient;
import org.pocketcampus.plugin.isacademia.server.ScheduleImpl;
import org.pocketcampus.plugin.isacademia.shared.IsaStatusCode;
import org.pocketcampus.plugin.isacademia.shared.ScheduleResponse;
import org.pocketcampus.plugin.isacademia.shared.StudyDay;
import org.pocketcampus.plugin.isacademia.shared.StudyPeriod;
import org.pocketcampus.plugin.isacademia.shared.StudyPeriodType;

/**
 * Tests for ScheduleImpl.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class ScheduleTests {
	private static final DateTimeZone ISA_TIME_ZONE = DateTimeZone.forID("Europe/Zurich");

	// All working week days are there (even Monday which isn't in the file)
	@Test
	public void allDaysAreParsed() {
		List<StudyDay> days = getDays("fr");

		assertEquals(5, days.size());
		assertEquals(new LocalDate(2013, 10, 14).toDateTimeAtStartOfDay(ISA_TIME_ZONE).getMillis(),
				days.get(0).getDay());
		assertEquals(new LocalDate(2013, 10, 15).toDateTimeAtStartOfDay(ISA_TIME_ZONE).getMillis(),
				days.get(1).getDay());
		assertEquals(new LocalDate(2013, 10, 16).toDateTimeAtStartOfDay(ISA_TIME_ZONE).getMillis(),
				days.get(2).getDay());
		assertEquals(new LocalDate(2013, 10, 17).toDateTimeAtStartOfDay(ISA_TIME_ZONE).getMillis(),
				days.get(3).getDay());
		assertEquals(new LocalDate(2013, 10, 18).toDateTimeAtStartOfDay(ISA_TIME_ZONE).getMillis(),
				days.get(4).getDay());
	}

	// Simple lecture, one room
	@Test
	public void simpleLecture() {
		StudyPeriod period = getDays("fr").get(1).getPeriods().get(1);

		assertEquals("Architecture des ordinateurs I", period.getName());
		assertEquals(StudyPeriodType.LECTURE, period.getPeriodType());
		assertEquals(new DateTime(2013, 10, 15, 13, 15, 00, ISA_TIME_ZONE), new DateTime(period.getStartTime(), ISA_TIME_ZONE));
		assertEquals(new DateTime(2013, 10, 15, 15, 00, 00, ISA_TIME_ZONE), new DateTime(period.getEndTime(), ISA_TIME_ZONE));
		assertEquals(Arrays.asList("CO 3"), period.getRooms());
	}

	// Exercise session, two rooms
	@Test
	public void exerciseSessionWithManyRooms() {
		StudyPeriod period = getDays("fr").get(1).getPeriods().get(0);

		assertEquals(Arrays.asList("INF 2", "BC 07", "BC 08"), period.getRooms());
	}

	// English works, too
	@Test
	public void englishWorks() {
		StudyPeriod period = getDays("en").get(1).getPeriods().get(2);

		assertEquals("State and human rights", period.getName());
	}

	// ISA Error returns the right error code
	@Test
	public void isaErrorIsUnderstood() {
		try {
			ScheduleResponse response = new ScheduleImpl(new TestHttpsClient("<error message=\"Connection error to ISA (no cookie)\"/>"))
					.get(new LocalDate(2013, 10, 14), "fr", "");

			assertEquals(IsaStatusCode.ISA_ERROR, response.getStatusCode());
		} catch (Exception e) {
			fail();
		}
	}

	private static List<StudyDay> getDays(String lang) {
		try {
			return new ScheduleImpl(new TestHttpsClient(getFileContents("ExampleSchedule.xml"))).get(new LocalDate(2013, 10, 14), lang, "").getDays();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String getFileContents(String name) {
		try {
			InputStream stream = new ScheduleTests().getClass().getResourceAsStream(name);
			return IOUtils.toString(stream, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final class TestHttpsClient implements HttpsClient {
		private final String _returnValue;

		public TestHttpsClient(String returnValue) {
			_returnValue = returnValue;
		}

		@Override
		public String get(String url, Charset charset) {
			return _returnValue;
		}
	}
}