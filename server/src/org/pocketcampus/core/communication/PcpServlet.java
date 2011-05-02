package org.pocketcampus.core.communication;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pocketcampus.core.communication.SignatureChecker.AnnotationsPolicy;
import org.pocketcampus.core.communication.SignatureChecker.ExceptionsPolicy;
import org.pocketcampus.core.communication.SignatureChecker.ReturnTypePolicy;
import org.pocketcampus.core.communication.exception.InternalError;
import org.pocketcampus.core.communication.exception.PcpException;
import org.pocketcampus.core.communication.packet.GsonTypeAdapters;
import org.pocketcampus.core.communication.packet.GsonTypeAdapters.Side;
import org.pocketcampus.core.communication.packet.ModuleInfo;
import org.pocketcampus.core.communication.packet.Options;
import org.pocketcampus.core.communication.packet.Packet;
import org.pocketcampus.core.communication.packet.Payload;
import org.pocketcampus.core.communication.packet.ServerOptions;
import org.pocketcampus.core.communication.pipe.IPipe;
import org.pocketcampus.core.communication.pipe.IPipeReader;
import org.pocketcampus.core.communication.pipe.IPipeWriter;
import org.pocketcampus.core.communication.pipe.SimpleBox;
import org.pocketcampus.core.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

public abstract class PcpServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4387266649432908292L;

	/**
	 * Name of this generator
	 */
	final private String generatorName_ = "PocketCampus Server v2 @ EPFL";
	
	/**
	 * HTTP parameter name which holds the PCP Packet
	 */
	final private String pcpPacketFieldName_ = "pcppacket";
	
	/**
	 * HTTP 'encoding' header for the response to the client
	 */
	final private String responseEncoding_ = "text/plain; charset=UTF-8";
	
	/**
	 * Client's request character encoding
	 */
	final private String requestEncoding_ = "UTF-8";
	
	
	/**
	 * Process HTTP GET requests, which are used solely for inter-module communication
	 */
	@Override
	final
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		sendHttpError(resp, HttpServletResponse.SC_BAD_REQUEST, "HTTP GET Not yet" +
				" implemented");
	}
	
	/**
	 * Process a PCP request.
	 */
	@Override
	final
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
			/* TODO :
			 * Implement versioning for multiple clients using different PCP versions
			 */
			
			
			req.setCharacterEncoding(this.requestEncoding_);
			
			// Retrieve and validate the PCP Packet
			String json = req.getParameter(pcpPacketFieldName_);
			if (json == null) {
				sendHttpError(resp, HttpServletResponse.SC_BAD_REQUEST, "PCP Parameter Name : '"
						+ pcpPacketFieldName_ + "'");
			}
			else {
				Gson clientPacketGson =
					GsonTypeAdapters.register(getDefaultGsonBuilder(), Side.Client)
					.create();
				
				Packet packet = clientPacketGson.fromJson(json, Packet.class);
				
				// TODO : Analyse protocol and version
				// TODO : Log Date and Generator
				// TODO : Process Options
				
				Payload payload = packet.getPayload();
				String action = payload.getModuleInfos().getAction();
				IPipe<Object> response = new SimpleBox<Object>();
				
				dispatch(action, payload, response.writer());
				Packet responsePacket = packageResponse(response.reader(), packet);
				sendResponse(responsePacket, resp);
			}
		} catch (IOException e) {
			Logger.log(e);
		} catch (JsonParseException e) {
			PcpException pe = new PcpException(e, Status.BAD_REQUEST);
			sendPcpError(pe, resp);
		} catch (PcpException e) {
			// TODO Send a PCP Error
			sendPcpError(e, resp);
		}
	}
	
	/**
	 * Dispatches the payload to the requested action
	 * @param action the action that will receive the payload
	 * @param payload the actual payload wrapper
	 * @param response a writer for the action's output
	 * @throws PcpException if something went wrong at the client or the module level
	 * @throws InternalError if something went wrong at the communication service level
	 */
	private void dispatch(String action, Payload payload, IPipeWriter<Object> response) throws PcpException, InternalError {
		Class<? extends PcpServlet> myClass = this.getClass();
			
			// Try block related to a Communication Service problem
			try {
				Method refMthd = ReferenceMethods.class
						.getDeclaredMethod("pcModuleAction", Payload.class);
				
				// Try block related to a module problem
				try {
					Method mthd = myClass.getMethod(action, Payload.class, IPipeWriter.class);
					
					if (!SignatureChecker.sameSignature(mthd, refMthd,
							ExceptionsPolicy.CHECKED,
							AnnotationsPolicy.AT_LEAST,
							ReturnTypePolicy.IGNORE)) {
						
						throw new NoSuchMethodException();
					}
					
					mthd.invoke(this, payload, response);
					
				} catch (NoSuchMethodException e) {
					/* Occurs when the action method was not found or when its
					 * signature is not valid
					 */
					throw new PcpException(e, Status.ACTION_NOT_FOUND);
					
				} catch (IllegalArgumentException e) {
					/* Should never occur since we already checked the method signature when
					 * retrieving it (name + arguments) and by checking its complete signature
					 * (return type + annotations + thrown exceptions)
					 */
					throw new InternalError(e);
					
				} catch (IllegalAccessException e) {
					/* Should never occur in normal execution conditions.
					 * Occurs if the invoked Method object enforces Java language access
					 * control and the underlying method is inaccessible.
					 */
					throw new PcpException(e, Status.UNRECOVERABLE_MODULE_ERROR);
					
				} catch (InvocationTargetException e) {
					/* Occurs when the invoked method throws an exception
					 */
					Throwable cause = e.getCause();
					throw new PcpException(cause, Status.UNRECOVERABLE_MODULE_ERROR);
					
				}
			} catch (SecurityException e1) {
				/* Occurs when a security manager is present and it prevents reflective
				 * behavior
				 */
				throw new InternalError(e1);
			} catch (NoSuchMethodException e1) {
				/* Occurs when the hard-coded method name 'pcModuleAction' is not present
				 * in ReferenceMethods.java
				 */
				throw new InternalError(e1);
			}
	}
	
	/**
	 * Creates a PCP Packet using some informations in the client request's Packet and the
	 * output of the invoked action
	 * @param response a reader for the output of the invoked action
	 * @param incomingRequest the client's request
	 * @return the packet that should be sent to the client
	 */
	private Packet packageResponse(IPipeReader<Object> response, Packet incomingRequest) {
		Gson serverPacketGson =
			GsonTypeAdapters.register(getDefaultGsonBuilder(), Side.Server)
			.create();
		
		String json = serverPacketGson.toJson(response.read());
		ModuleInfo moduleId = incomingRequest.getPayload().getModuleInfos();
		String interfaceVersion = incomingRequest.getPayload().getInterfaceVersion();
		
		Payload payload = new Payload(json, moduleId, interfaceVersion);
		
		Options options = new ServerOptions()
				.setStatus(Status.OK);
		String generator = this.generatorName_;
		Packet out = new Packet(payload, generator, options);
		
		return out;
	}
	
	/**
	 * Sends the given packet to the client.
	 * @param outgoingPacket the Packet that will be sent to the client
	 * @param resp the ServletResponse that will be used to send the response to the client
	 * @throws IOException
	 */
	private void sendResponse(Packet outgoingPacket, HttpServletResponse resp) throws IOException {
		resp.setContentType(this.responseEncoding_);
		resp.setStatus(HttpServletResponse.SC_OK);
		
		PrintWriter out = resp.getWriter();
		String jsonOut = getDefaultGson().toJson(outgoingPacket);
		out.println(jsonOut);
		out.flush();
		out.close();
		resp.flushBuffer();
	}
	
	/**
	 * Sends a PCP error code to the client. The returned PCP Packet contains the PCP status
	 * code contained in the given PcpException, and no payload.
	 * @param e a PcpException along with a status code
	 * @param resp the ServletResponse that will be used to send the response to the client
	 */
	private void sendPcpError(PcpException e, HttpServletResponse resp) {
		Options options = new ServerOptions()
				.setStatus(e.getStatus());
		
		String generator = this.generatorName_;
		Packet out = new Packet(null, generator, options);
		
		try {
			sendResponse(out, resp);
		} catch (IOException e1) {
			Logger.log(e1);
		}
	}
	
	/**
	 * Sends an HTTP Error code to the client
	 * @param resp the ServletResponse that will be used to send the response to the client
	 * @param httpStatusCode the HTTP Status code that will be sent to the client
	 * @param message the message that will be written with the response, if no error page
	 * declaration was made by the application (see HttpServletResponse's
	 * {@link javax.servlet.http.HttpServletResponse#sendError(int, String) sendError(int,
	 * String)} for additional informations about it)
	 */
	private void sendHttpError(HttpServletResponse resp, int httpStatusCode, String message) {
		try {
			resp.sendError(httpStatusCode, message);
			resp.flushBuffer();
		} catch (IOException e) {
			Logger.log(e);
		}
	}
	
	/**
	 * <p>Retrieves the Gson object that should be used to convert JSON into Java objects
	 * and vice-versa</p>
	 * 
	 * For an overview of the default options, see {@link #getDefaultGsonBuilder()}.
	 * @return a properly configured Gson object
	 */
	protected Gson getDefaultGson() {
		return getDefaultGsonBuilder().create();
	}
	
	
	/**
	 * <p>Returns the default GsonBuilder that should be used to convert JSON into Java objects
	 * and vice-versa.<br />
	 * <i>All transmitted objects should be simple enough to be
	 * (de)serialized with the default Gson obtained with {@link #getDefaultGson()}.
	 * However, if needed, this GsonBuilder can be personalized (e.g. register a new Gson
	 * type adapter).</i></p>
	 * 
	 * Default options are :
	 * <ul>
	 * 	<li>HTML escaping : Disabled</li>
	 * 	<li>Special floating point values : Serialized</li>
	 * 	<li>Field without {@code @Expose} annotation : Excluded</li>
	 *  <li>'null' values : Not serialized
	 * </ul>
	 * @return a properly configured GsonBuilder
	 */
	protected GsonBuilder getDefaultGsonBuilder() {
		return new GsonBuilder()
			.disableHtmlEscaping()
			.serializeSpecialFloatingPointValues()
			.excludeFieldsWithoutExposeAnnotation();
	}
}
