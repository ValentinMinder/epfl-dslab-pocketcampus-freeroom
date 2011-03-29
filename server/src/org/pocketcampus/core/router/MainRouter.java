package org.pocketcampus.core.router;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class MainRouter extends HttpServlet {
	
	private static final long serialVersionUID = 2912684020711306666L;
	
	// List of available methods for the plugin
	private static HashMap<String, Method> methods;
	
	// Computed when the class is loading, once
	// Puts the list of available methods into the HashMap
	{
		methods = new HashMap<String, Method>();
		
		// Get the methods from the class
		// Get the methods from the extending class, and not the parent
		Method methlist[] = this.getClass().getDeclaredMethods();
		
		for(Method m : methlist) {
			
			// Check if the programmer wants to put the method public
			if(m.isAnnotationPresent(PublicMethod.class)) {
				methods.put(m.getName(), m);
			}
		}
	}

	/**
	 * Handle the GET request
	 * Final because the children do not have to handle the request by themselves
	 */
	protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String name = getMethodFromPath(request.getPathInfo());
		
		Method m = methods.get(name);
		
		if(m == null) {
			// TODO check if the method does not exist
			//response.sendError(404);
			//return;
		}
		
		// Arguments list
		Object arglist[] = new Object[1];
		arglist[0] = request;
		
		try {
			
			// Invoke the method
			String ret = (String) m.invoke(this, arglist);
			
			// Put the method content into the response
			response.getOutputStream().println(ret);
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the name of the method that has to be invoked using the URL
	 * @param path path from the request
	 * @return the name of the request if exist, or the name of the default method
	 */
	private String getMethodFromPath(String path) {
		if(path == null) {
			return getDefaultMethod();
		} else {
			return path.substring(1);
		}
		
	}
	
	/**
	 * Give the name of the default method when calling the servlet without arguments
	 * @return default method name
	 */
	protected abstract String getDefaultMethod();

}
