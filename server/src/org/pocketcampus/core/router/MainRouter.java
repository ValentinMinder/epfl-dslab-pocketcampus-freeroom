package org.pocketcampus.core.router;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class MainRouter extends HttpServlet {
	
	private static final long serialVersionUID = 2912684020711306666L;
	
	private static HashMap<String, Method> methods;
	
	{
		methods = new HashMap<String, Method>();
		
		Method methlist[] = this.getClass().getDeclaredMethods();
		
		System.out.println(this.getClass().toString());
		
		for(Method m : methlist) {
			Annotation[] annotations = m.getDeclaredAnnotations();

			for(Annotation annotation : annotations) {
			    if(annotation instanceof PublicMethod) {
			    	methods.put(m.getName(), m);
			    }
			}
		}
	}

	protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String name = getMethodFromPath(request.getPathInfo());
		
		Method m = methods.get(name);
		
		if(m == null) {
			//response.sendError(404);
			//return;
		}
		
		Object arglist[] = new Object[1];
		arglist[0] = request;
		
		try {
			String ret = (String) m.invoke(this, arglist);
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
	
	private String getMethodFromPath(String path) {
		if(path == null) {
			return getDefaultMethod();
		} else {
			return path.substring(1);
		}
		
	}
	
	/**
	 * Give the name of the default method when calling the servelt without arguments
	 * @return default method name
	 */
	protected abstract String getDefaultMethod();

}
