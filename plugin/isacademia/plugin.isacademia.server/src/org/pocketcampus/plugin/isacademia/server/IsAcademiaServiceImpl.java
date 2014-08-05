package org.pocketcampus.plugin.isacademia.server;

import org.pocketcampus.plugin.authentication.server.AuthenticationServiceImpl;
import org.pocketcampus.plugin.isacademia.shared.*;

import org.apache.thrift.TException;

import org.joda.time.*;

/**
 * Implementation of IsAcademiaService.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class IsAcademiaServiceImpl implements IsAcademiaService.Iface {
	private final Schedule _schedule;

	public IsAcademiaServiceImpl(Schedule schedule) {
		_schedule = schedule;
	}

	public IsAcademiaServiceImpl() {
		this(new ScheduleImpl(new HttpsClientImpl()));
	}

	@Override
	public ScheduleResponse getSchedule(ScheduleRequest req) throws TException {
		String sciper = AuthenticationServiceImpl.authGetUserSciper();

		LocalDate date = req.isSetWeekStart() ? new LocalDate(req.getWeekStart()) : getCurrentWeekStart();
		String lang = req.isSetLanguage() ? req.getLanguage() : "fr";

		try {
			return _schedule.get(date, lang, sciper);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	private static LocalDate getCurrentWeekStart() {
		return LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
	}
}