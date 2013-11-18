package org.pocketcampus.plugin.isacademia.server;

import java.util.List;

import org.pocketcampus.plugin.isacademia.shared.*;

import org.joda.time.*;

/**
 * Retrieves an user's schedule from a Tequila cookie.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public interface Schedule {
	List<StudyDay> get(LocalDate weekBeginning, String language, String tequilaCookie) throws ScheduleException;
}