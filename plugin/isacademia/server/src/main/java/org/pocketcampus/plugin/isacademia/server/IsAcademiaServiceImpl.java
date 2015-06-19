package org.pocketcampus.plugin.isacademia.server;

import org.apache.thrift.TException;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.pocketcampus.platform.server.Authenticator;
import org.pocketcampus.platform.server.HttpClientImpl;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.plugin.authentication.server.AuthenticationServiceImpl;
import org.pocketcampus.plugin.authentication.server.AuthenticatorImpl;
import org.pocketcampus.plugin.isacademia.shared.*;

import java.io.IOException;
import java.util.Locale;

/**
 * Implementation of IsAcademiaService.
 *
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class IsAcademiaServiceImpl implements IsAcademiaService.Iface {
    private final Schedule _schedule;
    private final GradesService gradesService;
    private final Authenticator _authenticator;

    public IsAcademiaServiceImpl(Schedule schedule, GradesService grades, Authenticator authenticator) {
        _schedule = schedule;
        gradesService = grades;
        _authenticator = authenticator;
    }

    public IsAcademiaServiceImpl() {
        this(new ScheduleImpl(new HttpsClientImpl()), new IsAcademiaGradesService(new HttpClientImpl()), new AuthenticatorImpl());
    }

    @Override
    public ScheduleResponse getSchedule(ScheduleRequest req) throws TException {
        String sciper = _authenticator.getSciper();
        LocalDate date = req.isSetWeekStart() ? new LocalDate(req.getWeekStart()) : getCurrentWeekStart();
        String lang = req.isSetLanguage() ? req.getLanguage() : "fr";

        return _schedule.get(date, lang, sciper);
    }

    public static void main(String... args) throws Throwable{
      System.out.println(  new IsAcademiaServiceImpl().getGrades());
    }
    @Override
    public IsaGradesResponse getGrades() throws TException {
        Locale locale = Locale.forLanguageTag(PocketCampusServer.getUserLanguageCode());
        if (!gradesService.supportedLocales().contains(locale)) {
            locale = gradesService.supportedLocales().get(0);
        }

        final String token = AuthenticationServiceImpl.getAccessTokenForScope("ISA.read");
        if (token == null) {
            return new IsaGradesResponse(IsaStatusCode.INVALID_SESSION);
        }

        try {
            return new IsaGradesResponse(IsaStatusCode.OK).setSemesters(gradesService.get(token, locale));
        } catch (final IOException e) {
            return new IsaGradesResponse(IsaStatusCode.NETWORK_ERROR);
        }
    }

    private static LocalDate getCurrentWeekStart() {
        return LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
    }
}