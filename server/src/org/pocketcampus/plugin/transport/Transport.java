package org.pocketcampus.plugin.transport;


import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.shared.plugin.transport.Location;

import de.schildbach.pte.SbbProvider;

public class Transport implements IPlugin {
	
	private SbbProvider sbbProvider_;
	
	public Transport() {
		sbbProvider_ = new SbbProvider("MJXZ841ZfsmqqmSymWhBPy5dMNoqoGsHInHbWJQ5PTUZOJ1rLTkn8vVZOZDFfSe");
	}
	
	@PublicMethod
	public Object autocomplete(HttpServletRequest request) {
		String constraint = request.getParameter("constraint");
		List<Location> completions = null;
		
		try {
			completions = sbbProvider_.autocompleteStations(constraint);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return completions;
	}
	
	@PublicMethod
	public String hello(HttpServletRequest request) {
		return "Hello World";
	}

}
