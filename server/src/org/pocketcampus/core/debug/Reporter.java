package org.pocketcampus.core.debug;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.pocketcampus.core.exception.ServerException;
import org.pocketcampus.core.plugin.IPlugin;

public class Reporter {
	private Date startupTime_;
	enum ReportType {STATUS, ERROR};
	enum AdditionalInfo {SHOW_METHODS, SHOW_CLASSES};
	
	public Reporter() {
		startupTime_ = new Date();
	}
	
	private void displayReport(HttpServletResponse response, String content) {
		try {
			ServletOutputStream out = response.getOutputStream();
			
			out.println("<html>");
			
			out.println("<h1>PocketCampus Server</h1>");
			
			out.println(content);
			
			//out.println("<p><a href=\"/\">Status report</a></p>");
			
			out.println("<hr><i>");
			out.println(new Date().toString());
			out.println(" - ");
			out.println("uptime " + getUptime());
			out.println("</i>");
			out.println("</html>");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private String getUptime() {
		int uptime = (int) ((new Date().getTime() - startupTime_.getTime()) / (1000));
		return uptime + "s";
	}

	public void statusReport(HttpServletResponse response, HashMap<String, IPlugin> classes, HashMap<String, HashMap<String, Method>> methods) {
		String content = "<p><b>Loaded plugins</b></p>";
		content += "<ul>";

		for(String c : methods.keySet()) {
			content += "<li><b>" + c + "</b><ul>";

			for(String m : methods.get(c).keySet()) {
				content += "<li>" + m + "</li>";
			}
			
			content += "</ul></li>";
		}

		content += "</ul>";
		displayReport(response, content);
	}

	public void errorReport(HttpServletResponse response, ServerException e) {
		response.setStatus(500);
		
		String content = "<p><b>Arf! " + e.toString() + "</b></p>";
		content += "<p>" + e.getExplanation() + "</p>";
		
		content += "<ul><b>Stack</b>";
		for(StackTraceElement el : e.getStackTrace()) {
			content += "<li>" + el.toString() + "</li>";
		}
		content += "</ul>";
		
		displayReport(response, content);
	}

}






















