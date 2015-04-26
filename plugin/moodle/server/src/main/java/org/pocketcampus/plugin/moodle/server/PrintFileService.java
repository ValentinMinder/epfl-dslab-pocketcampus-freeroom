package org.pocketcampus.plugin.moodle.server;

import org.pocketcampus.plugin.moodle.shared.MoodlePrintFileRequest2;
import org.pocketcampus.plugin.moodle.shared.MoodlePrintFileResponse2;

/**
 * Print service that prints a Moodle file directly via the CloudPrint plugin.
 * 
 * @author Amer Chamseddine <amer@pocketcampus.org>
 */
public interface PrintFileService {
	MoodlePrintFileResponse2 printFile(final MoodlePrintFileRequest2 request);
}