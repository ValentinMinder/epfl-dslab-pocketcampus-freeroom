package org.pocketcampus.platform.server;

import javax.servlet.http.HttpServlet;

public interface RawPlugin {
	HttpServlet getServlet();
}