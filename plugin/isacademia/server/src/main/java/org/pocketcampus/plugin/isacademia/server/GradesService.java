package org.pocketcampus.plugin.isacademia.server;

import org.pocketcampus.plugin.isacademia.shared.SemesterGrades;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Fetches grades.
 *
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public interface GradesService {
    /**
     * Gets the locales supported by the service.
     *
     * @return The locales.
     */
    public abstract List<Locale> supportedLocales();

    /**
     * Gets the grades for the specified student.
     *
     * @param token  An OAuth2 token for the student.
     * @param locale The locale.
     * @return The grades.
     */
    public abstract List<SemesterGrades> get(final String token, final Locale locale) throws IOException;
}