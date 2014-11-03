package org.pocketcampus.plugin.isacademia.server;

import org.pocketcampus.platform.server.Authenticator;
import org.pocketcampus.plugin.authentication.server.AuthenticatorImpl;
import org.pocketcampus.plugin.isacademia.shared.*;
import org.apache.thrift.TException;
import org.joda.time.*;

/**
 * Implementation of IsAcademiaService.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class IsAcademiaServiceImpl implements IsAcademiaService.Iface {
	private final Schedule _schedule;
	private final Authenticator _authenticator;

	public IsAcademiaServiceImpl(Schedule schedule, Authenticator authenticator) {
		_schedule = schedule;
		_authenticator = authenticator;
	}

	public IsAcademiaServiceImpl() {
		this(new ScheduleImpl(new HttpsClientImpl()), new AuthenticatorImpl());
	}

	@Override
	public ScheduleResponse getSchedule(ScheduleRequest req) throws TException {
		String sciper = _authenticator.getSciper();
		LocalDate date = req.isSetWeekStart() ? new LocalDate(req.getWeekStart()) : getCurrentWeekStart();
		String lang = req.isSetLanguage() ? req.getLanguage() : "fr";

		return _schedule.get(date, lang, sciper);
	}

	private static LocalDate getCurrentWeekStart() {
		return LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
	}
}