package org.pocketcampus.core.communication.pcp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.shared.core.communication.DefaultGson;
import org.pocketcampus.shared.core.communication.PcpStatus;
import org.pocketcampus.shared.core.communication.packet.ClientOptions;
import org.pocketcampus.shared.core.communication.packet.GsonTypeAdapters;
import org.pocketcampus.shared.core.communication.packet.ModuleInfo;
import org.pocketcampus.shared.core.communication.packet.Options;
import org.pocketcampus.shared.core.communication.packet.Packet;
import org.pocketcampus.shared.core.communication.packet.Payload;
import org.pocketcampus.shared.core.communication.packet.ServerOptions;
import org.pocketcampus.shared.core.communication.packet.GsonTypeAdapters.Side;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import android.os.AsyncTask;
import android.util.Log;


/**
 * Manages requests to a given PCP Action on the remote PCP Server.<br />
 * Implement {@link #onPacketReceived(Payload)} to do the work in a background thread
 * without freezing the UI, and {@link #onProcessingComplete(Object)} to update the UI after
 * the previous method returned.
 *
 * @param <Result>
 */
public abstract class PcpRequest<Result> extends AsyncTask<Object, Integer, Result> {
	/**
	 * Tag for Android's {@code Log.d(...)}
	 */
	private static final String DEBUG_TAG = "AbstractRequest";
	
	/**
	 * Name of this Communication Service
	 */
	private static final String generator_ = "PocketCampus v2 @ Android";
	
	/**
	 * Charset used for PCP Communications
	 */
	private static final Charset pcpCharset_ = Charset.forName("UTF-8");
	
	/**
	 * Name of the HTTP POST and GET field which will contain the PCP Packet
	 */
	private static final String formKeyForPcpPacket_ = "pcppacket";
	
	/**
	 * GSON instance personalized for a PCP Packet
	 */
	private final static Gson gson_ = GsonTypeAdapters.register(
			DefaultGson.getGsonBuilder(),
			Side.Server)
			.create();
	
	/**
	 * Thread safe connection manager that uses connections from a pool.
	 */
	private final static ClientConnectionManager connectionManager_;
	static {
		HttpClient defaultClient = new DefaultHttpClient();
		ClientConnectionManager defaultConnMan = defaultClient.getConnectionManager();
		HttpParams defaultParams = defaultClient.getParams();
		
		Timeout pcpTimeout = Timeout.getDefaultTimeout(); 
		
		int connTimeout = new Long(pcpTimeout.connection(TimeUnit.MILLISECONDS)).intValue();
		HttpConnectionParams.setConnectionTimeout(defaultParams, connTimeout);
		
		int soTimeout = new Long(pcpTimeout.socket(TimeUnit.MILLISECONDS)).intValue();
		HttpConnectionParams.setSoTimeout(defaultParams, soTimeout);
		
		
		connectionManager_ = new ThreadSafeClientConnManager(
				defaultParams,
				defaultConnMan.getSchemeRegistry());
	}
	
	
	/**
	 * Host part of the URL
	 */
	private String host_;
	
	/**
	 * Will be used to retrieve the plugin ID which will be called on the server
	 */
	private PluginInfo pluginInfo_;
	
	/**
	 * Action that will be called on the server
	 */
	private String action_;
	
	/**
	 * Indicates whether or not this PcpRequest is properly configured and can be executed
	 */
	private boolean stateValid_ = false;
	
	
	
	
	
	/* ************************ Constructors ************************ */ 
	
	
	
	
	
	/**
	 * Creates a new instance of PcpRequest and configures it with the given parameters.
	 * @param host URL (host part) of the PocketCampus server 
	 * @param pluginInfo the PluginInfo of the requesting plugin (will be used to extract
	 * the server-side plugin that will be called
	 * @param action server plugin's action that will be triggered
	 */
	public PcpRequest(String host, PluginInfo pluginInfo, String action) {
		setTarget(host, pluginInfo, action);
	}
	
	/**
	 * Configures this PcpRequest with given parameters.
	 * @param host URL (host part) of the PocketCampus server 
	 * @param pluginInfo the PluginInfo of the requesting plugin (will be used to extract
	 * the server-side plugin that will be called
	 * @param action server plugin's action that will be triggered
	 */
	public void setTarget(String host, PluginInfo pluginInfo, String action) {
		this.host_ = host;
		this.pluginInfo_ = pluginInfo;
		this.action_ = action;
		this.stateValid_ = true;
	}
	
	
	/* ************************ Overriddable methods ************************ */
	
	/**
	 * Called in the <b>background thread</b> when the response is received from the
	 * server.<br />
	 * Since this method is executed in the background thread, it should perform all
	 * heavyweight computations.<br />
	 * This method should be implemented as {@link AsyncTask#doInBackground(Object...)} would be.<br />
	 * <br />
	 * After this method returns, {@link #onProcessingComplete(Object)} is executed in the
	 * UI thread using the returned Result.<br />
	 * {@link #testCancelled()} should be periodically invoked to stop this task if it was
	 * canceled.
	 * 
	 * @param payload the PCP Payload included in the server's response.
	 * @return A result, defined by the subclass of this task.
	 * @see #onProcessingComplete(Object)
	 */
	abstract protected Result onPacketReceived(Payload payload);
	
