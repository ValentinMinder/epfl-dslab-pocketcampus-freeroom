package org.pocketcampus.plugin.transport;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.router.IServerBase;
import org.pocketcampus.core.router.PublicMethod;

public class Transport implements IServerBase {
	
	@PublicMethod
	public String hello(HttpServletRequest request) {
		return "Hello World";
	}
	
	@Override
	public String getDefaultMethod() {
		return "hello";
	}

}
