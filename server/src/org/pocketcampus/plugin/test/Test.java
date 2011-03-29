package org.pocketcampus.plugin.test;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.router.MainRouter;
import org.pocketcampus.core.router.PublicMethod;

/**
 * Servlet implementation class SampleServlet
 */
public class Test extends MainRouter {

	private static final long serialVersionUID = -3719959899511131113L;

	@SuppressWarnings("unchecked")
	@PublicMethod
	public String capitalize(HttpServletRequest request) {
    	Enumeration<String> attrNames = request.getParameterNames();
		
		if(!attrNames.hasMoreElements())
			return "vide";
		
		String ret = new String();
		while(attrNames.hasMoreElements()){
			String s = (String)request.getParameter( attrNames.nextElement() );
			ret += s.toUpperCase();
		}
		
		return ret;
    }

	@Override
	protected String getDefaultMethod() {
		return "capitalize";
	}

}
