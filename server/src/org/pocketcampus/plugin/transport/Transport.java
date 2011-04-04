package org.pocketcampus.plugin.transport;


import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.router.IServerBase;
import org.pocketcampus.core.router.PublicMethod;

public class Transport implements IServerBase {
	
	@PublicMethod
	public String autocomplete(HttpServletRequest request) {
		return new Date().toString();
	}
	
	@PublicMethod
	public String hello(HttpServletRequest request) {
		return "Hello World";
	}

}
