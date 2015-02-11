package org.pocketcampus.plugin.moodle.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Downloads files from Moodle.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public interface FileService {
	void download(final HttpServletRequest request, final HttpServletResponse response) throws IOException;
}