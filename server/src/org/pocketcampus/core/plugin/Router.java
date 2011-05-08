package org.pocketcampus.core.plugin;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pocketcampus.core.debug.Reporter;
import org.pocketcampus.core.exception.ServerException;
import org.pocketcampus.plugin.transport.PartSerializer;
import org.pocketcampus.shared.plugin.transport.Connection.Footway;
import org.pocketcampus.shared.plugin.transport.Connection.Part;
import org.pocketcampus.shared.plugin.transport.Connection.Trip;
import org.pocketcampus.shared.utils.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Server core. Singleton
 * @author Jonas
 * @author Florian
 * @status working, incomplete
 */
public class Router extends HttpServlet {
	/** Serialization crap */
	private static final long serialVersionUID = 2912684020711306666L;
	
	private Core core_;
	
	/** Gson instance to serialize resulting objects */
	private static Gson gson_;
	
	/** Displays nice HTML pages */
	private static Reporter reporter_;
	
	@Override
	public void init() throws ServletException {
		super.init();
		
		core_ = Core.getInstance();
		reporter_ = new Reporter();
		
		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss Z");
		builder.setPrettyPrinting();
		builder.registerTypeAdapter(Part.class, new PartSerializer());

		gson_ = builder.create();
		
		String[] plugins = new String[] {
				"org.pocketcampus.plugin.food.Food",
				"org.pocketcampus.plugin.map.Map",
				"org.pocketcampus.plugin.transport.Transport",
//				"org.pocketcampus.plugin.test.Test",
				"org.pocketcampus.plugin.bikes.Bikes"
		};
		
		// XXX
		for(String s : plugins) {
			try {
				initClass(s);
			} catch (ServerException e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * Handle the GET request
	 * Final because the children do not have to handle the request by themselves
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Request: " + request.getRequestURL());
		Enumeration<String> ps = request.getParameterNames();
		while(ps.hasMoreElements()) {
			String s = ps.nextElement();
			System.out.println("Parameter: " + s + " -> " + request.getParameter(s));
		}
		
		// URL used to access the servlet
		//String path = request.getPathInfo();
		String path = request.getServletPath();
		
		if(path != null && path.endsWith(".do")) {
			path = path.substring(0, path.length() - 3);
		}

		// Request without any command
		if(path == null || path.equals("/")) {
			reporter_.statusReport(response, core_.getPluginList(), core_.getMethodList());
			return;
		}
		
		// Get the object and the method connected to the command
		try {
			IPlugin obj = getObject(path);
			Method m = getMethod(obj, path);
			invoke(request, response, obj, m);
			
		} catch (ServerException e) {
			reporter_.errorReport(response, e);
		}
		
	}
	
	/**
	 * Creates an instance of the plugin from its path.
	 * @param plugin path
	 * @return instance of the plugin
	 * @throws ServerException
	 */
	private IPlugin getObject(String path) throws ServerException {
		String className = getClassNameFromPath(path);
		
		if(className==null || className.equals("")) {
			throw new ServerException("No method provided.");
		}
		
		IPlugin obj = initClass(className);
		
		if(obj == null) {
			throw new ServerException("Object initialization failed.");
		}
		
		return obj;
	}
	
	/**
	 * Extracts the class name of a class path.
	 * @param class path of a plugin
	 * @return class name
	 */
	private String getClassNameFromPath(String path) {
		String split[] = path.split("/");
		
		if(split.length > 1) {
			return "org.pocketcampus.plugin." + split[1].toLowerCase() + "." + StringUtils.capitalize(split[1]);
		}
		
		return null;
	}
	
	/**
	 * Invokes the plugin.
	 * @param request
	 * @param response
	 * @param obj
	 * @param m
	 * @throws IOException
	 */
	private void invoke(HttpServletRequest request, HttpServletResponse response, IPlugin obj, Method m) throws IOException {
		// Create the arguments to pass to the method
		Object arglist[] = new Object[1];
		//request.setCharacterEncoding("iso-8859-15");
		request.setCharacterEncoding("UTF-8");
		
		Charset charset = Charset.forName("UTF-8");
		
		arglist[0] = request;
		try {			
			// Sets the content type
			final String encoding = "text/html; charset=UTF-8";
			response.setContentType(encoding);
				
			// Invokes the method
			Object ret = m.invoke(obj, arglist);
			
			String json = gson_.toJson(ret);
			
			// Fixes the damn encoding
			//json = charset.encode(json).toString();
			
			// Puts the method content into the response
			response.getWriter().println(json);
			
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
	private String getMethodNameFromPath(String path) {
		
		String split[] = path.split("/");
		
		if(split.length < 3 || split[2] == null) {
			return null;
		} else {
			return split[2];
		}
		
	}
	
	
	private Method getMethod(IPlugin obj, String path) throws ServerException {
		String className = getClassNameFromPath(path);
		String methodName = getMethodNameFromPath(path);
		
		if(methodName == null || !core_.getMethodList().get(className).containsKey(methodName)) {
			throw new ServerException("Method <b>"+ methodName +"</b> not found in package <b>"+ className +"</b>. Mispelled?");
		}
		
		Method m = core_.getMethodList().get(className).get(methodName);
		
		return m;
	}
	
	private IPlugin initClass(String className) throws ServerException {
		
		// Checks if the class already exists
		IPlugin clazz = core_.getPluginList().get(className);
		if(clazz != null) {
			return clazz;
		}
		
		IPlugin obj = null;
		
		try {
			Class<?> c = Class.forName(className);
			Constructor<?> ct = c.getConstructor();
			
			// Check that the class implements the correct interface
			Class<?>[] interfaces = c.getInterfaces();
			boolean ok = false;
			for(Class<?> i : interfaces) {
				ok = ok || i.equals(IPlugin.class);
			}
			
			if(!ok) {
				return null;
			}

			obj = (IPlugin) ct.newInstance(new Object[0]);
			
			core_.getPluginList().put(className, obj);
			
			// Get the methods from the class
			Method methlist[] = c.getDeclaredMethods();
			
			HashMap<String, Method> classMethods = new HashMap<String, Method>();
			
			for(Method m : methlist) {
				
				// Check if the programmer wants to put the method public
				if(m.isAnnotationPresent(PublicMethod.class)) {
					classMethods.put(m.getName(), m);
				}
			}
			
			core_.getMethodList().put(className, classMethods);
			
		} catch (ClassNotFoundException e) {
			throw new ServerException("The class <b>" + className + "</b> was not found. Mispelled?");
			
		} catch (IllegalArgumentException e) {
			throw new ServerException("IllegalArgumentException : " + className);
			
		} catch (InstantiationException e) {
			throw new ServerException("InstantiationException : " + className);
			
		} catch (IllegalAccessException e) {
			throw new ServerException("IllegalAccessException : " + className);
			
		} catch (InvocationTargetException e) {
			throw new ServerException("InvocationTargetException : " + className);
			
		} catch (SecurityException e) {
			throw new ServerException("SecurityException : " + className);
			
		} catch (NoSuchMethodException e) {
			throw new ServerException("NoSuchMethodException : " + className);
			
		}
		
		return obj;
		
	}
	


}
