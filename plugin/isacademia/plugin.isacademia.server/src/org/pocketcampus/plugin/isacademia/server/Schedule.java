package org.pocketcampus.plugin.isacademia.server;

import org.pocketcampus.plugin.isacademia.shared.*;

import org.joda.time.*;

/**
 * Retrieves a student's schedule.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public interface Schedule {
	ScheduleResponse get(LocalDate weekBeginning, String language, String sciper) throws Exception;
}