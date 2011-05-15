package org.pocketcampus.shared.core.communication.packet;

import com.google.gson.GsonBuilder;

public class GsonTypeAdapters {
	public static enum Side {
		Client,
		Server
	}
	
	/**
	 * Register all Gson adapters needed to (de)serialize correctly a PCP Packet.
	 * @param gsonBuilder the GsonBuilder into which the type adapters will be registered
	 * @param side specifies which side generated the packet, for side-specific fields
	 * @return
	 */
	public static GsonBuilder register(GsonBuilder gsonBuilder, Side side) {
		gsonBuilder
				.registerTypeAdapter(ModuleInfo.class, new ModuleInfo.GsonAdapter())
				.registerTypeAdapter(Protocol.class, new Protocol.GsonAdapter())
				.registerTypeAdapter(RawJson.class, new RawJson.GsonAdapter())
				;
		
		switch (side) {
		case Client:
			gsonBuilder.registerTypeAdapter(ClientOptions.class, new ClientOptions.GsonCyclicAdapter());
			break;
		case Server:
			gsonBuilder.registerTypeAdapter(ServerOptions.class, new ServerOptions.GsonCyclicAdapter());
			break;
		default:
			throw new UnsupportedOperationException();
		}
		
		return gsonBuilder;
	}
}
