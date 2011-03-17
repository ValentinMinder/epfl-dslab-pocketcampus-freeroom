package org.pocketcampus.plugin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SampleServlet
 */
public class SampleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SampleServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Enumeration<String> attrNames = request.getParameterNames();
		
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println("reponse");
		
		if(!attrNames.hasMoreElements())
			out.println("vide");
		
		while(attrNames.hasMoreElements()){
			String s = (String)request.getParameter( attrNames.nextElement() );
			
			out.println("In capital letters:" + s.toUpperCase());
		}
			
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Enumeration<String> attrNames = request.getAttributeNames();		
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		
		while(attrNames.hasMoreElements()){
			String s = (String)request.getAttribute( attrNames.nextElement() );
			
			out.println("In capital letters:" + s.toUpperCase());
		}
	}

}
