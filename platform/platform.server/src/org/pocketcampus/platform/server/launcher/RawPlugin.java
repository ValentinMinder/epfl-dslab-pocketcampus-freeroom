package org.pocketcampus.platform.server.launcher;

import javax.servlet.http.HttpServlet;

public interface RawPlugin {
	HttpServlet getServlet();
}