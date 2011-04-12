package org.pocketcampus.core.communication;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public abstract class PcpServlet extends HttpServlet {
	/**
	 * Stores the GSON Builder used by PCP Servlets.
	 * Since every instance of PcpServlet are able to personalize the builder (through
	 * predefined methods), it must not be static.
	 */
	private GsonBuilder gsonBuilder;
	
	
	
	
	
	/**
	 * Initialize PcpServlet with default fields values. If overridden, do not forget
	 * to call <code>super.init()</code> in the overriding method.
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() {
		this.gsonBuilder = new GsonBuilder()
		.disableHtmlEscaping()
		.serializeSpecialFloatingPointValues();
	}
	
	
	
	/**
	 * Process HTTP GET requests, which are used solely for inter-module communication
	 */
	@Override
	final
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			
		} catch (IOException e) {
			// Cannot do anything. Log the Exception.
			// TODO : Use server's logging
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// Cannot do anything. Log the Exception.
			// TODO : Use server's logging
			e.printStackTrace();
		}
	}
	
	@Override
	final
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		/* TODO :
		 * - Analyze Protocol name and version
		 * - (Dispatch to the PCP handler for the given version)
		 * - Log Date and Generator
		 * - Validate packet
		 * - Process options
		 * - Call the correct function of the implementing class
		 */
	}
	
	/**
	 * Register a type to Gson in order to be able to do the conversion JSON-Java object
	 * with this type
	 * @see com.google.gson.GsonBuilder#registerTypeAdapter(Type, Object)
	 * @param type
	 * @param typeAdapter
	 */
	final
	protected void registerTypeAdapter(Type type, Object typeAdapter) {
		this.gsonBuilder.registerTypeAdapter(type, typeAdapter);
	}
	
	/**
	 * Retrieves the Gson object that should be used to convert JSON into Java objects
	 * and vice-versa
	 * @return a properly configured Gson object
	 */
	final
	protected Gson gson() {
		return this.gsonBuilder.create();
	}
	
	
	
	/* Each function that can be called from the client must have the following signature :
	 * 
	 * Object functionName(String payload);
	 */
}
