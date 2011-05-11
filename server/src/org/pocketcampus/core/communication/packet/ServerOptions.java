package org.pocketcampus.core.communication.packet;

import java.lang.reflect.Type;

import org.pocketcampus.core.communication.PcpStatus;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Defines the set of PCP options that can be set by the server
 */
public class ServerOptions extends Options implements Cloneable {
	@Expose
	@SerializedName("Status")
	private PcpStatus status;
	
	/**
	 * Instance creator. Use it with the builder pattern
	 */
	public ServerOptions() {}
	
	/**
	 * Clones the given ServerOptions. Intended for deep cloning by subclasses.
	 * @param that the ServerOption to clone
	 */
	protected ServerOptions(ServerOptions that) {
		super(that);
		this.status = that.status;
	}
	
	/**
	 * Sets the <i>Status</i> field of this ServerOptions, and return this instance.
	 * @param status
	 * @return this modified instance
	 */
	public ServerOptions setStatus(PcpStatus status) {
		this.status = status;
		return this;
	}
	
	/**
	 * Returns the value of the <i>Status</i> field.
	 * @return
	 */
	public PcpStatus getStatus() {
		return this.status;
	}
	
	
	@Override
	public ServerOptions clone() {
		return new ServerOptions(this);
	}
	
	
	/**
	 * Provides a cyclic adapter to use as an adapter for other classes, giving them
	 * the default Gson's behavior for ServerOptions deserialization.<br />
	 * <b>Don't use it to register an adapter for <code>ServerOptions</code></b> : Since
	 * it is cyclic, it will generate a stack overflow.
	 */
	public static class GsonCyclicAdapter implements JsonDeserializer<ServerOptions> {

		@Override
		public ServerOptions deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			
			ServerOptions out = context.deserialize(json, ServerOptions.class);
			return out;
		}
		
	}
}