	/**
	 * Called in the <b>UI thread</b> when the background thread finished all its
	 * computations. Since this method is executed in the UI thread, it should be small and
	 * quick in order to not freeze the user interface.<br />
	 * This method should be implemented as AsyncTask#onPostExecute(Object) would be.<br />
	 * <i>The default implementation does nothing.</i>
	 * 
	 * @param result The result of the operation computed by {@link #onPacketReceived(Payload)}.
	 * @see #onPacketReceived(Payload)
	 */
	protected void onProcessingComplete(Result result) {
		// Default implementation does nothing
	}
	
	/**
	 * Called when an error occurred during the processing. The default implementation calls
	 * {@code cancel(true)}.
	 * @param e
	 */
	protected void onError(CommunicationException e) {
		cancel(true);
	}
	
	/**
	 * Returns a Timeout used to configure timeouts for this request. If default values are
	 * to be used, return null.
	 * Returns the timeout (in seconds) after which the request should be aborted if
	 * the result is still not available.
	 * @return the timeout that should be used for this request, or {@code null} for default
	 * values
	 */
	protected Timeout getTimeout() {
		return null;
	}
	
	
	/* ************************ Redirectors ************************ */
	
	
	
	/**
	 * Implement {@linkplain #onProcessingComplete(Object)} instead of this one.
	 */
	@Override
	final protected void onPostExecute(Result result) {
		onProcessingComplete(result);
	}
	
	
	/* ************************ Request Processing ************************ */
	
	
	
	
	@Override
	final protected Result doInBackground(Object... params) {
		int paramsSize = params.length;
		
		// Checks
		if (paramsSize < 1)
			throw new IllegalArgumentException("Not enough parameters");
		for (Object param : params)
			if (param == null)
				throw new IllegalArgumentException("null parameter");
		
		// Retrieving parameters
		Object pcpPayload = params[0];
		CacheOptions cacheOpt = null;
		Collection<BinaryData> binData = new LinkedList<BinaryData>();
		for (int i = 1; i < params.length; i++) {
			Object param = params[i];
			
			if (param instanceof CacheOptions) {
				cacheOpt = (CacheOptions) param;
			}
			else if (param instanceof BinaryData) {
				binData.add((BinaryData) param);
			}
		}
		
		// Sending request
		try {
			Packet result = sendRequest(pcpPayload, cacheOpt, binData);
			
			testCancelled();
			
			Payload rPayload = result.getPayload();
			return onPacketReceived(rPayload);
		} catch (CancellationException e) {
			// Already cancelled
			return null;
		} catch (CommunicationException e) {
			onError(e);
			return null;
		}
	}
	
	
	/**
	 * Sends a PCP request to the server
	 * @param payload
	 * @param cacheOpts
	 * @param binData
	 * @return
	 * @throws CommunicationException
	 */
	private Packet sendRequest(Object payload, CacheOptions cacheOpts, Collection<BinaryData> binData) throws CommunicationException {
		// TODO : Implement caching
		try {
			String jsonPayload = gson_.toJson(payload);
		
			Packet oPacket = createPacket(pluginInfo_, action_, jsonPayload);
			
			testCancelled();
			
			return getContent(oPacket, binData);
			
		} catch (CancellationException e) {
			return null;
		} catch (ClientProtocolException e) {
			Log.d(DEBUG_TAG, "Error in the HTTP Protocol", e);
			throw new CommunicationException(e);
		} catch (IOException e) {
			Log.d(DEBUG_TAG, "IOException in getContent()" , e);
			throw new CommunicationException(e);
		} catch (JsonParseException e) {
			Log.d(DEBUG_TAG, "Malformed PCP Packet : Unable to parse JSON", e);
			throw new CommunicationException(e);
		} catch (CommunicationException e) {
			Log.d(DEBUG_TAG, "An error occured while processing the request", e);
			throw e;
		}
	}
	
	/**
	 * Retrieves the result of the given PCP Request Packet
	 * @param rPacket
	 * @param url
	 * @return
	 * @throws CommunicationException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	private Packet getContent(Packet rPacket, Collection<BinaryData> binData) throws CommunicationException, IllegalStateException, IOException {
		String rJson = gson_.toJson(rPacket);
		
		MultipartEntity mpEntity = new MultipartEntity();
		mpEntity.addPart(formKeyForPcpPacket_, new StringBody(rJson, pcpCharset_));
		for (BinaryData bd : binData) {
			bd.addToMultipartEntity(mpEntity);
		}
		
		testCancelled();
		
		// Send Request, retrieve response
		HttpResponse response = httpPostRequest(mpEntity);
		
		testCancelled();
	
	
		int httpStatusCode = response.getStatusLine().getStatusCode();
		
		if (httpStatusCode != 200) {
			// TODO : Handle all other HTTP statuses
			throw new CommunicationException("Unhandled HTTP Error : " + httpStatusCode);
		}
		
		HttpEntity entity = response.getEntity();
		
		if (entity == null) {
			throw new CommunicationException("Unhandled null entity");
		}
		
		/* TODO
		 * - Check encoding (UTF-8)
		 */
//		long length = entity.getContentLength();
//		Header encoding = entity.getContentEncoding();
		
