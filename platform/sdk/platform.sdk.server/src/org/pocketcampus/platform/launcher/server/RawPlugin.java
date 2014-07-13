package org.pocketcampus.platform.launcher.server;

import javax.servlet.http.HttpServlet;

public interface RawPlugin {
	HttpServlet getServlet();
}