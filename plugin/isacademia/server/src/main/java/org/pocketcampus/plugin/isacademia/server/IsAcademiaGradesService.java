package org.pocketcampus.plugin.isacademia.server;

import org.pocketcampus.platform.server.HttpClient;
import org.pocketcampus.platform.server.XElement;
import org.pocketcampus.plugin.isacademia.shared.SemesterGrades;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Fetches IS-Academia grades.
 *
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class IsAcademiaGradesService implements GradesService {
    // HACK: THIS LINE IS ABOMINABLE. It's Tim's PC! TODO: Switch to an actual server.
    private static final String API_URL = "http://ogifdev1.epfl.ch:9000/services/student/authenticated/notes";
    private static final Charset API_CHARSET = StandardCharsets.UTF_8;
    private static final List<Locale> API_LOCALES = Arrays.asList(Locale.ENGLISH, Locale.FRENCH);

    private static final String CONTENT_TYPE_HEADER = "Accept";
    private static final String CONTENT_TYPE_XML = "application/xml";
    private static final String OAUTH_HEADER = "Authorization";

    private static final String SEMESTER_ELEMENT = "session";
    private static final String SEMESTER_TITLE_ELEMENT = "peda";
    private static final String SEMESTER_COURSE_ELEMENT = "course";
    private static final String COURSE_NAME_ELEMENT = "title";
    private static final String COURSE_NOTE_ELEMENT = "note";

    // Even when the default is in English, the API says it's French.
    private static final Locale NAMES_DEFAULT_LOCALE = Locale.FRENCH;

    private final HttpClient client;

    public IsAcademiaGradesService(final HttpClient client) {
        this.client = client;
    }

    @Override
    public List<Locale> supportedLocales() {
        return API_LOCALES;
    }

    @Override
    public List<SemesterGrades> get(final String token, final Locale locale) throws IOException {
        assert API_LOCALES.contains(locale) : "The locale must be a supported one.";

        final Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE_HEADER, CONTENT_TYPE_XML);
        headers.put(OAUTH_HEADER, token);

        final String xml = client.get(API_URL, headers, API_CHARSET);

        final List<SemesterGrades> grades = new ArrayList<>();
        for (final XElement semesterElem : XElement.parse(xml).children(SEMESTER_ELEMENT)) {
            grades.add(parseSemester(semesterElem, locale));
        }
        Collections.reverse(grades);
        return grades;
    }

    private static SemesterGrades parseSemester(final XElement semesterElem, final Locale locale) {
        final String title = parseLocalizedElement(semesterElem.child(SEMESTER_TITLE_ELEMENT), locale);
        final Map<String, String> grades = new HashMap<>();
        for (final XElement courseElem : semesterElem.children(SEMESTER_COURSE_ELEMENT)) {
            final String name = parseLocalizedElement(courseElem.child(COURSE_NAME_ELEMENT), locale);
            final String note = courseElem.child(COURSE_NOTE_ELEMENT).text();
            grades.put(name, note);
        }

        return new SemesterGrades(title, grades);
    }

    private static String parseLocalizedElement(final XElement element, final Locale locale) {
        final XElement candidate = element.child(locale.toLanguageTag());
        if (candidate != null) {
            return candidate.text();
        }
        return element.child(NAMES_DEFAULT_LOCALE.toLanguageTag()).text();
    }
}