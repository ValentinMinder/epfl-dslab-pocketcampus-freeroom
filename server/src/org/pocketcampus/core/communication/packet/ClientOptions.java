package org.pocketcampus.core.communication.packet;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class ClientOptions extends Options implements Cloneable {
	
	/**
	 * Instance creator. Use it with the builder pattern
	 */
	public ClientOptions() {}
	
	/**
	 * Clones the given ClientOptions. Intended for deep cloning by subclasses.
	 * @param that the ClientOption to clone
	 */
	protected ClientOptions(ClientOptions that) {
		super(that);
		// Clone that fields into this
	}
	
	public ClientOptions clone() {
		return new ClientOptions(this);
	}
	
	
	/**
	 * Provides a cyclic adapter to use as an adapter for other classes, giving them
	 * the default Gson's behavior for ClientOptions deserialization.<br />
	 * <b>Don't use it to register an adapter for <code>ClientOptions</code></b> : Since
	 * it is cyclic, it will generate a stack overflow.
	 */
	public static class GsonCyclicAdapter implements JsonDeserializer<ClientOptions> {

		@Override
		public ClientOptions deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			
			ClientOptions out = context.deserialize(json, ClientOptions.class);
			return out;
		}
		
	}
}
