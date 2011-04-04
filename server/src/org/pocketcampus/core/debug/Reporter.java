package org.pocketcampus.core.debug;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.pocketcampus.core.exception.ServerException;
import org.pocketcampus.core.router.IServerBase;

public class Reporter {
	
	enum ReportType {STATUS, ERROR};
	
	private void displayReport(HttpServletResponse response, String content) {
		try {
			ServletOutputStream out = response.getOutputStream();
			
			out.println("<html>");
			out.println("<h1>PocketCampus Server</h1>");
			
			out.println(content);
			
			out.println("<hr>");
			out.println("<i>" + new Date().toString() + "</i>");
			out.println("</html>");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void statusReport(HttpServletResponse response, HashMap<String, IServerBase> classes, HashMap<String, HashMap<String, Method>> methods) {
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
		String content = "<p><b>Arf! " + e.toString() + "</b></p>";
		content += "<p><i>" + e.getExplanation() + "</i></p>";
		
		content += "<ul><b>Stack</b>";
		for(StackTraceElement el : e.getStackTrace()) {
			content += "<li>" + el.toString() + "</li>";
		}
		content += "</ul>";
		
		displayReport(response, content);
	}

}






















