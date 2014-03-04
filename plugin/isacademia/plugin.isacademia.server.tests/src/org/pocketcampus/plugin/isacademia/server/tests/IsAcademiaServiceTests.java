package org.pocketcampus.plugin.isacademia.server.tests;

import static org.junit.Assert.*;
import org.junit.Test;

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
			public ScheduleResponse get(LocalDate weekBeginning, String language, String sciper) throws Exception {
				hit.set();
				return null;
			}
		};

		ScheduleRequest req = new ScheduleRequest();
		new IsAcademiaServiceImpl(getTestSessionManager(),schedule).getSchedule(req);

		hit.assertIsSet();
	}

	@Test
	public void defaultValueOfWeekStartIsCurrentWeekStart() throws Exception {
		final Flag hit = new Flag();
		Schedule schedule = new Schedule() {
			@Override
			public ScheduleResponse get(LocalDate weekBeginning, String language, String sciper) throws Exception {
				hit.set();
				assertEquals(weekBeginning, new LocalDate(2013, 11, 11));

				return new ScheduleResponse();
			}
		};

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 11, 11, 00, 00, 00).getMillis());
		ScheduleRequest req = new ScheduleRequest();
		new IsAcademiaServiceImpl(getTestSessionManager(),schedule ).getSchedule(req);

		hit.assertIsSet();
	}

	@Test
	public void parametersAreTransferredCorrectly() throws Exception {
		final Flag hit = new Flag();
		final String expectedLanguage = "xyz";
		final String expectedSciper = "123456";
		final LocalDate expectedWeek =  new LocalDate(2013, 11, 4);
		
		SessionManager manager=  new SessionManager(){
			@Override
			public String insert(String gaspar, String sciper) {
				return "";
			}

			@Override
			public String getGaspar(String sessionId) {
				return "";
			}

			@Override
			public String getSciper(String sessionId) {
				return expectedSciper;
			}	
		};
		
		Schedule schedule = new Schedule() {
			@Override
			public ScheduleResponse get(LocalDate weekBeginning, String language, String sciper) throws Exception {
				hit.set();
				assertEquals(expectedWeek,weekBeginning);
				assertEquals(expectedLanguage, language);
				assertEquals(expectedSciper, sciper);

				return new ScheduleResponse();
			}
		};

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2000, 01, 02, 00, 00, 00).getMillis());

		ScheduleRequest req = new ScheduleRequest()
				.setWeekStart(expectedWeek.toDateTimeAtStartOfDay().getMillis())
				.setLanguage(expectedLanguage);
		new IsAcademiaServiceImpl(manager, schedule).getSchedule(req);

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
	
	private static SessionManager getTestSessionManager(){
		return new SessionManager(){
			@Override
			public String insert(String gaspar, String sciper) {
				return "";
			}

			@Override
			public String getGaspar(String sessionId) {
				return "";
			}

			@Override
			public String getSciper(String sessionId) {
				return "";
			}
			
		};
	}
}