package org.pocketcampus.plugin.map;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.router.MainRouter;
import org.pocketcampus.core.router.PublicMethod;

public class Map extends MainRouter {
	
	private static final long serialVersionUID = -2805792171809919422L;

	@PublicMethod
	public String map(HttpServletRequest request) {
		return "I am MAP ";
	}
	
	@PublicMethod
	public String hello(HttpServletRequest request) {
		return "Hello World";
	}

	protected String getDefaultMethod() {
		return "map";
	}

}
