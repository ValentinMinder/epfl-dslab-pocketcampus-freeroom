package org.pocketcampus.plugin.moodle.server;

import javax.servlet.http.*;

/**
 * Downloads files from Moodle.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public interface FileService {
	void download(final HttpServletRequest request, final HttpServletResponse response);
}