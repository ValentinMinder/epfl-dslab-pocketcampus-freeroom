package org.pocketcampus.plugin.isacademia.server;

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
	public ScheduleTokenResponse getScheduleToken() throws TException {
		try {
			return _schedule.getToken();
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public ScheduleResponse getSchedule(ScheduleRequest req) throws TException {
		LocalDate date = req.isSetWeekStart() ? new LocalDate(req.getWeekStart()) : getCurrentWeekStart();
		String lang = req.isSetLanguage() ? req.getLanguage() : "fr";

		try {
			return _schedule.get(date, lang, req.getToken());
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	private static LocalDate getCurrentWeekStart() {
		return LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
	}
}