		testCancelled();
		
		InputStreamReader json = new InputStreamReader(entity.getContent());
		
		Packet packet = gson_.fromJson(json, Packet.class);
		
		ServerOptions options = (ServerOptions) packet.getOptions();
		PcpStatus status = options.getStatus();
		
		if (!status.equals(PcpStatus.OK)) {
			throw new CommunicationException("PCP Error : " + status.toString());
		}
		
		return packet;
	}

	/**
	 * Sends an HTTP POST request
	 * @param payload
	 * @param uri
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws CommunicationException 
	 */
	private HttpResponse httpPostRequest(HttpEntity entity) throws ClientProtocolException, IOException, CommunicationException {
		HttpPost request = new HttpPost(getUrl());
		request.setEntity(entity);
		
		return httpRequest(request);
	}
	
	/**
	 * Sends a generic HTTP request (the type is determined by the given request)
	 * @param request
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws CommunicationException 
	 */
	private HttpResponse httpRequest(HttpPost request) throws ClientProtocolException, IOException, CommunicationException {
		HttpParams params = new BasicHttpParams();
		
		// Configure timeouts
		Timeout timeout = getTimeout();
		if (timeout != null) {
			// Timeout for the connection to be established
			HttpConnectionParams.setConnectionTimeout(
					params,
					new Long(getTimeout().connection(TimeUnit.MILLISECONDS)).intValue());
			// Socket timeout, for waiting data
			HttpConnectionParams.setSoTimeout(
					params,
					new Long(getTimeout().socket(TimeUnit.MILLISECONDS)).intValue());
		}
		else {
			// Use default timeouts, already configured in the connection manager
		}
		
		HttpClient client = new DefaultHttpClient(connectionManager_, params);
		
		try {
			// Sends the request and retrieves the response
			HttpResponse response = client.execute(request);
			return response;
		} catch (ConnectTimeoutException e){
			// It took too long to connect to the remote host
			request.abort();
			cancel(true);
			throw new CommunicationException("Connection Timeout", e);
		} catch (SocketTimeoutException e){
			// Remote host didn’t respond in time
			request.abort();
			cancel(true);
			throw new CommunicationException("Socket Timeout", e);
		} catch (RuntimeException e) {
			request.abort();
			cancel(true);
			throw new CommunicationException(e);
		}
	}
	
	
	/* ************************ Helper Methods ************************ */
	
	
	/**
	 * Creates a PCP Client Packet
	 * @param pluginInfo
	 * @param action
	 * @param jsonPayload
	 * @return
	 */
	private static Packet createPacket(PluginInfo pluginInfo, String action, String jsonPayload) {
		ModuleInfo moduleInfo = new ModuleInfo(pluginInfo.getId().toString(), action);
		Payload payload = new Payload(jsonPayload, moduleInfo, pluginInfo.getVersion().toString());
		Options options = new ClientOptions();
		Packet packet = new Packet(payload, generator_, options);
		
		return packet;
	}
	
	/**
	 * @return this request's URL
	 */
	private String getUrl() {
		return host_ + "/" + pluginInfo_.getId() + "/" + action_;
	}
	
	/**
	 * Throws a {@code CancellationException} if this AsyncTask was canceled
	 * @throws CancellationException
	 * @see {@link android.os.AsyncTask#isCancelled()}
	 */
	final protected void testCancelled() throws CancellationException {
		if (isCancelled())
			throw new CancellationException();
	}
	
	
	/* ************************ Execution entry point ************************ */
	
	
	
	/**
	 * <p>Executes this PCP Request with given parameters. Use this method instead of
	 * {@link AsyncTask#execute(Object...)}.<br />
	 * <i>This method must be invoked in the <b>UI Thread</b>.</i></p>
	 * 
	 * <h1>Parameters</h1>
	 * <p>Multiple parameters are allowed, and except for the first parameter, their order
	 * does not matter :</p>
	 * <ul>
	 * <li>The first parameter is mandatory and is the actual PCP Payload : any Object,
	 * will be sent to the remote PCP server, automatically converted in JSON before
	 * being sent</li>
	 * <li>One parameter can be a {@code CacheOptions} object (optional)</li>
	 * <li>Many parameters can be {@code BinaryData} objects (optional)</li>
	 * </ul>
	 * @param params the parameters of the request
	 */
	public void start(final Object... params) {
		if (!stateValid_)
			throw new IllegalStateException("This PcpRequest is not properly configured" +
					" and cannot execute. Provide host, pluginInfo and target information.");
		
		super.execute(params);
	}
}
