package org.pocketcampus.plugin.isacademia.server;

import java.util.List;

import org.apache.thrift.TException;

import org.joda.time.*;

import org.pocketcampus.plugin.isacademia.shared.*;

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

	public static void main(String[] args) {
		try {
			// To test, replace with a valid Tequila cookie 
			// (use dev tools in your browser to view the Set-Cookie headers sent
			//  back after auth from https://tequila.epfl.ch/cgi-bin/tequila/login)
			ScheduleResponse r = new IsAcademiaServiceImpl().getSchedule(new ScheduleRequest(""));
			List<StudyDay> days = r.getDays();
			System.out.println(days);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ScheduleResponse getSchedule(ScheduleRequest req) throws ScheduleException, TException {
		LocalDate date = req.isSetWeekStart() ? new LocalDate(req.getWeekStart()) : getCurrentWeekStart();
		String lang = req.isSetLanguage() ? req.getLanguage() : "fr";
		List<StudyDay> days = _schedule.get(date, lang, req.getTequilaCookie());

		return new ScheduleResponse(days);
	}

	private static LocalDate getCurrentWeekStart() {
		return LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
	}
}