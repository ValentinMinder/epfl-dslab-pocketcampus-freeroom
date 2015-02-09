package org.pocketcampus.plugin.isacademia.server;

import org.joda.time.LocalDate;
import org.pocketcampus.plugin.isacademia.shared.ScheduleResponse;

/**
 * Retrieves a student's schedule.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public interface Schedule {
	ScheduleResponse get(LocalDate weekBeginning, String language, String sciper);
}