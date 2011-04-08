package org.pocketcampus.plugin.test;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.provider.newsfeed.INewsFeedProvider;

/**
 * Servlet implementation class Test
 */
public class Test implements IPlugin, INewsFeedProvider {

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
	

	public String getDefaultMethod() {
		return "capitalize";
	}

}
