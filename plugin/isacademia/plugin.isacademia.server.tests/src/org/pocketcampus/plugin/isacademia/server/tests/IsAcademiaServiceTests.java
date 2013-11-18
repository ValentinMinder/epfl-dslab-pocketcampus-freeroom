package org.pocketcampus.plugin.isacademia.server.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

import org.pocketcampus.plugin.isacademia.server.*;
import org.pocketcampus.plugin.isacademia.shared.*;

import org.joda.time.*;

/**
 * Tests for IsAcademiaServiceImpl.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class IsAcademiaServiceTests {
	@Test
	public void defaultValueOfLanguageIsFrench() throws Exception {
		final Flag hit = new Flag();
		Schedule schedule = new Schedule() {
			@Override
			public List<StudyDay> get(LocalDate weekBeginning, String language, String tequilaCookie) throws ScheduleException {
				hit.set();
				assertEquals(language, "fr");

				return new ArrayList<StudyDay>();
			}
		};

		ScheduleRequest req = new ScheduleRequest("");
		new IsAcademiaServiceImpl(schedule).getSchedule(req);

		hit.assertIsSet();
	}

	@Test
	public void defaultValueOfWeekStartIsCurrentWeekStart() throws Exception {
		final Flag hit = new Flag();
		Schedule schedule = new Schedule() {
			@Override
			public List<StudyDay> get(LocalDate weekBeginning, String language, String tequilaCookie) throws ScheduleException {
				hit.set();
				assertEquals(weekBeginning, new LocalDate(2013, 11, 11));

				return new ArrayList<StudyDay>();
			}
		};

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 11, 11, 00, 00, 00).getMillis());
		ScheduleRequest req = new ScheduleRequest("");
		new IsAcademiaServiceImpl(schedule).getSchedule(req);

		hit.assertIsSet();
	}

	@Test
	public void parametersAreTransferredCorrectly() throws Exception {
		final Flag hit = new Flag();
		Schedule schedule = new Schedule() {
			@Override
			public List<StudyDay> get(LocalDate weekBeginning, String language, String tequilaCookie) throws ScheduleException {
				hit.set();
				assertEquals(weekBeginning, new LocalDate(2013, 11, 4));
				assertEquals(language, "en");
				assertEquals(tequilaCookie, "12345");

				return new ArrayList<StudyDay>();
			}
		};

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 11, 4, 00, 00, 00).getMillis());
		ScheduleRequest req = new ScheduleRequest("12345").setWeekStart(new DateTime(2013, 11, 4, 00, 00, 00).getMillis()).setLanguage("en");
		new IsAcademiaServiceImpl(schedule).getSchedule(req);

		hit.assertIsSet();
	}

	private static final class Flag {
		private boolean _isSet = false;

		public void assertIsSet() {
			assertEquals(true, _isSet);
		}

		public void set() {
			if (_isSet) {
				throw new IllegalStateException("Cannot set a flag twice.");
			}
			_isSet = true;
		}
	}
}